package org.apache.hadoop.io.compress.crypto;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.compress.CryptoCodec;
import org.apache.hadoop.io.compress.Decompressor;
import sec.util.Crypto;

public class CryptoBasicDecompressor implements Decompressor {
  Crypto crypto;

  private static final Log LOG = LogFactory.getLog(
<<<<<<< /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/left.java
  CryptoBasicDecompressor
=======
  CryptoCodec
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java
  .class);

  byte[] in;

  ByteBuffer out;

  private boolean finished = false;

  public CryptoBasicDecompressor(String key) {
    crypto = new Crypto(key);
    LOG.info("Init CryptoBasicDecompressor...");
  }

  @Override public synchronized int decompress(byte[] buf, int off, int len) throws IOException {
    ensureBuffer(len);
    if (out.position() >= len) {
      LOG.debug("out.position():" + out.position() + " len:" + len);
      finished = true;
    }
    if (finished && in == null) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
      LOG.debug("decompress flushBuffer....");
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java

      return flushBuffer(buf, off, out.position());
    }
    if (needsInput()) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
      LOG.debug("needsInput....");
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java

      return 0;
    }
    byte[] b = crypto.decrypt(in);
    in = null;
    if (b == null) {
      throw new IOException("Invalid key");
    }

<<<<<<< Unknown file: This is a bug in JDime.
=======
    LOG.debug("decrypt:" + b.length);
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java

    ensureBuffer(out.position() + b.length);
    out.put(b);
    return flushBuffer(buf, off, len);
  }

  private void ensureBuffer(int n) {
    if (out == null) {
      out = ByteBuffer.allocate(n * 2);
    } else {
      if (out.capacity() < n) {
        ByteBuffer newBuffer = ByteBuffer.allocate(n);
        out.flip();
        newBuffer.put(out);
        out = newBuffer;
      }
    }
  }

  private int flushBuffer(byte[] buf, int off, int len) {
    int size = Math.min(Math.min(len, buf.length) - off, out.position());
    LOG.debug("flushBuffer size:" + size);
    if (size <= 0) {
      return 0;
    }
    out.flip();
    out.get(buf, off, size);
    out.compact();
    finished = true;
    return size;
  }

  @Override public int getRemaining() {
    return 0;
  }

  @Override public void end() {
  }

  @Override public boolean finished() {
    LOG.debug("finished:" + finished);
    return finished;
  }

  @Override public boolean needsInput() {
    boolean needsInput = 
<<<<<<< /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/left.java
    in == null || in.length < 0
=======
    (in == null || in.length < 0)
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java
    ;
    if (needsInput) {
      finished = true;
    }
    LOG.debug("needsInput:" + needsInput);
    return needsInput;
  }

  @Override public void reset() {
    in = null;
    finished = false;
  }

  @Override public void setDictionary(byte[] arg0, int arg1, int arg2) {
  }

  @Override public synchronized void setInput(byte[] buf, int offset, int length) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    LOG.debug("setInputsize buf size" + buf.length + " length:" + length);
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java

    if (length > 0) {
      in = new byte[length];
      int inIdx = 0;
      for (int i = offset; i < length; i++) {
        in[inIdx] = buf[i];
        inIdx++;
      }
      finished = false;
    }
  }

  @Override public boolean needsDictionary() {
    return false;
  }
}