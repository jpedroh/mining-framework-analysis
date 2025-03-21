package de.uni_koblenz.jgralab.graphvalidator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import de.uni_koblenz.jgralab.AttributedElement;
import de.uni_koblenz.jgralab.EdgeDirection;
import de.uni_koblenz.jgralab.Graph;
import de.uni_koblenz.jgralab.GraphIO;
import de.uni_koblenz.jgralab.GraphIOException;
import de.uni_koblenz.jgralab.Vertex;
import de.uni_koblenz.jgralab.greql2.evaluator.GreqlEvaluatorImpl;
import de.uni_koblenz.jgralab.greql2.evaluator.QueryImpl;
import de.uni_koblenz.jgralab.greql2.exception.GreqlException;
import de.uni_koblenz.jgralab.impl.ConsoleProgressFunction;
import de.uni_koblenz.jgralab.schema.AttributedElementClass;
import de.uni_koblenz.jgralab.schema.Constraint;
import de.uni_koblenz.jgralab.schema.EdgeClass;
import de.uni_koblenz.jgralab.schema.Schema;

/**
 * A <code>GraphValidator</code> can be used to check if all {@link Constraint}s
 * specified in the {@link Schema} of a given {@link Graph} are fulfilled.
 * 
 * @author Tassilo Horn <horn@uni-koblenz.de>
 */
public class GraphValidator {
  private Graph graph;

  /**
	 * @param graph
	 *            the {@link Graph} to validate
	 */
  public GraphValidator(Graph graph) {
    this.graph = graph;
  }

  public static void main(String[] args) throws GraphIOException, IOException {
    if (args.length != 1) {
      System.err.println("Usage: java GraphValidator <graph.tg>");
      System.exit(1);
    }
    Graph g = GraphIO.loadGraphFromFile(args[0], new ConsoleProgressFunction("Loading"));
    GraphValidator v = new GraphValidator(g);
    v.createValidationReport("__validation_report.html");
  }

  /**
	 * Checks if all multiplicities specified for the {@link EdgeClass}
	 * <code>ec</code> are fulfilled.
	 * 
	 * 
	 * @param ec
	 *            an {@link EdgeClass}
	 * @return a set of {@link MultiplicityConstraintViolation} describing which
	 *         and where {@link MultiplicityConstraintViolation} constraints
	 *         where violated
	 */
  public SortedSet<MultiplicityConstraintViolation> validateMultiplicities(EdgeClass ec) {
    SortedSet<MultiplicityConstraintViolation> brokenConstraints = new TreeSet<MultiplicityConstraintViolation>();
    int toMin = ec.getTo().getMin();
    int toMax = ec.getTo().getMax();
    Set<AttributedElement<?, ?>> badOutgoing = new HashSet<AttributedElement<?, ?>>();
    for (Vertex v : graph.vertices(ec.getFrom().getVertexClass())) {
      int degree = v.getDegree(ec, EdgeDirection.OUT);
      if ((degree < toMin) || (degree > toMax)) {
        badOutgoing.add(v);
      }
    }
    if (!badOutgoing.isEmpty()) {
      brokenConstraints.add(new MultiplicityConstraintViolation(ec, "Invalid number of outgoing edges, allowed are (" + toMin + "," + (toMax == Integer.MAX_VALUE ? "*" : toMax) + ").", badOutgoing));
    }
    int fromMin = ec.getFrom().getMin();
    int fromMax = ec.getFrom().getMax();
    Set<AttributedElement<?, ?>> badIncoming = new HashSet<AttributedElement<?, ?>>();
    for (Vertex v : graph.vertices(ec.getTo().getVertexClass())) {
      int degree = v.getDegree(ec, EdgeDirection.IN);
      if ((degree < fromMin) || (degree > fromMax)) {
        badIncoming.add(v);
      }
    }
    if (!badIncoming.isEmpty()) {
      brokenConstraints.add(new MultiplicityConstraintViolation(ec, "Invalid number of incoming edges, allowed are (" + fromMin + "," + (fromMax == Integer.MAX_VALUE ? "*" : fromMax) + ").", badIncoming));
    }
    return brokenConstraints;
  }

  /**
	 * Validates all constraints of the graph.
	 * 
	 * @see GraphValidator#validateMultiplicities(EdgeClass)
	 * @see GraphValidator#validateConstraints(AttributedElementClass)
	 * @return a set of {@link ConstraintViolation} objects, one for each
	 *         violation, sorted by their type
	 */
  public SortedSet<ConstraintViolation> validate() {
    SortedSet<ConstraintViolation> brokenConstraints = new TreeSet<ConstraintViolation>();
    for (EdgeClass ec : graph.getGraphClass().getEdgeClasses()) {
      if (ec.isInternal()) {
        continue;
      }
      brokenConstraints.addAll(validateMultiplicities(ec));
    }
    List<AttributedElementClass<?, ?>> aecs = new ArrayList<AttributedElementClass<?, ?>>();
    aecs.add(graph.getSchema().getGraphClass());
    aecs.addAll(graph.getSchema().getGraphClass().getVertexClasses());
    aecs.addAll(graph.getSchema().getGraphClass().getEdgeClasses());
    for (AttributedElementClass<?, ?> aec : aecs) {
      if (aec.isInternal()) {
        continue;
      }
      brokenConstraints.addAll(validateConstraints(aec));
    }
    return brokenConstraints;
  }

