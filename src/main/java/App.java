import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Example usage of Jumblr
 * @author jc
 */
public class App {

    public static void main(String[] args) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException {

        // Read in the JSON data for the credentials
        FileReader fr = new FileReader("credentials.json");
        BufferedReader br = new BufferedReader(fr);
<<<<<<< /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/App.java/left.java
        StringBuilder json = new StringBuilder();
        try {
        	while (br.ready()) { json.append(br.readLine()); }
        } finally {
        	br.close();
        }
||||||| /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/App.java/base.java
        String json = "";
        while (br.ready()) { json += br.readLine(); }
=======
        String json = "";
        while (br.ready()) { json += br.readLine(); }
        br.close();
>>>>>>> /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/App.java/right.java

        // Parse the credentials
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(json.toString());

        // Create a client
        JumblrClient client = new JumblrClient(
            obj.getAsJsonPrimitive("consumer_key").getAsString(),
            obj.getAsJsonPrimitive("consumer_secret").getAsString()
        );

        boolean b = client.authenticate();
        if (!b) {
            System.out.println("Failed to authenticate.");
        }
        
        // Usage
<<<<<<< /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/App.java/left.java
        List<Post> posts = client.blogPosts("seejohnrun");
        for (Post post : posts) {
            System.out.println(post.getShortUrl());
        }

||||||| /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/App.java/base.java
    
        Map<String, Integer> options = new HashMap<String, Integer>();
        options.put("limit", 2);
        List<Post> likes = client.blogLikes("seejohnrun", options);
        System.out.println(likes.size());

=======
        User user = client.user();
        System.out.printf("User %s has these blogs:%n", user.getName());

        // And list their blogs
        for (Blog blog : user.getBlogs()) {
            System.out.printf("\t%s (%s)%n", blog.getName(), blog.getTitle());
        }
        
        System.out.println("They are following these blogs:");
        List<Blog> blogs = client.userFollowing();
        for (Blog blog : blogs) {
            System.out.printf("\t%s (%s)%n", blog.getName(), blog.getTitle());
        }
>>>>>>> /usr/src/app/output/tumblr/jumblr/54323b978d80f80e8009fe9943d8ea7e16658414/src/main/java/App.java/right.java
    }

}
