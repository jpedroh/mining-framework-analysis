package com.fasterxml.classmate;
import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import com.fasterxml.classmate.types.*;
import com.fasterxml.classmate.util.ClassKey;
import com.fasterxml.classmate.util.ClassStack;
import com.fasterxml.classmate.util.ResolvedTypeCache;

/**
 * Object that is used for resolving generic type information of a class
 * so that it is accessible using simple API. Resolved types are also starting
 * point for accessing resolved (generics aware) return and argument types
 * of class members (methods, fields, constructors).
 *<p>
 * Note that resolver instances are stateful in that resolvers cache resolved
 * types for efficiency. Since this is internal state and not directly visible
 * to callers, access to state is fully synchronized so that access from
 * multiple threads is safe.
 */
@SuppressWarnings(value = { "serial" }) public class TypeResolver implements Serializable {
  private final static ResolvedType[] NO_TYPES = new ResolvedType[0];

  /**
     * We will also need to return "unknown" type for cases where type variable binding
     * is not found ('raw' instances of generic types); easiest way is to
     * pre-create type for <code>java.lang.Object</code>
     */
  private final static ResolvedObjectType sJavaLangObject = ResolvedObjectType.create(Object.class, null, null, null);

  /**
     * Since number of primitive types is small, and they are frequently needed,
     * let's actually pre-create them for efficient reuse. Same goes for limited number
     * of other "standard" types...
     */
  protected final static HashMap<ClassKey, ResolvedType> _primitiveTypes;

  static {
    _primitiveTypes = new HashMap<ClassKey, ResolvedType>(16);
    for (ResolvedPrimitiveType type : ResolvedPrimitiveType.all()) {
      _primitiveTypes.put(new ClassKey(type.getErasedType()), type);
    }
    _primitiveTypes.put(new ClassKey(Void.TYPE), ResolvedPrimitiveType.voidType());
    _primitiveTypes.put(new ClassKey(Object.class), sJavaLangObject);
  }

  /**
     * Simple cache of types resolved by this resolved; capped to last 200 resolved types.
     * Caching works because type instances themselves are mostly immutable;
     * and properly synchronized in cases where transient data (raw members) are
     * accessed.
     */
  protected final ResolvedTypeCache _resolvedTypes = new ResolvedTypeCache(200);

  public TypeResolver() {
  }

  /**
     * Factory method for resolving given base type
     * using specified types as type parameters.
     * Sample usage would be:
     *<pre>
     *  ResolvedType type = TypeResolver.resolve(List.class, Integer.class);
     *</pre>
     * which would be equivalent to
     *<pre>
     *  ResolvedType type = TypeResolver.resolve(new GenericType&lt;List&lt;Integer>>() { });
     *</pre>
     * Note that you can mix different types of type parameters, whether already
     * resolved ({@link ResolvedType}), type-erased ({@link java.lang.Class}) or
     * generic type reference ({@link GenericType}).
     */
  public ResolvedType resolve(Type type, Type... typeParameters) {
    boolean noParams = (typeParameters == null || typeParameters.length == 0);
    TypeBindings bindings;
    Class<?> rawBase;
    if (type instanceof Class<?>) {
      bindings = TypeBindings.emptyBindings();
      if (noParams) {
        return _fromClass(null, (Class<?>) type, bindings);
      }
      rawBase = (Class<?>) type;
    } else {
      if (type instanceof GenericType<?>) {
        bindings = TypeBindings.emptyBindings();
        if (noParams) {
          return _fromGenericType(null, (GenericType<?>) type, bindings);
        }
        ResolvedType rt = _fromAny(null, type, bindings);
        rawBase = rt.getErasedType();
      } else {
        if (type instanceof ResolvedType) {
          ResolvedType rt = (ResolvedType) type;
          if (noParams) {
            return rt;
          }
          bindings = rt.getTypeBindings();
          rawBase = rt.getErasedType();
        } else {
          bindings = TypeBindings.emptyBindings();
          if (noParams) {
            return resolve(bindings, type);
          }
          ResolvedType rt = _fromAny(null, type, bindings);
          rawBase = rt.getErasedType();
        }
      }
    }
    int len = typeParameters.length;
    ResolvedType[] resolvedParams = new ResolvedType[len];
    for (int i = 0; i < len; ++i) {
      resolvedParams[i] = _fromAny(null, typeParameters[i], bindings);
    }
    return _fromClass(null, rawBase, TypeBindings.create(rawBase, resolvedParams));
  }

