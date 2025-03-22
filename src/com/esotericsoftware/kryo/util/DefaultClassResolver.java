package com.esotericsoftware.kryo.util;
import static com.esotericsoftware.kryo.util.Util.*;
import static com.esotericsoftware.minlog.Log.*;
import com.esotericsoftware.kryo.ClassResolver;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/** Resolves classes by ID or by fully qualified class name.
 * @author Nathan Sweet */
public class DefaultClassResolver implements ClassResolver {
  public static final byte NAME = -1;

  protected Kryo kryo;

  protected final IntMap<Registration> idToRegistration = new IntMap<>();

  protected final IdentityMap<Class, Registration> classToRegistration = new IdentityMap<>();

  protected IdentityObjectIntMap<Class> classToNameId;

  protected IntMap<Class> nameIdToClass;

  protected ObjectMap<String, Class> nameToClass;

  protected int nextNameId;

  private int memoizedClassId = -1;

  private Registration memoizedClassIdValue;

  private Class memoizedClass;

  private Registration memoizedClassValue;

  public void setKryo(Kryo kryo) {
    this.kryo = kryo;
  }

  public Registration register(Registration registration) {
    memoizedClassId = -1;
    memoizedClass = null;
    if (registration == null) {
      throw new IllegalArgumentException("registration cannot be null.");
    }
    if (registration.getId() != NAME) {
      if (TRACE) {
        trace("kryo", "Register class ID " + registration.getId() + ": " + className(registration.getType()) + " (" + registration.getSerializer().getClass().getName() + ")");
      }
      idToRegistration.put(registration.getId(), registration);
    } else {
      if (TRACE) {
        trace("kryo", "Register class name: " + className(registration.getType()) + " (" + registration.getSerializer().getClass().getName() + ")");
      }
    }
    classToRegistration.put(registration.getType(), registration);
    Class wrapperClass = getWrapperClass(registration.getType());
    if (wrapperClass != registration.getType()) {
      classToRegistration.put(wrapperClass, registration);
    }
    return registration;
  }

  public Registration unregister(int classID) {
    Registration registration = idToRegistration.remove(classID);
    if (registration != null) {
      classToRegistration.remove(registration.getType());
      memoizedClassId = -1;
      memoizedClass = null;
      Class wrapperClass = getWrapperClass(registration.getType());
      if (wrapperClass != registration.getType()) {
        classToRegistration.remove(wrapperClass);
      }
    }
    return registration;
  }

  public Registration registerImplicit(Class type) {
    return register(new Registration(type, kryo.getDefaultSerializer(type), NAME));
  }

  public Registration getRegistration(Class type) {
    if (type == memoizedClass) {
      return memoizedClassValue;
    }
    Registration registration = classToRegistration.get(type);
    if (registration != null) {
      memoizedClass = type;
      memoizedClassValue = registration;
    }
    return registration;
  }

  public Registration getRegistration(int classID) {
    return idToRegistration.get(classID);
  }

  public Registration writeClass(Output output, Class type) {
    if (type == null) {
      if (TRACE || (DEBUG && kryo.getDepth() == 1)) {
        log("Write", null, output.position());
      }
      output.writeByte(Kryo.NULL);
      return null;
    }
    Registration registration = kryo.getRegistration(type);
    if (registration.getId() == NAME) {
      writeName(output, type, registration);
    } else {
      if (TRACE) {
        trace("kryo", "Write class " + registration.getId() + ": " + className(type) + pos(output.position()));
      }
      output.writeVarInt(registration.getId() + 2, true);
    }
    return registration;
  }

  protected void writeName(Output output, Class type, Registration registration) {
    output.writeByte(1);
    if (classToNameId != null) {
      int nameId = classToNameId.get(type, -1);
      if (nameId != -1) {
        if (TRACE) {
          trace("kryo", "Write class name reference " + nameId + ": " + className(type) + pos(output.position()));
        }
        output.writeVarInt(nameId, true);
        return;
      }
    }
    if (TRACE) {
      trace("kryo", "Write class name: " + className(type) + pos(output.position()));
    }
    int nameId = nextNameId++;
    if (classToNameId == null) {
      classToNameId = new IdentityObjectIntMap<>();
    }
    classToNameId.put(type, nameId);
    output.writeVarInt(nameId, true);
    if (registration.isTypeNameAscii()) {
      output.writeAscii(type.getName());
    } else {
      output.writeString(type.getName());
    }
  }

  public Registration readClass(Input input) {
    int classID = input.readVarInt(true);
    switch (classID) {
      case Kryo.NULL:
      if (TRACE || (DEBUG && kryo.getDepth() == 1)) {
        log("Read", null, input.position());
      }
      return null;
      case NAME + 2:
      return readName(input);
    }
    if (classID == memoizedClassId) {
      if (TRACE) {
        trace("kryo", "Read class " + (classID - 2) + ": " + className(memoizedClassIdValue.getType()) + pos(input.position()));
      }
      return memoizedClassIdValue;
    }
    Registration registration = idToRegistration.get(classID - 2);
    if (registration == null) {
      throw new KryoException("Encountered unregistered class ID: " + (classID - 2));
    }
    if (TRACE) {
      trace("kryo", "Read class " + (classID - 2) + ": " + className(registration.getType()) + pos(input.position()));
    }
    memoizedClassId = classID;
    memoizedClassIdValue = registration;
    return registration;
  }

  protected Registration readName(Input input) {
    int nameId = input.readVarInt(true);
    if (nameIdToClass == null) {
      nameIdToClass = new IntMap<>();
    }
    Class type = nameIdToClass.get(nameId);
    if (type == null) {
      String className = input.readString();
      type = getTypeByName(className);
      if (type == null) {
        try {
          type = Class.forName(className, false, kryo.getClassLoader());
        } catch (ClassNotFoundException ex) {
          try {
            type = Class.forName(className, false, Kryo.class.getClassLoader());
          } catch (ClassNotFoundException ex2) {
            throw new KryoException("Unable to find class: " + className, ex);
          }
        }
        if (nameToClass == null) {
          nameToClass = new ObjectMap<>();
        }
        nameToClass.put(className, type);
      }
      nameIdToClass.put(nameId, type);
      if (TRACE) {
        trace("kryo", "Read class name: " + className + pos(input.position()));
      }
    } else {
      if (TRACE) {
        trace("kryo", "Read class name reference " + nameId + ": " + className(type) + pos(input.position()));
      }
    }
    return kryo.getRegistration(type);
  }

  protected Class getTypeByName(final String className) {
    return nameToClass != null ? nameToClass.get(className) : null;
  }

  public void reset() {
    if (!kryo.isRegistrationRequired()) {
      if (classToNameId != null) {
        classToNameId.clear(2048);
      }
      if (nameIdToClass != null) {
        nameIdToClass.clear();
      }
      nextNameId = 0;
    }
  }
}