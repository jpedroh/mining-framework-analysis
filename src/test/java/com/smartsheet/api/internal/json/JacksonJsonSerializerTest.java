package com.smartsheet.api.internal.json;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.smartsheet.api.models.Folder;
import com.smartsheet.api.models.Result;
import com.smartsheet.api.models.User;
import javax.print.DocFlavor;
import static org.junit.Assert.*;

public class JacksonJsonSerializerTest {
  JacksonJsonSerializer jjs;

  @Before public void setUp() throws Exception {
    jjs = new JacksonJsonSerializer();
  }

  @Test public void testJacksonJsonSerializer() {
  }

  @Test public void testInit() {
  }

  @Test public void testSerialize() {
    try {
      try {
        jjs.serialize(null, new ByteArrayOutputStream());
        fail("should throw exception");
      } catch (IllegalArgumentException ex) {
      }
      try {
        jjs.serialize(new Object(), null);
        fail("should throw exception");
      } catch (IllegalArgumentException ex) {
      }
      try {
        jjs.serialize(null, null);
        fail("should throw exception");
      } catch (IllegalArgumentException ex) {
      }
    } catch (JSONSerializerException ex) {
      fail("Shouldn\'t have thrown this exception: " + ex);
    }
    try {
      jjs.serialize(new Object(), new ByteArrayOutputStream());
      fail("Should throw a JSONMappingException");
    } catch (JSONSerializerException ex) {
    }
    User user = new User();
    user.setEmail("test@test.com");
    try {
      jjs.serialize(user, new ByteArrayOutputStream());
    } catch (JSONSerializerException e) {
      fail("Shouldn\'t throw an exception");
    }
    User user1 = new User();
    user1.setId(123L);
    user1.setEmail("test@test.com");
    try {
      assertFalse("The id field should not be serialized. Instead the id is used in the url and not sent as part of the body.", jjs.serialize(user1).contains("id"));
    } catch (JSONSerializerException e) {
      fail("Shouldn\'t throw an exception");
    }
    File tempFile = null;
    try {
      tempFile = File.createTempFile("json_test", ".tmp");
      FileOutputStream fos = new FileOutputStream(tempFile);
      fos.close();
      try {
        jjs.serialize(user, fos);
      } catch (JSONSerializerException e) {
      }
    } catch (IOException e1) {
      fail("Trouble creating a temp file");
    }
  }

  @Test public void testDeserialize() throws JSONSerializerException, JsonParseException, JsonMappingException, IOException {
    try {
      try {
        jjs.deserialize(null, null);
        fail("Exception should have been thrown.");
      } catch (IllegalArgumentException e) {
      }
      try {
        jjs.deserialize(User.class, null);
        fail("Exception should have been thrown.");
      } catch (IllegalArgumentException e) {
      }
      try {
        jjs.deserialize(null, new ByteArrayInputStream(new byte[10]));
        fail("Exception should have been thrown.");
      } catch (IllegalArgumentException e) {
      }
    } catch (Exception ex) {
      fail("Exception should not be thrown: " + ex);
    }
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    User originalUser = new User();
    originalUser.setFirstName("Test");
    originalUser.setId(123L);
    jjs.serialize(originalUser, b);
    User user = jjs.deserialize(User.class, new ByteArrayInputStream(b.toByteArray()));
    assertEquals(originalUser.getFirstName(), user.getFirstName());
    assertNotEquals("The id was not deserialized into the User object.", (Long) originalUser.getId(), (Long) user.getId());
  }

  @Test public void testDeserializeMap() throws JSONSerializerException, FileNotFoundException, IOException {
    try {
      jjs.deserializeMap(null);
      fail("Exception should have been thrown.");
    } catch (IllegalArgumentException e) {
    }
    try {
      String str = "test";
      jjs.deserializeMap(new ByteArrayInputStream(str.getBytes()));
      fail("Exception should have been thrown.");
    } catch (JSONSerializerException e) {
    }
    String str = "[\"test\",\"test1\"]";
    try {
      jjs.deserializeMap(new ByteArrayInputStream(str.getBytes()));
      fail("Exception should have been thrown.");
    } catch (JSONSerializerException ex) {
    }
    try {
      FileInputStream fis = new FileInputStream(File.createTempFile("json_test", ".tmp"));
      fis.close();
      jjs.deserializeMap(fis);
      fail("Should have thrown an IOException");
    } catch (JSONSerializerException ex) {
    }
    str = "{\'key\':\'value\'},{\'key\':\'value\'}".replace("\'", "\"");
    jjs.deserializeMap(new ByteArrayInputStream(str.getBytes()));
  }

