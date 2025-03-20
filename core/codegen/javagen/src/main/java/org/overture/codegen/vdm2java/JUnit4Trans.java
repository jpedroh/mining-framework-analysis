package org.overture.codegen.vdm2java;
import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.overture.ast.lex.Dialect;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.assistant.NodeAssistantIR;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.config.Settings;
import java.util.LinkedList;
import java.util.List;

public class JUnit4Trans extends DepthFirstAnalysisAdaptor {
  protected Logger log = Logger.getLogger(this.getClass().getName());

  private static final String TEST_MODULE_NAME_PREFIX = "Test";

  private static final String TEST_NAME_PREFIX = "test";

  public static final String TEST_ANNOTATION = "@Test";

  public private static final String 
<<<<<<< /usr/src/app/output/overturetool/overture/1f2767e13541540d05a191d783138dcaba432939/core/codegen/javagen/src/main/java/org/overture/codegen/vdm2java/JUnit4Trans.java/left.java
  ASSERT_FALSE_MSG_METHOD = "assertFalseMsg"
=======
  TEST_SETUP = "setUp"
>>>>>>> /usr/src/app/output/overturetool/overture/1f2767e13541540d05a191d783138dcaba432939/core/codegen/javagen/src/main/java/org/overture/codegen/vdm2java/JUnit4Trans.java/right.java
  ;

  public static final String JUNI4_IMPORT = "org.junit.*";

  public private static final String 
<<<<<<< /usr/src/app/output/overturetool/overture/1f2767e13541540d05a191d783138dcaba432939/core/codegen/javagen/src/main/java/org/overture/codegen/vdm2java/JUnit4Trans.java/left.java
  JUNIT4_ASSERT_TRUE_METHOD = "assertTrue"
=======
  TEST_TEARDOWN = "tearDown"
>>>>>>> /usr/src/app/output/overturetool/overture/1f2767e13541540d05a191d783138dcaba432939/core/codegen/javagen/src/main/java/org/overture/codegen/vdm2java/JUnit4Trans.java/right.java
  ;

  public static final String 
<<<<<<< /usr/src/app/output/overturetool/overture/1f2767e13541540d05a191d783138dcaba432939/core/codegen/javagen/src/main/java/org/overture/codegen/vdm2java/JUnit4Trans.java/left.java
  ASSERT_MODULE = "Assert"
=======
  TEST_SETUP_ANNOTATION = "@Before"
>>>>>>> /usr/src/app/output/overturetool/overture/1f2767e13541540d05a191d783138dcaba432939/core/codegen/javagen/src/main/java/org/overture/codegen/vdm2java/JUnit4Trans.java/right.java
  ;

  public static final String 
<<<<<<< /usr/src/app/output/overturetool/overture/1f2767e13541540d05a191d783138dcaba432939/core/codegen/javagen/src/main/java/org/overture/codegen/vdm2java/JUnit4Trans.java/left.java
  ASSERT_TRUE_MSG_METHOD = "assertTrueMsg"
=======
  TEST_TEARDOWN_ANNOTATION = "@After"
>>>>>>> /usr/src/app/output/overturetool/overture/1f2767e13541540d05a191d783138dcaba432939/core/codegen/javagen/src/main/java/org/overture/codegen/vdm2java/JUnit4Trans.java/right.java
  ;

  public static final String JUNIT4_ASSERT_FALSE_METHOD = "assertFalse";

  public TransAssistantIR assist;

  private JavaCodeGen javaCg;

  private List<SClassDeclIR> classCopies;

  public JUnit4Trans(TransAssistantIR assist, JavaCodeGen javaCg) {
    this.assist = assist;
    this.javaCg = javaCg;
  }

  public ADefaultClassDeclIR findCopy(ADefaultClassDeclIR clazz) {
    for (SClassDeclIR c : classCopies) {
      if (c instanceof ADefaultClassDeclIR && c.getName().equals(clazz.getName())) {
        return (ADefaultClassDeclIR) c;
      }
    }
    return null;
  }

