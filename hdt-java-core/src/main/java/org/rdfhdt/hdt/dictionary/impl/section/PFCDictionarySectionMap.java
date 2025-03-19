package org.rdfhdt.hdt.dictionary.impl.section;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.util.Iterator;
import org.rdfhdt.hdt.compact.integer.VByte;
import org.rdfhdt.hdt.compact.sequence.Sequence;
import org.rdfhdt.hdt.compact.sequence.SequenceFactory;
import org.rdfhdt.hdt.dictionary.DictionarySectionPrivate;
import org.rdfhdt.hdt.dictionary.TempDictionarySection;
import org.rdfhdt.hdt.exceptions.CRCException;
import org.rdfhdt.hdt.exceptions.IllegalFormatException;
import org.rdfhdt.hdt.exceptions.NotImplementedException;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.util.crc.CRC8;
import org.rdfhdt.hdt.util.crc.CRCInputStream;
import org.rdfhdt.hdt.util.io.CountInputStream;
import org.rdfhdt.hdt.util.io.IOUtil;
import org.rdfhdt.hdt.util.string.ByteStringUtil;
import org.rdfhdt.hdt.util.string.CompactString;
import org.rdfhdt.hdt.util.string.ReplazableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mario.arias
 *
 */
public class PFCDictionarySectionMap implements DictionarySectionPrivate, Closeable {
  private protected static final 
<<<<<<< /usr/src/app/output/rdfhdt/hdt-java/cd88626a4e665e548f0981fdd26994e4819de68c/hdt-java-core/src/main/java/org/rdfhdt/hdt/dictionary/impl/section/PFCDictionarySectionMap.java/left.java
  Logger
=======
  FileInputStream
>>>>>>> /usr/src/app/output/rdfhdt/hdt-java/cd88626a4e665e548f0981fdd26994e4819de68c/hdt-java-core/src/main/java/org/rdfhdt/hdt/dictionary/impl/section/PFCDictionarySectionMap.java/right.java
   
<<<<<<< /usr/src/app/output/rdfhdt/hdt-java/cd88626a4e665e548f0981fdd26994e4819de68c/hdt-java-core/src/main/java/org/rdfhdt/hdt/dictionary/impl/section/PFCDictionarySectionMap.java/left.java
  log = LoggerFactory.getLogger(PFCDictionarySectionMap.class)
=======
  fis
>>>>>>> /usr/src/app/output/rdfhdt/hdt-java/cd88626a4e665e548f0981fdd26994e4819de68c/hdt-java-core/src/main/java/org/rdfhdt/hdt/dictionary/impl/section/PFCDictionarySectionMap.java/right.java
  ;

  public static final int TYPE_INDEX = 2;

  public static final int DEFAULT_BLOCK_SIZE = 16;

  static final int BLOCKS_PER_BYTEBUFFER = 50000;

  protected FileChannel ch;

  protected ByteBuffer[] buffers;

  long[] posFirst;

  protected int blocksize;

  protected int numstrings;

  protected Sequence blocks;

  protected long dataSize;

  private final File f;

  private final long startOffset;

  private long endOffset;

