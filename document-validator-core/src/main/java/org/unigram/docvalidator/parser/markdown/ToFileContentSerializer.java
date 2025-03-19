package org.unigram.docvalidator.parser.markdown;
import org.parboiled.common.StringUtils;
import org.pegdown.Printer;
import org.pegdown.ast.AbbreviationNode;
import org.pegdown.ast.AutoLinkNode;
import org.pegdown.ast.BlockQuoteNode;
import org.pegdown.ast.BulletListNode;
import org.pegdown.ast.CodeNode;
import org.pegdown.ast.DefinitionListNode;
import org.pegdown.ast.DefinitionNode;
import org.pegdown.ast.DefinitionTermNode;
import org.pegdown.ast.ExpImageNode;
import org.pegdown.ast.ExpLinkNode;
import org.pegdown.ast.HeaderNode;
import org.pegdown.ast.HtmlBlockNode;
import org.pegdown.ast.InlineHtmlNode;
import org.pegdown.ast.ListItemNode;
import org.pegdown.ast.MailLinkNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.OrderedListNode;
import org.pegdown.ast.ParaNode;
import org.pegdown.ast.QuotedNode;
import org.pegdown.ast.RefImageNode;
import org.pegdown.ast.RefLinkNode;
import org.pegdown.ast.ReferenceNode;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SimpleNode;
import org.pegdown.ast.SpecialTextNode;
import org.pegdown.ast.StrikeNode;
import org.pegdown.ast.StrongEmphSuperNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.TableBodyNode;
import org.pegdown.ast.TableCaptionNode;
import org.pegdown.ast.TableCellNode;
import org.pegdown.ast.TableColumnNode;
import org.pegdown.ast.TableHeaderNode;
import org.pegdown.ast.TableNode;
import org.pegdown.ast.TableRowNode;
import org.pegdown.ast.TextNode;
import org.pegdown.ast.VerbatimNode;
import org.pegdown.ast.Visitor;
import org.pegdown.ast.WikiLinkNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unigram.docvalidator.parser.SentenceExtractor;
import org.unigram.docvalidator.store.FileContent;
import org.unigram.docvalidator.store.Paragraph;
import org.unigram.docvalidator.store.Section;
import org.unigram.docvalidator.store.Sentence;
import org.unigram.docvalidator.util.DocumentValidatorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.parboiled.common.Preconditions.checkArgNotNull;

/**
 * Using Pegdown Parser. <br/>
 *
 * @see https://github.com/sirthias/pegdown
 */
public class ToFileContentSerializer implements Visitor {
  private static final Logger LOG = LoggerFactory.getLogger(ToFileContentSerializer.class);

  private FileContent fileContent = null;

  private SentenceExtractor sentenceExtractor;

  private final Map<String, ReferenceNode> references = new HashMap<String, ReferenceNode>();

  private final Map<String, String> abbreviations = new HashMap<String, String>();

  private int itemDepth = 0;

  private Section currentSection = null;

  protected void visitChildren(SuperNode node) {
    for (Node child : node.getChildren()) {
      child.accept(this);
    }
  }

  private List<Integer> lineList = null;

  private String period;

  private List<CandidateSentence> candidateSentences = new ArrayList<CandidateSentence>();

  /**
   * Constructor.
   *
   * @param content          FileContent
   * @param listOfLineNumber the list of line number
   * @param extractor        utility object to extract a sentence list
   */
  public ToFileContentSerializer(FileContent content, List<Integer> listOfLineNumber, SentenceExtractor extractor) {
    this.fileContent = content;
    this.lineList = listOfLineNumber;
    this.sentenceExtractor = extractor;
    currentSection = fileContent.getLastSection();
  }

  /**
   * Traverse markdown tree that parsed Pegdown.
   *
   * @param astRoot Pegdown RootNode
   *                (markdown tree that is parsed pegdown parser)
   * @return file content that re-parse Pegdown RootNode.
   * @throws org.unigram.docvalidator.util.DocumentValidatorException
   * Fail to traverse markdown tree
   */
  public FileContent toFileContent(RootNode astRoot) throws DocumentValidatorException {
    try {
      checkArgNotNull(astRoot, "astRoot");
      astRoot.accept(this);
    } catch (Throwable e) {
      LOG.error("Fail to traverse RootNode.");
      throw new DocumentValidatorException("Fail to traverse RootNode.", e);
    }
    return fileContent;
  }

  private void fixSentence() {
    List<Sentence> sentences = createSentenceList();
    for (Sentence sentence : sentences) {
      currentSection.appendSentence(sentence);
    }
  }

