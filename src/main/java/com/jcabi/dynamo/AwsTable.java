package com.jcabi.dynamo;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.immutable.Array;
import com.jcabi.log.Logger;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Single table in Dynamo, through AWS SDK.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCoupling (150 lines)
 * @since 0.1
 */
@Immutable @Loggable(value = Loggable.DEBUG) @ToString @EqualsAndHashCode(of = { "credentials", "reg", "self" }) final class AwsTable implements Table {
  /**
     * AWS credentials.
     */
  private final transient Credentials credentials;

  /**
     * Region.
     */
  private final transient Region reg;

  /**
     * Table name.
     */
  private final transient String self;

  /**
     * Public ctor.
     * @param creds Credentials
     * @param region Region
     * @param table Table name
     */
  AwsTable(final Credentials creds, final Region region, final String table) {
    this.credentials = creds;
    this.reg = region;
    this.self = table;
  }

  @Override public Item put(final Map<String, AttributeValue> attributes) throws IOException {
    final AmazonDynamoDB aws = this.credentials.aws();
    try {
      final PutItemRequest request = new PutItemRequest();
      request.setTableName(this.self);
      request.setItem(attributes);
      request.setReturnValues(ReturnValue.NONE);
      request.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
      final PutItemResult result = aws.putItem(request);
      final long start = System.currentTimeMillis();
      Logger.info(this, "#put(\'%[text]s\'): created item in \'%s\', %s, in %[ms]s", attributes, this.self, new PrintableConsumedCapacity(result.getConsumedCapacity()).print(), System.currentTimeMillis() - start);
      return new AwsItem(this.credentials, this.frame(), this.self, new Attributes(attributes).only(this.keys()), new Array<String>(this.keys()));
    } catch (final AmazonClientException ex) {
      throw new IOException(String.format("failed to put into \"%s\" with %s", this.self, attributes), ex);
    } finally {
      aws.shutdown();
    }
  }

  @Override public Region region() {
    return this.reg;
  }

  @Override public AwsFrame frame() {
    return new AwsFrame(this.credentials, this, this.self);
  }

  @Override public String name() {
    return this.self;
  }

  /**
     * Get names of keys.
     * @return Names of attributes, which are primary keys
     * @throws IOException If DynamoDB fails
     */
  @Cacheable(forever = true) public Collection<String> keys() throws IOException {
    final AmazonDynamoDB aws = this.credentials.aws();
    try {
      final long start = System.currentTimeMillis();
      final DescribeTableResult result = aws.describeTable(new DescribeTableRequest().withTableName(this.self));
      final Collection<String> keys = new LinkedList<String>();
      for (final KeySchemaElement key : result.getTable().getKeySchema()) {
        keys.add(key.getAttributeName());
      }
      Logger.info(this, "#keys(): table %s described, in %[ms]s", this.self, System.currentTimeMillis() - start);
      return keys;
    } catch (final AmazonClientException ex) {
      throw new IOException(String.format("failed to describe \"%s\"", this.self), ex);
    } finally {
      aws.shutdown();
    }
  }


<<<<<<< /usr/src/app/output/jcabi/jcabi-dynamo/a3e4660c7f264db9a33a64604ecc70a35afabe0e/src/main/java/com/jcabi/dynamo/AwsTable.java/left.java
  /**
     * Print consumed capacity nicely.
     * @param capacity Consumed capacity or NULL
     * @return Suffix to add to a log line
     */
  public static String print(final ConsumedCapacity capacity) {
    final String txt;
    if (capacity == null) {
      txt = "no units";
    } else {
      txt = String.format("%.2f units", capacity.getCapacityUnits());
    }
    return txt;
  }
=======
>>>>>>> Unknown file: This is a bug in JDime.


  @Override public void delete(final Map<String, AttributeValue> attributes) throws IOException {
    final AmazonDynamoDB aws = this.credentials.aws();
    try {
      final DeleteItemRequest request = new DeleteItemRequest();
      request.setTableName(this.self);
      request.setKey(attributes);
      request.setReturnValues(ReturnValue.NONE);
      request.setReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);
      final DeleteItemResult result = aws.deleteItem(request);
      final long start = System.currentTimeMillis();
      Logger.info(this, "#delete(\'%[text]s\'): deleted item in \'%s\', %s, in %[ms]s", attributes, this.self, new PrintableConsumedCapacity(result.getConsumedCapacity()).print(), System.currentTimeMillis() - start);
    } catch (final AmazonClientException ex) {
      throw new IOException(String.format("failed to delete at \"%s\" by keys %s", this.self, attributes), ex);
    } finally {
      aws.shutdown();
    }
  }
}