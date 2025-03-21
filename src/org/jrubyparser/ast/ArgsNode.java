package org.jrubyparser.ast;
import java.util.ArrayList;
import java.util.List;
import org.jrubyparser.NodeVisitor;
import org.jrubyparser.SourcePosition;

/**
 * Represents the argument declarations of a method.  The fields:
 * foo(p1, ..., pn, o1 = v1, ..., on = v2, *r, q1, ..., qn)
 *
 * p1...pn = pre arguments
 * o1...on = optional arguments
 * r       = rest argument
 * q1...qn = post arguments (only in 1.9)
 */
public class ArgsNode extends Node {
  private ListNode pre;

  private ListNode optional;

  private ListNode post;

  protected ArgumentNode rest;

  private ListNode keywords;

  private KeywordRestArgNode keywordRest;

  private BlockArgNode block;

  /**
     * @param position of the arguments
     * @param pre Required nodes at the beginning of the method definition
     * @param optional Node describing the optional arguments
     * @param rest The rest argument (*args).
     * @param post Required nodes at the end of the method definition
     * @param block An optional block argument (&amp;arg).
     **/
  public ArgsNode(SourcePosition position, ListNode pre, ListNode optional, RestArgNode rest, ListNode post, ListNode keywords, KeywordRestArgNode keywordRest, BlockArgNode block) {
    super(position);
    this.pre = (ListNode) adopt(pre);
    this.optional = (ListNode) adopt(optional);
    this.post = (ListNode) adopt(post);
    this.rest = (ArgumentNode) adopt(rest);
    this.keywords = keywords;
    this.keywordRest = keywordRest;
    this.block = (BlockArgNode) adopt(block);
  }

  /**
     * Checks node for 'sameness' for diffing.
     *
     * @param node to be compared to
     * @return Returns a boolean
     */
  @Override public boolean isSame(Node node) {
    if (!super.isSame(node)) {
      return false;
    }
    List<Node> params = getNormativeParameterList();
    List<Node> otherParams = ((ArgsNode) node).getNormativeParameterList();
    if (params.size() != otherParams.size()) {
      return false;
    }
    for (int i = 0; i <= params.size() - 1; i++) {
      if (!params.get(i).isSame(otherParams.get(i))) {
        return false;
      }
    }
    return true;
  }

  public NodeType getNodeType() {
    return NodeType.ARGSNODE;
  }

  /**
     * Accept for the visitor pattern.
     * @param iVisitor the visitor
     **/
  public Object accept(NodeVisitor iVisitor) {
    return iVisitor.visitArgsNode(this);
  }

  public int getPreCount() {
    return pre == null ? 0 : pre.size();
  }

  public int getOptionalCount() {
    return optional == null ? 0 : optional.size();
  }

  public int getPostCount() {
    return post == null ? 0 : post.size();
  }

  public int getRequiredCount() {
    return getPreCount() + getPostCount();
  }

  public int getMaxArgumentsCount() {
    return getRequiredCount() + getOptionalCount();
  }

  /**
     * Gets the optional Arguments.
     *
     * @return Returns a ListNode
     */
  public ListNode getOptional() {
    return optional;
  }

  public ListNode getPost() {
    return post;
  }

  /**
     * Gets the required arguments at the beginning of the argument definition
     */
  public ListNode getPre() {
    return pre;
  }

  /**
     * Gets the rest node.
     *
     * @return Returns an ArgumentNode
     */
  public ArgumentNode getRest() {
    return rest;
  }

  /**
     * Gets the explicit block argument of the parameter list (&amp;block).
     *
     * @return Returns a BlockArgNode
     */
  public BlockArgNode getBlock() {
    return block;
  }

  /**
     * Return a list of all possible parameter names.  IDE's can use this to generate
     * indexes or use it for parameter hinting.
     *
     * @param namesOnly do not prepend '*', '**', or '&amp;' onto front of special parameters
     */
  public List<String> getNormativeParameterNameList(boolean namesOnly) {
    List<String> parameters = new ArrayList<String>();
    if (getPreCount() > 0) {
      for (Node preArg : getPre().childNodes()) {
        if (preArg instanceof INameNode) {
          parameters.add(((INameNode) preArg).getName());
        }
      }
    }
    if (getOptionalCount() > 0) {
      for (Node optArg : getOptional().childNodes()) {
        if (optArg instanceof INameNode) {
          parameters.add(((INameNode) optArg).getName());
        }
      }
    }
    if (getPostCount() > 0) {
      for (Node postArg : getPost().childNodes()) {
        if (postArg instanceof INameNode) {
          parameters.add(((INameNode) postArg).getName());
        }
      }
    }
    if (getRest() != null) {
      parameters.add(namesOnly ? getRest().getName() : "*" + getRest().getName());
    }
    if (getBlock() != null) {
      parameters.add(namesOnly ? getBlock().getName() : "&" + getBlock().getName());
    }
    return parameters;
  }

  /**
     * @return list of all parameters within this args node
     */
  public List<Node> getNormativeParameterList() {
    List<Node> parameters = new ArrayList<Node>();
    if (getPreCount() > 0) {
      for (Node preArg : getPre().childNodes()) {
        if (preArg instanceof INameNode) {
          parameters.add(preArg);
        }
      }
    }
    if (getOptionalCount() > 0) {
      for (Node optArg : getOptional().childNodes()) {
        if (optArg instanceof INameNode) {
          parameters.add(optArg);
        }
      }
    }
    if (getPostCount() > 0) {
      for (Node postArg : getPost().childNodes()) {
        if (postArg instanceof INameNode) {
          parameters.add(postArg);
        }
      }
    }
    if (getRest() != null) {
      parameters.add(getRest());
    }
    if (getBlock() != null) {
      parameters.add(getBlock());
    }
    return parameters;
  }
}