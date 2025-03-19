package com.microsoft.hadoop.azure;

import java.util.*;

import com.microsoft.windowsazure.storage.table.*;
import com.microsoft.windowsazure.storage.StorageException;

public interface AzureTablePartitioner {
	public List<AzureTableInputSplit> getSplits(CloudTable table) throws StorageException;
}
