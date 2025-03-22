package edu.princeton.cs.algs4;
import java.io.File;

/**
 *  The {@code FileIndex} class provides a client for indexing a set of files,
 *  specified as command-line arguments. It takes queries from standard input
 *  and prints each file that contains the given query.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/35applications">Section 3.5</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *  
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class FileIndex {
  private FileIndex() {
  }

  public static void main(String[] args) {
    ST<String, SET<File>> st = new ST<String, SET<File>>();
    StdOut.println("Indexing files");
    for (String filename : args) {
      StdOut.println("  " + filename);
      File file = new File(filename);
      In in = new In(file);
      while (!in.isEmpty()) {
        String word = in.readString();
        if (!st.contains(word)) {
          st.put(word, new SET<File>());
        }
        SET<File> set = st.get(word);
        set.add(file);
      }
    }
    while (!StdIn.isEmpty()) {
      String query = StdIn.readString();
      if (st.contains(query)) {
        SET<File> set = st.get(query);
        for (File file : set) {
          StdOut.println("  " + file.getName());
        }
      }
    }
  }
}