  @Override public void caseADefaultClassDeclIR(ADefaultClassDeclIR node) throws AnalysisException {
    if (!javaCg.getJavaSettings().genJUnit4tests()) {
      return;
    }
    if (classCopies == null) {
      this.classCopies = new LinkedList<>();
      for (SClassDeclIR c : assist.getInfo().getClasses()) {
        if (c instanceof ADefaultClassDeclIR) {
          this.classCopies.add((ADefaultClassDeclIR) c.clone());
        }
      }
    }
    ADefaultClassDeclIR copy = findCopy(node);
    if (copy == null) {
      log.error("Could not find copy of " + node.getName());
    }
    if (!assist.getInfo().getDeclAssistant().isTest(copy, classCopies) && !followsSlTestConvention(copy)) {
      return;
    }
    if (Settings.dialect == Dialect.VDM_SL) {
      adjustTestClass(node);
    }
    removeRunFullSuiteMethod(node);
    if (assist.getInfo().getDeclAssistant().parentIsTest(copy)) {
      node.getSuperNames().clear();
    }
    importJunit4(node);
    addTestAnnotations(node);
  }

  private void adjustTestClass(ADefaultClassDeclIR copy) {
    for (AMethodDeclIR m : copy.getMethods()) {
      m.setStatic(false);
      try {
        m.apply(new DepthFirstAnalysisAdaptor() {
          @Override public void caseAPlainCallStmIR(APlainCallStmIR node) throws AnalysisException {
            STypeIR type = node.getClassType();
            if (type instanceof AClassTypeIR) {
              AClassTypeIR classType = (AClassTypeIR) type;
              if (classType.getName().equals(ASSERT_MODULE)) {
                if (node.getName().equals(ASSERT_FALSE_MSG_METHOD)) {
                  node.setName(JUNIT4_ASSERT_FALSE_METHOD);
                } else {
                  if (node.getName().equals(ASSERT_TRUE_MSG_METHOD)) {
                    node.setName(JUNIT4_ASSERT_TRUE_METHOD);
                  }
                }
              }
            } else {
              log.warn("Expected class type but got: " + type);
            }
          }
        });
      } catch (AnalysisException e) {
        log.error("Got unexpected analysis error: " + e.getMessage());
        e.printStackTrace();
      }
    }
    for (int i = 0; i < copy.getMethods().size(); i++) {
      AMethodDeclIR m = copy.getMethods().get(i);
      if (m.getIsConstructor()) {
        copy.getMethods().remove(i);
        break;
      }
    }
  }

  private boolean followsSlTestConvention(ADefaultClassDeclIR copy) {
    return Settings.dialect == Dialect.VDM_SL && copy.getName().endsWith(TEST_MODULE_NAME_PREFIX);
  }

  public void addTestAnnotations(ADefaultClassDeclIR node) {
    NodeAssistantIR nodeAssist = assist.getInfo().getNodeAssistant();
    for (AMethodDeclIR m : node.getMethods()) {
      if (isTestMethod(m)) {
        nodeAssist.addMetaData(m, str2meta(TEST_ANNOTATION), false);
      } else {
        if (isSetup(m)) {
          nodeAssist.addMetaData(m, str2meta(TEST_SETUP_ANNOTATION), false);
        } else {
          if (isTearDown(m)) {
            nodeAssist.addMetaData(m, str2meta(TEST_TEARDOWN_ANNOTATION), false);
          }
        }
      }
    }
  }

  private boolean isTestMethod(AMethodDeclIR m) {
    return m.getName().startsWith(TEST_NAME_PREFIX) && isJUnitSignature(m);
  }

  private boolean isSetup(AMethodDeclIR m) {
    return m.getName().equals(TEST_SETUP);
  }

  private boolean isTearDown(AMethodDeclIR m) {
    return m.getName().equals(TEST_TEARDOWN);
  }

  private boolean isJUnitSignature(AMethodDeclIR m) {
    return m.getAccess().equals(IRConstants.PUBLIC) && BooleanUtils.isFalse(m.getStatic()) && BooleanUtils.isFalse(m.getIsConstructor()) && m.getFormalParams().isEmpty() && !(m.getTag() instanceof IRGeneratedTag);
  }

  public void importJunit4(ADefaultClassDeclIR node) {
    assist.getInfo().getDeclAssistant().addDependencies(node, str2meta(JUNI4_IMPORT), false);
  }

  public void removeRunFullSuiteMethod(ADefaultClassDeclIR node) {
    for (int i = 0; i < node.getMethods().size(); i++) {
      AMethodDeclIR currentMethod = node.getMethods().get(i);
      if (currentMethod.getName().equals(IRConstants.TEST_CASE_RUN_FULL_SUITE) && currentMethod.getFormalParams().isEmpty()) {
        node.getMethods().remove(i);
        break;
      }
    }
  }

  public List<ClonableString> str2meta(String str) {
    List<ClonableString> extraMetaData = new LinkedList<>();
    extraMetaData.add(new ClonableString(str));
    return extraMetaData;
  }
}