package edu.umn.cs.spatialHadoop.visualization;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

/**
 * @author sjais001
 *
 */
public class DaVinciServerV2 extends AbstractHandler {
  private static final Log LOG = LogFactory.getLog(DaVinciServerV2.class);

  ImagePlot imagePlot;

  /**
	 * A constructor that starts the Jetty server
	 */
  public DaVinciServerV2() {
  }

  /**
	 * Create an HTTP web server (using Jetty) that will stay running to answer
	 * all queries
	 * @throws Exception 
	 */
  private static void startServer() throws Exception {
    int port = 8889;
    Server server = new Server(port);
    server.setHandler(new DaVinciServerV2());
    server.start();
    server.join();
  }

  public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Credentials", "true");
    ((Request) request).setHandled(true);
    try {
      if (target.startsWith("/dynamic/showImage.cgi")) {
        displayImage(target, response);
      } else {
        LOG.info("Received request: \'" + request.getRequestURL() + "\'");
        tryToLoadStaticResource(target, response);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void displayImage(String target, HttpServletResponse response) throws IOException {
    LOG.info("display image start time " + System.currentTimeMillis());
    target = target.replace("/dynamic/showImage.cgi/", "");
    if (new File(target).isFile() && target.endsWith("index.html")) {
      tryToLoadStaticResource(target, response);
    } else {
      if (new File(target).isFile() && target.endsWith(".png")) {
        double startTime = System.nanoTime();
        tryToLoadStaticResource(target, response);
        double finishTime = System.nanoTime();
        LOG.info("#### STATIC file: " + target + "image load time is: " + (finishTime - startTime));
      } else {
        if (target.endsWith(".png")) {
          String filename = new File(target).getName();
          if (filename.contains("--")) {
            tryToLoadStaticResource(target, response);
          } else {
            String[] splits = filename.split("[\\-\\.]");
            int zoom_level = Integer.parseInt(splits[1]);
            int column = Integer.parseInt(splits[2]);
            int row = Integer.parseInt(splits[3]);
            Boolean upLevel = false;
            do {
              System.out.println("***************target: " + target);
              File datafile = new File(new File(target).getAbsolutePath().replace(new File(target).getName(), "_master.rstar"));
              System.out.println(datafile.toString());
              if (datafile.exists()) {
                double startTime = System.nanoTime();
                imagePlot = new ImagePlot();
                DataOutputStream output = new DataOutputStream(response.getOutputStream());
                imagePlot.createImage(datafile.getParent(), datafile.getName(), output, upLevel, filename, zoom_level, column, row);
                upLevel = false;
                output.close();
                response.setContentType("image/png");
                response.setStatus(HttpServletResponse.SC_OK);
                double finishTime = System.nanoTime();
                LOG.info(String.format("****DATFILE : %s image generation and load time is %f seconds", filename, (finishTime - startTime) * 1E-9));
                return;
              } else {
                LOG.info("master.rstar not found");
              }
            } while(zoom_level >= 0);
          }
          LOG.info("display image end time " + System.currentTimeMillis());
        } else {
          LOG.error("Cannot handle " + target);
        }
      }
    }
  }

  /**
	 * Tries to load the given resource name from class path if it exists.
	 * Used to serve static files such as HTML pages, images and JavaScript files.
	 * @param target
	 * @param response
	 * @throws IOException
	 */
  private void tryToLoadStaticResource(String target, HttpServletResponse response) throws IOException {
    LOG.info("Loading resource " + target);
    try {
      InputStream resource = new FileInputStream(target);
      byte[] buffer = new byte[1024 * 1024];
      ServletOutputStream outResponse = response.getOutputStream();
      int size;
      while ((size = resource.read(buffer)) != -1) {
        outResponse.write(buffer, 0, size);
      }
      resource.close();
      outResponse.close();
      response.setStatus(HttpServletResponse.SC_OK);
      if (target.endsWith(".js")) {
        response.setContentType("application/javascript");
      } else {
        if (target.endsWith(".css")) {
          response.setContentType("text/css");
        } else {
          response.setContentType(URLConnection.guessContentTypeFromName(target));
        }
      }
      final DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZ");
      final long year = 1000L * 60 * 60 * 24 * 365;
      response.addHeader("Expires", format.format(new Date().getTime() + year));
    } catch (IOException e) {
      LOG.warn("File not found " + target);
      if (target.endsWith(".png")) {
        BufferedImage emptyImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = emptyImage.createGraphics();
        ServletOutputStream outResponse = response.getOutputStream();
        ImageIO.write(emptyImage, "png", outResponse);
        outResponse.close();
        response.setContentType("image/png");
        response.setStatus(HttpServletResponse.SC_OK);
      }
      return;
    }
  }

  private void reportError(HttpServletResponse response, String msg, Exception e) throws IOException {
    if (e != null) {
      e.printStackTrace();
    }
    LOG.error(msg);
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    response.getWriter().println("{\"message\": \'" + msg + "\',");
    if (e != null) {
      response.getWriter().println("\"error\": \'" + e.getMessage() + "\',");
      response.getWriter().println("\"stacktrace\": [");
      for (StackTraceElement trc : e.getStackTrace()) {
        response.getWriter().println("\'" + trc.toString() + "\',");
      }
      response.getWriter().println("]");
    }
    response.getWriter().println("}");
  }

  /**
	 * @param args
	 * @throws Exception 
	 */
  public static void main(String[] args) throws Exception {
    startServer();
  }
}