  @Test public void testDeserializeList() throws JsonParseException, IOException, JSONSerializerException {
    try {
      jjs.deserializeList(null, null);
      fail("Exception should have been thrown.");
    } catch (IllegalArgumentException e) {
    }
    try {
      jjs.deserializeList(ArrayList.class, null);
      fail("Exception should have been thrown.");
    } catch (IllegalArgumentException e) {
    }
    try {
      jjs.deserializeList(null, new ByteArrayInputStream(new byte[10]));
      fail("Exception should have been thrown.");
    } catch (IllegalArgumentException e) {
    }
    try {
      jjs.deserializeList(List.class, new ByteArrayInputStream("[broken jason".getBytes()));
      fail("Should have thrown a JsonParseException");
    } catch (JSONSerializerException e) {
    }
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    User originalUser = new User();
    jjs.serialize(originalUser, b);
    try {
      jjs.deserializeList(ArrayList.class, new ByteArrayInputStream(b.toByteArray()));
      fail("Exception should have been thrown.");
    } catch (JSONSerializerException ex) {
    }
    jjs = new JacksonJsonSerializer();
    List<String> originalList = new ArrayList<String>();
    originalList.add("something");
    originalList.add("something-else");
    b = new ByteArrayOutputStream();
    jjs.serialize(originalList, b);
    List<String> newList = jjs.deserializeList(String.class, new ByteArrayInputStream(b.toByteArray()));
    if (!newList.equals(originalList)) {
      fail("Types should be identical. Serialization/Deserialation might have failed.");
    }
    try {
      FileInputStream fis = new FileInputStream(File.createTempFile("json_test", ".tmp"));
      fis.close();
      jjs.deserializeList(List.class, fis);
      fail("Should have thrown an IOException");
    } catch (JSONSerializerException ex) {
    }
  }

  @Test public void testDeserializeResult() {
    try {
      try {
        jjs.deserializeResult(null, null);
        fail("Exception should have been thrown.");
      } catch (IllegalArgumentException e) {
      }
      try {
        jjs.deserializeResult(User.class, null);
        fail("Exception should have been thrown.");
      } catch (IllegalArgumentException e) {
      }
      try {
        jjs.deserializeResult(null, new ByteArrayInputStream(new byte[10]));
        fail("Exception should have been thrown.");
      } catch (IllegalArgumentException e) {
      }
    } catch (Exception ex) {
      fail("Exception should not be thrown: " + ex);
    }
    Result<Folder> result = new Result<Folder>();
    result.setMessage("Test Result");
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      jjs.serialize(result, outputStream);
      jjs.deserializeResult(Result.class, new ByteArrayInputStream(outputStream.toByteArray()));
    } catch (JSONSerializerException ex) {
      fail("Exception should not be thrown: " + ex);
    }
    try {
      outputStream = new ByteArrayOutputStream();
      ArrayList<User> users = new ArrayList<User>();
      jjs.serialize(users, outputStream);
      jjs.deserializeResult(Result.class, new ByteArrayInputStream(outputStream.toByteArray()));
      fail("Exception should have been thrown");
    } catch (JSONSerializerException ex) {
    }
    try {
      FileInputStream fis = null;
      try {
        fis = new FileInputStream(File.createTempFile("json_test", ".tmp"));
        fis.close();
      } catch (Exception ex) {
        fail("Issue running a test where a temp file is being created." + ex);
      }
      jjs.deserializeResult(Result.class, fis);
      fail("Should have thrown an IOException");
    } catch (JSONSerializerException ex) {
    }
    try {
      jjs.deserializeResult(Result.class, new ByteArrayInputStream("{oops it\'s broken".getBytes()));
      fail("Should have thrown a JsonParseException");
    } catch (JSONSerializerException e) {
    }
  }

  @Test public void testDeserializeListResult() {
    try {
      try {
        jjs.deserializeListResult(null, null);
        fail("Exception should have been thrown.");
      } catch (IllegalArgumentException e) {
      }
      try {
        jjs.deserializeListResult(User.class, null);
        fail("Exception should have been thrown.");
      } catch (IllegalArgumentException e) {
      }
      try {
        jjs.deserializeListResult(null, new ByteArrayInputStream(new byte[10]));
        fail("Exception should have been thrown.");
      } catch (IllegalArgumentException e) {
      }
    } catch (Exception ex) {
      fail("Exception should not be thrown: " + ex);
    }
    Result<ArrayList<Object>> result = new Result<ArrayList<Object>>();
    result.setMessage("Test Message");
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      jjs.serialize(result, outputStream);
      jjs.deserializeListResult(Result.class, new ByteArrayInputStream(outputStream.toByteArray()));
    } catch (JSONSerializerException ex) {
      fail("Exception should not be thrown: " + ex);
    }
    try {
      FileInputStream fis = null;
      try {
        fis = new FileInputStream(File.createTempFile("json_test", ".tmp"));
        fis.close();
      } catch (Exception ex) {
        fail("Issue running a test where a temp file is being created." + ex);
      }
      jjs.deserializeListResult(Result.class, fis);
      fail("Should have thrown an IOException");
    } catch (JSONSerializerException ex) {
    }
    try {
      outputStream = new ByteArrayOutputStream();
      ArrayList<User> users = new ArrayList<User>();
      jjs.serialize(users, outputStream);
      jjs.deserializeListResult(Result.class, new ByteArrayInputStream(outputStream.toByteArray()));
      fail("Exception should have been thrown");
    } catch (JSONSerializerException ex) {
    }
    try {
      jjs.deserializeListResult(Result.class, new ByteArrayInputStream("{bad json".getBytes()));
      fail("Should have thrown a JsonParseException");
    } catch (JSONSerializerException e) {
    }
  }
}