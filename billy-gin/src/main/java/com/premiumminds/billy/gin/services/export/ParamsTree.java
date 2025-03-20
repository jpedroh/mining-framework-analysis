package com.premiumminds.billy.gin.services.export;
import java.util.ArrayList;
import java.util.List;

public class ParamsTree<K extends java.lang.Object, V extends java.lang.Object> {
  private Node<K, V> root;

  public ParamsTree(K rootKey) {
    this(rootKey, null);
  }

  public ParamsTree(K rootKey, V rootValue) {
    this.root = new Node<>(rootKey, rootValue, null);
    this.root.value = rootValue;
    this.root.children = new ArrayList<>();
  }

  public Node<K, V> getRoot() {
    return this.root;
  }

  @Override public String toString() {
    return this.root.toString();
  }

  public static class Node<K extends java.lang.Object, V extends java.lang.Object> {
    private K key;

    private V value;

    private Node<K, V> parent;

    private List<Node<K, V>> children;

    public Node(K key, Node<K, V> parent) {
      this(key, null, parent);
    }

    public Node(K key, V value, Node<K, V> parent) {
      this.key = key;
      this.value = value;
      this.parent = parent;
      this.children = new ArrayList<>();
    }

    public Node<K, V> addChild(K key) {
      return this.addChild(key, null);
    }

    public Node<K, V> addChild(K key, V value) {
      Node<K, V> newBorn = new Node<>(key, value, this);
      this.children.add(newBorn);
      return newBorn;
    }

    public K getKey() {
      return this.key;
    }

    public V getValue() {
      return this.value;
    }

    public Node<K, V> getParent() {
      return this.parent;
    }

    public List<Node<K, V>> getChildren() {
      return this.children;
    }

    @Override public String toString() {
      return this.toString("");
    }

    public boolean hasChildren() {
      return !this.children.isEmpty();
    }

    private String toString(String indentation) {
      String rval = indentation + "[" + this.key.toString() + "]" + (null != this.value ? " - " + this.value.toString() : "") + "\n";
      for (Node<K, V> child : this.children) {
        rval += indentation + child.toString(indentation + " ");
      }
      return rval;
    }
  }
}