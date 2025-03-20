package info.aaronland.extruder;
import info.aaronland.extruder.Upload;
import info.aaronland.extruder.Document;
import info.aaronland.extruder.DocumentView;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import java.io.InputStream;
import java.io.File;
import java.net.URL;
import com.basistech.readability.Readability;
import com.basistech.readability.HttpPageReader;
import com.basistech.readability.FilePageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value = "/java-readability") @Produces(value = { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON }) public class JavaReadabilityResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(JavaReadabilityResource.class);

  private static final TextUtils utils = new TextUtils();

  @GET public Response extrudeThisURL(@QueryParam(value = "url") String url) {
    Document doc;
    DocumentView view;
    try {
      doc = extrudeThis(url);

<<<<<<< /usr/src/app/output/straup/dogeared-extruder/0176ca61e4bac5035afc89e56f3063a206c3a660/src/main/java/info/aaronland/extruder/JavaReadabilityResource.java/left.java
      text = massageText(text)
=======
      view = new DocumentView(doc)
>>>>>>> /usr/src/app/output/straup/dogeared-extruder/0176ca61e4bac5035afc89e56f3063a206c3a660/src/main/java/info/aaronland/extruder/JavaReadabilityResource.java/right.java
      ;
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
    }
    return Response.status(Response.Status.OK).entity(view).build();
  }

  @POST @Consumes(value = MediaType.MULTIPART_FORM_DATA) public Response extrudeThisFile(@FormDataParam(value = "file") InputStream input) {
    Upload upload = new Upload();
    File tmpfile = upload.writeTmpFile(input);
    String uri = "file://" + tmpfile.getAbsolutePath();
    Document doc;
    DocumentView view;
    try {
      doc = extrudeThis(uri);

<<<<<<< /usr/src/app/output/straup/dogeared-extruder/0176ca61e4bac5035afc89e56f3063a206c3a660/src/main/java/info/aaronland/extruder/JavaReadabilityResource.java/left.java
      text = massageText(text)
=======
      view = new DocumentView(doc)
>>>>>>> /usr/src/app/output/straup/dogeared-extruder/0176ca61e4bac5035afc89e56f3063a206c3a660/src/main/java/info/aaronland/extruder/JavaReadabilityResource.java/right.java
      ;
    } catch (Exception e) {
      tmpfile.delete();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.toString()).build();
    }
    tmpfile.delete();
    return Response.status(Response.Status.OK).entity(view).build();
  }

  private Document extrudeThis(String uri) {
    URL url = null;
    String text = "";
    try {
      url = new URL(uri);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      Readability parser = new Readability();
      String path = url.toString();
      if (path.startsWith("file:")) {
        path = path.replace("file:", "");
        FilePageReader reader = new FilePageReader();
        parser.setPageReader(reader);
      } else {
        HttpPageReader reader = new HttpPageReader();
        parser.setPageReader(reader);
      }
      parser.processDocument(path);
      text = parser.getArticleText();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return new Document(text);
  }

  private String massageText(String text) {
    text = utils.text2html(text);
    return text;
  }
}