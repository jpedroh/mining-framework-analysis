package org.sqlproc.dsl.generator;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;
import org.sqlproc.dsl.processorDsl.Extends;
import org.sqlproc.dsl.processorDsl.Implements;
import org.sqlproc.dsl.processorDsl.PojoEntity;
import org.sqlproc.dsl.processorDsl.PojoProperty;
import org.sqlproc.dsl.util.Utils;

@SuppressWarnings("all")
public class ProcessorDslGenerator implements IGenerator {
  @Inject
  private IQualifiedNameProvider _iQualifiedNameProvider;
  
  public void doGenerate(final Resource resource, final IFileSystemAccess fsa) {
    TreeIterator<EObject> _allContents = resource.getAllContents();
    Iterable<EObject> _iterable = IteratorExtensions.<EObject>toIterable(_allContents);
    Iterable<PojoEntity> _filter = Iterables.<PojoEntity>filter(_iterable, PojoEntity.class);
    for (final PojoEntity e : _filter) {
      EObject _eContainer = e.eContainer();
      QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(_eContainer);
      String _string = _fullyQualifiedName.toString("/");
      String _plus = (_string + "/");
      QualifiedName _fullyQualifiedName_1 = this._iQualifiedNameProvider.getFullyQualifiedName(e);
      String _plus_1 = (_plus + _fullyQualifiedName_1);
      String _plus_2 = (_plus_1 + ".java");
      CharSequence _compile = this.compile(e);
      fsa.generateFile(_plus_2, _compile);
    }
  }
  
