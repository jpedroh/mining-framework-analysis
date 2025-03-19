/*
 * Copyright (c) 2016 University Nice Sophia Antipolis
 *
 * This file is part of btrplace.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    @Override
    public List<Link> getPath(Node n1, Node n2) {

        if (net == null) {
            return Collections.emptyList();
        }

        // Initialize the cache
        if (routingCache == null) {
            int cacheSize = net.getConnectedNodes().size();
            routingCache = new LinkedHashMap[cacheSize][cacheSize]; // OK for the warning
        }

        // Fill the cache if needed
        if (routingCache[n1.id()][n2.id()] == null) {
            // Get the first physical path found between the two nodes
            LinkedHashMap<Link, Boolean> initialPath = new LinkedHashMap<Link, Boolean>();
            // From element to switch => true : UpLink
<<<<<<< /usr/src/app/output/btrplace/scheduler/d42500d47b1f8f88aada61e71e4880f680cee32f/api/src/main/java/org/btrplace/model/view/network/DefaultRouting.java/left.java
            Collections.singletonList(net.getConnectedLinks(n1).get(0), true);
||||||| /usr/src/app/output/btrplace/scheduler/d42500d47b1f8f88aada61e71e4880f680cee32f/api/src/main/java/org/btrplace/model/view/network/DefaultRouting.java/base.java
            Arrays.asList(net.getConnectedLinks(n1).get(0), true);
=======
            initialPath.put(net.getConnectedLinks(n1).get(0), true);
>>>>>>> /usr/src/app/output/btrplace/scheduler/d42500d47b1f8f88aada61e71e4880f680cee32f/api/src/main/java/org/btrplace/model/view/network/DefaultRouting.java/right.java
            routingCache[n1.id()][n2.id()] =
                    getFirstPhysicalPath(
                            initialPath, // Only one link per node
                            net.getConnectedLinks(n1).get(0).getSwitch(), // A node is always connected to a switch
                            n2
                    );
        }

        return new ArrayList<Link>(routingCache[n1.id()][n2.id()].keySet());
    }
    @Override
    public Boolean getLinkDirection(Node n1, Node n2, Link l) {

        // Initialize the cache (should be already done)
        if (routingCache == null) {
            int cacheSize = net.getConnectedNodes().size();
            routingCache = new LinkedHashMap[cacheSize][cacheSize]; // OK for the warning
        }

        // Fill the needed cache entry from getPath method
        if (routingCache[n1.id()][n2.id()] == null) {
            getPath(n1, n2);
        }

        // Link is not on route!
        if (!routingCache[n1.id()][n2.id()].keySet().contains(l)) return null;

        // Return the direction
        return routingCache[n1.id()][n2.id()].get(l);
    }

    @Override
    public Routing copy() {
        DefaultRouting clone = new DefaultRouting();
        clone.net = net; // Do not associate view->routing, only routing->view
        return clone;
    }
}