  private void addCandidateSentence(int lineNum, String text) {
    addCandidateSentence(lineNum, text, null);
  }

  private void addCandidateSentence(int lineNum, String text, String link) {
    candidateSentences.add(new CandidateSentence(lineNum, text, link));
  }

  private int lineNumberFromStartIndex(int startIndex) {
    int lineNum = 0;
    for (int end : lineList) {
      if (startIndex < end) {
        break;
      }
      lineNum++;
    }
    return lineNum;
  }

  private Printer printer = new Printer();

  private String printChildrenToString(SuperNode node) {
    Printer priorPrinter = printer;
    printer = new Printer();
    visitChildren(node);
    String result = printer.getString();
    printer = priorPrinter;
    return result;
  }

  private List<Sentence> createSentenceList() {
    List<Sentence> newSentences = new ArrayList<Sentence>();
    Sentence currentSentence = null;
    StringBuffer sentenceContent = new StringBuffer();
    for (CandidateSentence candidateSentence : candidateSentences) {
      String remain = sentenceExtractor.extractWithoutLastSentence(candidateSentence.getSentence(), newSentences, candidateSentence.getLineNum());
      if (StringUtils.isNotEmpty(remain)) {
        if (currentSentence != null) {
          currentSentence.content += candidateSentence.getSentence();
        } else {
          currentSentence = new Sentence(remain, candidateSentence.getLineNum());
          newSentences.add(currentSentence);
        }
        if (candidateSentence.getLink() != null) {
          currentSentence.links.add(candidateSentence.getLink());
        }
      }
      if (sentenceExtractor.getSentenceEndPosition(currentSentence.content) != -1) {
        currentSentence = null;
      }
    }
    candidateSentences.clear();
    return newSentences;
  }

  private boolean addChild(Section candidate, Section child) {
    if (candidate.getLevel() < child.getLevel()) {
      candidate.appendSubSection(child);
      child.setParentSection(candidate);
    } else {
      Section parent = candidate.getParentSection();
      while (parent != null) {
        if (parent.getLevel() < child.getLevel()) {
          parent.appendSubSection(child);
          child.setParentSection(parent);
          break;
        }
        parent = parent.getParentSection();
      }
      if (parent == null) {
        return false;
      }
    }
    return true;
  }

  private void appendSection(HeaderNode headerNode) {
    fixSentence();
    visitChildren(headerNode);
    List<Sentence> headerContents = createSentenceList();
    if (headerContents.size() > 0) {
      headerContents.get(0).isFirstSentence = true;
    }
    Section newSection = new Section(headerNode.getLevel(), headerContents);
    fileContent.appendSection(newSection);
    if (!addChild(currentSection, newSection)) {
      LOG.warn("Failed to add parent for a Section: " + newSection.getHeaderContents().next());
    }
    currentSection = newSection;
  }

  @Override public void visit(AbbreviationNode abbreviationNode) {
  }

  @Override public void visit(AutoLinkNode autoLinkNode) {
    addCandidateSentence(lineNumberFromStartIndex(autoLinkNode.getStartIndex()), autoLinkNode.getText(), autoLinkNode.getText());
  }

  @Override public void visit(BlockQuoteNode blockQuoteNode) {
    visitChildren(blockQuoteNode);
  }

  @Override public void visit(CodeNode codeNode) {
    addCandidateSentence(lineNumberFromStartIndex(codeNode.getStartIndex()), codeNode.getText());
  }

  @Override public void visit(ExpImageNode expImageNode) {
  }

  @Override public void visit(ExpLinkNode expLinkNode) {
    String linkName = printChildrenToString(expLinkNode);
    CandidateSentence lastCandidateSentence = candidateSentences.get(candidateSentences.size() - 1);
    lastCandidateSentence.setLink(expLinkNode.url);
  }

  @Override public void visit(HeaderNode headerNode) {
    appendSection(headerNode);
  }

  @Override public void visit(BulletListNode bulletListNode) {
    if (itemDepth == 0) {
      fixSentence();
      currentSection.appendListBlock();
    } else {
      List<Sentence> sentences = createSentenceList();
      currentSection.appendListElement(itemDepth, sentences);
    }
    itemDepth++;
    visitChildren(bulletListNode);
    itemDepth--;
  }

  @Override public void visit(OrderedListNode orderedListNode) {
    if (itemDepth == 0) {
      fixSentence();
      currentSection.appendListBlock();
    } else {
      List<Sentence> sentences = createSentenceList();
      currentSection.appendListElement(itemDepth, sentences);
    }
    itemDepth++;
    visitChildren(orderedListNode);
    itemDepth--;
  }

