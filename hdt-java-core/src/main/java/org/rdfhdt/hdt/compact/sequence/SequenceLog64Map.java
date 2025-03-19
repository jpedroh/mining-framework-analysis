package org.rdfhdt.hdt.compact.sequence;
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
import org.rdfhdt.hdt.exceptions.CRCException;
import org.rdfhdt.hdt.exceptions.IllegalFormatException;
import org.rdfhdt.hdt.exceptions.NotImplementedException;
import org.rdfhdt.hdt.hdt.HDTVocabulary;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.util.BitUtil;
import org.rdfhdt.hdt.util.crc.CRC32;
import org.rdfhdt.hdt.util.crc.CRC8;
import org.rdfhdt.hdt.util.crc.CRCInputStream;
import org.rdfhdt.hdt.util.crc.CRCOutputStream;
import org.rdfhdt.hdt.util.io.CountInputStream;
import org.rdfhdt.hdt.util.io.IOUtil;

/**
 * @author mario.arias
 *
 */
public class SequenceLog64Map implements Sequence, Closeable {
  private static final byte W = 64;

  private static final long LONGS_PER_BUFFER = 128 * 1024 * 1024;

  private ByteBuffer[] buffers;

  private FileChannel ch;

  private int numbits;

  private long numentries;

  private long lastword;

  private long numwords;

  public SequenceLog64Map(File f) throws IOException {
    this(new CountInputStream(new BufferedInputStream(new FileInputStream(f))), f, true);
  }

  public SequenceLog64Map(CountInputStream in, File f) throws IOException {
    this(in, f, false);
  }

  @SuppressWarnings(value = { "resource" }) private SequenceLog64Map(CountInputStream in, File f, boolean closeInput) throws IOException {
    CRCInputStream crcin = new CRCInputStream(in, new CRC8());
    int type = crcin.read();
    if (type != SequenceFactory.TYPE_SEQLOG) {
      throw new IllegalFormatException("Trying to read a LogArray but the data is not LogArray");
    }
    numbits = crcin.read();
    numentries = VByte.decode(crcin);
    if (!crcin.readCRCAndCheck()) {
      throw new CRCException("CRC Error while reading LogArray64 header.");
    }
    if (numbits > 64) {
      throw new IllegalFormatException("LogArray64 cannot deal with more than 64bit per entry");
    }
    long base = in.getTotalBytes();
    numwords = SequenceLog64.numWordsFor(numbits, numentries);
    if (numwords > 0) {
      IOUtil.skip(in, (numwords - 1) * 8L);
      int lastWordUsed = SequenceLog64.lastWordNumBits(numbits, numentries);
      lastword = BitUtil.readLowerBitsByteAligned(lastWordUsed, in);
    }
    IOUtil.skip(in, 4);
    mapFiles(f, base);
    if (closeInput) {
      in.close();
    }
  }

  public SequenceLog64Map(int numbits, long numentries, File f) throws IOException {
    this.numbits = numbits;
    this.numentries = numentries;
    this.numwords = SequenceLog64.numWordsFor(numbits, numentries);
    mapFiles(f, 0);
  }

  private void mapFiles(File f, long base) throws IOException {
    ch = FileChannel.open(Paths.get(f.toString()));
    long maxSize = base + SequenceLog64.numBytesFor(numbits, numentries);
    int buffer = 0;
    long block = 0;
    buffers = new ByteBuffer[(int) (1L + numwords / LONGS_PER_BUFFER)];
    while (block < numwords) {
      long current = base + buffer * 8L * LONGS_PER_BUFFER;
      long next = current + 8L * LONGS_PER_BUFFER;
      long length = Math.min(maxSize, next) - current;
      buffers[buffer] = ch.map(MapMode.READ_ONLY, current, length);
      buffers[buffer].order(ByteOrder.LITTLE_ENDIAN);
      block += LONGS_PER_BUFFER;
      buffer++;
    }
    CountInputStream in = new CountInputStream(new BufferedInputStream(new FileInputStream(f)));
    IOUtil.skip(in, base + ((numwords - 1) * 8L));
    int lastWordUsedBits = SequenceLog64.lastWordNumBits(numbits, numentries);
    lastword = BitUtil.readLowerBitsByteAligned(lastWordUsedBits, in);
    in.close();
  }

  private long getWord(long w) {
    if (w == numwords - 1) {
      return lastword;
    }
    ByteBuffer buffer = buffers[(int) (w / LONGS_PER_BUFFER)];
    return buffer.getLong((int) ((w % LONGS_PER_BUFFER) * 8));
  }

  @Override public long get(long index) {
    if (index < 0 || index >= numentries) {
      throw new IndexOutOfBoundsException();
    }
    if (numbits == 0) {
      return 0;
    }
    long bitPos = index * numbits;
    long i = bitPos / W;
    int j = (int) (bitPos % W);
    long result;
    if (j + numbits <= W) {
      result = (getWord(i) << (W - j - numbits)) >>> (W - numbits);
    } else {
      result = getWord(i) >>> j;
      result = result | (getWord(i + 1) << ((W << 1) - j - numbits)) >>> (W - numbits);
    }
    return result;
  }

  @Override public long getNumberOfElements() {
    return numentries;
  }

  @Override public void save(OutputStream output, ProgressListener listener) throws IOException {
    CRCOutputStream out = new CRCOutputStream(output, new CRC8());
    out.write(SequenceFactory.TYPE_SEQLOG);
    out.write(numbits);
    VByte.encode(out, numentries);
    out.writeCRC();
    out.setCRC(new CRC32());
    int numwords = (int) SequenceLog64.numWordsFor(numbits, numentries);
    for (int i = 0; i < numwords - 1; i++) {
      IOUtil.writeLong(out, getWord(i));
    }
    if (numwords > 0) {
      int lastWordUsedBits = SequenceLog64.lastWordNumBits(numbits, numentries);
      BitUtil.writeLowerBitsByteAligned(lastword, lastWordUsedBits, out);
    }
    out.writeCRC();
  }

  @Override public long size() {
    return SequenceLog64.numBytesFor(numbits, numentries);
  }

  public int getNumBits() {
    return numbits;
  }

  @Override public String getType() {
    return HDTVocabulary.SEQ_TYPE_LOG;
  }

  @Override public void add(Iterator<Long> elements) {
    throw new NotImplementedException();
  }

  @Override public void load(InputStream input, ProgressListener listener) throws IOException {
    throw new NotImplementedException();
  }

  @Override public void close() throws IOException {
    buffers = null;
    System.gc();
    ch.close();
  }
}