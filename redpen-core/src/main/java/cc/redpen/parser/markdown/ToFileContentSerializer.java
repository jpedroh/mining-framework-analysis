/**
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.redpen.parser.markdown;

import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.model.Section;
import cc.redpen.model.Sentence;
import cc.redpen.parser.SentenceExtractor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.parboiled.common.StringUtils;
import org.pegdown.Printer;
import org.pegdown.ast.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.parboiled.common.Preconditions.checkArgNotNull;


/**
 * Using Pegdown Parser. <br/>
 *
 * @see <a herf="https://github.com/sirthias/pegdown">pegdown</a>
 */
public class ToFileContentSerializer implements Visitor {
  private static final Logger LOG =
      LoggerFactory.getLogger(ToFileContentSerializer.class);

  private final Map<String, ReferenceNode> references = new HashMap<String, ReferenceNode>();

  private final Map<String, String> abbreviations = new HashMap<String, String>();

  private DocumentCollection.Builder builder = null;

  private SentenceExtractor sentenceExtractor;

  private int itemDepth = 0;

  private List<Integer> lineList = null;

  // TODO multi period character not supported
  // TODO multi period character not supported
  private String period;

  private List<CandidateSentence> candidateSentences = new ArrayList<CandidateSentence>();

  private Printer printer = new Printer();

  /**
   * Constructor.
   *
   * @param docBuilder       DocumentBuilder
   * @param listOfLineNumber the list of line number
   * @param extractor        utility object to extract a sentence list
   */
  public ToFileContentSerializer(DocumentCollection.Builder docBuilder, List<Integer> listOfLineNumber, SentenceExtractor extractor) {
    this.builder = docBuilder;
    this.lineList = listOfLineNumber;
    this.sentenceExtractor = extractor;
  }

  protected void visitChildren(SuperNode node) {
    for (Node child : node.getChildren()) {
      child.accept(this);
    }
  }

  /**
   * Traverse markdown tree that parsed Pegdown.
   *
   * @param astRoot Pegdown RootNode
   *                (markdown tree that is parsed pegdown parser)
   * @return file content that re-parse Pegdown RootNode.
   * @throws cc.redpen.DocumentValidatorException
   * Fail to traverse markdown tree
   */
  public Document toFileContent(RootNode astRoot) throws RedPenException {
    try {
      checkArgNotNull(astRoot, "astRoot");
      astRoot.accept(this);
    } catch (java.lang.NullPointerException e) {
      LOG.error("Fail to traverse RootNode.");
      throw new RedPenException("Fail to traverse RootNode.", e);
    }
    return builder.getLastDocument();
  }

