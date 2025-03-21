package com.xebia.graph.neo4j.plugins;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.server.plugins.Description;
import org.neo4j.server.plugins.Name;
import org.neo4j.server.plugins.Parameter;
import org.neo4j.server.plugins.PluginTarget;
import org.neo4j.server.plugins.ServerPlugin;
import org.neo4j.server.plugins.Source;

@Description(value = "Computes link salience of each edge in a (sub-) graph") public class LinkSaliencePlugin extends ServerPlugin {
  @Name(value = "computeLinkSalience") @PluginTarget(value = GraphDatabaseService.class) public void computeLinkSalience(@Source GraphDatabaseService graphDb, @Description(value = "Name of the relationship property representing edge weight.") @Parameter(name = "weightProperty") String weightProperty, @Description(value = "Indicates whether the graph should be treated as a directed graph. " + "False means edge direction is ignored in computing shortest paths.") @Parameter(name = "directed") Boolean treatGraphAsDirected) {
    LinkSalienceComputer salienceComputer = new LinkSalienceComputer(graphDb);
    salienceComputer.computeLinkSalience(weightProperty, treatGraphAsDirected);
  }

  @Name(value = "computeLinkSalienceWithDijkstra") @PluginTarget(value = GraphDatabaseService.class) public void computeLinkSalienceWithDijkstra(@Source GraphDatabaseService graphDb, @Description(value = "Name of the relationship property representing edge weight.") @Parameter(name = "weightProperty") String weightProperty) {
    LinkSalienceComputer salienceComputer = new LinkSalienceComputer(graphDb);
    salienceComputer.computeLinkSalienceWithDijkstra(weightProperty);
  }

  @Name(value = "computeLinkSalienceForQueryResult") @PluginTarget(value = GraphDatabaseService.class) public void computeLinkSalienceForQueryResult(@Source GraphDatabaseService graphDb, @Description(value = "Name of the cypher query to provide the subgraph.") @Parameter(name = "query") String query, @Description(value = "Name of the relationship property representing edge weight.") @Parameter(name = "weightProperty") String weightProperty) {
    LinkSalienceComputer salienceComputer = new LinkSalienceComputer(graphDb);
    salienceComputer.computeLinkSalienceForQueryResult(query, weightProperty);
  }
}