  /**
     * Factory method for constructing array type of given element type.
     */
  public ResolvedArrayType arrayType(Type elementType) {
    ResolvedType resolvedElementType = resolve(TypeBindings.emptyBindings(), elementType);
    Object emptyArray = Array.newInstance(resolvedElementType.getErasedType(), 0);
    return new ResolvedArrayType(emptyArray.getClass(), TypeBindings.emptyBindings(), resolvedElementType);
  }

  /**
     * Factory method for resolving specified Java {@link java.lang.reflect.Type}, given
     * {@link TypeBindings} needed to resolve any type variables.
     *<p>
     * Use of this method is discouraged (use if and only if you really know what you
     * are doing!); but if used, type bindings passed should come from {@link ResolvedType}
     * instance of declaring class (or interface).
     *<p>
     * NOTE: order of arguments was reversed for 0.8, to avoid problems with
     * overload varargs method.
     */
  public ResolvedType resolve(TypeBindings typeBindings, Type jdkType) {
    return _fromAny(null, jdkType, typeBindings);
  }

  /**
     * Factory method for constructing sub-classing specified type; class specified
     * as sub-class must be compatible according to basic Java inheritance rules
     * (subtype must properly extend or implement specified supertype).
     *<p>
     * A typical use case here is to refine a generic type; for example, given
     * that we have generic type like <code>List&ltInteger></code>, but we want
     * a more specific implementation type like
     * class <code>ArrayList</code> but with same parameterization (here just <code>Integer</code>),
     * we could achieve it by:
     *<pre>
     *  ResolvedType mapType = typeResolver.resolve(List.class, Integer.class);
     *  ResolveType concreteMapType = typeResolver.resolveSubType(mapType, ArrayList.class);
     *</pre>
     * (in this case, it would have been simpler to resolve directly; but in some
     * cases we are handled supertype and want to refine it, in which case steps
     * would be the same but separated by other code)
     *<p>
     * Note that this method will fail if extension can not succeed; either because
     * this type is not extendable (sub-classable) -- which is true for primitive
     * and array types -- or because given class is not a subtype of this type.
     * To check whether subtyping could succeed, you can call
     * {@link ResolvedType#canCreateSubtypes()} to see if supertype can ever
     * be extended.
     *
     * @param supertype Type to subtype (extend)
     * @param subtype Type-erased sub-class or sub-interface
     * 
     * @return Resolved subtype
     * 
     * @throws IllegalArgumentException If this type can be extended in general, but not into specified sub-class
     * @throws UnsupportedOperationException If this type can not be sub-classed
     */
  public ResolvedType resolveSubtype(ResolvedType supertype, Class<?> subtype) throws IllegalArgumentException, UnsupportedOperationException {
    ResolvedType refType = supertype.getSelfReferencedType();
    if (refType != null) {
      supertype = refType;
    }
    if (supertype.getErasedType() == subtype) {
      return supertype;
    }
    if (!supertype.canCreateSubtypes()) {
      throw new UnsupportedOperationException("Can not subtype primitive or array types (type " + supertype.getFullDescription() + ")");
    }
    Class<?> superclass = supertype.getErasedType();
    if (!superclass.isAssignableFrom(subtype)) {
      throw new IllegalArgumentException("Can not sub-class " + supertype.getBriefDescription() + " into " + subtype.getName());
    }
    ResolvedType resolvedSubtype;
    int paramCount = subtype.getTypeParameters().length;
    TypePlaceHolder[] placeholders;
    if (paramCount == 0) {
      placeholders = null;
      resolvedSubtype = resolve(subtype);
    } else {
      placeholders = new TypePlaceHolder[paramCount];
      for (int i = 0; i < paramCount; ++i) {
        placeholders[i] = new TypePlaceHolder(i);
      }
      resolvedSubtype = resolve(subtype, placeholders);
    }
    ResolvedType rawSupertype = resolvedSubtype.findSupertype(superclass);
    if (rawSupertype == null) {
      throw new IllegalArgumentException("Internal error: unable to locate supertype (" + subtype.getName() + ") for type " + supertype.getBriefDescription());
    }
    _resolveTypePlaceholders(supertype, rawSupertype);
    if (paramCount == 0) {
      return resolvedSubtype;
    }
    ResolvedType[] typeParams = new ResolvedType[paramCount];
    for (int i = 0; i < paramCount; ++i) {
      ResolvedType t = placeholders[i].actualType();
      if (t == null) {
        throw new IllegalArgumentException("Failed to find type parameter #" + (i + 1) + "/" + paramCount + " for " + subtype.getName());
      }
      typeParams[i] = t;
    }
    return resolve(subtype, typeParams);
  }

