package com.microsoft.hadoop.azure;
import java.util.*;
import com.microsoft.windowsazure.storage.StorageException;
import com.microsoft.windowsazure.storage.table.*;

/**
 * A default partitioner for Azure Tables that splits the table by
 * putting all the rows with a given Partition Key into a split.
 */
public class DefaultTablePartitioner implements AzureTablePartitioner {
  @Override public List<AzureTableInputSplit> getSplits(CloudTable table) throws StorageException {
    ArrayList<AzureTableInputSplit> ret = new ArrayList<AzureTableInputSplit>();
    Iterable<String> partitionKeys;
    partitionKeys = getAllPartitionKeys(table);
    for (String currentPartitionKey : partitionKeys) {
      ret.add(new PartitionInputSplit(currentPartitionKey));
    }
    return ret;
  }

  /**
	 * Gets the single entity in the given list.
	 * @param results The list of results that we know contains at most one entity.
	 * @return The single entity or null if none found.
	 */
  private static DynamicTableEntity GetSingleton(Iterable<DynamicTableEntity> results) {
    for (DynamicTableEntity t : results) {
      return t;
    }
    return null;
  }

  /**
	 * Query for the first row in the given table and don't retrieve
	 * any actual fields beyond the always-given partition and row keys.
	 * @param table The table to query.
	 * @return The query.
	 */
  private static TableQuery<DynamicTableEntity> getFirstRowNoFields() {
    return TableQuery.from(DynamicTableEntity.class).select(new String[0]).take(1);
  }

  /**
	 * Gets all the distinct partition keys in the given table.
	 * @param table The table to query.
	 * @return The list of distinct partition keys.
	 */
  static List<String> getAllPartitionKeys(CloudTable table) throws StorageException {
    TableQuery<DynamicTableEntity> getNextKeyQuery = getFirstRowNoFields();
    TableEntity currentEntity;
    ArrayList<String> ret = new ArrayList<String>();

<<<<<<< /usr/src/app/output/mooso/azure-tables-hadoop/f5ae55a8d5024fba05bd9a188ae18116b624a69d/src/main/java/com/microsoft/hadoop/azure/DefaultTablePartitioner.java/left.java
    try {
      while ((currentEntity = GetSingleton(table.execute(getNextKeyQuery))) != null) {
        ret.add(currentEntity.getPartitionKey());
        getNextKeyQuery = getFirstRowNoFields().where("PartitionKey gt \'" + currentEntity.getPartitionKey() + "\'");
      }
    } catch (StorageException e) {
      e.printStackTrace();
    }
=======
    while ((currentEntity = GetSingleton(table.execute(getNextKeyQuery))) != null) {
      ret.add(currentEntity.getPartitionKey());
      getNextKeyQuery = getFirstRowNoFields().where("PartitionKey gt \'" + currentEntity.getPartitionKey() + "\'");
    }
>>>>>>> /usr/src/app/output/mooso/azure-tables-hadoop/f5ae55a8d5024fba05bd9a188ae18116b624a69d/src/main/java/com/microsoft/hadoop/azure/DefaultTablePartitioner.java/right.java

    return ret;
  }
}