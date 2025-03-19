package com.microsoft.hadoop.azure;
import static com.microsoft.hadoop.azure.AzureTableConfiguration.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import com.microsoft.windowsazure.storage.StorageException;
import com.microsoft.windowsazure.storage.table.*;

/**
 * A record reader that will read the rows from a given input
 * split.
 */
public class AzureTableRecordReader extends RecordReader<Text, WritableEntity> {
  private Iterator<WritableEntity> queryResults;

  private WritableEntity currentEntity;

  private Text currentKey = new Text();

  /**
	 * Called once at initialization.
	 * @param split the split that defines the range of records to read
	 * @param context the information about the task
	 * @throws IOException
	 * @throws InterruptedException
	 */
  public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
    Configuration job = context.getConfiguration();
    CloudTableClient tableClient = createTableClient(job);
    String tableName = getTableName(job);
    TableQuery<WritableEntity> query = ((AzureTableInputSplit) split).getQuery();
    try {
      queryResults = tableClient.getTableReference(tableName).execute(query).iterator();
    } catch (StorageException e) {

<<<<<<< /usr/src/app/output/mooso/azure-tables-hadoop/f5ae55a8d5024fba05bd9a188ae18116b624a69d/src/main/java/com/microsoft/hadoop/azure/AzureTableRecordReader.java/left.java
      e.printStackTrace();
=======
      throw new IOException(e);
>>>>>>> /usr/src/app/output/mooso/azure-tables-hadoop/f5ae55a8d5024fba05bd9a188ae18116b624a69d/src/main/java/com/microsoft/hadoop/azure/AzureTableRecordReader.java/right.java
    } catch (URISyntaxException e) {

<<<<<<< /usr/src/app/output/mooso/azure-tables-hadoop/f5ae55a8d5024fba05bd9a188ae18116b624a69d/src/main/java/com/microsoft/hadoop/azure/AzureTableRecordReader.java/left.java
      e.printStackTrace();
=======
      throw new IllegalArgumentException(e);
>>>>>>> /usr/src/app/output/mooso/azure-tables-hadoop/f5ae55a8d5024fba05bd9a188ae18116b624a69d/src/main/java/com/microsoft/hadoop/azure/AzureTableRecordReader.java/right.java
    }
  }

  /**
	 * Read the next key, value pair.
	 * @return true if a key/value pair was read
	 * @throws IOException
	 * @throws InterruptedException
	 */
  public boolean nextKeyValue() throws IOException, InterruptedException {
    if (queryResults.hasNext()) {
      currentEntity = queryResults.next();
      currentKey.set(currentEntity.getRowKey());
      return true;
    } else {
      currentEntity = null;
      return false;
    }
  }

  /**
	 * Get the current key
	 * @return the current key or null if there is no current key
	 * @throws IOException
	 * @throws InterruptedException
	 */
  public Text getCurrentKey() throws IOException, InterruptedException {
    if (currentEntity == null) {
      return null;
    }
    return currentKey;
  }

  /**
	 * Get the current value.
	 * @return the object that was read
	 * @throws IOException
	 * @throws InterruptedException
	 */
  public WritableEntity getCurrentValue() throws IOException, InterruptedException {
    return currentEntity;
  }

  /**
	 * The current progress of the record reader through its data.
	 * @return a number between 0.0 and 1.0 that is the fraction of the data read
	 * @throws IOException
	 * @throws InterruptedException
	 */
  public float getProgress() throws IOException, InterruptedException {
    return 0.5f;
  }

  /**
	 * Close the record reader.
	 */
  public void close() throws IOException {
  }
}