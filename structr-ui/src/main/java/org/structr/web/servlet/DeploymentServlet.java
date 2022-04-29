/*
 * Copyright (C) 2010-2022 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.lingala.zip4j.ZipFile;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.api.config.Settings;
import org.structr.common.AccessMode;
import org.structr.common.PathHelper;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.app.StructrApp;
import org.structr.core.auth.exception.AuthenticationException;
import org.structr.core.graph.Tx;
import org.structr.rest.common.HttpHelper;
import org.structr.rest.service.HttpServiceServlet;
import org.structr.rest.service.StructrHttpServiceConfig;
import org.structr.rest.servlet.AbstractServletBase;
import org.structr.web.auth.UiAuthenticator;
import org.structr.web.maintenance.DeployCommand;
import org.structr.web.maintenance.DeployDataCommand;

//~--- classes ----------------------------------------------------------------
/**
 * Servlet to handle upload and download of application and data deployment files.
 */
public class DeploymentServlet extends AbstractServletBase implements HttpServiceServlet {

	private static final Logger logger = LoggerFactory.getLogger(DeploymentServlet.class.getName());

	private static final String DOWNLOAD_URL_PARAMETER        = "downloadUrl";
	private static final String REDIRECT_URL_PARAMETER        = "redirectUrl";
	private static final String FILE_PARAMETER                = "file";
	private static final String NAME_PARAMETER                = "name";
	private static final String MODE_PARAMETER                = "mode";
	private static final String TYPES_PARAMETER               = "types";
	private static final int MEGABYTE                         = 1024 * 1024;
	private static final int MEMORY_THRESHOLD                 = 10 * MEGABYTE;  // above 10 MB, files are stored on disk

	// non-static fields
	private ServletFileUpload uploader = null;
	private File filesDir = null;
	private final StructrHttpServiceConfig config = new StructrHttpServiceConfig();

	public DeploymentServlet() {
	}

	//~--- methods --------------------------------------------------------
	@Override
	public StructrHttpServiceConfig getConfig() {
		return config;
	}

	@Override
	public String getModuleName() {
		return "deployment";
	}

