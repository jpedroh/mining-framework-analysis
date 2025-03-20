/**
 * jira-client - a simple JIRA REST client
 * Copyright (c) 2013 Bob Carroll (bob.carroll@alum.rit.edu)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.rcarz.jiraclient;

import java.util.Map;

import net.sf.json.JSONObject;

/**
 * Represents issue time tracking data.
 */
public class TimeTracking {

	private String originalEstimate = null;
	private String remainingEstimate = null;
	private String timeSpent = null;
	private Integer originalEstimateSeconds = null;
	private Integer remainingEstimateSeconds = null;
<<<<<<< /usr/src/app/output/rcarz/jira-client/b3dcbe8462805ea4fb8d7eee0a8cd312d69c50d3/src/main/java/net/rcarz/jiraclient/TimeTracking.java/left.java
	private Integer timeSpentSeconds = null;
||||||| /usr/src/app/output/rcarz/jira-client/b3dcbe8462805ea4fb8d7eee0a8cd312d69c50d3/src/main/java/net/rcarz/jiraclient/TimeTracking.java/base.java
=======
	private int timeSpentSeconds = 0;
>>>>>>> /usr/src/app/output/rcarz/jira-client/b3dcbe8462805ea4fb8d7eee0a8cd312d69c50d3/src/main/java/net/rcarz/jiraclient/TimeTracking.java/right.java

    /**
 * Creates a time tracking structure from a JSON payload.
 * 
 * @param json
 *            JSON payload
 */
	protected TimeTracking(JSONObject json) {
		Map<?, ?> map = json;

		originalEstimate = Field.getString(map.get("originalEstimate"));
		remainingEstimate = Field.getString(map.get("remainingEstimate"));
		timeSpent = Field.getString(map.get("timeSpent"));
		originalEstimateSeconds = Field.getInteger(map.get("originalEstimateSeconds"));
		remainingEstimateSeconds = Field.getInteger(map.get("remainingEstimateSeconds"));
		timeSpentSeconds = Field.getInteger(map.get("timeSpentSeconds"));
	}

	public String getOriginalEstimate() {
		return originalEstimate;
	}

	public String getRemainingEstimate() {
		return remainingEstimate;
	}

	public String getTimeSpent() {
		return timeSpent;
	}

	public int getOriginalEstimateSeconds() {
		return originalEstimateSeconds;
	}

	public int getRemainingEstimateSeconds() {
		return remainingEstimateSeconds;
	}

	public int getTimeSpentSeconds() {
		return timeSpentSeconds;
	}
	
}
