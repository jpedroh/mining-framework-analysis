/*
 * Copyright 2012 OmniFaces.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.omnifaces.resourcehandler;

import static org.omnifaces.util.Faces.getMimeType;

import java.io.IOException;

import java.io.InputStream;

import javax.faces.application.Resource;

import static org.omnifaces.util.Utils.toByteArray;

import java.io.ByteArrayInputStream;

import javax.faces.context.FacesContext;

import org.omnifaces.component.output.cache.Cache;

import org.omnifaces.component.output.cache.CacheFactory;

/**
 * <p>
 * This {@link Resource} implementation holds all the necessary information about combined resources in order to
 * properly serve combined resources on a single HTTP request.
 *
 * @author Bauke Scholtz
 * @author Stephan Rauh {@literal <www.beyondjava.net>}
 */
public class CombinedResource extends DynamicResource {

	// Properties -----------------------------------------------------------------------------------------------------

	private CombinedResourceInfo info;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Constructs a new combined resource based on the given resource name. This constructor is only used by
	 * {@link CombinedResourceHandler#createResource(String, String)}.
	 * @param resourceName The resource name of the combined resource.
	 */

	public CombinedResource(String resourceName, Integer cacheTTL) {
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/resourcehandler/CombinedResource.java/left.java
		super(resourceName, CombinedResourceHandler.LIBRARY_NAME, getMimeType(resourceName));
		String[] resourcePathParts = resourceName.split("\\.", 2)[0].split("/");
		String resourceId = resourcePathParts[resourcePathParts.length - 1];
||||||| /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/resourcehandler/CombinedResource.java/base.java
		String[] resourcePathParts = name.split("\\.", 2)[0].split("/");
		String resourceId = resourcePathParts[resourcePathParts.length - 1];
=======
		super(resourceName, CombinedResourceHandler.LIBRARY_NAME, getMimeType(resourceName));
		String[] resourcePathParts = resourceName.split("\\.", 2)[0].split("/");
		resourceId = resourcePathParts[resourcePathParts.length - 1];
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/resourcehandler/CombinedResource.java/right.java
		info = CombinedResourceInfo.get(resourceId);
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/resourcehandler/CombinedResource.java/left.java
||||||| /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/resourcehandler/CombinedResource.java/base.java
		setResourceName(name);
		setLibraryName(CombinedResourceHandler.LIBRARY_NAME);
		setContentType(Faces.getMimeType(name));
=======
		this.cacheTTL = cacheTTL;
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/resourcehandler/CombinedResource.java/right.java
	}

	// Actions --------------------------------------------------------------------------------------------------------

	@Override
	public long getLastModified() {
		return (info != null) ? info.getLastModified() : super.getLastModified();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (info != null && !info.getResources().isEmpty()) {
			if (cacheTTL == null) {
				return new CombinedResourceInputStream(info.getResources());
			}
			else {
				return getInputStreamFromCache();
			}
		}
		else {
			return null;
		}
	}

	// Constants ------------------------------------------------------------------------------------------------------

	private static final String CACHE_SCOPE = "application";

	// Properties -----------------------------------------------------------------------------------------------------

	private String resourceId;

	private Integer cacheTTL;

	// Constructors ---------------------------------------------------------------------------------------------------

	/**
	 * Constructs a new combined resource based on the given resource name. This constructor is only used by
	 * {@link CombinedResourceHandler#createResource(String, String)}.
	 * @param resourceName The resource name of the combined resource.
	 * @param cacheTTL The combined resource content cache TTL.
	 */

	// Actions --------------------------------------------------------------------------------------------------------

	/**
	 * Returns the cached input stream, or if there is none, then create one.
	 */

	private InputStream getInputStreamFromCache() throws IOException {
		Cache combinedResourceCache = CacheFactory.getCache(FacesContext.getCurrentInstance(), CACHE_SCOPE);
		byte[] cachedCombinedResource;

		synchronized (CombinedResourceHandler.class) {
			cachedCombinedResource = (byte[]) combinedResourceCache.getObject(resourceId);
		}

		if (cachedCombinedResource == null) {
			cachedCombinedResource = toByteArray(new CombinedResourceInputStream(info.getResources()));

			synchronized (CombinedResourceHandler.class) {
				if (combinedResourceCache.getObject(resourceId) == null) {
					combinedResourceCache.putObject(resourceId, cachedCombinedResource, cacheTTL);
				}
			}
		}

		return new ByteArrayInputStream(cachedCombinedResource);
	}

}