	@Override
	public void init() {

		try (final Tx tx = StructrApp.getInstance().tx()) {

			final DiskFileItemFactory fileFactory = new DiskFileItemFactory();
			fileFactory.setSizeThreshold(MEMORY_THRESHOLD);

			filesDir = new File(Settings.TmpPath.getValue()); // new File(Services.getInstance().getTmpPath());
			if (!filesDir.exists()) {

				filesDir.mkdir();
			}

			fileFactory.setRepository(filesDir);
			uploader = new ServletFileUpload(fileFactory);

			tx.success();

		} catch (FrameworkException t) {

			logger.warn("", t);
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {

		initRequest(request, response);

		SecurityContext securityContext = null;

		setCustomResponseHeaders(response);


		try (final Tx tx = StructrApp.getInstance().tx()) {

			try {

				securityContext = getConfig().getAuthenticator().initializeAndExamineRequest(request, response);

			} catch (final AuthenticationException ae) {

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getOutputStream().write("ERROR (401): Invalid user or password.\n".getBytes("UTF-8"));
				return;
			}

			if (!securityContext.isSuperUser()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getOutputStream().write("ERROR (401): Download of export ZIP file only allowed for admins.\n".getBytes("UTF-8"));
				return;
			}

			tx.success();

		} catch (Throwable t) {

			logger.error("Exception while processing request", t);
		}

		try {

			final String name = request.getParameter(NAME_PARAMETER);
			final String mode = StringUtils.defaultIfBlank(request.getParameter(MODE_PARAMETER), "app");

			if ("test".equals(mode)) {
				response.setStatus(HttpServletResponse.SC_OK);
				return;
			}

			if (StringUtils.isEmpty(name)) {

				final String message = "ERROR (400): Empty name parameter.\n";
				logger.error(message);

				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getOutputStream().write(message.getBytes("UTF-8"));

				return;
			}

			if ("app".equals(mode)) {

				handleAppDownloadAsZip(response, securityContext, name);

			} else if ("data".equals(mode)) {

				final String types = request.getParameter(TYPES_PARAMETER);
				handleDataDownloadAsZip(response, securityContext, name, types);

			} else {

				final String message = "ERROR (400): Unknown mode.\n";
				logger.error(message);

				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getOutputStream().write(message.getBytes("UTF-8"));

				return;
			}

		} catch (Throwable t) {

			logger.error("Exception while processing request", t);
		}
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {

		initRequest(request, response);

		SecurityContext securityContext = null;

		setCustomResponseHeaders(response);

		try (final Tx tx = StructrApp.getInstance().tx()) {

			if (!ServletFileUpload.isMultipartContent(request)) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getOutputStream().write("ERROR (400): Request does not contain multipart content.\n".getBytes("UTF-8"));
				return;
			}

			try {

				securityContext = getConfig().getAuthenticator().initializeAndExamineRequest(request, response);

			} catch (AuthenticationException ae) {

				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getOutputStream().write("ERROR (401): Invalid user or password.\n".getBytes("UTF-8"));
				return;
			}

			if (securityContext.getUser(false) == null && !Settings.DeploymentAllowAnonymousUploads.getValue()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getOutputStream().write("ERROR (401): Anonymous uploads forbidden.\n".getBytes("UTF-8"));
				return;
			}

			tx.success();

		} catch (Throwable t) {

			logger.error("Exception while processing request", t);
			return;
		}

		String redirectUrl = null;

		try {

			// Ensure access mode is frontend
			securityContext.setAccessMode(AccessMode.Frontend);

			request.setCharacterEncoding("UTF-8");

			// Important: Set character encoding before calling response.getWriter() !!, see Servlet Spec 5.4
			response.setCharacterEncoding("UTF-8");

			// don't continue on redirects
			if (response.getStatus() == 302) {
				return;
			}

			uploader.setFileSizeMax(MEGABYTE * Settings.DeploymentMaxFileSize.getValue());
			uploader.setSizeMax(MEGABYTE * Settings.DeploymentMaxRequestSize.getValue());

			response.setContentType("text/html");

			final FileItemIterator fileItemsIterator   = uploader.getItemIterator(request);
			final String directoryPath                 = "/tmp/" + UUID.randomUUID();
			final String filePath                      = directoryPath + ".zip";
			final File file                            = new File(filePath);

			String downloadUrl        = null;
			String fileName           = null;
			String mode               = null;
			boolean rebuildAllIndexes = false;

			while (fileItemsIterator.hasNext()) {

				final FileItemStream item = fileItemsIterator.next();

				if (item.isFormField()) {

					final String fieldName = item.getFieldName();
					final String fieldValue = IOUtils.toString(item.openStream(), "UTF-8");

					if (DOWNLOAD_URL_PARAMETER.equals(fieldName)) {

						downloadUrl = StringUtils.trim(fieldValue);

					} else if (REDIRECT_URL_PARAMETER.equals(fieldName)) {

						redirectUrl = fieldValue;

					} else if (DeployDataCommand.REBUILDALLINDEXES_PARAMTER_NAME.equals(fieldName)) {

						rebuildAllIndexes = fieldValue.equals("true");

					} else if (MODE_PARAMETER.equals(fieldName)) {

						mode = fieldValue;

					} else if (FILE_PARAMETER.equals(fieldName)) {

						try (final InputStream is = item.openStream()) {

							Files.write(file.toPath(), IOUtils.toByteArray(is));
							fileName = item.getName();
						}
					}

				} else {

					try (final InputStream is = item.openStream()) {

						Files.write(file.toPath(), IOUtils.toByteArray(is));
						fileName = item.getName();
					}
				}
			}

			if (StringUtils.isNotBlank(downloadUrl)) {

				try {

					HttpHelper.streamURLToFile(downloadUrl, file);
					fileName = PathHelper.getName(downloadUrl);

				} catch (final Throwable t) {

					final String message = "ERROR (400): Unable to download from URL.\n" + t.getMessage() + "\n";

					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getOutputStream().write(message.getBytes("UTF-8"));

					return;
				}

				if (!(file.exists() && file.length() > 0L)) {

					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getOutputStream().write("ERROR: Unable to process downloaded file.\n".getBytes("UTF-8"));

					return;
				}
			}

			if (file.exists() && file.length() > 0L) {

				try {

					if ("app".equals(mode)) {

						deployAppFile(file, fileName, directoryPath, securityContext);

					} else if ("data".equals(mode)) {

						deployDataFile(file, fileName, directoryPath, securityContext, rebuildAllIndexes);

					} else {

						final String message = "ERROR (400): Unknown mode.\n";
						logger.error(message);

						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getOutputStream().write(message.getBytes("UTF-8"));

						return;
					}

				} catch (final Throwable t) {

					final String message = "ERROR (400): Unable to deploy file.\n" + t.getMessage() + "\n";

					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getOutputStream().write(message.getBytes("UTF-8"));

					return;
				}

			} else {

				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getOutputStream().write("ERROR: Unable to process uploaded file.\n".getBytes("UTF-8"));

				return;
			}

			// send redirect to allow form-based file upload without JavaScript...
			if (StringUtils.isNotBlank(redirectUrl)) {

				sendRedirectHeader(response, redirectUrl, false);	// user-provided, should be already prefixed
			}

		} catch (Exception t) {

			logger.error("Exception while processing request", t);

			// send redirect to allow form-based file upload without JavaScript...
			if (StringUtils.isNotBlank(redirectUrl)) {

				try {
					sendRedirectHeader(response, redirectUrl, false);	// user-provided, should be already prefixed
				} catch (IOException ex) {
					logger.error("Unable to redirect to " + redirectUrl);
				}

			} else {

				UiAuthenticator.writeInternalServerError(response);
			}
		}
	}

	/**
	 *
	 * @param response
	 * @param securityContext
	 * @param name
	 */
	private void handleAppDownloadAsZip(final HttpServletResponse response, final SecurityContext securityContext, final String name) {

		logger.info("Preparing deployment export for download as zip");

		try {

			DeployCommand deployCommand = StructrApp.getInstance(securityContext).command(DeployCommand.class);
			final Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), Long.toString(System.currentTimeMillis()), name);
			final String exportTargetFolder = tmpDir.toString();

			if (!exportTargetFolder.equals(tmpDir.normalize().toString())) {

				final String message = "ERROR (403): Path traversal not allowed - not serving deployment zip!\n";
				logger.error(message);

				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getOutputStream().write(message.getBytes("UTF-8"));

				return;
			}

			final Map<String, Object> attributes = new HashMap<>();

			attributes.put("mode", "export");
			attributes.put("target", exportTargetFolder);

			deployCommand.execute(attributes);

			logger.info("Creating zip");

			final File file = zip(exportTargetFolder);

			response.setContentType("application/zip");
			response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

			final FileInputStream     in  = new FileInputStream(file);
			final ServletOutputStream out = response.getOutputStream();

			final long fileSize = IOUtils.copyLarge(in, out);
			final int status    = response.getStatus();

			response.addHeader("Content-Length", Long.toString(fileSize));
			response.setStatus(status);

			out.flush();
			out.close();

		} catch (final Exception ex) {

			logger.error("Exception while processing request", ex);
		}
	}

