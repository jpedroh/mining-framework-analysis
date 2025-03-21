package org.apache.accumulo.server.gc;
import static org.apache.accumulo.server.gc.GcVolumeUtil.ALL_VOLUMES_PREFIX;
import org.apache.accumulo.core.Constants;
import org.apache.accumulo.core.data.TableId;
import org.apache.accumulo.core.gc.ReferenceFile;
import org.apache.accumulo.core.metadata.schema.MetadataSchema;
import org.apache.hadoop.fs.Path;

/**
 * A specially encoded GC Reference to a directory with the {@link GcVolumeUtil#ALL_VOLUMES_PREFIX}
 */
public class AllVolumesDirectory extends ReferenceFile {
  public AllVolumesDirectory(TableId tableId, String dirName) {
    super(tableId, getDeleteTabletOnAllVolumesUri(tableId, dirName), false);
  }

  private static String getDeleteTabletOnAllVolumesUri(TableId tableId, String dirName) {
    MetadataSchema.TabletsSection.ServerColumnFamily.validateDirCol(dirName);
    return ALL_VOLUMES_PREFIX + Constants.TABLE_DIR + Path.SEPARATOR + tableId + Path.SEPARATOR + dirName;
  }

  @Override public String getMetadataPath() {
    return metadataPath;
  }

  @Override public boolean isDirectory() {
    return true;
  }
}