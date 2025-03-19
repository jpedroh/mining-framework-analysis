package org.apache.olingo.commons.core.edm;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

public class EdmTypeDefinitionImpl extends EdmTypeImpl implements EdmTypeDefinition {
  private CsdlTypeDefinition typeDefinition;

  private EdmPrimitiveType edmPrimitiveTypeInstance;

  public EdmTypeDefinitionImpl(final Edm edm, final FullQualifiedName typeDefinitionName, final CsdlTypeDefinition typeDefinition) {
    super(edm, typeDefinitionName, EdmTypeKind.DEFINITION, typeDefinition);
    this.typeDefinition = typeDefinition;
  }

  @Override public EdmPrimitiveType getUnderlyingType() {
    if (edmPrimitiveTypeInstance == null) {
      try {
        if (typeDefinition.getUnderlyingType() == null) {
          throw new EdmException("Underlying Type for type definition: " + typeName.getFullQualifiedNameAsString() + " must not be null.");
        }
        edmPrimitiveTypeInstance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.valueOfFQN(typeDefinition.getUnderlyingType()));
      } catch (IllegalArgumentException e) {
        throw new EdmException("Invalid underlying type: " + typeDefinition.getUnderlyingType(), e);
      }
    }
    return edmPrimitiveTypeInstance;
  }

  @Override public Integer getMaxLength() {
    return typeDefinition.getMaxLength();
  }

  @Override public Integer getPrecision() {
    return typeDefinition.getPrecision();
  }

  @Override public Integer getScale() {
    return typeDefinition.getScale();
  }

  @Override public SRID getSrid() {
    return typeDefinition.getSrid();
  }

  @Override public Boolean isUnicode() {
    return typeDefinition.isUnicode();
  }

  @Override public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return this == primitiveType || getUnderlyingType().isCompatible(primitiveType);
  }

  @Override public Class<?> getDefaultType() {
    return getUnderlyingType().getDefaultType();
  }

  @Override public boolean validate(final String value, final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode) {
    return getUnderlyingType().validate(value, isNullable, maxLength == null ? getMaxLength() : maxLength, precision == null ? getPrecision() : precision, scale == null ? getScale() : scale, isUnicode == null ? isUnicode() : isUnicode);
  }

  @Override public <T extends java.lang.Object> T valueOfString(final String value, final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    return getUnderlyingType().valueOfString(value, isNullable, maxLength == null ? getMaxLength() : maxLength, precision == null ? getPrecision() : precision, scale == null ? getScale() : scale, isUnicode == null ? isUnicode() : isUnicode, returnType);
  }

  @Override public String valueToString(final Object value, final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    return getUnderlyingType().valueToString(value, isNullable, maxLength == null ? getMaxLength() : maxLength, precision == null ? getPrecision() : precision, scale == null ? getScale() : scale, isUnicode == null ? isUnicode() : isUnicode);
  }

  @Override public String toUriLiteral(final String literal) {
    return getUnderlyingType().toUriLiteral(literal);
  }

  @Override public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    return getUnderlyingType().fromUriLiteral(literal);
  }
}