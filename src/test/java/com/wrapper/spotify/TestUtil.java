package com.wrapper.spotify;
import com.wrapper.spotify.exceptions.*;
import java.io.BufferedReader;
import java.io.*;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestUtil {
  private static final String TEST_DATA_DIR = "src/test/fixtures/";

  private static final int MAX_TEST_DATA_FILE_SIZE = 65536;

  public static String readTestData(String fileName) throws IOException {
    return readFromFile(new File(TEST_DATA_DIR, fileName));
  }

  private static String readFromFile(File file) throws IOException {

<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
    BufferedReader
=======
    String
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java
     
<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
    in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))
=======
    currentLine
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java
    ;
    StringBuilder 
<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
    out = new StringBuilder()
=======
    result = new StringBuilder()
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java
    ;

<<<<<<< Unknown file: This is a bug in JDime.
=======
    InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java


<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
    String
=======
    BufferedReader
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java
     
<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
    line
=======
    bufReader = new BufferedReader(reader)
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java
    ;
    while ((
<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
    line
=======
    currentLine
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java
     = 
<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
    in
=======
    bufReader
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java
    .readLine()) != null) {

<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
      out
=======
      result
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java
      .append(
<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
      line
=======
      currentLine
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java
      );
    }

<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
    in.close();
=======
>>>>>>> Unknown file: This is a bug in JDime.

    return 
<<<<<<< /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/left.java
    out
=======
    result
>>>>>>> /usr/src/app/output/thelinmichael/spotify-web-api-java/1b1cd8eb1933453c6c1985aa960e69506070ecf5/src/test/java/com/wrapper/spotify/TestUtil.java/right.java
    .toString();
  }

  public static class MockedHttpManager {
    public static HttpManager returningJson(String jsonFixture) throws Exception {
      final HttpManager mockedHttpManager = mock(HttpManager.class);
      final String fixture = readTestData(jsonFixture);
      when(mockedHttpManager.get((UtilProtos.Url) any())).thenReturn(fixture);
      when(mockedHttpManager.post((UtilProtos.Url) any())).thenReturn(fixture);
      when(mockedHttpManager.put((UtilProtos.Url) any())).thenReturn(fixture);
      when(mockedHttpManager.delete((UtilProtos.Url) any())).thenReturn(fixture);
      return mockedHttpManager;
    }

    public static HttpManager returningString(String returnedString) throws IOException, NoContentException, BadRequestException, UnauthorizedException, ForbiddenException, NotFoundException, TooManyRequestsException, InternalServerErrorException, BadGatewayException, ServiceUnavailableException {
      final HttpManager mockedHttpManager = mock(HttpManager.class);
      when(mockedHttpManager.get((UtilProtos.Url) any())).thenReturn(returnedString);
      when(mockedHttpManager.post((UtilProtos.Url) any())).thenReturn(returnedString);
      when(mockedHttpManager.put((UtilProtos.Url) any())).thenReturn(returnedString);
      when(mockedHttpManager.delete((UtilProtos.Url) any())).thenReturn(returnedString);
      return mockedHttpManager;
    }
  }
}