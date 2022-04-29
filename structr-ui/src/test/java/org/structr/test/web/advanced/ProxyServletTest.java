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
package org.structr.test.web.advanced;

import com.jayway.restassured.RestAssured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.api.config.Settings;
import org.structr.api.schema.JsonSchema;
import org.structr.api.schema.JsonType;
import org.structr.common.SecurityContext;
import org.structr.common.error.FrameworkException;
import org.structr.core.Services;
import org.structr.core.app.StructrApp;
import org.structr.core.graph.NodeAttribute;
import org.structr.core.graph.Tx;
import org.structr.rest.service.HttpService;
import org.structr.schema.export.StructrSchema;
import org.structr.test.web.StructrUiTest;
import org.structr.web.entity.File;
import org.structr.web.entity.Folder;
import org.structr.web.entity.User;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.charset.Charset;

import static org.testng.AssertJUnit.*;

/**
 *
 */
public class ProxyServletTest extends StructrUiTest {

	private static final Logger logger = LoggerFactory.getLogger(ProxyServletTest.class);

	@BeforeClass(alwaysRun = true)
	@Override
	public void setup() {
		final long timestamp = System.currentTimeMillis();

		basePath = "/tmp/structr-test-" + timestamp + System.nanoTime();

		setupDatabaseConnection();

		// example for new configuration setup
		Settings.BasePath.setValue(basePath);
		Settings.DatabasePath.setValue(basePath + "/db");
		Settings.FilesPath.setValue(basePath + "/files");

		Settings.SuperUserName.setValue("superadmin");
		Settings.SuperUserPassword.setValue("sehrgeheim");

		Settings.ApplicationTitle.setValue("structr unit test app" + timestamp);
		Settings.ApplicationHost.setValue(host);
		Settings.HttpPort.setValue(httpPort);

		Settings.Servlets.setValue("ProxyServlet");

		final Services services = Services.getInstance();

		// wait for service layer to be initialized
		do {
			try {
				Thread.sleep(100);
			} catch (Throwable t) {
			}

		} while (!services.isInitialized());

		securityContext = SecurityContext.getSuperUserInstance();

		app = StructrApp.getInstance(securityContext);

		baseUri = "http://" + host + ":" + httpPort + htmlUrl + "/";

		// configure RestAssured
		RestAssured.basePath = restUrl;
		RestAssured.baseURI  = "http://" + host + ":" + httpPort;
		RestAssured.port     = httpPort;
	}

	@Test
	public void testDisabledMode() {

		restartHttpServiceInMode("disabled");

		RestAssured.basePath = "/";
		final String response = RestAssured

			.expect()
				.statusCode(503)

			.when()
				.post("structr/proxy")

			.andReturn()
				.body()
				.asString();
	}

	@Test
	public void testProtectedMode1() {

		restartHttpServiceInMode("protected");

		RestAssured.basePath = "/";
		final String response = RestAssured

			.expect()
				.statusCode(401)

			.when()
				.post("structr/proxy")

			.andReturn()
				.body()
				.asString();
	}

	@Test
	public void testProtectedMode2() {

		restartHttpServiceInMode("protected");

		try (final Tx tx = app.tx()) {

			createTestNode(User.class,
					new NodeAttribute<>(StructrApp.key(User.class, "name"),     "admin"),
					new NodeAttribute<>(StructrApp.key(User.class, "password"), "admin"),
					new NodeAttribute<>(StructrApp.key(User.class, "isAdmin"), true)
			);
			tx.success();

		} catch (FrameworkException fex) {

			fail("Unexpected exception");
			logger.warn("", fex);
		}

		RestAssured.basePath = "/";
		final String response = RestAssured

			.given()
				.header("X-User",     "admin")
				.header("X-Password", "admin")

			.expect()
				.statusCode(200)

			.when()
				.post("structr/proxy?url=test")

			.andReturn()
				.body()
				.asString();
	}

	public void testPublicMode() {

		restartHttpServiceInMode("public");

		RestAssured.basePath = "/";
		final String response = RestAssured

				.expect()
				.statusCode(200)

				.when()
				.post("structr/?url=test")

				.andReturn()
				.body()
				.asString();
	}

	private void restartHttpServiceInMode(final String mode) {

		Settings.ProxyServletMode.setValue(mode);

		final Services services       = Services.getInstance();
		final Class httpServiceClass  = services.getServiceClassForName("HttpService");
		final HttpService httpService = ((HttpService)services.getService(httpServiceClass, "default"));

		httpService.stopService();
		try {
			httpService.startService();
		} catch (final Exception ex) {

		}
	}

}