	/**
	 *
	 * @param response
	 * @param securityContext
	 * @param name
	 * @param types
	 */
	private void handleDataDownloadAsZip(final HttpServletResponse response, final SecurityContext securityContext, final String name, final String types) {

		logger.info("Preparing data deployment export for download as zip");

		try {

			DeployDataCommand deployDataCommand = StructrApp.getInstance(securityContext).command(DeployDataCommand.class);
			final Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), Long.toString(System.currentTimeMillis()), name);
			final String exportTargetFolder = tmpDir.toString();

			if (!exportTargetFolder.equals(tmpDir.normalize().toString())) {

				final String message = "ERROR (403): Path traversal not allowed - not serving deployment zip!\n";
				logger.error(message);

				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.getOutputStream().write(message.getBytes("UTF-8"));

				return;
			}

			final Map<String, Object> attributes = new HashMap<>();

			attributes.put("mode", "export");
			attributes.put("target", exportTargetFolder);
			attributes.put("types", types);

			deployDataCommand.execute(attributes);

			logger.info("Creating zip");

			final File file = zip(exportTargetFolder);

			response.setContentType("application/zip");
			response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");

			final FileInputStream     in  = new FileInputStream(file);
			final ServletOutputStream out = response.getOutputStream();

			final long fileSize = IOUtils.copyLarge(in, out);
			final int status    = response.getStatus();

