package org.apache.accumulo.core.gc;
import org.apache.accumulo.core.data.TableId;

/**
 * A GC reference used for collecting files and directories into a single stream. The GC deals with
 * two inputs conceptually: candidates and references. Candidates are files that could be possibly
 * be deleted if they are not defeated by a reference.
 */
public interface Reference {
  /**
   * Only return true if the reference is a directory.
   */
  boolean isDirectory();

  /**
   * Only return true if the reference is a scan.
   */
  boolean isScan();

  /**
   * Get the {@link TableId} of the reference.
   */
  TableId getTableId();

  /**
   * Get the path stored in the metadata table for this file or directory. The path will be read
   * from the Tablet "file" column family:
   * {@link org.apache.accumulo.core.metadata.schema.MetadataSchema.TabletsSection.DataFileColumnFamily}
   * A directory will be read from the "srv:dir" column family:
   * {@link org.apache.accumulo.core.metadata.schema.MetadataSchema.TabletsSection.ServerColumnFamily}
   */
  String getMetadataPath();
}