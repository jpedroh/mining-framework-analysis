package pl.maciejwalkowiak.plist;
import org.junit.Test;
import pl.maciejwalkowiak.plist.handler.Handler;
import pl.maciejwalkowiak.plist.strategy.UppercaseNamingStrategy;
import pl.maciejwalkowiak.plist.plistypes.PlistData;
import java.math.BigDecimal;
import java.util.*;
import static org.fest.assertions.Assertions.assertThat;

public class PlistSerializerImplTest {
  private PlistSerializerImpl plistSerializer = new PlistSerializerImpl();

  @Test public void testStringArraySerialization() {
    List<String> strings = Arrays.asList("string1", "string2", "string3");
    String xml = plistSerializer.serialize(strings);
    assertThat(xml).isEqualTo("<array><string>string1</string><string>string2</string><string>string3</string></array>");
  }

  @Test public void testIntegerListSerialization() {
    List<Integer> strings = Arrays.asList(4, 6, 8);
    String xml = plistSerializer.serialize(strings);
    assertThat(xml).isEqualTo("<array><integer>4</integer><integer>6</integer><integer>8</integer></array>");
  }

  @Test public void testObjectSerialization() {
    Post post = new Post(new Author("jason bourne"), "java-plist-serializer introduction", 9);
    post.addComment(new Comment("maciejwalkowiak", "first comment"));
    post.addComment(new Comment("john doe", "second comment"));
    String xml = plistSerializer.serialize(post);
    assertThat(xml).isEqualTo("<dict><key>author</key><dict><key>name</key><string>jason bourne</string></dict>" + "<key>comments</key><array>" + "<dict><key>author</key><string>maciejwalkowiak</string><key>content</key><string>first comment</string></dict>" + "<dict><key>author</key><string>john doe</string><key>content</key><string>second comment</string></dict></array>" + "<key>title</key><string>" + post.getTitle() + "</string>" + "<key>views</key><integer>" + post.getViews() + "</integer>" + "</dict>");
  }

  @Test public void testNullSerialization() {
    assertThat(plistSerializer.serialize(null)).isEqualTo("");
  }

  @Test public void testStaticFieldSerialization() {
    ClassWithStaticFields classWithStaticFields = new ClassWithStaticFields();
    String result = plistSerializer.serialize(classWithStaticFields);
    assertThat(result).isEqualTo("<dict><key>serializableField</key><integer>1</integer></dict>");
  }

  @Test public void testAnnotations() {
    ClassWithAnnotations classWithAnnotations = new ClassWithAnnotations();
    String xml = plistSerializer.serialize(classWithAnnotations);
    assertThat(xml).isEqualTo("<dict><key>trick</key><string>i am renamed</string></dict>");
  }

  @Test public void testPlistRenameWithFollowingStrategy() {
    PlistSerializerImpl plistSerializer = new PlistSerializerImpl(new UppercaseNamingStrategy());
    AnnotatedClass annotatedClass = new AnnotatedClass();
    String xml = plistSerializer.serialize(annotatedClass);
    assertThat(xml).isEqualTo("<dict><key>FIRST_FIELD</key><string>i am following</string><key>secondField</key><string>i am not following</string></dict>");
  }

  @Test public void testFieldSerializationWhenNull() {
    Comment comment = new Comment(null, "content");
    String xml = plistSerializer.serialize(comment);
    assertThat(xml).isEqualTo("<dict><key>content</key><string>content</string></dict>");
  }

  @Test public void testToXML() {
    String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\"><plist version=\"1.0\">";
    String footer = "</plist>";
    String testObject = "testObject";
    String xml = plistSerializer.toXmlPlist(testObject);
    assertThat(xml).startsWith(header).endsWith(footer);
  }

  @Test public void testAdditionalHandler() {
    Handler bigDecimalHandler = new BigDecimalHandler();
    plistSerializer.setAdditionalHandlers(Arrays.asList(bigDecimalHandler));
    String xml = plistSerializer.serialize(new BigDecimal(3));
    assertThat(xml).isEqualTo("<integer>3</integer>");
  }

  @Test public void testDoubleSerializationHandler() {
    Double object = 4.55d;
    String xml = plistSerializer.serialize(object);
    assertThat(xml).isEqualTo("<real>4.55</real>");
  }

  @Test public void testInheritedFieldsSerialization() {
    FooChild object = new FooChild("test1", "test2");
    String xml = plistSerializer.serialize(object);
    assertThat(xml).isEqualTo("<dict><key>bar</key><string>test2</string><key>foo</key><string>test1</string></dict>");
  }

  @Test public void testPlistDataSerializationHandler() {
    byte[] data = new String("test").getBytes();
    PlistData object = new PlistData(data);
    String xml = plistSerializer.serialize(object);
    assertThat(xml).isEqualTo("<data>dGVzdA==</data>");
  }

  @Test public void testSupportedDataTypes() throws IllegalAccessException, InstantiationException {
    List supportedDataTypes = Arrays.asList(Integer.valueOf(1), Double.valueOf(1d), Float.valueOf(1f), Short.valueOf("1"), Long.valueOf(1l), Double.valueOf(1d), new Boolean(true), new Date(), new HashSet<String>(), new HashMap<String, String>());
    for (Object o : supportedDataTypes) {
      boolean isSupported = plistSerializer.getHandlerWrapper().isSupported(o);
      assertThat(isSupported).isTrue();
    }
  }
}