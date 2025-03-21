package org.fusesource.hawtjni.runtime;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */public enum FieldFlag {
  FIELD_SKIP,
  CONSTANT,
  POINTER_FIELD,
  SHARED_PTR,
  GETTER_NONMEMBER,
  SETTER_NONMEMBER
}