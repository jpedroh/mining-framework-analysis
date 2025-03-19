package org.btrplace.model.view.network;
import org.btrplace.Copyable;
import org.btrplace.model.Node;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A routing must be associated to a network view, it provides methods to get the path between two nodes and the
 * corresponding maximal bandwidth available on the path.
 * It can be either physical L2 or logical L3 routing depending on the desired implementation.
 * 
 * Note: A routing should be first associated to a network view, see {@link #setNetwork(Network)}.
 * 
 * @author Vincent Kherbache
 * @see #setNetwork(Network)
 */
public abstract class Routing implements Copyable<Routing> {
  protected Network net;

  protected LinkedHashMap<Link, Boolean>[][] routingCache;

  /**
     * Set the network view (recursively).
     */
  public void setNetwork(Network net) {
    this.net = net;
    if (net.getRouting() != this) {
      net.setRouting(this);
    }
  }

  /**
     * Get the path between two nodes.
     *
     * @param n1    the source node
     * @param n2    the destination node
     * @return the path consisting of an ordered list of links
     */
  public abstract List<Link> getPath(Node n1, Node n2);

  /**
     * Get the maximal bandwidth available between two nodes.
     *
     * @param n1    the source node
     * @param n2    the destination node
     * @return  the bandwidth
     */
  public int getMaxBW(Node n1, Node n2) {
    int max = Integer.MAX_VALUE;
    for (Link inf : getPath(n1, n2)) {
      if (inf.getCapacity() < max) {
        max = inf.getCapacity();
      }
      Switch sw = inf.getSwitch();
      if (sw.getCapacity() >= 0 && sw.getCapacity() < max) {
        max = sw.getCapacity();
      }
    }
    return max;
  }

  /**
     * Recursive method to get the first physical path found from a switch to a destination node.
     *
     * @param currentPath the current or initial path containing the link(s) crossed
     * @param sw          the current switch to browse
     * @param dst         the destination node to reach
     * @return the ordered list of links that make the path to dst
     */
  protected List<Link> getFirstPhysicalPath(List<Link> currentPath, Switch sw, Node dst) {
    for (Link l : net.getConnectedLinks(sw)) {
      if (currentPath.contains(l)) {
        continue;
      }
      currentPath.add(l);
      if (l.getElement() instanceof Node) {
        if (l.getElement().equals(dst)) {
          return currentPath;
        }
      } else {
        List<Link> recall = getFirstPhysicalPath(currentPath, l.getSwitch().equals(sw) ? (Switch) l.getElement() : l.getSwitch(), dst);
        if (!recall.isEmpty()) {
          return recall;
        }
      }
      currentPath.remove(currentPath.size() - 1);
    }
    return Collections.emptyList();
  }

  /**
     * Get the direction of a crossed link between two given nodes
     *
     * @param n1    the source node
     * @param n2    the destination node
     * @param l     the link to check
     * @return  null if *not* crossed, true for DownLink, false for UpLink
     */
  abstract public Boolean getLinkDirection(Node n1, Node n2, Link l);
}