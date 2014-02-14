package com.smartsheet.api;

/*
 * #[license]
 * Smartsheet SDK for Java
 * %%
 * Copyright (C) 2014 Smartsheet
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * %[license]
 */



import com.smartsheet.api.models.Discussion;

/**
 * <p>This interface provides methods to access Discussion resources that are associated to a resource object. Currently 
 * discussions can be added to sheets or rows.</p>
 * 
 * <p>Thread Safety: Implementation of this interface must be thread safe.</p>
 */
public interface AssociatedDiscussionResources {
	
	/**
	 * Create a discussion.
	 * 
	 * It mirrors to the following Smartsheet REST API method: POST /sheet/{id}/discussions POST /row/{id}/discussions
	 * 
	 * Parameters: - objectId : the ID of the object - discussion : the discussion object limited to the following
	 * attributes: * title * comment
	 * 
	 * Returns: the created discussion
	 * 
	 * Exceptions: - IllegalArgumentException : if any argument is null - InvalidRequestException : if there is any
	 * problem with the REST API request - AuthorizationException : if there is any problem with the REST API
	 * authorization(access token) - ResourceNotFoundException : if the resource can not be found -
	 * ServiceUnavailableException : if the REST API service is not available (possibly due to rate limiting) -
	 * SmartsheetRestException : if there is any other REST API related error occurred during the operation -
	 * SmartsheetException : if there is any other error occurred during the operation
	 *
	 * @param objectId the object id
	 * @param discussion the discussion
	 * @return the discussion
	 * @throws SmartsheetException the Smartsheet exception
	 */
	public Discussion createDiscussion(long objectId, Discussion discussion) throws SmartsheetException;
}