  /**
     * Helper method that can be used to checked whether given resolved type
     * (with erased type of <code>java.lang.Object</code>) is a placeholder
     * for "self-reference"; these are nasty recursive ("self") types
     * needed with some interfaces
     */
  public static boolean isSelfReference(ResolvedType type) {
    return (type instanceof ResolvedRecursiveType);
  }

  private ResolvedType _fromAny(ClassStack context, Type mainType, TypeBindings typeBindings) {
    if (mainType instanceof Class<?>) {
      return _fromClass(context, (Class<?>) mainType, typeBindings);
    }
    if (mainType instanceof ResolvedType) {
      return (ResolvedType) mainType;
    }
    if (mainType instanceof ParameterizedType) {
      return _fromParamType(context, (ParameterizedType) mainType, typeBindings);
    }
    if (mainType instanceof GenericType<?>) {
      return _fromGenericType(context, (GenericType<?>) mainType, typeBindings);
    }
    if (mainType instanceof GenericArrayType) {
      return _fromArrayType(context, (GenericArrayType) mainType, typeBindings);
    }
    if (mainType instanceof TypeVariable<?>) {
      return _fromVariable(context, (TypeVariable<?>) mainType, typeBindings);
    }
    if (mainType instanceof WildcardType) {
      return _fromWildcard(context, (WildcardType) mainType, typeBindings);
    }
    throw new IllegalArgumentException("Unrecognized type class: " + mainType.getClass().getName());
  }

  private ResolvedType _fromClass(ClassStack context, Class<?> rawType, TypeBindings typeBindings) {
    ResolvedType type = _primitiveTypes.get(new ClassKey(rawType));
    if (type != null) {
      return type;
    }
    if (context == null) {
      context = new ClassStack(rawType);
    } else {
      ClassStack prev = context.find(rawType);
      if (prev != null) {
        ResolvedRecursiveType selfRef = new ResolvedRecursiveType(rawType, typeBindings);
        prev.addSelfReference(selfRef);
        return selfRef;
      }
      context = context.child(rawType);
    }
    ResolvedType[] typeParameters = typeBindings.typeParameterArray();
    ResolvedTypeCache.Key key = _resolvedTypes.key(rawType, typeParameters);
    type = _resolvedTypes.find(key);
    if (type == null) {
      type = _constructType(context, rawType, typeBindings);
      _resolvedTypes.put(key, type);
    }
    context.resolveSelfReferences(type);
    return type;
  }

  /**
     * Factory method for resolving given generic type, defined by using sub-class
     * instance of {@link GenericType}
     */
  private ResolvedType _fromGenericType(ClassStack context, GenericType<?> generic, TypeBindings typeBindings) {
    ResolvedType type = _fromClass(context, generic.getClass(), typeBindings);
    ResolvedType genType = type.findSupertype(GenericType.class);
    if (genType == null) {
      throw new IllegalArgumentException("Unparameterized GenericType instance (" + generic.getClass().getName() + ")");
    }
    TypeBindings b = genType.getTypeBindings();
    ResolvedType[] params = b.typeParameterArray();
    if (params.length == 0) {
      throw new IllegalArgumentException("Unparameterized GenericType instance (" + generic.getClass().getName() + ")");
    }
    return params[0];
  }

  private ResolvedType _constructType(ClassStack context, Class<?> rawType, TypeBindings typeBindings) {
    if (rawType.isArray()) {
      ResolvedType elementType = _fromAny(context, rawType.getComponentType(), typeBindings);
      return new ResolvedArrayType(rawType, typeBindings, elementType);
    }
    if (rawType.isInterface()) {
      return new ResolvedInterfaceType(rawType, typeBindings, _resolveSuperInterfaces(context, rawType, typeBindings));
    }
    return new ResolvedObjectType(rawType, typeBindings, _resolveSuperClass(context, rawType, typeBindings), _resolveSuperInterfaces(context, rawType, typeBindings));
  }

