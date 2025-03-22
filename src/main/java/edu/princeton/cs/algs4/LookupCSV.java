package edu.princeton.cs.algs4;

/**
 *  The {@code LookupCSV} class provides a data-driven client for reading in a
 *  key-value pairs from a file; then, printing the values corresponding to the
 *  keys found on standard input. Both keys and values are strings.
 *  The fields to serve as the key and value are taken as command-line arguments.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/35applications">Section 3.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class LookupCSV {
  private LookupCSV() {
  }

  public static void main(String[] args) {
    int keyField = Integer.parseInt(args[1]);
    int valField = Integer.parseInt(args[2]);
    ST<String, String> st = new ST<String, String>();
    In in = new In(args[0]);
    while (in.hasNextLine()) {
      String line = in.readLine();
      String[] tokens = line.split(",");
      String key = tokens[keyField];
      String val = tokens[valField];
      st.put(key, val);
    }
    while (!StdIn.isEmpty()) {
      String s = StdIn.readString();
      if (st.contains(s)) {
        StdOut.println(st.get(s));
      } else {
        StdOut.println("Not found");
      }
    }
  }
}