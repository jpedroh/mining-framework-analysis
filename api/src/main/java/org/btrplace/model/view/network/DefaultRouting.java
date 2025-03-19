package org.btrplace.model.view.network;
import org.btrplace.model.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link Routing}.
 * Allows to retrieve physical path (L2) between network elements by looking at physical connections.
 *
 * If instantiated manually, it should be first attached to an existing network view,
 * see {@link #setNetwork(Network)}.
 *
 * @author Vincent Kherbache
 * @see #setNetwork(Network)
 */
public class DefaultRouting extends Routing {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  /**
     * Recursive method to get the first physical path found from a switch to a destination node
     *
     * @param   currentPath the current or initial path containing the link(s) crossed
     * @param   sw the current switch to browse
     * @param   dst the destination node to reach
     * @return  the ordered list of links that make the path to dst
     */
  private LinkedHashMap<Link, Boolean> getFirstPhysicalPath(LinkedHashMap<Link, Boolean> currentPath, Switch sw, Node dst) {
    for (Link l : net.getConnectedLinks(sw)) {
      if (currentPath.keySet().contains(l)) {
        continue;
      }
      if (l.getSwitch().equals(sw)) {
        currentPath.put(l, false);
      } else {
        currentPath.put(l, true);
      }
      if (l.getElement() instanceof Node) {
        if (l.getElement().equals(dst)) {
          return currentPath;
        }
      } else {
        LinkedHashMap<Link, Boolean> recall = getFirstPhysicalPath(currentPath, l.getSwitch().equals(sw) ? (Switch) l.getElement() : l.getSwitch(), dst);
        if (!recall.isEmpty()) {
          return recall;
        }
      }
      currentPath.remove(l);
    }
    return new LinkedHashMap<>();
  }
>>>>>>> /usr/src/app/output/btrplace/scheduler/d42500d47b1f8f88aada61e71e4880f680cee32f/api/src/main/java/org/btrplace/model/view/network/DefaultRouting.java/right.java


  @Override public List<Link> getPath(Node n1, Node n2) {
    if (net == null) {
      return Collections.emptyList();
    }
    if (routingCache == null) {
      int cacheSize = net.getConnectedNodes().size();
      routingCache = new LinkedHashMap[cacheSize][cacheSize];
    }
    if (routingCache[n1.id()][n2.id()] == null) {
      LinkedHashMap<Link, Boolean> initialPath = new LinkedHashMap<Link, Boolean>();
      initialPath.put(net.getConnectedLinks(n1).get(0), true);
      routingCache[n1.id()][n2.id()] = getFirstPhysicalPath(initialPath, net.getConnectedLinks(n1).get(0).getSwitch(), n2);
    }
    return 
<<<<<<< /usr/src/app/output/btrplace/scheduler/d42500d47b1f8f88aada61e71e4880f680cee32f/api/src/main/java/org/btrplace/model/view/network/DefaultRouting.java/left.java
    getFirstPhysicalPath(new ArrayList<>(Collections.singletonList(net.getConnectedLinks(n1).get(0))), net.getConnectedLinks(n1).get(0).getSwitch(), n2)
=======
    new ArrayList<Link>(routingCache[n1.id()][n2.id()].keySet())
>>>>>>> /usr/src/app/output/btrplace/scheduler/d42500d47b1f8f88aada61e71e4880f680cee32f/api/src/main/java/org/btrplace/model/view/network/DefaultRouting.java/right.java
    ;
  }

  @Override public Routing copy() {
    DefaultRouting clone = new DefaultRouting();
    clone.net = net;
    return clone;
  }

  @Override public Boolean getLinkDirection(Node n1, Node n2, Link l) {
    if (routingCache == null) {
      int cacheSize = net.getConnectedNodes().size();
      routingCache = new LinkedHashMap[cacheSize][cacheSize];
    }
    if (routingCache[n1.id()][n2.id()] == null) {
      getPath(n1, n2);
    }
    if (!routingCache[n1.id()][n2.id()].keySet().contains(l)) {
      return null;
    }
    return routingCache[n1.id()][n2.id()].get(l);
  }
}