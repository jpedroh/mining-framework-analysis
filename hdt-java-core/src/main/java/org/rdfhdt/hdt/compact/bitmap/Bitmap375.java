package org.rdfhdt.hdt.compact.bitmap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.rdfhdt.hdt.hdt.HDTVocabulary;
import org.rdfhdt.hdt.listener.ProgressListener;
import org.rdfhdt.hdt.util.BitUtil;
import org.rdfhdt.hdt.util.io.IOUtil;

/**
 * Implements an index on top of the Bitmap64 to solve select and rank queries more efficiently.
 * 
 * index -> O(n)
 * rank1 -> O(1)
 * select1 -> O(log log n)
 * 
 * @author mario.arias
 *
 */
public class Bitmap375 extends Bitmap64 implements ModifiableBitmap {
  private static final int BLOCKS_PER_SUPER = 4;

  private long pop;

  private long[] superBlocksLong;

  private int[] superBlocksInt;

  private byte[] blocks;

  private boolean indexUpToDate;

  public Bitmap375() {
    super();
  }

  public Bitmap375(long nbits) {
    super(nbits);
  }

  public Bitmap375(long nbits, InputStream in) throws IOException {
    this.numbits = nbits;
    int numwords = (int) numWords(numbits);
    words = new long[numwords];
    for (int i = 0; i < numwords - 1; i++) {
      words[i] = IOUtil.readLong(in);
    }
    if (numwords > 0) {
      int lastWordUsedBits = lastWordNumBits(numbits);
      words[numwords - 1] = BitUtil.readLowerBitsByteAligned(lastWordUsedBits, in);
    }
  }

  public void dump() {
    int count = (int) numWords(this.numbits);
    for (int i = 0; i < count; i++) {
      System.out.print(i + "\t");
      IOUtil.printBitsln(words[i], 64);
    }
  }

  public void updateIndex() {
    trimToSize();
    if (numbits > Integer.MAX_VALUE) {
      superBlocksLong = new long[1 + (words.length - 1) / BLOCKS_PER_SUPER];
    } else {
      superBlocksInt = new int[1 + (words.length - 1) / BLOCKS_PER_SUPER];
    }
    blocks = new byte[words.length];
    long countBlock = 0, countSuperBlock = 0;
    int blockIndex = 0, superBlockIndex = 0;
    while (blockIndex < words.length) {
      if ((blockIndex % BLOCKS_PER_SUPER) == 0) {
        countSuperBlock += countBlock;
        if (superBlocksLong != null) {
          if (superBlockIndex < superBlocksLong.length) {
            superBlocksLong[superBlockIndex++] = countSuperBlock;
          }
        } else {
          if (superBlockIndex < superBlocksInt.length) {
            superBlocksInt[superBlockIndex++] = (int) countSuperBlock;
          }
        }
        countBlock = 0;
      }
      blocks[blockIndex] = (byte) countBlock;
      countBlock += Long.bitCount(words[blockIndex]);
      blockIndex++;
    }
    pop = countSuperBlock + countBlock;
    indexUpToDate = true;
  }

  @Override public boolean access(long bitIndex) {
    if (bitIndex < 0) {
      throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
    }
    int wordIndex = wordIndex(bitIndex);
    if (wordIndex >= words.length) {
      return false;
    }
    return (words[wordIndex] & (1L << bitIndex)) != 0;
  }

  public void set(long bitIndex, boolean value) {
    indexUpToDate = false;
    super.set(bitIndex, value);
  }

  @Override public long rank1(long pos) {
    if (pos < 0) {
      return 0;
    }
    if (!indexUpToDate) {
      updateIndex();
    }
    if (pos >= numbits) {
      return pop;
    }
    long superBlockIndex = pos / (BLOCKS_PER_SUPER * W);
    long superBlockRank;
    if (superBlocksLong != null) {
      superBlockRank = superBlocksLong[(int) superBlockIndex];
    } else {
      superBlockRank = superBlocksInt[(int) superBlockIndex];
    }
    long blockIndex = pos / W;
    long blockRank = 0xFF & blocks[(int) blockIndex];
    long chunkIndex = W - 1 - pos % W;
    long block = words[(int) blockIndex] << chunkIndex;
    long chunkRank = Long.bitCount(block);
    return superBlockRank + blockRank + chunkRank;
  }

  @Override public long rank0(long pos) {
    return pos + 1L - rank1(pos);
  }

  private static int binarySearch0(long[] a, int fromIndex, int toIndex, long key) {
    int low = fromIndex;
    int high = toIndex - 1;
    while (low <= high) {
      int mid = (low + high) >>> 1;
      long midVal = mid * BLOCKS_PER_SUPER * W - a[mid];
      if (midVal < key) {
        low = mid + 1;
      } else {
        if (midVal > key) {
          high = mid - 1;
        } else {
          return mid;
        }
      }
    }
    return -(low + 1);
  }

  private static int binarySearch0(int[] a, int fromIndex, int toIndex, long key) {
    int low = fromIndex;
    int high = toIndex - 1;
    while (low <= high) {
      int mid = (low + high) >>> 1;
      long midVal = mid * BLOCKS_PER_SUPER * W - a[mid];
      if (midVal < key) {
        low = mid + 1;
      } else {
        if (midVal > key) {
          high = mid - 1;
        } else {
          return mid;
        }
      }
    }
    return -(low + 1);
  }

