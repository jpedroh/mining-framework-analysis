package edu.princeton.cs.algs4;

/**
 *  The {@code Multiway} class provides a client for reading in several
 *  sorted text files and merging them together into a single sorted
 *  text stream.
 *  This implementation uses a {@link IndexMinPQ} to perform the multiway
 *  merge. 
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a>
 *  of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Multiway {
  private Multiway() {
  }

  private static void merge(In[] streams) {
    int n = streams.length;
    IndexMinPQ<String> pq = new IndexMinPQ<String>(n);
    for (int i = 0; i < n; i++) {
      if (!streams[i].isEmpty()) {
        pq.insert(i, streams[i].readString());
      }
    }
    while (!pq.isEmpty()) {
      StdOut.print(pq.minKey() + " ");
      int i = pq.delMin();
      if (!streams[i].isEmpty()) {
        pq.insert(i, streams[i].readString());
      }
    }
    StdOut.println();
  }

  /**
     *  Reads sorted text files specified as command-line arguments;
     *  merges them together into a sorted output; and writes
     *  the results to standard output.
     *  Note: this client does not check that the input files are sorted.
     */
  public static void main(String[] args) {
    int n = args.length;
    In[] streams = new In[n];
    for (int i = 0; i < n; i++) {
      streams[i] = new In(args[i]);
    }
    merge(streams);
  }
}