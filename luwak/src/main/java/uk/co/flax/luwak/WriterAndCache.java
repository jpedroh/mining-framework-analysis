package uk.co.flax.luwak;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IOUtils;

public class WriterAndCache {
  private final IndexWriter writer;

  private final SearcherManager manager;

  private volatile Map<BytesRef, QueryCacheEntry> purgeCache = null;

  private final ReadWriteLock purgeLock = new ReentrantReadWriteLock();

  private final Object commitLock = new Object();

  private volatile Map<BytesRef, QueryCacheEntry> queries = new ConcurrentHashMap<>();

  public WriterAndCache(IndexWriter indexWriter, SearcherFactory searcherFactory) throws IOException {
    this.writer = indexWriter;
    this.manager = new SearcherManager(writer, true, searcherFactory);
  }

  public WriterAndCache() throws IOException {
    this(Monitor.defaultIndexWriter(new RAMDirectory()), new SearcherFactory());
  }

  public void commit(List<Indexable> updates, String deleteField) throws IOException {
    synchronized (commitLock) {
      purgeLock.readLock().lock();
      try {
        if (updates != null) {
          Set<String> ids = new HashSet<>();
          for (Indexable update : updates) {
            ids.add(update.id);
          }
          for (String id : ids) {
            writer.deleteDocuments(new Term(deleteField, id));
          }
          for (Indexable update : updates) {
            this.queries.put(update.queryCacheEntry.hash, update.queryCacheEntry);
            writer.addDocument(update.document);
            if (purgeCache != null) {
              purgeCache.put(update.queryCacheEntry.hash, update.queryCacheEntry);
            }
          }
        }
        writer.commit();
        manager.maybeRefresh();
      }  finally {
        purgeLock.readLock().unlock();
      }
    }
  }

  public interface WithQueryMap {
    public void setQueryMap(Map<BytesRef, QueryCacheEntry> queries);
  }

  public Searcher getSearcher(WithQueryMap qm) throws IOException {
    purgeLock.readLock().lock();
    try {
      if (qm != null) {
        qm.setQueryMap(queries);
      }
      return new Searcher(manager.acquire(), this);
    }  finally {
      purgeLock.readLock().unlock();
    }
  }

  public interface CachePopulator {
    public void populateCacheWithIndex(ConcurrentMap<BytesRef, QueryCacheEntry> newCache) throws IOException;
  }

  /**
     * Remove unused queries from the query cache.
     *
     * This is normally called from a background thread at a rate set by configurePurgeFrequency().
     *
     * @throws IOException on IO errors
     */
  public synchronized void purgeCache(CachePopulator populator) throws IOException {
    final ConcurrentMap<BytesRef, QueryCacheEntry> newCache = new ConcurrentHashMap<>();
    purgeLock.writeLock().lock();
    try {
      purgeCache = new ConcurrentHashMap<>();
    }  finally {
      purgeLock.writeLock().unlock();
    }
    populator.populateCacheWithIndex(newCache);
    purgeLock.writeLock().lock();
    try {
      newCache.putAll(purgeCache);
      purgeCache = null;
      queries = newCache;
    }  finally {
      purgeLock.writeLock().unlock();
    }
  }

  public void closeWhileHandlingException() throws IOException {
    IOUtils.closeWhileHandlingException(manager, writer, writer.getDirectory());
  }

  public int numDocs() {
    return writer.numDocs();
  }

  public int numRamDocs() {
    return writer.numRamDocs();
  }

  public int cacheSize() {
    return queries.size();
  }

  public void deleteDocuments(Term term) throws IOException {
    writer.deleteDocuments(term);
  }

  public void deleteDocuments(Query query) throws IOException {
    writer.deleteDocuments(query);
  }

  public void release(IndexSearcher searcher) throws IOException {
    manager.release(searcher);
  }

  public static class QueryCacheEntry {
    /** The (possibly partial due to decomposition) query */
    public final Query matchQuery;

    /** A hash value for lookups */
    public final BytesRef hash;

    /** The metadata from the entry's parent {@link MonitorQuery} */
    public final Map<String, String> metadata;

    public QueryCacheEntry(BytesRef hash, Query matchQuery, Map<String, String> metadata) {
      this.hash = hash;
      this.matchQuery = matchQuery;
      this.metadata = metadata;
    }
  }

  public static class Searcher implements AutoCloseable {
    private IndexSearcher searcher;

    private WriterAndCache wac;

    private Searcher(IndexSearcher searcher, WriterAndCache wac) {
      this.searcher = searcher;
      this.wac = wac;
    }

    public IndexReader getIndexReader() {
      return searcher.getIndexReader();
    }

    public void search(Query query, Collector results) throws IOException {
      searcher.search(query, results);
    }

    @Override public void close() throws IOException {
      wac.release(searcher);
    }
  }
}