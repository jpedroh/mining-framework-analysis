package org.sonar.plugins.web.checks.sonar;
import org.sonar.check.Rule;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;

@Rule(key = "BoldAndItalicTagsCheck") public class BoldAndItalicTagsCheck extends AbstractPageCheck {
  @Override public void startElement(TagNode node) {
    if (isBold(node)) {
      createViolation(node.getStartLinePosition(), "Replace this <" + node.getNodeName() + "> tag by <strong>.");
    } else {
      if (isItalicAndNotAriaHidden(node)) {
        createViolation(node.getStartLinePosition(), "Replace this <" + node.getNodeName() + "> tag by <em>.");
      }
    }
  }

  private static boolean isBold(TagNode node) {
    return "B".equalsIgnoreCase(node.getNodeName());
  }

  /**
   * Check if node is an italic tag and attribute 'aria-hidden' is not true.
   * Rule should be relaxed in this case ; <a href="https://www.w3.org/WAI/GL/wiki/Using_aria-hidden%3Dtrue_on_an_icon_font_that_AT_should_ignore">icon font</a> usage.
   * @param node The current HTML start tag
   * @return true if violation should be applied, false otherwise
   */
  private static boolean isItalicAndNotAriaHidden(TagNode node) {
    return "I".equalsIgnoreCase(node.getNodeName()) && !"true".equalsIgnoreCase(node.getAttribute("aria-hidden"));
  }
}