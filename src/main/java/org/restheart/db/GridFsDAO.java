package org.restheart.db;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import java.io.File;
import java.io.IOException;
import org.bson.types.ObjectId;
import org.restheart.utils.HttpStatus;

/**
 *
 * @author Andrea Di Cesare <andrea@softinstigate.com>
 */
public class GridFsDAO implements GridFsRepository {
  private final MongoClient client;

  public GridFsDAO() {
    client = MongoDBClientSingleton.getInstance().getClient();
  }

  @Override public int createFile(Database db, String dbName, String bucketName, Object fileId, DBObject properties, File data) throws IOException, DuplicateKeyException {
    final String bucket = extractBucketName(bucketName);
    GridFS gridfs = new GridFS(db.getDB(dbName), bucket);
    GridFSInputFile gfsFile = gridfs.createFile(data);
    properties.removeField("_id");
    Object _fileName = properties.removeField("filename");
    properties.removeField("chunkSize");
    properties.removeField("uploadDate");
    properties.removeField("length");
    properties.removeField("md5");
    String fileName;
    if (_fileName != null && _fileName instanceof String) {
      fileName = (String) _fileName;
    } else {
      fileName = null;
    }
    properties.put("_etag", new ObjectId());
    Object _contentType = properties.removeField("contentType");
    String contentType;
    if (_contentType != null && _contentType instanceof String) {
      contentType = (String) _contentType;
    } else {
      contentType = null;
    }
    gfsFile.setId(fileId);
    gfsFile.setContentType(contentType);
    gfsFile.setFilename(fileName);
    properties.toMap().keySet().stream().forEach((k) -> gfsFile.put((String) k, properties.get((String) k)));
    gfsFile.save();
    return HttpStatus.SC_CREATED;
  }

  @Override public int deleteFile(Database db, String dbName, String bucketName, Object fileId, ObjectId requestEtag) {
    GridFS gridfs = new GridFS(db.getDB(dbName), extractBucketName(bucketName));
    GridFSDBFile dbsfile = gridfs.findOne(new BasicDBObject("_id", fileId));
    if (dbsfile == null) {
      return HttpStatus.SC_NOT_FOUND;
    } else {
      int code = checkEtag(requestEtag, dbsfile);
      if (code == HttpStatus.SC_NO_CONTENT) {
        gridfs.remove(new BasicDBObject("_id", fileId));
      }
      return code;
    }
  }

  @Override public void deleteChunksCollection(Database db, String dbName, String bucketName) {
    String chunksCollName = extractBucketName(bucketName).concat(".chunks");
    client.getDB(dbName).getCollection(chunksCollName).drop();
  }

  /**
     *
     * @param requestEtag
     * @param dbsfile
     * @return HttpStatus.SC_NO_CONTENT if check is ok
     */
  private int checkEtag(ObjectId requestEtag, GridFSDBFile dbsfile) {
    if (dbsfile != null) {
      Object etag = dbsfile.get("_etag");
      if (etag == null) {
        return HttpStatus.SC_NO_CONTENT;
      }
      if (requestEtag == null) {
        return HttpStatus.SC_CONFLICT;
      }
      if (etag.equals(requestEtag)) {
        return HttpStatus.SC_NO_CONTENT;
      } else {
        return HttpStatus.SC_PRECONDITION_FAILED;
      }
    }
    return HttpStatus.SC_NO_CONTENT;
  }

  private static String extractBucketName(final String collectionName) {
    return collectionName.split("\\.")[0];
  }
}