  /**
	 * Checks if all {@link Constraint}s attached to the
	 * {@link AttributedElementClass} <code>aec</code> are fulfilled.
	 * 
	 * @param aec
	 *            an {@link AttributedElementClass}
	 * @return a set of {@link ConstraintViolation} objects
	 */
  public SortedSet<ConstraintViolation> validateConstraints(AttributedElementClass<?, ?> aec) {
    SortedSet<ConstraintViolation> brokenConstraints = new TreeSet<ConstraintViolation>();
    for (Constraint constraint : aec.getConstraints()) {
      String query = constraint.getPredicate();
      GreqlEvaluatorImpl eval = new GreqlEvaluatorImpl(new QueryImpl(query), graph, null, null);
      try {
        if (!eval.<Boolean>getSingleResult()) {
          if (constraint.getOffendingElementsQuery() != null) {
            String oeq = constraint.getOffendingElementsQuery();
            GreqlEvaluatorImpl eval1 = new GreqlEvaluatorImpl(new QueryImpl(oeq), graph, null, null);
            Set<AttributedElement> resultSet = eval1.<AttributedElement>getResultSet();
            brokenConstraints.add(new GReQLConstraintViolation(aec, constraint, resultSet));
          } else {
            brokenConstraints.add(new GReQLConstraintViolation(aec, constraint, null));
          }
        }
      } catch (GreqlException e) {
        brokenConstraints.add(new BrokenGReQLConstraintViolation(aec, constraint, query));
      }
    }
    return brokenConstraints;
  }

  /**
	 * Do just like {@link GraphValidator#validate()}, but generate a HTML
	 * report saved to <code>fileName</code>, too.
	 * 
	 * @param fileName
	 *            the name of the HTML report file
	 * @return a set of {@link ConstraintViolation} objects, one for each
	 *         invalidation, sorted by their type
	 * @see GraphValidator#validate()
	 * @throws IOException
	 *             if the given file cannot be written
	 */
  public SortedSet<ConstraintViolation> createValidationReport(String fileName) throws IOException {
    SortedSet<ConstraintViolation> brokenConstraints = validate();
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new FileWriter(new File(fileName)));
      bw.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n" + "\"http://www.w3.org/TR/html4/strict.dtd\">\n" + "<html>");
      bw.append("<head>");
      bw.append("<style type=\"text/css\">");
      bw.append("th {");
      bw.append("\tfont: bold 11px sans-serif;");
      bw.append("\tcolor: MidnightBlue;");
      bw.append("\tborder-right: 1px solid #C1DAD7;");
      bw.append("\tborder-bottom: 1px solid #C1DAD7;");
      bw.append("\tborder-top: 1px solid #C1DAD7;");
      bw.append("\tletter-spacing: 2px;");
      bw.append("\ttext-align: left;");
      bw.append(" padding: 6px 6px 6px 12px;");
      bw.append("\tbackground: #CAE8EA;");
      bw.append("}");
      bw.append("td {");
      bw.append(" border-right: 1px solid #C1DAD7;");
      bw.append("\tborder-bottom: 1px solid #C1DAD7;");
      bw.append("\tbackground: #fff;");
      bw.append("\tpadding: 6px 6px 6px 12px;");
      bw.append("\tcolor: DimGrey;");
      bw.append("}");
      bw.append("td.other {");
      bw.append(" border-right: 1px solid #C1DAD7;");
      bw.append("\tborder-bottom: 1px solid #C1DAD7;");
      bw.append("\tbackground: AliceBlue;");
      bw.append("\tpadding: 6px 6px 6px 12px;");
      bw.append("\tcolor: DimGrey;");
      bw.append("}");
      bw.append("</style>");
      bw.append("<title>");
      bw.append("Validation Report for the " + graph.getSchemaClass().getSimpleName() + " with id " + graph.getId() + ".");
      bw.append("</title>");
      bw.append("</head>");
      bw.append("<body>");
      if (brokenConstraints.size() == 0) {
        bw.append("<p><b>The graph is valid!</b></p>");
      } else {
        bw.append("<p><b>The " + graph.getSchemaClass().getSimpleName() + " violates " + brokenConstraints.size() + " constraints.</b></p>");
        bw.append("<table border=\"1\">");
        bw.append("<tr>");
        bw.append("<th>#</th>");
        bw.append("<th>ConstraintType</th>");
        bw.append("<th>AttributedElementClass</th>");
        bw.append("<th>Message</th>");
        bw.append("<th>Broken Elements</th>");
        bw.append("</tr>");
        int no = 1;
        String cssClass = "";
        for (ConstraintViolation ci : brokenConstraints) {
          if (no % 2 == 0) {
            cssClass = "other";
          } else {
            cssClass = "";
          }
          bw.append("<tr>");
          bw.append("<td class=\"" + cssClass + "\">");
          bw.append(Integer.valueOf(no++).toString());
          bw.append("</td>");
          bw.append("<td class=\"" + cssClass + "\">");
          bw.append(ci.getClass().getSimpleName());
          bw.append("</td>");
          bw.append("<td class=\"" + cssClass + "\">");
          bw.append(ci.getAttributedElementClass().getQualifiedName());
          bw.append("</td>");
          bw.append("<td class=\"" + cssClass + "\">");
          bw.append(ci.getMessage());
          bw.append("</td>");
          bw.append("<td class=\"" + cssClass + "\">");
          if (ci.getOffendingElements() != null) {
            for (AttributedElement<?, ?> ae : ci.getOffendingElements()) {
              bw.append(ae.toString());
              bw.append("<br/>");
            }
          }
          bw.append("</td>");
          bw.append("</tr>");
        }
        bw.append("</table>");
      }
      bw.append("</body></html>");
      bw.flush();
    }  finally {
      try {
        bw.close();
      } catch (IOException ex) {
        throw new RuntimeException("An Exception occurred while closing the stream.", ex);
      }
    }
    return brokenConstraints;
  }
}