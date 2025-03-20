package org.omnifaces.component.tree;
import static javax.faces.component.visit.VisitHint.SKIP_ITERATION;
import static org.omnifaces.util.Components.getClosestParent;
import static org.omnifaces.util.Components.shouldVisitSkipIteration;
import static org.omnifaces.util.Components.validateHasNoChildren;
import static org.omnifaces.util.Components.validateHasParent;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

/**
 * <strong>TreeInsertChildren</strong> is an {@link UIComponent} that represents the insertion point for the
 * children of a parent tree node which is represented by {@link TreeNodeItem}.
 * <p>
 * This component does not allow any children.
 *
 * @author Bauke Scholtz
 * @see TreeNodeItem
 */
@FacesComponent(value = TreeInsertChildren.COMPONENT_TYPE) public class TreeInsertChildren extends TreeFamily {
  /** The standard component type. */
  public static final String COMPONENT_TYPE = "org.omnifaces.component.tree.TreeInsertChildren";

  /**
	 * Validate the component hierarchy.
	 * @throws IllegalArgumentException When there is no parent of type {@link TreeNodeItem}, or when there are any
	 * children.
	 */
  @Override protected void validateHierarchy() {
    validateHasParent(this, TreeNodeItem.class);
    validateHasNoChildren(this);
  }

  /**
	 * Delegate processing of the tree node to {@link Tree#processTreeNode(FacesContext, PhaseId)}.
	 * @see Tree#processTreeNode(FacesContext, PhaseId)
	 */
  @Override protected void process(FacesContext context, PhaseId phaseId) {
    getClosestParent(this, Tree.class).processTreeNode(context, phaseId);
  }

  /**
	 * Delegate visiting of the tree node to {@link Tree#visitTreeNode(VisitContext, VisitCallback)}.
	 * @see Tree#visitTreeNode(VisitContext, VisitCallback)
	 */
  @Override public boolean visitTree(VisitContext context, VisitCallback callback) {
    if (
<<<<<<< /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/component/tree/TreeInsertChildren.java/left.java
    shouldVisitSkipIteration(context)
=======
    context.getHints().contains(SKIP_ITERATION)
>>>>>>> /usr/src/app/output/omnifaces/omnifaces/54a7f76c73dc469f57484ebdcdf2203dc9f3a193/src/main/java/org/omnifaces/component/tree/TreeInsertChildren.java/right.java
    ) {
      return super.visitTree(context, callback);
    }
    return getClosestParent(this, Tree.class).visitTreeNode(context, callback);
  }
}