  private ResolvedType[] _resolveSuperInterfaces(ClassStack context, Class<?> rawType, TypeBindings typeBindings) {
    Type[] types = rawType.getGenericInterfaces();
    if (types == null || types.length == 0) {
      return NO_TYPES;
    }
    int len = types.length;
    ResolvedType[] resolved = new ResolvedType[len];
    for (int i = 0; i < len; ++i) {
      resolved[i] = _fromAny(context, types[i], typeBindings);
    }
    return resolved;
  }

  /**
     * NOTE: return type changed in 1.0.1 from {@link ResolvedObjectType} to
     *    {@link ResolvedType}, since it was found that other types may
     *    be returned...
     * 
     * @return Usually a {@link ResolvedObjectType}, but possibly also
     *    {@link ResolvedRecursiveType}
     */
  private ResolvedType _resolveSuperClass(ClassStack context, Class<?> rawType, TypeBindings typeBindings) {
    Type parent = rawType.getGenericSuperclass();
    if (parent == null) {
      return null;
    }
    return _fromAny(context, parent, typeBindings);
  }

  private ResolvedType _fromParamType(ClassStack context, ParameterizedType ptype, TypeBindings parentBindings) {
    Class<?> rawType = (Class<?>) ptype.getRawType();
    Type[] params = ptype.getActualTypeArguments();
    int len = params.length;
    ResolvedType[] types = new ResolvedType[len];
    for (int i = 0; i < len; ++i) {
      types[i] = _fromAny(context, params[i], parentBindings);
    }
    TypeBindings newBindings = TypeBindings.create(rawType, types);
    return _fromClass(context, rawType, newBindings);
  }

  private ResolvedType _fromArrayType(ClassStack context, GenericArrayType arrayType, TypeBindings typeBindings) {
    ResolvedType elementType = _fromAny(context, arrayType.getGenericComponentType(), typeBindings);
    Object emptyArray = Array.newInstance(elementType.getErasedType(), 0);
    return new ResolvedArrayType(emptyArray.getClass(), typeBindings, elementType);
  }

  private ResolvedType _fromWildcard(ClassStack context, WildcardType wildType, TypeBindings typeBindings) {
    return _fromAny(context, wildType.getUpperBounds()[0], typeBindings);
  }

  private ResolvedType _fromVariable(ClassStack context, TypeVariable<?> variable, TypeBindings typeBindings) {
    String name = variable.getName();
    ResolvedType type = typeBindings.findBoundType(name);
    if (type != null) {
      return type;
    }
    if (typeBindings.hasUnbound(name)) {
      return sJavaLangObject;
    }
    typeBindings = typeBindings.withUnboundVariable(name);
    Type[] bounds = variable.getBounds();
    return _fromAny(context, bounds[0], typeBindings);
  }

  /**
     * Method called to verify that types match; and if there are
     */
  private void _resolveTypePlaceholders(ResolvedType expectedType, ResolvedType actualType) throws IllegalArgumentException {
    List<ResolvedType> expectedTypes = expectedType.getTypeParameters();
    List<ResolvedType> actualTypes = actualType.getTypeParameters();
    for (int i = 0, len = expectedTypes.size(); i < len; ++i) {
      ResolvedType exp = expectedTypes.get(i);
      ResolvedType act = actualTypes.get(i);
      if (!_typesMatch(exp, act)) {
        throw new IllegalArgumentException("Type parameter #" + (i + 1) + "/" + len + " differs; expected " + exp.getBriefDescription() + ", got " + act.getBriefDescription());
      }
    }
  }

  private boolean _typesMatch(ResolvedType exp, ResolvedType act) {
    if (act instanceof TypePlaceHolder) {
      ((TypePlaceHolder) act).actualType(exp);
      return true;
    }
    if (exp.getErasedType() != act.getErasedType()) {
      return false;
    }
    List<ResolvedType> expectedTypes = exp.getTypeParameters();
    List<ResolvedType> actualTypes = act.getTypeParameters();
    for (int i = 0, len = expectedTypes.size(); i < len; ++i) {
      ResolvedType exp2 = expectedTypes.get(i);
      ResolvedType act2 = actualTypes.get(i);
      if (!_typesMatch(exp2, act2)) {
        return false;
      }
    }
    return true;
  }
}