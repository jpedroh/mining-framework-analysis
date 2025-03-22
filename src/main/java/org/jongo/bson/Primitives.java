package org.jongo.bson;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import org.bson.types.BSONTimestamp;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.CodeWScope;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

class Primitives {
  private static final Set<Class<?>> PRIMITIVES;

  static {
    PRIMITIVES = new HashSet<Class<?>>();
    PRIMITIVES.add(String.class);
    PRIMITIVES.add(Number.class);
    PRIMITIVES.add(Boolean.class);
    PRIMITIVES.add(MinKey.class);
    PRIMITIVES.add(MaxKey.class);
    PRIMITIVES.add(ObjectId.class);
    PRIMITIVES.add(Pattern.class);
    PRIMITIVES.add(BSONTimestamp.class);
    PRIMITIVES.add(Date.class);
    PRIMITIVES.add(UUID.class);
    PRIMITIVES.add(Code.class);
    PRIMITIVES.add(DBObject.class);
    PRIMITIVES.add(DBRef.class);
    PRIMITIVES.add(CodeWScope.class);
    PRIMITIVES.add(Binary.class);
  }

  public static <T extends java.lang.Object> boolean contains(Class<T> clazz) {
    if (PRIMITIVES.contains(clazz) || isAJavaPrimitiveArray(clazz)) {
      return true;
    }
    for (Class<?> primitive : PRIMITIVES) {
      if (primitive.isAssignableFrom(clazz)) {
        return true;
      }
    }
    return false;
  }

  private static <T extends java.lang.Object> boolean isAJavaPrimitiveArray(Class<T> clazz) {
    return clazz.isArray() && clazz.getComponentType().isPrimitive();
  }

  private Primitives() {
  }
}