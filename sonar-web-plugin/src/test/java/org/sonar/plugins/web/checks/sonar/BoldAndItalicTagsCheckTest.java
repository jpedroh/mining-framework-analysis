package org.sonar.plugins.web.checks.sonar;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.plugins.web.checks.CheckMessagesVerifierRule;
import org.sonar.plugins.web.checks.TestHelper;
import org.sonar.plugins.web.visitor.WebSourceCode;

public class BoldAndItalicTagsCheckTest {
  @Rule public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test public void detected() throws Exception {
    WebSourceCode sourceCode = TestHelper.scan(new File("src/test/resources/checks/BoldAndItalicTagsCheck.html"), new BoldAndItalicTagsCheck());
    checkMessagesVerifier.verify(sourceCode.getIssues()).next().atLine(1).withMessage("Replace this <b> tag by <strong>.").next().atLine(5).withMessage("Replace this <i> tag by <em>.").next().atLine(7).withMessage("Replace this <B> tag by <strong>.").next().atLine(11).withMessage("Replace this <i> tag by <em>.").next().atLine(17).withMessage("Replace this <i> tag by <em>.").next().atLine(19).withMessage("Replace this <i> tag by <em>.").next().atLine(21).withMessage("Replace this <i> tag by <em>.");
  }
}