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

	byte[] in;

	ByteBuffer out;

	private boolean finished = false;

<<<<<<< /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/left.java
	private static final Log LOG = LogFactory.getLog(CryptoBasicDecompressor.class);
||||||| /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/base.java
=======
	private static final Log LOG = LogFactory.getLog(CryptoCodec.class);
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java

	public CryptoBasicDecompressor(String key) {
		crypto = new Crypto(key);
		LOG.info("Init CryptoBasicDecompressor...");
	}

	@Override
<<<<<<< /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/left.java
	public synchronized int decompress(byte[] buf, int off, int len) throws IOException {
		ensureBuffer(len);

		if(out.position() >= len) {
			finished = true;
		}

		if(finished && in == null) {
			return flushBuffer(buf, off, out.position());
		}

		if(needsInput()) {
			return 0;
		}

		byte[] b = crypto.decrypt(in);
		in = null;
		if(b == null) {
			throw new IOException("Invalid key");
		}
		ensureBuffer(out.position() + b.length);
		out.put(b);
		return flushBuffer(buf, off, len);
	}
||||||| /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/base.java
	public synchronized int decompress(byte[] buf, int off, int len) throws IOException 
=======
	public synchronized int decompress(byte[] buf, int off, int len) throws IOException {
		ensureBuffer(len);

		if(out.position() >= len) {
			LOG.debug("out.position():" + out.position() + " len:" + len);
			finished = true;
		}

		if(finished && in == null) {
			LOG.debug("decompress flushBuffer....");
			return flushBuffer(buf, off, out.position());
		}

		if(needsInput()) {
			LOG.debug("needsInput....");
			return 0;
		}

		byte[] b = crypto.decrypt(in);
		in = null;
		if(b == null) {
			throw new IOException("Invalid key");
		}
		LOG.debug("decrypt:" + b.length);
		ensureBuffer(out.position() + b.length);
		out.put(b);
		return flushBuffer(buf, off, len);
	}
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java

	private void ensureBuffer(int n) {
		if(out == null) {// Initial Allocation
			out = ByteBuffer.allocate(n * 2);
		}
		else if(out.capacity() < n) { // Grow
			ByteBuffer newBuffer = ByteBuffer.allocate(n);
			out.flip();
			newBuffer.put(out);
			out = newBuffer;
		}
	}

<<<<<<< /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/left.java
	private int flushBuffer(byte[] buf, int off, int len) {
		int size = Math.min(Math.min(len, buf.length) - off, out.position());
		if(size <= 0)
			return 0;
		out.flip();
		out.get(buf, off, size);
		out.compact();
		finished = true; // We don't know if there is more data for this block, but caller checks for block completion
		return size;
	}
||||||| /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/base.java
	private int flushBuffer(byte[] buf, int off, int len) 
=======
	private int flushBuffer(byte[] buf, int off, int len) {
		int size = Math.min(Math.min(len, buf.length) - off, out.position());
		LOG.debug("flushBuffer size:" + size);
		if(size <= 0)
			return 0;
		out.flip();
		out.get(buf, off, size);
		out.compact();
		finished = true; // We don't know if there is more data for this block, but caller checks for block completion
		return size;
	}
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java

	@Override
	public void end() {

	}

	@Override
	public boolean finished() {
		LOG.debug("finished:" + finished);
		return finished;
	}

	@Override
<<<<<<< /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/left.java
	public boolean needsInput() {
		boolean needsInput = in == null || in.length < 0;
		if(needsInput)
			finished = true;
		return needsInput;
	}
||||||| /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/base.java
	public boolean needsInput() 
=======
	public boolean needsInput() {
		boolean needsInput = (in == null || in.length < 0);
		if(needsInput)
			finished = true;
		LOG.debug("needsInput:" + needsInput);
		return needsInput;
	}
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java

	@Override
	public void reset() {
		in = null;
		finished = false;
	}

	@Override
	public void setDictionary(byte[] arg0, int arg1, int arg2) {
	}

	@Override
<<<<<<< /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/left.java
	public synchronized void setInput(byte[] buf, int offset, int length) {
		if(length > 0) {
			in = new byte[length];
			int inIdx = 0;
			for(int i = offset; i < length; i++) {
				in[inIdx] = buf[i];
				inIdx++;
			}
			finished = false;
		}
	}
||||||| /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/base.java
	public synchronized void setInput(byte[] buf, int offset, int length) 
=======
	public synchronized void setInput(byte[] buf, int offset, int length) {
		LOG.debug("setInputsize buf size" + buf.length + " length:" + length);
		if(length > 0) {
			in = new byte[length];
			int inIdx = 0;
			for(int i = offset; i < length; i++) {
				in[inIdx] = buf[i];
				inIdx++;
			}
			finished = false;
		}
	}
>>>>>>> /usr/src/app/output/geisbruch/hadoopcryptocompressor/fd94be7cfc23758491ddcbf03b92de026f2d7647/src/main/java/org/apache/hadoop/io/compress/crypto/CryptoBasicDecompressor.java/right.java

	@Override
	public int getRemaining() {
		return 0;
	}

	@Override
	public boolean needsDictionary() {
		return false;
	}
}
