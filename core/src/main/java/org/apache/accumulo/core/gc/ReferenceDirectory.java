package org.apache.accumulo.core.gc;
import org.apache.accumulo.core.data.TableId;
import org.apache.accumulo.core.metadata.schema.MetadataSchema;

/**
 * A GC reference to a Tablet directory, like t-0003.
 */
public class ReferenceDirectory extends ReferenceFile {
  private final String tabletDir;

  public ReferenceDirectory(TableId tableId, String dirName) {
    super(tableId, dirName, false);
    MetadataSchema.TabletsSection.ServerColumnFamily.validateDirCol(dirName);
    this.tabletDir = dirName;
  }

  @Override public boolean isDirectory() {
    return true;
  }

  public String getTabletDir() {
    return tabletDir;
  }

  /**
   * A Tablet directory should have a metadata entry equal to the dirName.
   */
  @Override public String getMetadataPath() {
    if (!tabletDir.equals(metadataPath)) {
      throw new IllegalStateException("Tablet dir " + tabletDir + " is not equal to metadataPath: " + metadataPath);
    }
    return metadataPath;
  }
}