  @SuppressWarnings(value = { "resource" }) public PFCDictionarySectionMap(CountInputStream input, File f) throws IOException {
    this.f = f;
    startOffset = input.getTotalBytes();
    CRCInputStream crcin = new CRCInputStream(input, new CRC8());
    int type = crcin.read();
    if (type != TYPE_INDEX) {
      throw new IllegalFormatException("Trying to read a DictionarySectionPFC from data that is not of the suitable type");
    }
    numstrings = (int) VByte.decode(crcin);
    dataSize = VByte.decode(crcin);
    blocksize = (int) VByte.decode(crcin);
    if (!crcin.readCRCAndCheck()) {
      throw new CRCException("CRC Error while reading Dictionary Section Plain Front Coding Header.");
    }
    blocks = SequenceFactory.createStream(input, f);
    long base = input.getTotalBytes();
    IOUtil.skip(crcin, dataSize + 4);
    endOffset = input.getTotalBytes();
    ch = FileChannel.open(Paths.get(f.toString()));
    int block = 0;
    int buffer = 0;
    long numBlocks = blocks.getNumberOfElements();
    long bytePos = 0;
    long numBuffers = 1 + numBlocks / BLOCKS_PER_BYTEBUFFER;
    buffers = new ByteBuffer[(int) numBuffers];
    posFirst = new long[(int) numBuffers];
    while (block < numBlocks - 1) {
      int nextBlock = (int) Math.min(numBlocks - 1, block + BLOCKS_PER_BYTEBUFFER);
      long nextBytePos = blocks.get(nextBlock);
      buffers[buffer] = ch.map(MapMode.READ_ONLY, base + bytePos, nextBytePos - bytePos);
      buffers[buffer].order(ByteOrder.LITTLE_ENDIAN);
      posFirst[buffer] = bytePos;
      bytePos = nextBytePos;
      block += BLOCKS_PER_BYTEBUFFER;
      buffer++;
    }
  }

  protected int locateBlock(CharSequence str) {
    if (blocks.getNumberOfElements() == 0) {
      return -1;
    }
    int low = 0;
    int high = (int) blocks.getNumberOfElements() - 1;
    int max = high;
    while (low <= high) {
      int mid = low + (high - low) / 2;
      int cmp;
      if (mid == max) {
        cmp = -1;
      } else {
        ByteBuffer buffer = buffers[mid / BLOCKS_PER_BYTEBUFFER];
        cmp = ByteStringUtil.strcmp(str, buffer, (int) (blocks.get(mid) - posFirst[mid / BLOCKS_PER_BYTEBUFFER]));
      }
      if (cmp < 0) {
        high = mid - 1;
      } else {
        if (cmp > 0) {
          low = mid + 1;
        } else {
          return mid;
        }
      }
    }
    return -(low + 1);
  }

  @Override public int locate(CharSequence str) {
    if (buffers == null || blocks == null) {
      return 0;
    }
    int blocknum = locateBlock(str);
    if (blocknum >= 0) {
      return (blocknum * blocksize) + 1;
    } else {
      blocknum = -blocknum - 2;
      if (blocknum >= 0) {
        int idblock = locateInBlock(blocknum, str);
        if (idblock != 0) {
          return (blocknum * blocksize) + idblock + 1;
        }
      }
    }
    return 0;
  }

  public int locateInBlock(int block, CharSequence str) {
    if (block >= blocks.getNumberOfElements()) {
      return 0;
    }
    ReplazableString tempString = new ReplazableString();
    int idInBlock = 0;
    int cshared = 0;
    ByteBuffer buffer = buffers[block / BLOCKS_PER_BYTEBUFFER].duplicate();
    buffer.position((int) (blocks.get(block) - posFirst[block / BLOCKS_PER_BYTEBUFFER]));
    try {
      if (!buffer.hasRemaining()) {
        return 0;
      }
      tempString.replace(buffer, 0);
      idInBlock++;
      while ((idInBlock < blocksize) && buffer.hasRemaining()) {
        long delta = VByte.decode(buffer);
        tempString.replace(buffer, (int) delta);
        if (delta >= cshared) {
          cshared += ByteStringUtil.longestCommonPrefix(tempString, str, cshared);
          if ((cshared == str.length()) && (tempString.length() == str.length())) {
            return idInBlock;
          }
        } else {
          return 0;
        }
        idInBlock++;
      }
      return 0;
    } catch (IOException e) {
      log.error("Unexpected exception.", e);
      return 0;
    }
  }

