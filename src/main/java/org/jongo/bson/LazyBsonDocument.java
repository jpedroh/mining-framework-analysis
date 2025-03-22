package org.jongo.bson;
import com.mongodb.DBObject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class LazyBsonDocument implements BsonDocument {
  private final byte[] bytes;

  LazyBsonDocument(byte[] bytes) {
    this.bytes = bytes;
  }

  public int getSize() {
    final ByteBuffer buffer = ByteBuffer.wrap(bytes);
    buffer.order(ByteOrder.LITTLE_ENDIAN);
    return buffer.getInt(0);
  }

  public byte[] toByteArray() {
    return bytes;
  }

  public DBObject toDBObject() {
    return new BsonDBObject(bytes, 0);
  }

  @Override public String toString() {
    return toDBObject().toString();
  }
}