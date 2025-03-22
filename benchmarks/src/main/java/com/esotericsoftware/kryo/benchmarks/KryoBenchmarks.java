package com.esotericsoftware.kryo.benchmarks;
import org.openjdk.jmh.Main;

public class KryoBenchmarks {
  /** To run from command line: $ mvn clean install exec:java -Dexec.args="-f 4 -wi 5 -i 3 -t 2 -w 2s -r 2s"
	 * <p>
	 * Fork 0 can be used for debugging/development, eg: -f 0 -wi 1 -i 1 -t 1 -w 1s -r 1s [benchmarkClassName] */
  static public void main(String[] args) throws Exception {
    if (args.length == 0) {
      String commandLine = "-f 0 -wi 2 -i 1 -t 1 -w 1s -r 1s ";
      System.out.println(commandLine);
      args = commandLine.split(" ");
    }
    Main.main(args);
  }
}