  @Override public long select0(long x) {
    if (x < 0) {
      return -1;
    }
    if (!indexUpToDate) {
      updateIndex();
    }
    if (x > numbits - pop) {
      return numbits;
    }
    int superBlockIndex;
    if (superBlocksLong != null) {
      superBlockIndex = binarySearch0(superBlocksLong, 0, superBlocksLong.length, x);
    } else {
      superBlockIndex = binarySearch0(superBlocksInt, 0, superBlocksInt.length, x);
    }
    if (superBlockIndex < 0) {
      superBlockIndex = -superBlockIndex - 2;
    } else {
      if (superBlockIndex > 0) {
        superBlockIndex--;
      }
    }
    long countdown;
    if (superBlocksLong != null) {
      while (superBlockIndex > 0 && (superBlockIndex * BLOCKS_PER_SUPER * W - superBlocksLong[superBlockIndex] >= x)) {
        superBlockIndex--;
      }
      countdown = x - (superBlockIndex * BLOCKS_PER_SUPER * W - superBlocksLong[superBlockIndex]);
    } else {
      while (superBlockIndex > 0 && (superBlockIndex * BLOCKS_PER_SUPER * W - superBlocksInt[superBlockIndex] >= x)) {
        superBlockIndex--;
      }
      countdown = x - (superBlockIndex * BLOCKS_PER_SUPER * W - superBlocksInt[superBlockIndex]);
    }
    int blockIdx = superBlockIndex * BLOCKS_PER_SUPER;
    while (true) {
      if (blockIdx >= (superBlockIndex + 1) * BLOCKS_PER_SUPER || blockIdx >= blocks.length) {
        blockIdx--;
        break;
      }
      if ((0xFF & (W * (blockIdx % BLOCKS_PER_SUPER) - blocks[blockIdx])) >= countdown) {
        blockIdx--;
        break;
      }
      blockIdx++;
    }
    if (blockIdx < 0) {
      blockIdx = 0;
    }
    countdown = countdown - (0xFF & ((blockIdx % BLOCKS_PER_SUPER) * W - blocks[blockIdx]));
    int bitpos = BitUtil.select0(words[blockIdx], (int) countdown);
    return ((long) blockIdx) * W + bitpos - 1;
  }

  @Override public long select1(long x) {
    if (x < 0) {
      return -1;
    }
    if (!indexUpToDate) {
      updateIndex();
    }
    if (x > pop) {
      return numbits;
    }
    if (numbits == 0) {
      return 0;
    }
    int superBlockIndex;
    if (superBlocksLong != null) {
      superBlockIndex = Arrays.binarySearch(superBlocksLong, x);
    } else {
      superBlockIndex = Arrays.binarySearch(superBlocksInt, (int) x);
    }
    if (superBlockIndex < 0) {
      superBlockIndex = -superBlockIndex - 2;
    } else {
      if (superBlockIndex > 0) {
        superBlockIndex--;
      }
    }
    long countdown;
    if (superBlocksLong != null) {
      while (superBlockIndex > 0 && (superBlocksLong[superBlockIndex] >= x)) {
        superBlockIndex--;
      }
      countdown = x - superBlocksLong[superBlockIndex];
    } else {
      while (superBlockIndex > 0 && (superBlocksInt[superBlockIndex] >= x)) {
        superBlockIndex--;
      }
      countdown = x - superBlocksInt[superBlockIndex];
    }
    int blockIdx = superBlockIndex * BLOCKS_PER_SUPER;
    while (true) {
      if (blockIdx >= (superBlockIndex + 1) * BLOCKS_PER_SUPER || blockIdx >= blocks.length) {
        blockIdx--;
        break;
      }
      if ((0xFF & blocks[blockIdx]) >= countdown) {
        blockIdx--;
        break;
      }
      blockIdx++;
    }
    if (blockIdx < 0) {
      blockIdx = 0;
    }
    countdown = countdown - (0xFF & blocks[blockIdx]);
    int bitpos = BitUtil.select1(words[blockIdx], (int) countdown);
    return ((long) blockIdx) * W + bitpos - 1;
  }

  @Override public long countOnes() {
    return rank1(numbits);
  }

  @Override public long countZeros() {
    return numbits - countOnes();
  }

  @Override public long getRealSizeBytes() {
    updateIndex();
    long accum = super.getRealSizeBytes();
    if (superBlocksLong != null) {
      accum += superBlocksLong.length * 8;
    }
    if (superBlocksInt != null) {
      accum += superBlocksInt.length * 4;
    }
    accum += blocks.length;
    return accum;
  }

  @Override public String getType() {
    return HDTVocabulary.BITMAP_TYPE_PLAIN;
  }

  @Override public void save(OutputStream output, ProgressListener listener) throws IOException {
    super.save(output, listener);
  }

  @Override public void load(InputStream input, ProgressListener listener) throws IOException {
    super.load(input, listener);
    updateIndex();
  }
}