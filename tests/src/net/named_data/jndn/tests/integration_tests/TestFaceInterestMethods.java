package src.net.named_data.jndn.tests.integration_tests;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnTimeout;
import net.named_data.jndn.encoding.EncodingException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

class CallbackCounter implements OnData, OnTimeout {
  public void onData(Interest interest, Data data) {
    interest_ = interest;
    data_ = data;
    ++onDataCallCount_;
  }

  public void onTimeout(Interest interest) {
    interest_ = interest;
    ++onTimeoutCallCount_;
  }

  public int onDataCallCount_ = 0;

  public int onTimeoutCallCount_ = 0;

  public Interest interest_;

  public Data data_;
}

public class TestFaceInterestMethods {
  public static double getNowMilliseconds() {
    return (double) System.currentTimeMillis();
  }

  private static CallbackCounter runExpressNameTest(Face face, String interestName, double timeout) {
    Name name = new Name(interestName);
    CallbackCounter counter = new CallbackCounter();
    try {
      face.expressInterest(name, counter, counter);
    } catch (IOException ex) {
      Logger.getLogger(TestFaceInterestMethods.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
    double startTime = getNowMilliseconds();
    while (getNowMilliseconds() - startTime < timeout && counter.onDataCallCount_ == 0 && counter.onTimeoutCallCount_ == 0) {
      try {
        try {
          face.processEvents();
        } catch (IOException ex) {
          Logger.getLogger(TestFaceInterestMethods.class.getName()).log(Level.SEVERE, null, ex);
          break;
        } catch (EncodingException ex) {
          Logger.getLogger(TestFaceInterestMethods.class.getName()).log(Level.SEVERE, null, ex);
          break;
        }
        Thread.sleep(10);
      } catch (InterruptedException ex) {
        Logger.getLogger(TestFaceInterestMethods.class.getName()).log(Level.SEVERE, null, ex);
        break;
      }
    }
    return counter;
  }

  private static CallbackCounter runExpressNameTest(Face face, String interestName) {
    return runExpressNameTest(face, interestName, 10000);
  }

  Face face;

  @Before public void setUp() {
    face = new Face("localhost");
  }

  @Test public void testAnyInterest() {
    String uri = "/";
    CallbackCounter counter = runExpressNameTest(face, uri);
    assertTrue("Timeout on expressed interest", counter.onTimeoutCallCount_ == 0);
    assertEquals("Expected 1 onData callback, got " + counter.onDataCallCount_, 1, counter.onDataCallCount_);
    Interest callbackInterest = counter.interest_;
    assertTrue("Interest returned on callback had different name", callbackInterest.getName().equals(new Name(uri)));
  }

  @Test public void testTimeout() {
    String uri = "/test/timeout";
    CallbackCounter counter = runExpressNameTest(face, uri);
    assertEquals("Data callback called for invalid interest", 0, counter.onDataCallCount_);
    assertTrue("Expected 1 timeout call, got " + counter.onTimeoutCallCount_, counter.onTimeoutCallCount_ == 1);
    Interest callbackInterest = counter.interest_;
    assertTrue("Interest returned on callback had different name", callbackInterest.getName().equals(new Name(uri)));
  }

  @Test public void testRemovePending() {
    Name name = new Name("/ndn/edu/ucla/remap/");
    CallbackCounter counter = new CallbackCounter();
    long interestID;
    try {
      interestID = face.expressInterest(name, counter, counter);
    } catch (IOException ex) {
      fail("Error in expressInterest: " + ex);
      return;
    }
    face.removePendingInterest(interestID);
    double timeout = 10000;
    double startTime = getNowMilliseconds();
    while (getNowMilliseconds() - startTime < timeout && counter.onDataCallCount_ == 0 && counter.onTimeoutCallCount_ == 0) {
      try {
        face.processEvents();
      } catch (IOException ex) {
        fail("Error in processEvents: " + ex);
        return;
      } catch (EncodingException ex) {
        fail("Error in processEvents: " + ex);
        return;
      }
      try {
        Thread.sleep(10);
      } catch (InterruptedException ex) {
        fail("Error in sleep: " + ex);
        return;
      }
    }
    assertEquals("Should not have called data callback after interest was removed", 0, counter.onDataCallCount_);
    assertTrue("Should not have called timeout callback after interest was removed", counter.onTimeoutCallCount_ == 0);
  }

  @Test public void testMaxNdnPacketSize() throws IOException {
    int targetSize = Face.getMaxNdnPacketSize() + 1;
    Interest interest = new Interest();
    interest.getName().append(new byte[targetSize]);
    int initialSize = interest.wireEncode().size();
    interest.setName(new Name().append(new byte[targetSize - (initialSize - targetSize)]));
    int interestSize = interest.wireEncode().size();
    assertEquals("Wrong interest size for MaxNdnPacketSize", targetSize, interestSize);
    CallbackCounter counter = new CallbackCounter();
    boolean gotError = true;
    try {
      face.expressInterest(interest, counter, counter);
      gotError = false;
    } catch (Error ex) {
    }
    if (!gotError) {
      fail("expressInterest didn\'t throw an exception when the interest size exceeds getMaxNdnPacketSize()");
    }
  }
}