  private void fixSentence() {
    // 1. remain sentence append currentSection
    //TODO need line number
    List<Sentence> sentences = createSentenceList();
    for (Sentence sentence : sentences) {
      builder.addSentence(sentence);
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
    // TODO test
    for (int end : lineList) {
      if (startIndex < end) {
        break;
      }
      lineNum++;
    }
    return lineNum;
  }

  private String printChildrenToString(SuperNode node) {
    // FIXME validate usecase
    Printer priorPrinter = printer;
    printer = new Printer();
    visitChildren(node);
    String result = printer.getString();
    printer = priorPrinter;
    return result;
  }

  private List<Sentence> createSentenceList() {
    List<Sentence> newSentences = new ArrayList<>();
    String remainStr = "";
    Sentence currentSentence = null;
    List<String> remainLinks = new ArrayList<>();
    int lineNum = -1;
    for (CandidateSentence candidateSentence : candidateSentences) {
      lineNum = candidateSentence.getLineNum();
      // extract sentences in input line
      List<Sentence> currentSentences = new ArrayList<>();
      remainStr = sentenceExtractor.extract(remainStr + candidateSentence.getSentence(), currentSentences, lineNum);
      if (currentSentences.size() > 0) {
        currentSentence = addExtractedSentences(newSentences, remainLinks, currentSentences);
        remainLinks = new ArrayList<>();
      }
      if (candidateSentence.getLink() == null) {
        continue;
      }
      if (currentSentence != null) {
        currentSentence.links.add(candidateSentence.getLink());
      } else {
        remainLinks.add(candidateSentence.getLink());
      }
    }
    // for remaining
    if (remainStr.length() > 0) {
      newSentences.add(new Sentence(remainStr, lineNum));
    }
    candidateSentences.clear();
    return newSentences;
  }

  private Sentence addExtractedSentences(List<Sentence> newSentences, List<String> remainLinks, List<Sentence> currentSentences) {
    Sentence currentSentence;
    newSentences.addAll(currentSentences);
    currentSentence = currentSentences.get(currentSentences.size() - 1);
    for (String remainLink : remainLinks) {
      currentSentence.links.add(remainLink);
    }
    return currentSentence;
  }

  //FIXME wikiparser have same method. pull up or expand to utils
  private boolean addChild(Section candidate, Section child) {
    if (candidate.getLevel() < child.getLevel()) {
      candidate.appendSubSection(child);
      child.setParentSection(candidate);
    } else { // search parent
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
    // 1. remain sentence flush to current section
    fixSentence();
    // 2. retrieve children for header content create;
    visitChildren(headerNode);
    List<Sentence> headerContents = createSentenceList();
    // To deal with a header content as a paragraph
    if (headerContents.size() > 0) {
      headerContents.get(0).isFirstSentence = true;
    }
    // 3. create new Section
    Section currentSection = builder.getLastSection();
    builder.addSection(headerNode.getLevel(), headerContents);
    // FIXME move this validate process to addChild
    if (!addChild(currentSection, builder.getLastSection())) {
      LOG.warn("Failed to add parent for a Section: " + builder.getLastSection().getHeaderContents().get(0));
    }
  }

  @Override
  public void visit(AbbreviationNode abbreviationNode) {
    // current not implement
  }

  @Override
  public void visit(AutoLinkNode autoLinkNode) {
    // TODO GitHub Markdown Extension
    addCandidateSentence(lineNumberFromStartIndex(autoLinkNode.getStartIndex()), autoLinkNode.getText(), autoLinkNode.getText());
  }

  @Override
  public void visit(BlockQuoteNode blockQuoteNode) {
    visitChildren(blockQuoteNode);
  }

  @Override
  public void visit(CodeNode codeNode) {
    addCandidateSentence(lineNumberFromStartIndex(codeNode.getStartIndex()), codeNode.getText());
  }

  @Override
  public void visit(ExpImageNode expImageNode) {
    // TODO exp image not implement
  }

  @Override
  public void visit(ExpLinkNode expLinkNode) {
    // title attribute don't use
    String linkName = printChildrenToString(expLinkNode);
    // FIXME how to handle url, if linkName includes period character?
    // TODO temporary implementation
    CandidateSentence lastCandidateSentence = candidateSentences.get(candidateSentences.size() - 1);
    lastCandidateSentence.setLink(expLinkNode.url);
  }

  @Override
  public void visit(HeaderNode headerNode) {
    appendSection(headerNode);
  }

  // list part
  @Override
  public void visit(BulletListNode bulletListNode) {
    // FIXME test and validate
    // TODO handle bulletListNode and orderdListNode
    if (itemDepth == 0) {
      fixSentence();
      builder.addListBlock();
    } else {
      List<Sentence> sentences = createSentenceList();
      builder.addListElement(itemDepth, sentences);
    }
    itemDepth++;
    visitChildren(bulletListNode);
    itemDepth--;
  }

  @Override
  public void visit(OrderedListNode orderedListNode) {
    // TODO handle bulletListNode and orderdListNode
    if (itemDepth == 0) {
      fixSentence();
      builder.addListBlock();
    } else {
      List<Sentence> sentences = createSentenceList();
      builder.addListElement(itemDepth, sentences);
    }
    itemDepth++;
    visitChildren(orderedListNode);
    itemDepth--;
  }

  @Override
  public void visit(ListItemNode listItemNode) {
    visitChildren(listItemNode);
    List<Sentence> sentences = createSentenceList();
    // TODO for nested ListNode process
    if ((sentences != null) && (sentences.size() > 0)) {
      builder.addListElement(itemDepth, sentences);
    }
  }

  @Override
  public void visit(ParaNode paraNode) {
    builder.addParagraph();
    visitChildren(paraNode);
    fixSentence();
  }

  @Override
  public void visit(RootNode rootNode) {
    // create refNode reference map
    for (ReferenceNode refNode : rootNode.getReferences()) {
      // visitChildren(refNode);
      // TODO need to decide reference node handling
    }
    // create abbrNode reference map
    for (AbbreviationNode abbrNode : rootNode.getAbbreviations()) {
      // visitChildren(abbrNode);
      // TODO need to decide abbreviation node handling
    }
    visitChildren(rootNode);
  }

  @Override
  public void visit(SimpleNode simpleNode) {
    //TODO validate detail
    switch (simpleNode.getType()) {
      case Linebreak :
        break;
      case Nbsp :
        break;
      case HRule :
        break;
      case Apostrophe :
        addCandidateSentence(lineNumberFromStartIndex(simpleNode.getStartIndex()), "'");
        break;
      case Ellipsis :
        addCandidateSentence(lineNumberFromStartIndex(simpleNode.getStartIndex()), "...");
        break;
      case Emdash :
        addCandidateSentence(lineNumberFromStartIndex(simpleNode.getStartIndex()), "–");
        break;
      case Endash :
        addCandidateSentence(lineNumberFromStartIndex(simpleNode.getStartIndex()), "—");
        break;
      default :
        LOG.warn(("Illegal SimpleNode:[" + simpleNode.toString()) + "]");
    }
  }

  @Override
  public void visit(SpecialTextNode specialTextNode) {
    // TODO to sentence
    addCandidateSentence(lineNumberFromStartIndex(specialTextNode.getStartIndex()), specialTextNode.getText());
  }

  @Override
  public void visit(StrikeNode strikeNode) {
    visitChildren(strikeNode);
  }

  @Override
  public void visit(StrongEmphSuperNode strongEmphSuperNode) {
    visitChildren(strongEmphSuperNode);
  }

  @Override
  public void visit(TextNode textNode) {
    // to sentence, if sentence breaker appear
    // append remain sentence, if sentence breaker not appear
    addCandidateSentence(lineNumberFromStartIndex(textNode.getStartIndex()), textNode.getText());
    // for printChildrenToString
    printer.print(textNode.getText());
  }

  // code block
  @Override
  public void visit(VerbatimNode verbatimNode) {
    // paragraph?
    // FIXME implement
    // TODO remove tag
  }

  @Override
  public void visit(QuotedNode quotedNode) {
    // TODO quoted not implement
  }

  @Override
  public void visit(ReferenceNode referenceNode) {
    // TODO reference node not implement
  }

  @Override
  public void visit(RefImageNode refImageNode) {
    // TODO reference image require implement
    // to expand sentence
  }

  @Override
  public void visit(RefLinkNode refLinkNode) {
    // TODO reference link require implement
    // to expand sentence
    String linkName = printChildrenToString(refLinkNode);
    String url = getRefLinkUrl(refLinkNode.referenceKey, linkName);
    // FIXME how to handle url, if linkName include period character?
    // TODO temporary implementation
    CandidateSentence lastCandidateSentence = candidateSentences.get(candidateSentences.size() - 1);
    if (StringUtils.isNotEmpty(url)) {
      lastCandidateSentence.setLink(url);
    } else {
      lastCandidateSentence.setSentence(("[" + lastCandidateSentence.getSentence()) + "]");
    }
  }

  private String getRefLinkUrl(SuperNode referenceKey, String linkName) {
    //FIXME need to implement
    ReferenceNode refNode = references.get(linkName);
    StringBuilder sb = new StringBuilder();
    if (refNode != null) {
      sb.append(refNode.getUrl());
    }
    return sb.toString();
  }

  // html part
  @Override
  public void visit(HtmlBlockNode htmlBlockNode) {
    // TODO html block not implement
  }

  @Override
  public void visit(InlineHtmlNode inlineHtmlNode) {
    // TODO inline html not implement
  }

  @Override
  public void visit(MailLinkNode mailLinkNode) {
    // TODO mail link not implement.
  }

  @Override
  public void visit(WikiLinkNode wikiLinkNode) {
    // TODO not supported
    // no handle
  }

  @Override
  public void visit(SuperNode superNode) {
    visitChildren(superNode);
  }

  @Override
  public void visit(Node node) {
    // not necessary implement, for pegdown parser plugin
  }

  // handle definition list
  @Override
  public void visit(DefinitionListNode definitionListNode) {
    // TODO dl tag not implement
  }

  @Override
  public void visit(DefinitionNode definitionNode) {
    // TODO dt tag not implement
  }

  @Override
  public void visit(DefinitionTermNode definitionTermNode) {
    // TODO dd tag not implement
  }

  // handle Table contents
  // current not implemented
  @Override
  public void visit(TableBodyNode tableBodyNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableCaptionNode tableCaptionNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableCellNode tableCellNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableColumnNode tableColumnNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableHeaderNode tableHeaderNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableNode tableNode) {
    // TODO not implement
  }

  @Override
  public void visit(TableRowNode tableRowNode) {
    // TODO not implement
  }
}