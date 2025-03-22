package org.vafer.jdeb;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarConstants;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.compressors.CompressorException;
import org.vafer.jdeb.utils.Utils;

/**
 * Builds the data archive of the Debian package.
 */
class DataBuilder {
  private Console console;

  private ZipEncoding encoding;

  private static final class Total {
    private BigInteger count = BigInteger.valueOf(0);

    public void add(long size) {
      count = count.add(BigInteger.valueOf(size));
    }

    public String toString() {
      return "" + count;
    }
  }

  DataBuilder(Console console) {
    this.console = console;
    this.encoding = ZipEncodingHelper.getZipEncoding(null);
  }

  private void checkField(String name, int length) throws IOException {
    if (name != null) {
      ByteBuffer b = encoding.encode(name);
      if (b.limit() > length) {
        throw new IllegalArgumentException("Field \'" + name + "\' too long, maximum is " + length);
      }
    }
  }

  /**
     * Build the data archive of the deb from the provided DataProducers
     *
     * @param producers
     * @param output
     * @param checksums
     * @param compression the compression method used for the data file
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.io.IOException
     * @throws org.apache.commons.compress.compressors.CompressorException
     */
  BigInteger buildData(Collection<DataProducer> producers, File output, final StringBuilder checksums, Compression compression) throws NoSuchAlgorithmException, IOException, CompressorException {
    final File dir = output.getParentFile();
    if (dir != null && (!dir.exists() || !dir.isDirectory())) {
      throw new IOException("Cannot write data file at \'" + output.getAbsolutePath() + "\'");
    }
    final TarArchiveOutputStream tarOutputStream = new TarArchiveOutputStream(compression.toCompressedOutputStream(new FileOutputStream(output)));
    tarOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
    final MessageDigest digest = MessageDigest.getInstance("MD5");
    final Total dataSize = new Total();
    final List<String> addedDirectories = new ArrayList<String>();
    final DataConsumer receiver = new DataConsumer() {
      public void onEachDir(String dirname, String linkname, String user, int uid, String group, int gid, int mode, long size) throws IOException {
        checkField(linkname, TarConstants.NAMELEN);
        checkField(user, TarConstants.UNAMELEN);
        checkField(group, TarConstants.GNAMELEN);
        dirname = fixPath(dirname);
        createParentDirectories(dirname, user, uid, group, gid);
        createDirectory(dirname, user, uid, group, gid, mode, 0);
        console.debug("dir: " + dirname);
      }

      public void onEachFile(InputStream input, TarArchiveEntry entry) throws IOException {
        checkField(entry.getLinkName(), TarConstants.NAMELEN);
        checkField(entry.getUserName(), TarConstants.UNAMELEN);
        checkField(entry.getGroupName(), TarConstants.GNAMELEN);
        entry.setName(fixPath(entry.getName()));
        createParentDirectories(entry.getName(), entry.getUserName(), entry.getUserId(), entry.getGroupName(), entry.getGroupId());
        tarOutputStream.putArchiveEntry(entry);
        dataSize.add(entry.getSize());
        digest.reset();
        Utils.copy(input, new DigestOutputStream(tarOutputStream, digest));
        final String md5 = Utils.toHex(digest.digest());
        tarOutputStream.closeArchiveEntry();
        console.debug("file:" + entry.getName() + " size:" + entry.getSize() + " mode:" + entry.getMode() + " linkname:" + entry.getLinkName() + " username:" + entry.getUserName() + " userid:" + entry.getUserId() + " groupname:" + entry.getGroupName() + " groupid:" + entry.getGroupId() + " modtime:" + entry.getModTime() + " md5: " + md5);
        checksums.append(md5).append("  ").append(entry.getName()).append('\n');
      }

      public void onEachLink(TarArchiveEntry entry) throws IOException {
        checkField(entry.getLinkName(), TarConstants.NAMELEN);
        checkField(entry.getUserName(), TarConstants.UNAMELEN);
        checkField(entry.getGroupName(), TarConstants.GNAMELEN);
        entry.setName(fixPath(entry.getName()));
        createParentDirectories(entry.getName(), entry.getUserName(), entry.getUserId(), entry.getGroupName(), entry.getGroupId());
        tarOutputStream.putArchiveEntry(entry);
        tarOutputStream.closeArchiveEntry();
        console.debug("link:" + entry.getName() + " mode:" + entry.getMode() + " linkname:" + entry.getLinkName() + " username:" + entry.getUserName() + " userid:" + entry.getUserId() + " groupname:" + entry.getGroupName() + " groupid:" + entry.getGroupId());
      }

      private void createDirectory(String directory, String user, int uid, String group, int gid, int mode, long size) throws IOException {
        if (!directory.endsWith("/")) {
          directory += "/";
        }
        if (!addedDirectories.contains(directory)) {
          TarArchiveEntry entry = new TarArchiveEntry(directory, true);
          entry.setUserName(user);
          entry.setUserId(uid);
          entry.setGroupName(group);
          entry.setGroupId(gid);
          entry.setMode(mode);
          entry.setSize(size);
          tarOutputStream.putArchiveEntry(entry);
          tarOutputStream.closeArchiveEntry();
          addedDirectories.add(directory);
        }
      }

      private void createParentDirectories(String filename, String user, int uid, String group, int gid) throws IOException {
        String dirname = fixPath(new File(filename).getParent());
        if (dirname == null) {
          return;
        }
        String[] pathParts = dirname.split("/");
        String parentDir = "./";
        for (int i = 1; i < pathParts.length; i++) {
          parentDir += pathParts[i] + "/";
          int mode = TarArchiveEntry.DEFAULT_DIR_MODE;
          createDirectory(parentDir, user, uid, group, gid, mode, 0);
        }
      }
    };
    try {
      for (DataProducer data : producers) {
        data.produce(receiver);
      }
    }  finally {
      tarOutputStream.close();
    }
    console.debug("Total size: " + dataSize);
    return dataSize.count;
  }

  private String fixPath(String path) {
    if (path == null || path.equals(".")) {
      return path;
    }
    if (path.contains("\\")) {
      path = path.replace('\\', '/');
    }
    if (path.startsWith("/")) {
      path = "." + path;
    } else {
      if (!path.startsWith("./")) {
        path = "./" + path;
      }
    }
    return path;
  }
}