			response.addHeader("Content-Length", Long.toString(fileSize));
			response.setStatus(status);

			out.flush();
			out.close();

		} catch (final Exception ex) {

			logger.error("Exception while processing request", ex);
		}

	}

	/**
	 *
	 * @param file
	 * @param fileName
	 * @param directoryPath
	 * @param securityContext
	 * @throws FrameworkException
	 * @throws IOException
	 */
	private void deployAppFile(final File file, final String fileName, final String directoryPath, final SecurityContext securityContext) throws FrameworkException, IOException {

		unzip(file, directoryPath);

		final DeployCommand deployCommand = StructrApp.getInstance(securityContext).command(DeployCommand.class);

		final Map<String, Object> attributes = new HashMap<>();

		attributes.put("mode", "import");
		attributes.put("source", directoryPath  + "/" + StringUtils.substringBeforeLast(fileName, "."));

		deployCommand.execute(attributes);

		file.delete();
		final File dir = new File(directoryPath);
		dir.delete();
	}

	/**
	 *
	 * @param file
	 * @param fileName
	 * @param directoryPath
	 * @param securityContext
	 * @throws FrameworkException
	 * @throws IOException
	 */
	private void deployDataFile(final File file, final String fileName, final String directoryPath, final SecurityContext securityContext, final boolean rebuildAllIndexes) throws FrameworkException, IOException {

		unzip(file, directoryPath);

		final DeployDataCommand deployDataCommand = StructrApp.getInstance(securityContext).command(DeployDataCommand.class);

		final Map<String, Object> attributes = new HashMap<>();

		attributes.put("mode", "import");
		attributes.put("source", directoryPath  + "/" + StringUtils.substringBeforeLast(fileName, "."));
		attributes.put(DeployDataCommand.REBUILDALLINDEXES_PARAMTER_NAME, rebuildAllIndexes);

		deployDataCommand.execute(attributes);

		file.delete();
		final File dir = new File(directoryPath);
		dir.delete();
	}

	/**
	 * Zip given directory to file.
	 *
	 * @param sourceDirectoryPath
	 * @throws IOException
	 * @return file
	 */
	private File zip(final String sourceDirectoryPath) throws IOException {

		final String zipFilePath   = StringUtils.stripEnd(sourceDirectoryPath, "/").concat(".zip");

		final ZipFile zipFile = new ZipFile(zipFilePath);
		zipFile.addFolder(new File(sourceDirectoryPath));

		return zipFile.getFile();
	}

	/**
	 * Unzip given file to given output directory.
	 *
	 * @param file
	 * @param outputDir
	 * @throws IOException
	 */
	private void unzip(final File file, final String outputDir) throws IOException {
		new ZipFile(file).extractAll(outputDir);
	}


	/**
	 * Initalize request.
	 *
	 * @param request
	 * @param response
	 * @throws ServletException
	 */
	private void initRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {

		try {

			assertInitialized();

		} catch (FrameworkException fex) {

			try {
				response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
				response.getOutputStream().write(fex.getMessage().getBytes("UTF-8"));

			} catch (IOException ioex) {

				logger.warn("Unable to send response", ioex);
			}

		}
	}
}

