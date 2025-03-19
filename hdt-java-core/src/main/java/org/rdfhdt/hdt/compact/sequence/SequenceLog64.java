package org.rdfhdt.hdt.compact.sequence;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.rdfhdt.hdt.compact.integer.VByte;
import org.rdfhdt.hdt.exceptions.CRCException;
import org.rdfhdt.hdt.exceptions.IllegalFormatException;
import org.rdfhdt.hdt.hdt.HDTVocabulary;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.util.BitUtil;
import org.rdfhdt.hdt.util.crc.CRC32;
import org.rdfhdt.hdt.util.crc.CRC8;
import org.rdfhdt.hdt.util.crc.CRCInputStream;
import org.rdfhdt.hdt.util.crc.CRCOutputStream;
import org.rdfhdt.hdt.util.io.IOUtil;

/**
 * @author mario.arias
 *
 */
public class SequenceLog64 implements DynamicSequence {
  protected static final byte W = 64;

  protected long[] data;

  protected int numbits;

  protected long numentries;

  protected long maxvalue;

  public SequenceLog64() {
    this(W);
  }

  public SequenceLog64(int numbits) {
    this(numbits, 0);
  }

  public SequenceLog64(int numbits, long capacity) {
    this.numentries = 0;
    this.numbits = numbits;
    this.maxvalue = BitUtil.maxVal(numbits);
    long size = numWordsFor(numbits, capacity);
    assert size >= 0 && size <= Integer.MAX_VALUE;
    data = new long[Math.max((int) size, 1)];
  }

  public SequenceLog64(int numbits, long capacity, boolean initialize) {
    this(numbits, capacity);
    if (initialize) {
      numentries = capacity;
    }
  }

  /** longs required to represent "total" integers of "bitsField" bits each */
  public static final long numWordsFor(int bitsField, long total) {
    return (bitsField * total + 63) / 64;
  }

  /** Number of bits required for last word */
  public static final int lastWordNumBits(int bitsField, long total) {
    long totalBits = bitsField * total;
    if (totalBits == 0) {
      return 0;
    }
    return (int) ((totalBits - 1) % W) + 1;
  }

  /** Number of bits required for last word */
  public static final int lastWordNumBytes(int bitsField, long total) {
    return ((lastWordNumBits(bitsField, total) - 1) / 8) + 1;
  }

  /** Number of bytes required to represent n integers of e bits each */
  public static final long numBytesFor(int bitsField, long total) {
    return (bitsField * total + 7) / 8;
  }

  /** Retrieve a given index from array data where every value uses bitsField bits
     * @param data Array
     * @param bitsField Length in bits of each field
     * @param index Position to be retrieved
     */
  private static final long getField(long[] data, int bitsField, long index) {
    if (bitsField == 0) {
      return 0;
    }
    long bitPos = index * bitsField;
    int i = (int) (bitPos / W);
    int j = (int) (bitPos % W);
    long result;
    if (j + bitsField <= W) {
      result = (data[i] << (W - j - bitsField)) >>> (W - bitsField);
    } else {
      result = data[i] >>> j;
      result = result | (data[i + 1] << ((W << 1) - j - bitsField)) >>> (W - bitsField);
    }
    return result;
  }

  /** Store a given value in index into array data where every value uses bitsField bits
     * @param data Array
     * @param bitsField Length in bits of each field
     * @param index Position to store in
     * @param value Value to be stored
     */
  private static final void setField(long[] data, int bitsField, long index, long value) {
    if (bitsField == 0) {
      return;
    }
    long bitPos = index * bitsField;
    int i = (int) (bitPos / W);
    int j = (int) (bitPos % W);
    long mask = ~(~0L << bitsField) << j;
    data[i] = (data[i] & ~mask) | (value << j);
    if (j + bitsField > W) {
      mask = ~0L << (bitsField + j - W);
      data[i + 1] = (data[i + 1] & mask) | value >>> (W - j);
    }
  }

  private final void resizeArray(int size) {
    data = Arrays.copyOf(data, size);
  }

  @Override public void add(Iterator<Long> elements) {
    long max = 0;
    numentries = 0;
    while (elements.hasNext()) {
      long val = elements.next().longValue();
      max = val > max ? val : max;
      numentries++;
    }
    numbits = BitUtil.log2(max);
    int size = (int) numWordsFor(numbits, numentries);
    data = new long[size];
    int count = 0;
    while (elements.hasNext()) {
      long element = elements.next().longValue();
      assert element <= maxvalue;
      setField(data, numbits, count, element);
      count++;
    }
  }

