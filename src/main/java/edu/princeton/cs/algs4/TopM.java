package edu.princeton.cs.algs4;

/**
 *  The {@code TopM} class provides a client that reads a sequence of
 *  transactions from standard input and prints the <em>m</em> largest ones
 *  to standard output. This implementation uses a {@link MinPQ} of size
 *  at most <em>m</em> + 1 to identify the <em>M</em> largest transactions
 *  and a {@link Stack} to output them in the proper order.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a>
 *  of <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class TopM {
  private TopM() {
  }

  /**
     *  Reads a sequence of transactions from standard input; takes a
     *  command-line integer m; prints to standard output the m largest
     *  transactions in descending order.
     */
  public static void main(String[] args) {
    int m = Integer.parseInt(args[0]);
    MinPQ<Transaction> pq = new MinPQ<Transaction>(m + 1);
    while (StdIn.hasNextLine()) {
      String line = StdIn.readLine();
      Transaction transaction = new Transaction(line);
      pq.insert(transaction);
      if (pq.size() > m) {
        pq.delMin();
      }
    }
    Stack<Transaction> stack = new Stack<Transaction>();
    for (Transaction transaction : pq) {
      stack.push(transaction);
    }
    for (Transaction transaction : stack) {
      StdOut.println(transaction);
    }
  }
}