  public CharSequence compile(final PojoEntity e) {
    StringConcatenation _builder = new StringConcatenation();
    ImportManager _importManager = new ImportManager(true);
    final ImportManager importManager = _importManager;
    _builder.newLineIfNotEmpty();
    this.addImplements(e, importManager);
    _builder.newLineIfNotEmpty();
    this.addExtends(e, importManager);
    _builder.newLineIfNotEmpty();
    final CharSequence classBody = this.compile(e, importManager);
    _builder.newLineIfNotEmpty();
    {
      EObject _eContainer = e.eContainer();
      boolean _notEquals = (!Objects.equal(_eContainer, null));
      if (_notEquals) {
        _builder.append("package ");
        EObject _eContainer_1 = e.eContainer();
        QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(_eContainer_1);
        _builder.append(_fullyQualifiedName, "");
        _builder.append(";");
      }
    }
    _builder.newLineIfNotEmpty();
    {
      List<String> _imports = importManager.getImports();
      boolean _isEmpty = _imports.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("  ");
        _builder.newLine();
        {
          List<String> _imports_1 = importManager.getImports();
          for(final String i : _imports_1) {
            _builder.append("import ");
            _builder.append(i, "");
            _builder.append(";");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    {
      String _sernum = Utils.getSernum(e);
      boolean _notEquals_1 = (!Objects.equal(_sernum, null));
      if (_notEquals_1) {
        _builder.newLine();
        _builder.append("import java.io.Serializable;");
        _builder.newLine();
      }
    }
    {
      ArrayList<PojoProperty> _listFeatures = this.listFeatures(e);
      boolean _isEmpty_1 = _listFeatures.isEmpty();
      boolean _not_1 = (!_isEmpty_1);
      if (_not_1) {
        _builder.append("import java.util.ArrayList;");
        _builder.newLine();
      }
    }
    {
      boolean _or = false;
      PojoProperty _hasIsDef = this.hasIsDef(e);
      boolean _notEquals_2 = (!Objects.equal(_hasIsDef, null));
      if (_notEquals_2) {
        _or = true;
      } else {
        PojoProperty _hasToInit = this.hasToInit(e);
        boolean _notEquals_3 = (!Objects.equal(_hasToInit, null));
        _or = (_notEquals_2 || _notEquals_3);
      }
      if (_or) {
        _builder.append("import java.util.Set;");
        _builder.newLine();
        _builder.append("import java.util.HashSet;");
        _builder.newLine();
        _builder.append("import java.lang.reflect.InvocationTargetException;");
        _builder.newLine();
        _builder.append("import org.apache.commons.beanutils.MethodUtils;");
        _builder.newLine();
      }
    }
    _builder.newLine();
    _builder.append(classBody, "");
    _builder.newLineIfNotEmpty();
    return _builder;
  }
  
  public CharSequence compile(final PojoEntity e, final ImportManager importManager) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public ");
    {
      boolean _isAbstract = Utils.isAbstract(e);
      if (_isAbstract) {
        _builder.append("abstract ");
      }
    }
    _builder.append("class ");
    String _name = e.getName();
    _builder.append(_name, "");
    _builder.append(" ");
    CharSequence _compileExtends = this.compileExtends(e);
    _builder.append(_compileExtends, "");
    CharSequence _compileImplements = this.compileImplements(e);
    _builder.append(_compileImplements, "");
    _builder.append("{");
    _builder.newLineIfNotEmpty();
    {
      String _sernum = Utils.getSernum(e);
      boolean _notEquals = (!Objects.equal(_sernum, null));
      if (_notEquals) {
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("private static final long serialVersionUID = ");
        String _sernum_1 = Utils.getSernum(e);
        _builder.append(_sernum_1, "  ");
        _builder.append("L;");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("\t");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("public ");
    String _name_1 = e.getName();
    _builder.append(_name_1, "  ");
    _builder.append("() {");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    {
      ArrayList<PojoProperty> _requiredFeatures = this.requiredFeatures(e);
      boolean _isEmpty = _requiredFeatures.isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        _builder.append("  ");
        _builder.newLine();
        _builder.append("  ");
        _builder.append("public ");
        String _name_2 = e.getName();
        _builder.append(_name_2, "  ");
        _builder.append("(");
        {
          ArrayList<PojoProperty> _requiredFeatures_1 = this.requiredFeatures(e);
          boolean _hasElements = false;
          for(final PojoProperty f : _requiredFeatures_1) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(", ", "  ");
            }
            CharSequence _compileType = this.compileType(f, importManager);
            _builder.append(_compileType, "  ");
            _builder.append(" ");
            String _name_3 = f.getName();
            _builder.append(_name_3, "  ");
          }
        }
        _builder.append(") {");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        {
          ArrayList<PojoProperty> _requiredSuperFeatures = this.requiredSuperFeatures(e);
          boolean _hasElements_1 = false;
          for(final PojoProperty f_1 : _requiredSuperFeatures) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
              _builder.append("  super(", "  ");
            } else {
              _builder.appendImmediate(", ", "  ");
            }
            String _name_4 = f_1.getName();
            _builder.append(_name_4, "  ");
          }
          if (_hasElements_1) {
            _builder.append(");", "  ");
          }
        }
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        {
          List<PojoProperty> _requiredFeatures1 = this.requiredFeatures1(e);
          boolean _hasElements_2 = false;
          for(final PojoProperty f_2 : _requiredFeatures1) {
            if (!_hasElements_2) {
              _hasElements_2 = true;
            } else {
              _builder.appendImmediate("\n", "  ");
            }
            _builder.append("  this.");
            String _name_5 = f_2.getName();
            _builder.append(_name_5, "  ");
            _builder.append(" = ");
            String _name_6 = f_2.getName();
            _builder.append(_name_6, "  ");
            _builder.append(";");
          }
        }
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("}");
        _builder.newLine();
      }
    }
    {
      EList<PojoProperty> _features = e.getFeatures();
      final Function1<PojoProperty,Boolean> _function = new Function1<PojoProperty,Boolean>() {
          public Boolean apply(final PojoProperty x) {
            boolean _isAttribute = ProcessorDslGenerator.this.isAttribute(x);
            return Boolean.valueOf(_isAttribute);
          }
        };
      Iterable<PojoProperty> _filter = IterableExtensions.<PojoProperty>filter(_features, _function);
      for(final PojoProperty f_3 : _filter) {
        _builder.append("  ");
        CharSequence _compile = this.compile(f_3, importManager, e);
        _builder.append(_compile, "  ");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    {
      EList<PojoProperty> _features_1 = e.getFeatures();
      final Function1<PojoProperty,Boolean> _function_1 = new Function1<PojoProperty,Boolean>() {
          public Boolean apply(final PojoProperty x) {
            boolean _isAttribute = ProcessorDslGenerator.this.isAttribute(x);
            boolean _not = (!_isAttribute);
            return Boolean.valueOf(_not);
          }
        };
      Iterable<PojoProperty> _filter_1 = IterableExtensions.<PojoProperty>filter(_features_1, _function_1);
      for(final PojoProperty f_4 : _filter_1) {
        {
          String _name_7 = f_4.getName();
          boolean _equalsIgnoreCase = _name_7.equalsIgnoreCase("hashCode");
          if (_equalsIgnoreCase) {
            CharSequence _compileHashCode = this.compileHashCode(f_4, importManager, e);
            _builder.append(_compileHashCode, "  ");
            _builder.newLineIfNotEmpty();
            _builder.append("  ");
          } else {
            String _name_8 = f_4.getName();
            boolean _equalsIgnoreCase_1 = _name_8.equalsIgnoreCase("equals");
            if (_equalsIgnoreCase_1) {
              CharSequence _compileEquals = this.compileEquals(f_4, importManager, e);
              _builder.append(_compileEquals, "  ");
              _builder.newLineIfNotEmpty();
              _builder.append("  ");
            } else {
              String _name_9 = f_4.getName();
              boolean _equalsIgnoreCase_2 = _name_9.equalsIgnoreCase("toInit");
              if (_equalsIgnoreCase_2) {
                CharSequence _compileToInit = this.compileToInit(f_4, importManager, e);
                _builder.append(_compileToInit, "  ");
                _builder.newLineIfNotEmpty();
                _builder.append("  ");
              } else {
                String _name_10 = f_4.getName();
                boolean _equalsIgnoreCase_3 = _name_10.equalsIgnoreCase("isDef");
                if (_equalsIgnoreCase_3) {
                  CharSequence _compileIsDef = this.compileIsDef(f_4, importManager, e);
                  _builder.append(_compileIsDef, "  ");
                  _builder.newLineIfNotEmpty();
                  _builder.append("  ");
                } else {
                  String _name_11 = f_4.getName();
                  boolean _equalsIgnoreCase_4 = _name_11.equalsIgnoreCase("toString");
                  if (_equalsIgnoreCase_4) {
                    CharSequence _compileToString = this.compileToString(f_4, importManager, e);
                    _builder.append(_compileToString, "  ");
                  }
                }
              }
            }
          }
        }
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence compile(final PojoProperty f, final ImportManager importManager, final PojoEntity e) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.newLine();
    _builder.append("private ");
    CharSequence _compileType = this.compileType(f, importManager);
    _builder.append(_compileType, "");
    _builder.append(" ");
    String _name = f.getName();
    _builder.append(_name, "");
    {
      boolean _isList = Utils.isList(f);
      if (_isList) {
        _builder.append(" = new Array");
        CharSequence _compileType_1 = this.compileType(f, importManager);
        _builder.append(_compileType_1, "");
        _builder.append("()");
      }
    }
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("public ");
    CharSequence _compileType_2 = this.compileType(f, importManager);
    _builder.append(_compileType_2, "");
    _builder.append(" get");
    String _name_1 = f.getName();
    String _firstUpper = StringExtensions.toFirstUpper(_name_1);
    _builder.append(_firstUpper, "");
    _builder.append("() {");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("return ");
    String _name_2 = f.getName();
    _builder.append(_name_2, "  ");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("public void set");
    String _name_3 = f.getName();
    String _firstUpper_1 = StringExtensions.toFirstUpper(_name_3);
    _builder.append(_firstUpper_1, "");
    _builder.append("(");
    CharSequence _compileType_3 = this.compileType(f, importManager);
    _builder.append(_compileType_3, "");
    _builder.append(" ");
    String _name_4 = f.getName();
    _builder.append(_name_4, "");
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("this.");
    String _name_5 = f.getName();
    _builder.append(_name_5, "  ");
    _builder.append(" = ");
    String _name_6 = f.getName();
    _builder.append(_name_6, "  ");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.newLine();
    _builder.append("public ");
    String _name_7 = e.getName();
    _builder.append(_name_7, "");
    _builder.append(" _set");
    String _name_8 = f.getName();
    String _firstUpper_2 = StringExtensions.toFirstUpper(_name_8);
    _builder.append(_firstUpper_2, "");
    _builder.append("(");
    CharSequence _compileType_4 = this.compileType(f, importManager);
    _builder.append(_compileType_4, "");
    _builder.append(" ");
    String _name_9 = f.getName();
    _builder.append(_name_9, "");
    _builder.append(") {");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("this.");
    String _name_10 = f.getName();
    _builder.append(_name_10, "  ");
    _builder.append(" = ");
    String _name_11 = f.getName();
    _builder.append(_name_11, "  ");
    _builder.append(";");
    _builder.newLineIfNotEmpty();
    _builder.append("  ");
    _builder.append("return this;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence compileHashCode(final PojoProperty f, final ImportManager importManager, final PojoEntity e) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.newLine();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("public int hashCode() {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("final int prime = 31;");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("int result = 1;");
    _builder.newLine();
    {
      EList<PojoProperty> _attrs = f.getAttrs();
      for(final PojoProperty f2 : _attrs) {
        _builder.append("  ");
        _builder.append("result = prime * result + (int) (");
        String _name = f2.getName();
        _builder.append(_name, "  ");
        _builder.append(" ^ (");
        String _name_1 = f2.getName();
        _builder.append(_name_1, "  ");
        _builder.append(" >>> 32));");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("  ");
    _builder.append("return result;");
    _builder.newLine();
    _builder.append("}  ");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence compileEquals(final PojoProperty f, final ImportManager importManager, final PojoEntity e) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.newLine();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("public boolean equals(Object obj) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (this == obj)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("return true;");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (obj == null)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("return false;");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (getClass() != obj.getClass())");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("return false;");
    _builder.newLine();
    _builder.append("  ");
    String _name = e.getName();
    _builder.append(_name, "  ");
    _builder.append(" other = (");
    String _name_1 = e.getName();
    _builder.append(_name_1, "  ");
    _builder.append(") obj;");
    _builder.newLineIfNotEmpty();
    {
      EList<PojoProperty> _attrs = f.getAttrs();
      for(final PojoProperty f2 : _attrs) {
        _builder.append("  ");
        _builder.append("if (");
        String _name_2 = f2.getName();
        _builder.append(_name_2, "  ");
        _builder.append(" != other.");
        String _name_3 = f2.getName();
        _builder.append(_name_3, "  ");
        _builder.append(")");
        _builder.newLineIfNotEmpty();
        _builder.append("  ");
        _builder.append("  ");
        _builder.append("return false;");
        _builder.newLine();
      }
    }
    _builder.append("  ");
    _builder.append("return true;");
    _builder.newLine();
    _builder.append("}  ");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence compileToString(final PojoProperty f, final ImportManager importManager, final PojoEntity e) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.newLine();
    _builder.append("@Override");
    _builder.newLine();
    _builder.append("public String toString() {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("return \"");
    String _name = e.getName();
    _builder.append(_name, "  ");
    _builder.append(" [");
    {
      List<PojoProperty> _simplAttrs = this.simplAttrs(f);
      boolean _hasElements = false;
      for(final PojoProperty f2 : _simplAttrs) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(" + \", ", "  ");
        }
        String _name_1 = f2.getName();
        _builder.append(_name_1, "  ");
        _builder.append("=\" + ");
        String _name_2 = f2.getName();
        _builder.append(_name_2, "  ");
      }
    }
    {
      PojoEntity _superType = Utils.getSuperType(e);
      boolean _notEquals = (!Objects.equal(_superType, null));
      if (_notEquals) {
        _builder.append(" + super.toString()");
      }
    }
    _builder.append(" + \"]\";");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public String toStringFull() {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("return \"");
    String _name_3 = e.getName();
    _builder.append(_name_3, "  ");
    _builder.append(" [");
    {
      EList<PojoProperty> _attrs = f.getAttrs();
      boolean _hasElements_1 = false;
      for(final PojoProperty f2_1 : _attrs) {
        if (!_hasElements_1) {
          _hasElements_1 = true;
        } else {
          _builder.appendImmediate(" + \", ", "  ");
        }
        String _name_4 = f2_1.getName();
        _builder.append(_name_4, "  ");
        _builder.append("=\" + ");
        String _name_5 = f2_1.getName();
        _builder.append(_name_5, "  ");
      }
    }
    {
      PojoEntity _superType_1 = Utils.getSuperType(e);
      boolean _notEquals_1 = (!Objects.equal(_superType_1, null));
      if (_notEquals_1) {
        _builder.append(" + super.toString()");
      }
    }
    _builder.append(" + \"]\";");
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence compileIsDef(final PojoProperty f, final ImportManager importManager, final PojoEntity e) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.newLine();
    _builder.append("private Set<String> nullValues = new HashSet<String>();");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public enum Attribute {");
    _builder.newLine();
    _builder.append("  ");
    {
      EList<PojoProperty> _attrs = f.getAttrs();
      boolean _hasElements = false;
      for(final PojoProperty f2 : _attrs) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(", ", "  ");
        }
        String _name = f2.getName();
        _builder.append(_name, "  ");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public void setNull(Attribute... attributes) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (attributes == null)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new IllegalArgumentException();");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("for (Attribute attribute : attributes)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("nullValues.add(attribute.name());");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public void clearNull(Attribute... attributes) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (attributes == null)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new IllegalArgumentException();");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("for (Attribute attribute : attributes)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("nullValues.remove(attribute.name());");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public Boolean isNull(String attrName) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (attrName == null)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new IllegalArgumentException();");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("return nullValues.contains(attrName);");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public Boolean isNull(Attribute attribute) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (attribute == null)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new IllegalArgumentException();");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("return nullValues.contains(attribute.name());");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public Boolean isDef(String attrName) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (attrName == null)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new IllegalArgumentException();");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (nullValues.contains(attrName))");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("return true;");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("try {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("Object result = MethodUtils.invokeMethod(this, \"get\" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1, attrName.length()), null);");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("return (result != null) ? true : false;");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("} catch (NoSuchMethodException e) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("} catch (IllegalAccessException e) {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new RuntimeException(e);");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("} catch (InvocationTargetException e) {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new RuntimeException(e);");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("try {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("Object result = MethodUtils.invokeMethod(this, \"is\" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1, attrName.length()), null);");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("return (result != null) ? true : false;");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("} catch (NoSuchMethodException e) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("} catch (IllegalAccessException e) {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new RuntimeException(e);");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("} catch (InvocationTargetException e) {");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new RuntimeException(e);");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("return false;");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public void clearAllNull() {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("nullValues = new HashSet<String>();");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence compileToInit(final PojoProperty f, final ImportManager importManager, final PojoEntity e) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.newLine();
    _builder.append("private Set<String> initAssociations = new HashSet<String>();");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public enum Association {");
    _builder.newLine();
    _builder.append("  ");
    {
      EList<PojoProperty> _attrs = f.getAttrs();
      boolean _hasElements = false;
      for(final PojoProperty f2 : _attrs) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(", ", "  ");
        }
        String _name = f2.getName();
        _builder.append(_name, "  ");
      }
    }
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public void setInit(Association... associations) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (associations == null)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new IllegalArgumentException();");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("for (Association association : associations)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("initAssociations.add(association.name());");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public void clearInit(Association... associations) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (associations == null)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new IllegalArgumentException();");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("for (Association association : associations)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("initAssociations.remove(association.name());");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public Boolean toInit(String attrName) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (attrName == null)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new IllegalArgumentException();");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("return initAssociations.contains(attrName);");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public Boolean toInit(Association association) {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("if (association == null)");
    _builder.newLine();
    _builder.append("    ");
    _builder.append("throw new IllegalArgumentException();");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("return initAssociations.contains(association.name());");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.newLine();
    _builder.append("public void clearAllInit() {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("initAssociations = new HashSet<String>();");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }
  
  public CharSequence compileType(final PojoProperty f, final ImportManager importManager) {
    StringConcatenation _builder = new StringConcatenation();
    {
      String _native = f.getNative();
      boolean _notEquals = (!Objects.equal(_native, null));
      if (_notEquals) {
        String _native_1 = f.getNative();
        String _substring = _native_1.substring(1);
        _builder.append(_substring, "");
      } else {
        PojoEntity _ref = f.getRef();
        boolean _notEquals_1 = (!Objects.equal(_ref, null));
        if (_notEquals_1) {
          PojoEntity _ref_1 = f.getRef();
          QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(_ref_1);
          _builder.append(_fullyQualifiedName, "");
        } else {
          JvmType _type = f.getType();
          boolean _notEquals_2 = (!Objects.equal(_type, null));
          if (_notEquals_2) {
            JvmType _type_1 = f.getType();
            CharSequence _serialize = importManager.serialize(_type_1);
            _builder.append(_serialize, "");
          }
        }
      }
    }
    {
      JvmType _gtype = f.getGtype();
      boolean _notEquals_3 = (!Objects.equal(_gtype, null));
      if (_notEquals_3) {
        _builder.append("<");
        JvmType _gtype_1 = f.getGtype();
        CharSequence _serialize_1 = importManager.serialize(_gtype_1);
        _builder.append(_serialize_1, "");
        _builder.append(">");
      }
    }
    {
      PojoEntity _gref = f.getGref();
      boolean _notEquals_4 = (!Objects.equal(_gref, null));
      if (_notEquals_4) {
        _builder.append("<");
        PojoEntity _gref_1 = f.getGref();
        QualifiedName _fullyQualifiedName_1 = this._iQualifiedNameProvider.getFullyQualifiedName(_gref_1);
        _builder.append(_fullyQualifiedName_1, "");
        _builder.append(">");
      }
    }
    {
      boolean _isArray = f.isArray();
      if (_isArray) {
        _builder.append("[]");
      }
    }
    return _builder;
  }
  
  public ArrayList<PojoProperty> listFeatures(final PojoEntity e) {
    ArrayList<PojoProperty> _arrayList = new ArrayList<PojoProperty>();
    final ArrayList<PojoProperty> list = _arrayList;
    PojoEntity _superType = Utils.getSuperType(e);
    boolean _notEquals = (!Objects.equal(_superType, null));
    if (_notEquals) {
      PojoEntity _superType_1 = Utils.getSuperType(e);
      ArrayList<PojoProperty> _listFeatures = this.listFeatures(_superType_1);
      list.addAll(_listFeatures);
    }
    List<PojoProperty> _listFeatures1 = this.listFeatures1(e);
    list.addAll(_listFeatures1);
    return list;
  }
  
  public List<PojoProperty> listFeatures1(final PojoEntity e) {
    EList<PojoProperty> _features = e.getFeatures();
    final Function1<PojoProperty,Boolean> _function = new Function1<PojoProperty,Boolean>() {
        public Boolean apply(final PojoProperty f) {
          boolean _isList = Utils.isList(f);
          return Boolean.valueOf(_isList);
        }
      };
    Iterable<PojoProperty> _filter = IterableExtensions.<PojoProperty>filter(_features, _function);
    return IterableExtensions.<PojoProperty>toList(_filter);
  }
  
  public ArrayList<PojoProperty> requiredFeatures(final PojoEntity e) {
    ArrayList<PojoProperty> _arrayList = new ArrayList<PojoProperty>();
    final ArrayList<PojoProperty> list = _arrayList;
    PojoEntity _superType = Utils.getSuperType(e);
    boolean _notEquals = (!Objects.equal(_superType, null));
    if (_notEquals) {
      PojoEntity _superType_1 = Utils.getSuperType(e);
      ArrayList<PojoProperty> _requiredFeatures = this.requiredFeatures(_superType_1);
      list.addAll(_requiredFeatures);
    }
    List<PojoProperty> _requiredFeatures1 = this.requiredFeatures1(e);
    list.addAll(_requiredFeatures1);
    return list;
  }
  
  public ArrayList<PojoProperty> requiredSuperFeatures(final PojoEntity e) {
    ArrayList<PojoProperty> _arrayList = new ArrayList<PojoProperty>();
    final ArrayList<PojoProperty> list = _arrayList;
    PojoEntity _superType = Utils.getSuperType(e);
    boolean _notEquals = (!Objects.equal(_superType, null));
    if (_notEquals) {
      PojoEntity _superType_1 = Utils.getSuperType(e);
      ArrayList<PojoProperty> _requiredFeatures = this.requiredFeatures(_superType_1);
      list.addAll(_requiredFeatures);
    }
    return list;
  }
  
  public List<PojoProperty> requiredFeatures1(final PojoEntity e) {
    EList<PojoProperty> _features = e.getFeatures();
    final Function1<PojoProperty,Boolean> _function = new Function1<PojoProperty,Boolean>() {
        public Boolean apply(final PojoProperty f) {
          boolean _isRequired = Utils.isRequired(f);
          return Boolean.valueOf(_isRequired);
        }
      };
    Iterable<PojoProperty> _filter = IterableExtensions.<PojoProperty>filter(_features, _function);
    return IterableExtensions.<PojoProperty>toList(_filter);
  }
  
  public PojoProperty hasIsDef(final PojoEntity e) {
    EList<PojoProperty> _features = e.getFeatures();
    final Function1<PojoProperty,Boolean> _function = new Function1<PojoProperty,Boolean>() {
        public Boolean apply(final PojoProperty f) {
          String _name = f.getName();
          boolean _equals = Objects.equal(_name, "isDef");
          return Boolean.valueOf(_equals);
        }
      };
    return IterableExtensions.<PojoProperty>findFirst(_features, _function);
  }
  
  public PojoProperty hasToInit(final PojoEntity e) {
    EList<PojoProperty> _features = e.getFeatures();
    final Function1<PojoProperty,Boolean> _function = new Function1<PojoProperty,Boolean>() {
        public Boolean apply(final PojoProperty f) {
          String _name = f.getName();
          boolean _equals = Objects.equal(_name, "toInit");
          return Boolean.valueOf(_equals);
        }
      };
    return IterableExtensions.<PojoProperty>findFirst(_features, _function);
  }
  
  public boolean isAttribute(final PojoProperty f) {
    boolean _or = false;
    boolean _or_1 = false;
    String _native = f.getNative();
    boolean _notEquals = (!Objects.equal(_native, null));
    if (_notEquals) {
      _or_1 = true;
    } else {
      PojoEntity _ref = f.getRef();
      boolean _notEquals_1 = (!Objects.equal(_ref, null));
      _or_1 = (_notEquals || _notEquals_1);
    }
    if (_or_1) {
      _or = true;
    } else {
      JvmType _type = f.getType();
      boolean _notEquals_2 = (!Objects.equal(_type, null));
      _or = (_or_1 || _notEquals_2);
    }
    return _or;
  }
  
  public List<PojoProperty> simplAttrs(final PojoProperty f) {
    EList<PojoProperty> _attrs = f.getAttrs();
    final Function1<PojoProperty,Boolean> _function = new Function1<PojoProperty,Boolean>() {
        public Boolean apply(final PojoProperty f2) {
          boolean _or = false;
          String _native = f2.getNative();
          boolean _notEquals = (!Objects.equal(_native, null));
          if (_notEquals) {
            _or = true;
          } else {
            JvmType _type = f2.getType();
            boolean _notEquals_1 = (!Objects.equal(_type, null));
            _or = (_notEquals || _notEquals_1);
          }
          return Boolean.valueOf(_or);
        }
      };
    Iterable<PojoProperty> _filter = IterableExtensions.<PojoProperty>filter(_attrs, _function);
    return IterableExtensions.<PojoProperty>toList(_filter);
  }
  
  public CharSequence compileExtends(final PojoEntity e) {
    StringConcatenation _builder = new StringConcatenation();
    {
      PojoEntity _superType = Utils.getSuperType(e);
      boolean _notEquals = (!Objects.equal(_superType, null));
      if (_notEquals) {
        _builder.append("extends ");
        PojoEntity _superType_1 = Utils.getSuperType(e);
        QualifiedName _fullyQualifiedName = this._iQualifiedNameProvider.getFullyQualifiedName(_superType_1);
        _builder.append(_fullyQualifiedName, "");
        _builder.append(" ");
      } else {
        String _extends = this.getExtends(e);
        boolean _notEquals_1 = (!Objects.equal(_extends, ""));
        if (_notEquals_1) {
          _builder.append("extends ");
          String _extends_1 = this.getExtends(e);
          _builder.append(_extends_1, "");
          _builder.append(" ");
        }
      }
    }
    return _builder;
  }
  
  public CharSequence compileImplements(final PojoEntity e) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _or = false;
      boolean _isImplements = this.isImplements(e);
      if (_isImplements) {
        _or = true;
      } else {
        String _sernum = Utils.getSernum(e);
        boolean _notEquals = (!Objects.equal(_sernum, null));
        _or = (_isImplements || _notEquals);
      }
      if (_or) {
        _builder.append("implements ");
        {
          EObject _eContainer = e.eContainer();
          EList<EObject> _eContents = _eContainer.eContents();
          Iterable<Implements> _filter = Iterables.<Implements>filter(_eContents, Implements.class);
          boolean _hasElements = false;
          for(final Implements f : _filter) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(", ", "");
            }
            JvmType _implements = f.getImplements();
            String _simpleName = _implements.getSimpleName();
            _builder.append(_simpleName, "");
          }
        }
        {
          String _sernum_1 = Utils.getSernum(e);
          boolean _notEquals_1 = (!Objects.equal(_sernum_1, null));
          if (_notEquals_1) {
            {
              boolean _isImplements_1 = this.isImplements(e);
              if (_isImplements_1) {
                _builder.append(", ");
              }
            }
            _builder.append("Serializable");
          }
        }
        _builder.append(" ");
      }
    }
    return _builder;
  }
  
  public boolean compile(final Extends e, final ImportManager importManager) {
    JvmType _extends = e.getExtends();
    boolean _addImportFor = importManager.addImportFor(_extends);
    return _addImportFor;
  }
  
  public void addImplements(final PojoEntity e, final ImportManager importManager) {
    EObject _eContainer = e.eContainer();
    EList<EObject> _eContents = _eContainer.eContents();
    Iterable<Implements> _filter = Iterables.<Implements>filter(_eContents, Implements.class);
    for (final Implements impl : _filter) {
      JvmType _implements = impl.getImplements();
      importManager.addImportFor(_implements);
    }
  }
  
  public void addExtends(final PojoEntity e, final ImportManager importManager) {
    EObject _eContainer = e.eContainer();
    EList<EObject> _eContents = _eContainer.eContents();
    Iterable<Extends> _filter = Iterables.<Extends>filter(_eContents, Extends.class);
    for (final Extends ext : _filter) {
      JvmType _extends = ext.getExtends();
      importManager.addImportFor(_extends);
    }
  }
  
  public String getExtends(final PojoEntity e) {
    EObject _eContainer = e.eContainer();
    EList<EObject> _eContents = _eContainer.eContents();
    Iterable<Extends> _filter = Iterables.<Extends>filter(_eContents, Extends.class);
    for (final Extends ext : _filter) {
      JvmType _extends = ext.getExtends();
      return _extends.getSimpleName();
    }
    return "";
  }
  
  public boolean isImplements(final PojoEntity e) {
    EObject _eContainer = e.eContainer();
    EList<EObject> _eContents = _eContainer.eContents();
    Iterable<Implements> _filter = Iterables.<Implements>filter(_eContents, Implements.class);
    for (final Implements ext : _filter) {
      return true;
    }
    return false;
  }
}