  @Override public void visit(ListItemNode listItemNode) {
    visitChildren(listItemNode);
    List<Sentence> sentences = createSentenceList();
    if (sentences != null && sentences.size() > 0) {
      currentSection.appendListElement(itemDepth, sentences);
    }
  }

  @Override public void visit(ParaNode paraNode) {
    currentSection.appendParagraph(new Paragraph());
    visitChildren(paraNode);
    fixSentence();
  }

  @Override public void visit(RootNode rootNode) {
    for (ReferenceNode refNode : rootNode.getReferences()) {
    }
    for (AbbreviationNode abbrNode : rootNode.getAbbreviations()) {
    }
    visitChildren(rootNode);
  }

  @Override public void visit(SimpleNode simpleNode) {
    switch (simpleNode.getType()) {
      case Linebreak:
      break;
      case Nbsp:
      break;
      case HRule:
      break;
      case Apostrophe:
      addCandidateSentence(lineNumberFromStartIndex(simpleNode.getStartIndex()), "\'");
      break;
      case Ellipsis:
      addCandidateSentence(lineNumberFromStartIndex(simpleNode.getStartIndex()), "...");
      break;
      case Emdash:
      addCandidateSentence(lineNumberFromStartIndex(simpleNode.getStartIndex()), "\u2013");
      break;
      case Endash:
      addCandidateSentence(lineNumberFromStartIndex(simpleNode.getStartIndex()), "\u2014");
      break;
      default:
      LOG.warn("Illegal SimpleNode:[" + simpleNode.toString() + "]");
    }
  }

  @Override public void visit(SpecialTextNode specialTextNode) {
    addCandidateSentence(lineNumberFromStartIndex(specialTextNode.getStartIndex()), specialTextNode.getText());
  }

  @Override public void visit(StrikeNode strikeNode) {
    visitChildren(strikeNode);
  }

  @Override public void visit(StrongEmphSuperNode strongEmphSuperNode) {
    visitChildren(strongEmphSuperNode);
  }

  @Override public void visit(TextNode textNode) {
    addCandidateSentence(lineNumberFromStartIndex(textNode.getStartIndex()), textNode.getText());
    printer.print(textNode.getText());
  }

  @Override public void visit(VerbatimNode verbatimNode) {
  }

  @Override public void visit(QuotedNode quotedNode) {
  }

  @Override public void visit(ReferenceNode referenceNode) {
  }

  @Override public void visit(RefImageNode refImageNode) {
  }

  @Override public void visit(RefLinkNode refLinkNode) {
    String linkName = printChildrenToString(refLinkNode);
    String url = getRefLinkUrl(refLinkNode.referenceKey, linkName);
    CandidateSentence lastCandidateSentence = candidateSentences.get(candidateSentences.size() - 1);
    if (StringUtils.isNotEmpty(url)) {
      lastCandidateSentence.setLink(url);
    } else {
      lastCandidateSentence.setSentence("[" + lastCandidateSentence.getSentence() + "]");
    }
  }

  private String getRefLinkUrl(SuperNode referenceKey, String linkName) {
    ReferenceNode refNode = references.get(linkName);
    StringBuilder sb = new StringBuilder();
    if (refNode != null) {
      sb.append(refNode.getUrl());
    }
    return sb.toString();
  }

  @Override public void visit(HtmlBlockNode htmlBlockNode) {
  }

  @Override public void visit(InlineHtmlNode inlineHtmlNode) {
  }

  @Override public void visit(MailLinkNode mailLinkNode) {
  }

  @Override public void visit(WikiLinkNode wikiLinkNode) {
  }

  @Override public void visit(SuperNode superNode) {
    visitChildren(superNode);
  }

  @Override public void visit(Node node) {
  }

  @Override public void visit(DefinitionListNode definitionListNode) {
  }

  @Override public void visit(DefinitionNode definitionNode) {
  }

  @Override public void visit(DefinitionTermNode definitionTermNode) {
  }

  @Override public void visit(TableBodyNode tableBodyNode) {
  }

  @Override public void visit(TableCaptionNode tableCaptionNode) {
  }

  @Override public void visit(TableCellNode tableCellNode) {
  }

  @Override public void visit(TableColumnNode tableColumnNode) {
  }

  @Override public void visit(TableHeaderNode tableHeaderNode) {
  }

  @Override public void visit(TableNode tableNode) {
  }

  @Override public void visit(TableRowNode tableRowNode) {
  }
}