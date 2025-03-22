package net.tridentsdk.api.reflect;
import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import java.lang.reflect.Field;

public class FastClass {
  private final Class<?> cls;

  private final FieldAccess fieldAccess;

  private final MethodAccess methodAccess;

  private final ConstructorAccess constructorAccess;

  private FastClass(Class<?> cls) {
    this.cls = cls;
    this.fieldAccess = FieldAccess.get(cls);
    this.methodAccess = MethodAccess.get(cls);
    this.constructorAccess = ConstructorAccess.get(cls);
  }

  public static FastClass get(Class<?> cls) {
    return new FastClass(cls);
  }

  public static FastClass get(Object obj) {
    return get(obj.getClass());
  }

  /**
     * Get a field from the class
     *
     * @param name     Name of the field
     * @return FastField instance
     */
  public FastField getField(String name) {
    return new FastField(this, this.fieldAccess, name);
  }

  /**
     * Get a method from the class
     *
     * @param name     Name of the method
     * @return FastMethod instance
     */
  public FastMethod getMethod(
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastClass.java/left.java
  Object object
=======
  Object o
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastClass.java/right.java
  , String name) {
    return new FastMethod(
<<<<<<< /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastClass.java/left.java
    object
=======
    o
>>>>>>> /usr/src/app/output/tridentsdk/tridentsdk/c0a77afe78ec62f2674ece3a732fd3c8643162d6/src/main/java/net/tridentsdk/api/reflect/FastClass.java/right.java
    , this.methodAccess, name);
  }

  public FastField[] getFields() {
    Field[] fields = this.cls.getDeclaredFields();
    FastField[] fastFields = new FastField[fields.length];
    for (int i = 0; i < fields.length; i += 1) {
      fastFields[i] = new FastField(this, this.fieldAccess, fields[i].getName());
    }
    return fastFields;
  }

  /**
     * Get the default constructor found
     *
     * @return the default FastConstructor
     */
  public FastConstructor getConstructor() {
    return new FastConstructor(this.constructorAccess);
  }

  public Class<?> toClass() {
    return this.cls;
  }
}