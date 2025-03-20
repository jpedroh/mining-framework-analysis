package io.dashbase.clue.commands;

import java.io.PrintStream;
import java.util.Random;

import io.dashbase.clue.util.DocIdMatcher;
import io.dashbase.clue.util.MatcherDocIdSetIterator;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Query;

import io.dashbase.clue.ClueContext;
import io.dashbase.clue.util.MatchSomeDocsQuery;

public class IndexTrimCommand extends ClueCommand {

  public IndexTrimCommand(ClueContext ctx) {
    super(ctx);
  }

  @Override
  public String getName() {
    return "trim";
  }

  @Override
  public String help() {
    return "trims the index, <TRIM PERCENTAGE>"; 
  }
  
  private static Query buildDeleteQuery(final int percentToDelete, int maxDoc) {
    assert percentToDelete >= 0 && percentToDelete <= 100;
<<<<<<< /usr/src/app/output/javasoze/clue/50ef81d6971ce97277810a9f4f2f013e7c76477d/src/main/java/io/dashbase/clue/commands/IndexTrimCommand.java/left.java
    return new MatchSomeDocsQuery(new MatcherDocIdSetIterator(DocIdMatcher.newRandomMatcher(percentToDelete), maxDoc));
||||||| /usr/src/app/output/javasoze/clue/50ef81d6971ce97277810a9f4f2f013e7c76477d/src/main/java/io/dashbase/clue/commands/IndexTrimCommand.java/base.java
    return new MatchSomeDocsQuery(new MatcherDocIdSetIterator(DocIdMatcher.newRandomMatcher(percentToDelete)));
=======
    return new MatchSomeDocsQuery(new MatcherDocIdSetIterator(DocIdMatcher.newRandomMatcher(percentToDelete), Integer.MAX_VALUE));
>>>>>>> /usr/src/app/output/javasoze/clue/50ef81d6971ce97277810a9f4f2f013e7c76477d/src/main/java/io/dashbase/clue/commands/IndexTrimCommand.java/right.java
  }

  @Override
  public void execute(String[] args, PrintStream out) throws Exception {
    if (args.length < 1) {
      out.println("usage: <TRIM PERCENTAGE>");
      return;
    }
    
    int trimPercent = Integer.parseInt(args[0]);
    
    if (trimPercent < 0 || trimPercent > 100) {
      throw new IllegalArgumentException("invalid percent: " + trimPercent);
    }
    
    IndexWriter writer = ctx.getIndexWriter();    
    if (writer != null) {      
      IndexReader reader = ctx.getIndexReader();
      
      writer.deleteDocuments(buildDeleteQuery(trimPercent, reader.maxDoc()));
      writer.commit();      
      ctx.refreshReader();
      reader = ctx.getIndexReader();
      out.println("trim successful, index now contains: " + reader.numDocs() + " docs.");
    }
    else {
      out.println("unable to open writer, index is in readonly mode");
    }
  }

}
