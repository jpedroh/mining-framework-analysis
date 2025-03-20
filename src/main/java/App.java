import java.io.BufferedReader;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import com.google.gson.JsonParser;
import java.io.FileReader;
import com.tumblr.jumblr.JumblrClient;
import java.io.IOException;

/**
 * Example usage of Jumblr
 * @author jc
 */
public class App {
  public static void main(String[] args) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException {
    FileReader fr = new FileReader("credentials.json");
    BufferedReader br = new BufferedReader(fr);
    StringBuilder json = new StringBuilder();
    try {
      while (br.ready()) {
        json.append(br.readLine());
      }
    }  finally {
      br.close();
    }
    JsonParser parser = new JsonParser();
    JsonObject obj = (JsonObject) parser.parse(json.toString());
    JumblrClient client = new JumblrClient(obj.getAsJsonPrimitive("consumer_key").getAsString(), obj.getAsJsonPrimitive("consumer_secret").getAsString());
    client.setToken(obj.getAsJsonPrimitive("oauth_token").getAsString(), obj.getAsJsonPrimitive("oauth_token_secret").getAsString());
    for (Blog blog : client.user().getBlogs()) {
      System.out.println(blog.getName());
    }
  }
}