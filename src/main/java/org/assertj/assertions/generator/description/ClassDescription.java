package org.assertj.assertions.generator.description;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * Stores the information needed to generate assertions for a given class.
 * 
 * @author Joel Costigliola
 * 
 */
public class ClassDescription {
  private Set<TypeName> typesToImports;

  private Set<GetterDescription> gettersDescriptions;

  private Set<FieldDescription> fieldsDescriptions;

  private Set<GetterDescription> declaredGettersDescriptions;

  private Set<FieldDescription> declaredFieldsDescriptions;

  private TypeName classTypeName;

  private Class<?> superType;

  public ClassDescription(TypeName typeName) {
    super();
    this.classTypeName = typeName;
    this.typesToImports = new TreeSet<TypeName>();
    this.gettersDescriptions = new TreeSet<GetterDescription>();
    this.fieldsDescriptions = new TreeSet<FieldDescription>();
    this.declaredGettersDescriptions = new TreeSet<GetterDescription>();
    this.declaredFieldsDescriptions = new TreeSet<FieldDescription>();
  }

  public String getClassName() {
    return classTypeName.getSimpleName();
  }

  public TypeName getTypeName() {
    return classTypeName;
  }

  public String getClassNameWithOuterClass() {
    return classTypeName.getSimpleNameWithOuterClass();
  }

  public String getClassNameWithOuterClassNotSeparatedByDots() {
    return classTypeName.getSimpleNameWithOuterClassNotSeparatedByDots();
  }

  public String getPackageName() {
    return classTypeName.getPackageName();
  }

  /**
   * Return the type to import for the corresponding assertions class
   * @return
   */
  public Set<TypeName> getImports() {
    return typesToImports;
  }

  public void addTypeToImport(Collection<TypeName> typesToImport) {
    this.typesToImports.addAll(typesToImport);
  }

  public Set<GetterDescription> getGettersDescriptions() {
    return gettersDescriptions;
  }

  public void addGetterDescriptions(Collection<GetterDescription> getterDescriptions) {
    this.gettersDescriptions.addAll(getterDescriptions);
  }

  public void addFieldDescriptions(Set<FieldDescription> fieldDescriptions) {
    this.fieldsDescriptions.addAll(fieldDescriptions);
  }

  public Set<FieldDescription> getFieldsDescriptions() {
    return fieldsDescriptions;
  }

  public Set<GetterDescription> getDeclaredGettersDescriptions() {
    return declaredGettersDescriptions;
  }

  public Set<FieldDescription> getDeclaredFieldsDescriptions() {
    return declaredFieldsDescriptions;
  }

  public void addDeclaredGetterDescriptions(Collection<GetterDescription> declaredGetterDescriptions) {
    this.declaredGettersDescriptions.addAll(declaredGetterDescriptions);
  }

  public void addDeclaredFieldDescriptions(Set<FieldDescription> declaredFieldDescriptions) {
    this.declaredFieldsDescriptions.addAll(declaredFieldDescriptions);
  }

  @Override public String toString() {
    return "ClassDescription [classTypeName=" + classTypeName + ", typesToImports=" + typesToImports + "]";
  }

  @Override public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ClassDescription)) {
      return false;
    }
    final ClassDescription that = (ClassDescription) o;
    if (classTypeName != null ? !classTypeName.equals(that.classTypeName) : that.classTypeName != null) {
      return false;
    }
    return true;
  }

  @Override public int hashCode() {
    return classTypeName != null ? classTypeName.hashCode() : 0;
  }

  public Class<?> getSuperType() {
    return superType;
  }

  public void setSuperType(Class<?> superType) {
    this.superType = superType;
  }
}