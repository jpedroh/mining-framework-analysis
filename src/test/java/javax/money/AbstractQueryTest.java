package javax.money;
import org.testng.annotations.Test;
import java.time.Instant;
import java.util.List;
import static org.testng.Assert.*;
import java.time.LocalDateTime;

public class AbstractQueryTest {
  private AbstractQueryBuilder<AbstractQueryBuilder, AbstractQuery> createBuilder() {
    return new AbstractQueryBuilder() {
      @Override public AbstractQuery build() {
        return new AbstractQuery(this) { };
      }
    };
  }

  @Test public void testGetProviders() throws Exception {
    AbstractQueryBuilder b = createBuilder();
    b.setProviderNames("1", "2", "3");
    AbstractQuery query = b.build();
    assertNotNull(query);
    List<String> providers = query.getProviderNames();
    assertNotNull(providers);
    assertTrue(providers.size() == 3);
    assertTrue(providers.contains("1"));
    assertTrue(providers.contains("2"));
    assertTrue(providers.contains("3"));
  }

  @Test public void testGetTargetType() throws Exception {
    AbstractQueryBuilder b = createBuilder();
    b.setTargetType(String.class);
    AbstractQuery query = b.build();
    assertEquals(query.getTargetType(), String.class);
  }

  @Test public void testGetTimestampMillis() throws Exception {
    AbstractQueryBuilder b = createBuilder();
    b.setTimestampMillis(200L);
    AbstractQuery query = b.build();
    assertEquals(query.getTimestampMillis().longValue(), 200L);
    LocalDateTime localDateTime = LocalDateTime.parse("1969-12-31T21:00:00.200");
    assertEquals(query.getTimestamp(), localDateTime);
  }

  @Test public void testGetTimestamp() throws Exception {
    AbstractQueryBuilder b = createBuilder();
    LocalDateTime instant = LocalDateTime.now();
    b.setTimestamp(instant);
    AbstractQuery query = b.build();
    assertEquals(query.getTimestamp(), instant);
    assertEquals(query.getTimestamp(), instant);
  }
}