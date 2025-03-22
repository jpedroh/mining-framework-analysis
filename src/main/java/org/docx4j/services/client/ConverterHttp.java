package org.docx4j.services.client;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Java client library for Plutext PDF Converter
 * 
 * @since 3.3.0
 */
public class ConverterHttp implements Converter {
  private static Logger log = LoggerFactory.getLogger(ConverterHttp.class);

  private String URL = null;

  public ConverterHttp() {
  }

  public ConverterHttp(String endpointURL) {
    log.debug("starting, with endpointURL: " + endpointURL);
    if (endpointURL != null) {
      this.URL = endpointURL;
    }
  }

  /**
	 * Convert File fromFormat to toFormat, streaming result to OutputStream os.
	 * 
	 * fromFormat supported: DOC, DOCX
	 * 
	 * toFormat supported: PDF
	 * 
	 * @param f
	 * @param fromFormat
	 * @param toFormat
	 * @param os
	 * @throws IOException
	 * @throws ConversionException
	 */
  public void convert(File f, Format fromFormat, Format toFormat, OutputStream os) throws IOException, ConversionException {
    checkParameters(fromFormat, toFormat);
    CloseableHttpClient httpclient = HttpClients.custom().setRetryHandler(new MyRetryHandler()).build();
    try {
      HttpPost httppost = getUrlForFormat(toFormat);
      HttpEntity reqEntity = new FileEntity(f, map(fromFormat));
      httppost.setEntity(reqEntity);
      execute(httpclient, httppost, os);
      log.debug("..done");
    }  finally {
      httpclient.close();
    }
  }

  private HttpPost getUrlForFormat(Format toFormat) {
    if (Format.TOC.equals(toFormat)) {
      return new HttpPost(URL + "?format=application/json");
    } else {
      if (Format.DOCX.equals(toFormat)) {
        return new HttpPost(URL + "?application/vnd.openxmlformats-officedocument.wordprocessingml.document");
      } else {
        return new HttpPost(URL);
      }
    }
  }

  /**
	 * Convert InputStream fromFormat to toFormat, streaming result to OutputStream os.
	 * 
	 * fromFormat supported: DOC, DOCX
	 * 
	 * toFormat supported: PDF
	 * 
	 * Note this uses a non-repeatable request entity, so it may not be suitable
	 * (depending on the endpoint config).
	 * 
	 * @param instream
	 * @param fromFormat
	 * @param toFormat
	 * @param os
	 * @throws IOException
	 * @throws ConversionException
	 */
  public void convert(InputStream instream, Format fromFormat, Format toFormat, OutputStream os) throws IOException, ConversionException {
    checkParameters(fromFormat, toFormat);
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      HttpPost httppost = getUrlForFormat(toFormat);
      BasicHttpEntity reqEntity = new BasicHttpEntity();
      reqEntity.setContentType(map(fromFormat).getMimeType());
      reqEntity.setContent(instream);
      httppost.setEntity(reqEntity);
      execute(httpclient, httppost, os);
    }  finally {
      httpclient.close();
    }
  }

  /**
	 * Convert byte array fromFormat to toFormat, streaming result to OutputStream os.
	 * 
	 * fromFormat supported: DOC, DOCX
	 * 
	 * toFormat supported: PDF
	 * 
	 * @param bytesIn
	 * @param fromFormat
	 * @param toFormat
	 * @param os
	 * @throws IOException
	 * @throws ConversionException
	 */
  public void convert(byte[] bytesIn, Format fromFormat, Format toFormat, OutputStream os) throws IOException, ConversionException {
    checkParameters(fromFormat, toFormat);
    CloseableHttpClient httpclient = HttpClients.createDefault();
    try {
      HttpPost httppost = getUrlForFormat(toFormat);
      HttpEntity reqEntity = new ByteArrayEntity(bytesIn, map(fromFormat));
      httppost.setEntity(reqEntity);
      execute(httpclient, httppost, os);
    }  finally {
      httpclient.close();
    }
  }

  /**
	 * @param httpclient
	 * @param httppost
	 * @param os
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @throws ConversionException 
	 */
  protected void execute(CloseableHttpClient httpclient, HttpPost httppost, OutputStream os) throws ClientProtocolException, ConversionException {
    CloseableHttpResponse response = null;
    try {
      response = httpclient.execute(httppost);
      HttpEntity resEntity = response.getEntity();
      resEntity.writeTo(os);
      if (response.getStatusLine().getStatusCode() == 403) {
        throw new ConversionException("403 license expired?", response);
      } else {
        if (response.getStatusLine().getStatusCode() != 200) {
          throw new ConversionException(response);
        }
      }
    } catch (java.net.UnknownHostException uhe) {
      log.error("\nLooks like you have the wrong host in your endpoint URL \'" + URL + "\'\n");
      throw new ConversionException(uhe.getMessage(), uhe);
    } catch (java.net.SocketException se) {
      log.error(se.getMessage());
      if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") > -1) {
        log.error("This behaviour may be Windows client OS specific; please look in the server logs or try a Linux client");
        System.err.println("This behaviour may be Windows client OS specific; please look in the server logs or try a Linux client");
      }
      throw new ConversionException(se.getMessage(), se);
    } catch (IOException ioe) {
      ioe.printStackTrace();
      throw new ConversionException(ioe.getMessage(), ioe);
    } finally {
      try {
        if (response == null) {
          log.error("\nLooks like your endpoint URL \'" + URL + "\' is wrong\n");
        } else {
          response.close();
        }
      } catch (IOException e) {
      }
    }
  }

  private ContentType map(Format f) {
    if (Format.DOCX.equals(f)) {
      return ContentType.create("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    } else {
      if (Format.DOC.equals(f)) {
        return ContentType.create("application/msword");
      }
    }
    return null;
  }

  private void checkParameters(Format fromFormat, Format toFormat) throws ConversionException {
    if (URL == null) {
      throw new ConversionException("Endpoint URL not configured.");
    }
    if (Format.DOCX.equals(fromFormat) || Format.DOC.equals(fromFormat)) {
    } else {
      throw new ConversionException("Conversion from format " + fromFormat + " not supported");
    }
    if (Format.PDF.equals(toFormat) || Format.TOC.equals(toFormat) || Format.DOCX.equals(toFormat)) {
    } else {
      throw new ConversionException("Conversion to format " + toFormat + " not supported");
    }
  }
}