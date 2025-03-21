package de.deepamehta.files;
import de.deepamehta.core.JSONEnabled;
import org.codehaus.jettison.json.JSONObject;

public class StoredFile implements JSONEnabled {
  private final String fileName;

  private final String repoPath;

  private final long fileTopicId;

  StoredFile(String fileName, String repoPath, long fileTopicId) {
    this.fileName = fileName;
    this.repoPath = repoPath;
    this.fileTopicId = fileTopicId;
  }

  public String getFileName() {
    return fileName;
  }

  public String getRepoPath() {
    return repoPath;
  }

  public long getFileTopicId() {
    return fileTopicId;
  }

  @Override public JSONObject toJSON() {
    try {
      JSONObject storedFile = new JSONObject();
      storedFile.put("fileName", fileName);
      storedFile.put("repo_path", repoPath);
      storedFile.put("topicId", fileTopicId);
      return storedFile;
    } catch (Exception e) {
      throw new RuntimeException("Serialization failed (" + this + ")", e);
    }
  }
}