package com.github.javaparser.ast.nodeTypes.modifiers;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithModifiers;
import static com.github.javaparser.ast.Modifier.Keyword.STATIC;

/**
 * A node that can be static.
 */
public interface NodeWithStaticModifier<N extends Node> extends NodeWithModifiers<N> {
  /**
     * @return true, if the modifier {@code static} is explicitly added to this node. If the node is implicitly static
     * without an explicit modifier (e.g. nested records), this method should be overridden.
     */
  default boolean isStatic() {
    return hasModifier(STATIC);
  }

  @SuppressWarnings(value = { "unchecked" }) default N setStatic(boolean set) {
    return setModifier(STATIC, set);
  }
}