  @Override public CharSequence extract(int id) {
    if (buffers == null || blocks == null) {
      return null;
    }
    if (id < 1 || id > numstrings) {
      return null;
    }
    int block = (id - 1) / blocksize;
    ByteBuffer buffer = buffers[block / BLOCKS_PER_BYTEBUFFER].duplicate();
    buffer.position((int) (blocks.get(block) - posFirst[block / BLOCKS_PER_BYTEBUFFER]));
    try {
      ReplazableString tempString = new ReplazableString();
      tempString.replace(buffer, 0);
      int stringid = (id - 1) % blocksize;
      for (int i = 0; i < stringid; i++) {
        long delta = VByte.decode(buffer);
        tempString.replace(buffer, (int) delta);
      }
      return new CompactString(tempString).getDelayed();
    } catch (IOException e) {
      log.error("Unexpected exception.", e);
      return null;
    }
  }

  @Override public long size() {
    return dataSize + blocks.size();
  }

  @Override public int getNumberOfElements() {
    return numstrings;
  }

  @Override public Iterator<CharSequence> getSortedEntries() {
    return new Iterator<CharSequence>() {
      int id;

      final ReplazableString tempString = new ReplazableString();

      int bytebufferIndex;

      ByteBuffer buffer = buffers[0].duplicate();

      @Override public boolean hasNext() {
        return id < getNumberOfElements();
      }

      @Override public CharSequence next() {
        if (!buffer.hasRemaining()) {
          buffer = buffers[++bytebufferIndex].duplicate();
          buffer.rewind();
        }
        try {
          if ((id % blocksize) == 0) {
            tempString.replace(buffer, 0);
          } else {
            long delta = VByte.decode(buffer);
            tempString.replace(buffer, (int) delta);
          }
          id++;
          return new CompactString(tempString).getDelayed();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }

      @Override public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  public Iterator<CharSequence> getSortedEntries(final Iterator<Integer> in) {
    return new Iterator<CharSequence>() {
      int id = 0;

      ReplazableString tempString = new ReplazableString();

      int bytebufferIndex = 0;

      ByteBuffer buffer = buffers[0].duplicate();

      @Override public boolean hasNext() {
        return in.hasNext();
      }

      @Override public CharSequence next() {
        int target = in.next();
        if (target < 1 || target > numstrings) {
          throw new IndexOutOfBoundsException("Trying to access position " + target + " but PFC has " + numstrings + " elements.");
        }
        if (target < ((id % blocksize) + blocksize)) {
          while (id < target) {
            if (!buffer.hasRemaining()) {
              buffer = buffers[++bytebufferIndex].duplicate();
              buffer.rewind();
            }
            try {
              if ((id % blocksize) == 0) {
                tempString.replace(buffer, 0);
              } else {
                long delta = VByte.decode(buffer);
                tempString.replace(buffer, (int) delta);
              }
              id++;
              if (id == target) {
                return new CompactString(tempString).getDelayed();
              }
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
          throw new RuntimeException("Not found");
        } else {
          id = target;
          int block = (target - 1) / blocksize;
          bytebufferIndex = block / BLOCKS_PER_BYTEBUFFER;
          buffer = buffers[bytebufferIndex++].duplicate();
          buffer.position((int) (blocks.get(block) - posFirst[block / BLOCKS_PER_BYTEBUFFER]));
          try {
            tempString = new ReplazableString();
            tempString.replace(buffer, 0);
            int stringid = (target - 1) % blocksize;
            for (int i = 0; i < stringid; i++) {
              long delta = VByte.decode(buffer);
              tempString.replace(buffer, (int) delta);
            }
            return new CompactString(tempString).getDelayed();
          } catch (IOException e) {
            e.printStackTrace();
            return null;
          }
        }
      }

      @Override public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override public void close() throws IOException {
    blocks.close();
    buffers = null;
    System.gc();
    ch.close();
  }

  @Override public void load(TempDictionarySection other, ProgressListener listener) {
    throw new NotImplementedException();
  }

  @Override public void save(OutputStream output, ProgressListener listener) throws IOException {
    InputStream in = new BufferedInputStream(new FileInputStream(f));
    IOUtil.skip(in, startOffset);
    IOUtil.copyStream(in, output, endOffset - startOffset);
    in.close();
  }

  @Override public void load(InputStream input, ProgressListener listener) throws IOException {
    throw new NotImplementedException();
  }
}