  public void addIntegers(ArrayList<Integer> elements) {
    long max = 0;
    numentries = 0;
    for (int i = 0; i < elements.size(); i++) {
      long val = elements.get(i).longValue();
      max = val > max ? val : max;
      numentries++;
    }
    numbits = BitUtil.log2(max);
    int size = (int) numWordsFor(numbits, numentries);
    data = new long[size];
    int count = 0;
    for (int i = 0; i < elements.size(); i++) {
      long element = elements.get(i).longValue();
      assert element <= maxvalue;
      setField(data, numbits, count, element);
      count++;
    }
  }

  @Override public long get(long position) {
    if (position < 0 || position >= numentries) {
      throw new IndexOutOfBoundsException();
    }
    return getField(data, numbits, position);
  }

  public void set(long position, long value) {
    if (value < 0 || value > maxvalue) {
      throw new IllegalArgumentException("Value exceeds the maximum for this data structure");
    }
    setField(data, numbits, position, value);
  }

  public void append(long value) {
    assert numentries < Integer.MAX_VALUE;
    if (value < 0 || value > maxvalue) {
      throw new IllegalArgumentException("Value exceeds the maximum for this data structure");
    }
    long neededSize = numWordsFor(numbits, numentries + 1);
    if (data.length < neededSize) {
      resizeArray(data.length * 2);
    }
    this.set((int) numentries, value);
    numentries++;
  }

  public void aggresiveTrimToSize() {
    long max = 0;
    for (int i = 0; i < numentries; i++) {
      long value = this.get(i);
      max = value > max ? value : max;
    }
    int newbits = BitUtil.log2(max);
    assert newbits <= numbits;
    if (newbits != numbits) {
      for (int i = 0; i < numentries; i++) {
        long value = getField(data, numbits, i);
        setField(data, newbits, i, value);
      }
      numbits = newbits;
      maxvalue = BitUtil.maxVal(numbits);
      long totalSize = numWordsFor(numbits, numentries);
      resizeArray((int) totalSize);
    }
  }

  public void trimToSize() {
    resizeArray((int) numWordsFor(numbits, numentries));
  }

  public void resize(long numentries) {
    this.numentries = numentries;
    resizeArray((int) numWordsFor(numbits, numentries));
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
    int numwords = (int) numWordsFor(numbits, numentries);
    for (int i = 0; i < numwords - 1; i++) {
      IOUtil.writeLong(out, data[i]);
    }
    if (numwords > 0) {
      int lastWordUsedBits = lastWordNumBits(numbits, numentries);
      BitUtil.writeLowerBitsByteAligned(data[numwords - 1], lastWordUsedBits, out);
    }
    out.writeCRC();
  }

  @Override public void load(InputStream input, ProgressListener listener) throws IOException {
    CRCInputStream in = new CRCInputStream(input, new CRC8());
    int type = in.read();
    if (type != SequenceFactory.TYPE_SEQLOG) {
      throw new IllegalFormatException("Trying to read a LogArray but the data is not LogArray");
    }
    numbits = in.read();
    numentries = VByte.decode(in);
    if (!in.readCRCAndCheck()) {
      throw new CRCException("CRC Error while reading LogArray64 header.");
    }
    if (numbits > 64) {
      throw new IllegalFormatException("LogArray64 cannot deal with more than 64bit per entry");
    }
    in.setCRC(new CRC32());
    int numwords = (int) numWordsFor(numbits, numentries);
    data = new long[numwords];
    for (int i = 0; i < numwords - 1; i++) {
      data[i] = IOUtil.readLong(in);
    }
    if (numwords > 0) {
      int lastWordUsed = lastWordNumBits(numbits, numentries);
      data[numwords - 1] = BitUtil.readLowerBitsByteAligned(lastWordUsed, in);
    }
    if (!in.readCRCAndCheck()) {
      throw new CRCException("CRC Error while reading LogArray64 data.");
    }
  }

  @Override public long size() {
    return numBytesFor(numbits, numentries);
  }

  public long getRealSize() {
    return data.length * 8L;
  }

  public int getNumBits() {
    return numbits;
  }

  @Override public String getType() {
    return HDTVocabulary.SEQ_TYPE_LOG;
  }

  @Override public void close() throws IOException {
    data = null;
  }
}