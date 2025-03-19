package jmetal.util.avl;

/**
 * Created with IntelliJ IDEA.
 * User: Antonio J. Nebro
 * Date: 08/07/13
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */
public class AvlNode<T extends java.lang.Object> {
  private AvlNode<T> left_;

  private AvlNode<T> right_;

  private AvlNode<T> parent_;

  private int height_;

  private AvlNode<T> closestNode_;

  private T item_;

  /**
   * Constructor
   *
   * @param item_
   */
  public AvlNode(T item_) {
    this.left_ = null;
    this.right_ = null;
    this.parent_ = null;
    height_ = 0;
    closestNode_ = null;
    this.item_ = item_;
  }

  public AvlNode<T> getLeft() {
    return left_;
  }

  public void setLeft(AvlNode<T> left) {
    this.left_ = left;
  }

  public AvlNode<T> getParent() {
    return parent_;
  }

  public void setParent(AvlNode<T> parent) {
    this.parent_ = parent;
  }

  public AvlNode<T> getRight() {
    return right_;
  }

  public void setRight(AvlNode<T> right) {
    this.right_ = right;
  }

  public T getItem() {
    return item_;
  }

  public void setItem(T item) {
    this.item_ = item;
  }

  public int getHeight() {
    return height_;
  }

  public void setHeight(int height) {
    this.height_ = height;
  }

  public void updateHeight() {
    if (!hasLeft() && !hasRight()) {
      height_ = 0;
    } else {
      if (!hasRight()) {
        height_ = 1 + getLeft().getHeight();
      } else {
        if (!hasLeft()) {
          height_ = 1 + getRight().getHeight();
        } else {
          height_ = 1 + Math.max(getLeft().getHeight(), getRight().getHeight());
        }
      }
    }
  }

  public AvlNode<T> getClosestNode() {
    return closestNode_;
  }

  public void setClosestNode_(AvlNode<T> closestNode) {
    this.closestNode_ = closestNode;
  }

  public boolean hasParent() {
    return parent_ != null;
  }

  public boolean hasLeft() {
    return left_ != null;
  }

  public boolean hasRight() {
    return right_ != null;
  }

  public boolean isLeaf() {
    return 
<<<<<<< /usr/src/app/output/jmetal/jmetal/e4779bb8d29bc4ce6f4e0f0df52062a7992e5423/src/main/java/jmetal/util/avl/AvlNode.java/left.java
    !(hasLeft() || hasRight())
=======
    (!hasLeft() && !hasRight())
>>>>>>> /usr/src/app/output/jmetal/jmetal/e4779bb8d29bc4ce6f4e0f0df52062a7992e5423/src/main/java/jmetal/util/avl/AvlNode.java/right.java
    ;
  }

  public boolean hasOnlyALeftChild() {
    return 
<<<<<<< /usr/src/app/output/jmetal/jmetal/e4779bb8d29bc4ce6f4e0f0df52062a7992e5423/src/main/java/jmetal/util/avl/AvlNode.java/left.java
    hasLeft() && !hasRight()
=======
    (hasLeft() && !hasRight())
>>>>>>> /usr/src/app/output/jmetal/jmetal/e4779bb8d29bc4ce6f4e0f0df52062a7992e5423/src/main/java/jmetal/util/avl/AvlNode.java/right.java
    ;
  }

  public boolean hasOnlyARightChild() {
    return 
<<<<<<< /usr/src/app/output/jmetal/jmetal/e4779bb8d29bc4ce6f4e0f0df52062a7992e5423/src/main/java/jmetal/util/avl/AvlNode.java/left.java
    hasRight() && !hasLeft()
=======
    (hasRight() && !hasLeft())
>>>>>>> /usr/src/app/output/jmetal/jmetal/e4779bb8d29bc4ce6f4e0f0df52062a7992e5423/src/main/java/jmetal/util/avl/AvlNode.java/right.java
    ;
  }
}