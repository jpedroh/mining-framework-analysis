/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.lists;

import java.util.List;

import org.cloudbus.cloudsim.ResCloudlet;

/**
 * ResCloudletList is a collection of operations on lists of ResCloudlets.
 * 
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 2.0
 */
public class ResCloudletList {

	/**
<<<<<<< /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/left.java
	 * Gets a {@link ResCloudlet} with a given id and owned by a given user.
	         * This method needs a combination of Cloudlet Id and User Id because
	 * each Cloud User might have exactly the same Cloudlet Id.
||||||| /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/base.java
	 * Returns a given Cloudlet. This method needs a combination of Cloudlet Id and User Id because
	 * each Cloud Users might have exactly same Cloudlet Ids.
=======
	 * Gets a ResCloudlet that has a given id and user id. 
	         * This method needs a combination of Cloudlet Id and User Id because
	 * each Cloud User might have exactly the same Cloudlet Id.
>>>>>>> /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/right.java
	 * 
	 * @param cloudletId a Cloudlet Id
	 * @param userId an User Id
	 * @param list the list of ResCloudlet
	 * @return a Cloudlet or null if not found

	         * @pre cloudletId >= 0
	 * @pre userId >= 0
	 * @post $none
	         * 
	         * @todo The second phrase of the class documentation is not clear. 
	 */
	public static <T extends ResCloudlet> ResCloudlet getByIdAndUserId(
			List<T> list,
			int cloudletId,
			int userId) {
		for (T rcl : list) {
			if (rcl.getCloudletId() == cloudletId && rcl.getUserId() == userId) {
				return rcl;
			}
		}
		return null;
	}

	/**
<<<<<<< /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/left.java
	 * Finds the index of a ResCloudlet inside a list.  
         * This method needs a combination of Cloudlet Id
||||||| /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/base.java
	 * Finds the index of a Cloudlet inside the list. This method needs a combination of Cloudlet Id
=======
	 * Finds the index of a ResCloudlet inside the list.  
         * This method needs a combination of Cloudlet Id
>>>>>>> /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/right.java
	 * and User Id because each Cloud User might have exactly the same Cloudlet Id.
	 * 
	 * @param cloudletId a Cloudlet Id
	 * @param userId an User Id
	 * @param list the list of ResCloudlets
	 * @return the index in this list of the first occurrence of the specified Cloudlet, or
	 *         <code>-1</code> if the list does not contain the Cloudlet.
	 * @pre cloudletId >= 0
	 * @pre userId >= 0
	 * @post $none
	 */
	public static <T extends ResCloudlet> int indexOf(List<T> list, int cloudletId, int userId) {
		int i = 0;
		for (T rcl : list) {
			if (rcl.getCloudletId() == cloudletId && rcl.getUserId() == userId) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
<<<<<<< /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/left.java
	 * Moves a ResCloudlet object from a list to another.
||||||| /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/base.java
	 * Move a ResCloudlet object from this linked-list into a specified one.
=======
	 * Move a ResCloudlet object from a list to another.
>>>>>>> /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/right.java
	 * 
	 * @param listFrom the source list
	 * @param listTo the destination list
	 * @param cloudlet the cloudlet to be moved from the source to the destination list
	 * @return <b>true</b> if the moving operation successful, <b>false</b> otherwise
	 * @pre obj != null
	 * @pre list != null
	 * @post $result == true || $result == false
	 */
	public static <T extends ResCloudlet> boolean move(List<T> listFrom, List<T> listTo, T cloudlet) {
		if (listFrom.remove(cloudlet)) {
			listTo.add(cloudlet);
			return true;
		}
		return false;
	}

	/**
<<<<<<< /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/left.java
	 * Gets the position of a ResCloudlet with a given id.
	         * 
	 * @param cloudletList the list of cloudlets.
	 * @param id the cloudlet id
	 * @return the position of the cloudlet with that id, or -1 if not found.
||||||| /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/base.java
	 * Returns the position of the cloudlet with that id, if it exists. Otherwise -1.
	 * @param cloudletList - the list of cloudlets.
	 * @param id - the id we search for.
	 * @return - the position of the cloudlet with that id, or -1 otherwise.
=======
	 * Returns the position of a ResCloudlet with a given id.
	         * 
	 * @param cloudletList the list of cloudlets.
	 * @param id the cloudlet id
	 * @return the position of the cloudlet with that id, or -1 if not found.
>>>>>>> /usr/src/app/output/cloudslab/cloudsim/e0205e647461340fcbdf2de516d1cd0ad9fd1154/modules/cloudsim/src/main/java/org/cloudbus/cloudsim/lists/ResCloudletList.java/right.java
	 */
	public static <T extends ResCloudlet> int getPositionById(List<T> cloudletList, int id) {
		int i = 0 ;
	        for (T cloudlet : cloudletList) {
			if (cloudlet.getCloudletId() == id) {
				return i;
			}
			i++;
		}
		return -1;
	}
}
