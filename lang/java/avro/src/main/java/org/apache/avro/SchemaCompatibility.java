package org.apache.avro;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evaluate the compatibility between a reader schema and a writer schema.
 * A reader and a writer schema are declared compatible if all datum instances of the writer
 * schema can be successfully decoded using the specified reader schema.
 */
public class SchemaCompatibility {
  private static final Logger LOG = LoggerFactory.getLogger(SchemaCompatibility.class);

  /** Utility class cannot be instantiated. */
  private SchemaCompatibility() {
  }

  /** Message to annotate reader/writer schema pairs that are compatible. */
  public static final String READER_WRITER_COMPATIBLE_MESSAGE = "Reader schema can always successfully decode data written using the writer schema.";

  /**
   * Validates that the provided reader schema can be used to decode avro data written with the
   * provided writer schema.
   *
   * @param reader schema to check.
   * @param writer schema to check.
   * @return a result object identifying any compatibility errors.
   */
  public static SchemaPairCompatibility checkReaderWriterCompatibility(final Schema reader, final Schema writer) {
    final SchemaCompatibilityResult compatibility = new ReaderWriterCompatiblityChecker().getCompatibility(reader, writer);
    final String message;
    switch (compatibility.getCompatibility()) {
      case INCOMPATIBLE:
      {
        message = String.format("Data encoded using writer schema:%n%s%n" + "will or may fail to decode using reader schema:%n%s%n", writer.toString(true), reader.toString(true));
        break;
      }
      case COMPATIBLE:
      {
        message = READER_WRITER_COMPATIBLE_MESSAGE;
        break;
      }
      default:
      throw new AvroRuntimeException("Unknown compatibility: " + compatibility);
    }
    return new SchemaPairCompatibility(compatibility, reader, writer, message);
  }

  /**
   * Tests the equality of two Avro named schemas.
   *
   * <p> Matching includes reader name aliases. </p>
   *
   * @param reader Named reader schema.
   * @param writer Named writer schema.
   * @return whether the names of the named schemas match or not.
   */
  public static boolean schemaNameEquals(final Schema reader, final Schema writer) {
    final String writerFullName = writer.getFullName();
    if (objectsEqual(reader.getFullName(), writerFullName)) {
      return true;
    }
    if (reader.getAliases().contains(writerFullName)) {
      return true;
    }
    return false;
  }

  /**
   * Identifies the writer field that corresponds to the specified reader field.
   *
   * <p> Matching includes reader name aliases. </p>
   *
   * @param writerSchema Schema of the record where to look for the writer field.
   * @param readerField Reader field to identify the corresponding writer field of.
   * @return the writer field, if any does correspond, or None.
   */
  public static Field lookupWriterField(final Schema writerSchema, final Field readerField) {
    assert (writerSchema.getType() == Type.RECORD);
    final List<Field> writerFields = new ArrayList<Field>();
    final Field direct = writerSchema.getField(readerField.name());
    if (direct != null) {
      writerFields.add(direct);
    }
    for (final String readerFieldAliasName : readerField.aliases()) {
      final Field writerField = writerSchema.getField(readerFieldAliasName);
      if (writerField != null) {
        writerFields.add(writerField);
      }
    }
    switch (writerFields.size()) {
      case 0:
      return null;
      case 1:
      return writerFields.get(0);
      default:
      {
        throw new AvroRuntimeException(String.format("Reader record field %s matches multiple fields in writer record schema %s", readerField, writerSchema));
      }
    }
  }

  private static final class ReaderWriter {
    private final Schema mReader;

    private final Schema mWriter;

    /**
     * Initializes a new reader/writer pair.
     *
     * @param reader Reader schema.
     * @param writer Writer schema.
     */
    public ReaderWriter(final Schema reader, final Schema writer) {
      mReader = reader;
      mWriter = writer;
    }

    /**
     * Returns the reader schema in this pair.
     * @return the reader schema in this pair.
     */
    public Schema getReader() {
      return mReader;
    }

    /**
     * Returns the writer schema in this pair.
     * @return the writer schema in this pair.
     */
    public Schema getWriter() {
      return mWriter;
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
      return System.identityHashCode(mReader) ^ System.identityHashCode(mWriter);
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object obj) {
      if (!(obj instanceof ReaderWriter)) {
        return false;
      }
      final ReaderWriter that = (ReaderWriter) obj;
      return (this.mReader == that.mReader) && (this.mWriter == that.mWriter);
    }

    /** {@inheritDoc} */
    @Override public String toString() {
      return String.format("ReaderWriter{reader:%s, writer:%s}", mReader, mWriter);
    }
  }

  private static final class ReaderWriterCompatiblityChecker {
    private static final String ROOT_REFERENCE_TOKEN = "";

    private final Map<ReaderWriter, SchemaCompatibilityResult> mMemoizeMap = new HashMap<ReaderWriter, SchemaCompatibilityResult>();

    /**
     * Reports the compatibility of a reader/writer schema pair.
     *
     * <p> Memoizes the compatibility results. </p>
     *
     * @param reader Reader schema to test.
     * @param writer Writer schema to test.
     * @return the compatibility of the reader/writer schema pair.
     */
    public SchemaCompatibilityResult getCompatibility(final Schema reader, final Schema writer) {
      Stack<String> location = new Stack<String>();
      return getCompatibility(ROOT_REFERENCE_TOKEN, reader, writer, location);
    }

    /**
     * Reports the compatibility of a reader/writer schema pair.
     * <p>
     * Memoizes the compatibility results.
     * </p>
     * @param referenceToken The equivalent JSON pointer reference token representation of the schema node being visited.
     * @param reader Reader schema to test.
     * @param writer Writer schema to test.
     * @param location Stack with which to track the location within the schema.
     * @return the compatibility of the reader/writer schema pair.
     */
    private SchemaCompatibilityResult getCompatibility(String referenceToken, final Schema reader, final Schema writer, final Stack<String> location) {
      location.push(referenceToken);
      LOG.debug("Checking compatibility of reader {} with writer {}", reader, writer);
      final ReaderWriter pair = new ReaderWriter(reader, writer);
      SchemaCompatibilityResult result = mMemoizeMap.get(pair);
      if (result != null) {
        if (
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
        result
=======
        existing
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
        .getCompatibility() == SchemaCompatibilityType.RECURSION_IN_PROGRESS) {

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result = SchemaCompatibilityResult.compatible();
=======
          return SchemaCompatibilityResult.compatible();
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
        }
      } else {
        mMemoizeMap.put(pair, SchemaCompatibilityResult.recursionInProgress());
        result = calculateCompatibility(reader, writer, location);
        mMemoizeMap.put(pair, result);
      }

<<<<<<< Unknown file: This is a bug in JDime.
=======
      mMemoizeMap.put(pair, SchemaCompatibilityResult.recursionInProgress());
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
      final SchemaCompatibilityResult calculated = calculateCompatibility(reader, writer);
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java

      location.pop();
      return result;
    }

    /**
     * Calculates the compatibility of a reader/writer schema pair.
     *
     * <p>
     * Relies on external memoization performed by {@link #getCompatibility(Schema, Schema)}.
     * </p>
     *
     * @param reader Reader schema to test.
     * @param writer Writer schema to test.
     * @param location Stack with which to track the location within the schema.
     * @return the compatibility of the reader/writer schema pair.
     */
    private SchemaCompatibilityResult calculateCompatibility(final Schema reader, final Schema writer, final Stack<String> location) {
      assert (reader != null);
      assert (writer != null);
      SchemaCompatibilityResult result = SchemaCompatibilityResult.compatible();
      if (reader.getType() == writer.getType()) {
        switch (reader.getType()) {
          case NULL:
          case BOOLEAN:
          case INT:
          case LONG:
          case FLOAT:
          case DOUBLE:
          case BYTES:
          case STRING:
          {
            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result
=======
            SchemaCompatibilityResult.compatible()
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          case ARRAY:
          {
            return result.mergedWith(getCompatibility("items", reader.getElementType(), writer.getElementType(), location));
          }
          case MAP:
          {
            return result.mergedWith(getCompatibility("values", reader.getValueType(), writer.getValueType(), location));
          }
          case FIXED:
          {

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result = result.mergedWith(checkSchemaNames(reader, writer, location));
=======
            SchemaCompatibilityResult nameCheck = checkSchemaNames(reader, writer);
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
            if (nameCheck.getCompatibility() == SchemaCompatibilityType.INCOMPATIBLE) {
              return nameCheck;
            }
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java

            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result.mergedWith(checkFixedSize(reader, writer, location))
=======
            checkFixedSize(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          case ENUM:
          {

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result = result.mergedWith(checkSchemaNames(reader, writer, location));
=======
            SchemaCompatibilityResult nameCheck = checkSchemaNames(reader, writer);
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
            if (nameCheck.getCompatibility() == SchemaCompatibilityType.INCOMPATIBLE) {
              return nameCheck;
            }
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java

            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result.mergedWith(checkReaderEnumContainsAllWriterEnumSymbols(reader, writer, location))
=======
            checkReaderEnumContainsAllWriterEnumSymbols(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          case RECORD:
          {

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result = result.mergedWith(checkSchemaNames(reader, writer, location));
=======
            SchemaCompatibilityResult nameCheck = checkSchemaNames(reader, writer);
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
            if (nameCheck.getCompatibility() == SchemaCompatibilityType.INCOMPATIBLE) {
              return nameCheck;
            }
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java

            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result.mergedWith(checkReaderWriterRecordFields(reader, writer, location))
=======
            checkReaderWriterRecordFields(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          case UNION:
          {
            int i = 0;
            for (final Schema writerBranch : writer.getTypes()) {
              location.push(Integer.toString(i));
              SchemaCompatibilityResult compatibility = getCompatibility(reader, writerBranch);
              if (compatibility.getCompatibility() == SchemaCompatibilityType.INCOMPATIBLE) {
                String 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
                message = String.format("reader union lacking writer type: %s", writerBranch.getType())
=======
                msg = String.format("reader union lacking writer type: %s", writerBranch.getType())
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
                ;

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
                result = result.mergedWith(SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.MISSING_UNION_BRANCH, reader, writer, message, location));
=======
                return SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.MISSING_UNION_BRANCH, reader, writer, msg);
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
              }
              location.pop();
              i++;
            }
            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result
=======
            SchemaCompatibilityResult.compatible()
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          default:
          {
            throw new AvroRuntimeException("Unknown schema type: " + reader.getType());
          }
        }
      } else {
        if (writer.getType() == Schema.Type.UNION) {
          for (Schema s : writer.getTypes()) {

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result = result.mergedWith(getCompatibility(reader, s));
=======
            SchemaCompatibilityResult compat = getCompatibility(reader, s);
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java

            if (compat.getCompatibility() == SchemaCompatibilityType.INCOMPATIBLE) {
              return compat;
            }
          }
          return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result
=======
          SchemaCompatibilityResult.compatible()
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
          ;
        }
        switch (reader.getType()) {
          case NULL:
          return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result.mergedWith(typeMismatch(reader, writer, location))
=======
          typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
          ;
          case BOOLEAN:
          return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result.mergedWith(typeMismatch(reader, writer, location))
=======
          typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
          ;
          case INT:
          return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result.mergedWith(typeMismatch(reader, writer, location))
=======
          typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
          ;
          case LONG:
          {
            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            (writer.getType() == Type.INT) ? result : result.mergedWith(typeMismatch(reader, writer, location))
=======
            (writer.getType() == Type.INT) ? SchemaCompatibilityResult.compatible() : typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          case FLOAT:
          {
            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            ((writer.getType() == Type.INT) || (writer.getType() == Type.LONG)) ? result : result.mergedWith(typeMismatch(reader, writer, location))
=======
            ((writer.getType() == Type.INT) || (writer.getType() == Type.LONG)) ? SchemaCompatibilityResult.compatible() : typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          case DOUBLE:
          {
            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            ((writer.getType() == Type.INT) || (writer.getType() == Type.LONG) || (writer.getType() == Type.FLOAT)) ? result : result.mergedWith(typeMismatch(reader, writer, location))
=======
            ((writer.getType() == Type.INT) || (writer.getType() == Type.LONG) || (writer.getType() == Type.FLOAT)) ? SchemaCompatibilityResult.compatible() : typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          case BYTES:
          {
            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            (writer.getType() == Type.STRING) ? result : result.mergedWith(typeMismatch(reader, writer, location))
=======
            (writer.getType() == Type.STRING) ? SchemaCompatibilityResult.compatible() : typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          case STRING:
          {
            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            (writer.getType() == Type.BYTES) ? result : result.mergedWith(typeMismatch(reader, writer, location))
=======
            (writer.getType() == Type.BYTES) ? SchemaCompatibilityResult.compatible() : typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          case ARRAY:
          return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result.mergedWith(typeMismatch(reader, writer, location))
=======
          typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
          ;
          case MAP:
          return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result.mergedWith(typeMismatch(reader, writer, location))
=======
          typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
          ;
          case FIXED:
          return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result.mergedWith(typeMismatch(reader, writer, location))
=======
          typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
          ;
          case ENUM:
          return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result.mergedWith(typeMismatch(reader, writer, location))
=======
          typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
          ;
          case RECORD:
          return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result.mergedWith(typeMismatch(reader, writer, location))
=======
          typeMismatch(reader, writer)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
          ;
          case UNION:
          {
            for (final Schema readerBranch : reader.getTypes()) {
              SchemaCompatibilityResult compatibility = getCompatibility(readerBranch, writer);
              if (compatibility.getCompatibility() == SchemaCompatibilityType.COMPATIBLE) {
                return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
                result
=======
                SchemaCompatibilityResult.compatible()
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
                ;
              }
            }
            String 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            message = String.format("reader union lacking writer type: %s", writer.getType())
=======
            msg = String.format("reader union lacking writer type: %s", writer.getType())
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
            return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result.mergedWith(SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.MISSING_UNION_BRANCH, reader, writer, message, location))
=======
            SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.MISSING_UNION_BRANCH, reader, writer, msg)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
            ;
          }
          default:
          {
            throw new AvroRuntimeException("Unknown schema type: " + reader.getType());
          }
        }
      }
    }

    private SchemaCompatibilityResult checkReaderWriterRecordFields(final Schema reader, final Schema writer, final Stack<String> location) {
      SchemaCompatibilityResult result = SchemaCompatibilityResult.compatible();
      location.push("fields");
      for (final Field readerField : reader.getFields()) {
        location.push(Integer.toString(readerField.pos()));
        final Field writerField = lookupWriterField(writer, readerField);
        if (writerField == null) {
          if (readerField.defaultValue() == null) {

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
            result = result.mergedWith(SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.READER_FIELD_MISSING_DEFAULT_VALUE, reader, writer, readerField.name(), location));
=======
            return SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.READER_FIELD_MISSING_DEFAULT_VALUE, reader, writer, readerField.name());
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
          }
        } else {

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
          result = result.mergedWith(getCompatibility("type", readerField.schema(), writerField.schema(), location));
=======
          SchemaCompatibilityResult compatibility = getCompatibility(readerField.schema(), writerField.schema());
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java

          if (compatibility.getCompatibility() == SchemaCompatibilityType.INCOMPATIBLE) {
            return compatibility;
          }
        }
        location.pop();
      }
      location.pop();
      return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      result
=======
      SchemaCompatibilityResult.compatible()
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      ;
    }

    private SchemaCompatibilityResult checkReaderEnumContainsAllWriterEnumSymbols(final Schema reader, final Schema writer, final Stack<String> location) {
      SchemaCompatibilityResult result = SchemaCompatibilityResult.compatible();
      location.push("symbols");
      final Set<String> symbols = 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      new TreeSet<String>(writer.getEnumSymbols())
=======
      new TreeSet<>(writer.getEnumSymbols())
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      ;
      symbols.removeAll(reader.getEnumSymbols());
      if (!symbols.isEmpty()) {
        result = SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.MISSING_ENUM_SYMBOLS, reader, writer, symbols.toString(), location);
      }
      location.pop();
      return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      result
=======
      symbols.isEmpty() ? SchemaCompatibilityResult.compatible() : SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.MISSING_ENUM_SYMBOLS, reader, writer, symbols.toString())
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      ;
    }

    private SchemaCompatibilityResult checkFixedSize(final Schema reader, final Schema writer, final Stack<String> location) {
      SchemaCompatibilityResult result = SchemaCompatibilityResult.compatible();
      location.push("size");
      int actual = reader.getFixedSize();
      int expected = writer.getFixedSize();
      if (actual != expected) {
        String 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
        message = String.format("expected: %d, found: %d", expected, actual)
=======
        msg = String.format("expected: %d, found: %d", expected, actual)
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
        ;

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
        result = SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.FIXED_SIZE_MISMATCH, reader, writer, message, location);
=======
        return SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.FIXED_SIZE_MISMATCH, reader, writer, msg);
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      }
      location.pop();
      return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      result
=======
      SchemaCompatibilityResult.compatible()
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      ;
    }

    private SchemaCompatibilityResult checkSchemaNames(final Schema reader, final Schema writer, final Stack<String> location) {
      SchemaCompatibilityResult result = SchemaCompatibilityResult.compatible();
      location.push("name");
      if (!schemaNameEquals(reader, writer)) {
        String 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
        message = String.format("expected: %s", writer.getFullName())
=======
        msg = String.format("expected: %s", writer.getFullName())
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
        ;

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
        result = SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.NAME_MISMATCH, reader, writer, message, location);
=======
        return SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.NAME_MISMATCH, reader, writer, msg);
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      }
      location.pop();
      return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      result
=======
      SchemaCompatibilityResult.compatible()
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      ;
    }

    private SchemaCompatibilityResult typeMismatch(final Schema reader, final Schema writer, final Stack<String> location) {
      String 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      message = String.format("reader type: %s not compatible with writer type: %s", reader.getType(), writer.getType())
=======
      msg = String.format("reader type: %s not compatible with writer type: %s", reader.getType(), writer.getType())
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      ;
      return SchemaCompatibilityResult.incompatible(SchemaIncompatibilityType.TYPE_MISMATCH, reader, writer, 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      message
=======
      msg
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      , location);
    }
  }

  public enum SchemaCompatibilityType {
    COMPATIBLE,
    INCOMPATIBLE,
    RECURSION_IN_PROGRESS
  }

  public enum SchemaIncompatibilityType {
    NAME_MISMATCH,
    FIXED_SIZE_MISMATCH,
    MISSING_ENUM_SYMBOLS,
    READER_FIELD_MISSING_DEFAULT_VALUE,
    TYPE_MISMATCH,
    MISSING_UNION_BRANCH
  }

  public static final class SchemaCompatibilityResult {
    /**
     * Merges the current {@code SchemaCompatibilityResult} with the supplied result into a new instance, combining the
     * list of {@code Incompatibility Incompatibilities} and regressing to the
     * {@code SchemaCompatibilityType#INCOMPATIBLE INCOMPATIBLE} state if any incompatibilities are encountered.
     *
     * @param toMerge The {@code SchemaCompatibilityResult} to merge with the current instance.
     * @return A {@code SchemaCompatibilityResult} that combines the state of the current and supplied instances.
     */
    public SchemaCompatibilityResult mergedWith(SchemaCompatibilityResult toMerge) {
      List<Incompatibility> mergedIncompatibilities = new ArrayList<Incompatibility>(mIncompatibilities);
      mergedIncompatibilities.addAll(toMerge.getIncompatibilities());
      SchemaCompatibilityType compatibilityType = mCompatibilityType == SchemaCompatibilityType.COMPATIBLE && toMerge.mCompatibilityType == SchemaCompatibilityType.COMPATIBLE ? SchemaCompatibilityType.COMPATIBLE : SchemaCompatibilityType.INCOMPATIBLE;
      return new SchemaCompatibilityResult(compatibilityType, mergedIncompatibilities);
    }

    private final SchemaCompatibilityType 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
    mCompatibilityType
=======
    mCompatibility
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
    ;


<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
    private final List<Incompatibility> mIncompatibilities;
=======
    private final SchemaIncompatibilityType mSchemaIncompatibilityType;
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java


    private final Schema mReaderSubset;

    private final Schema mWriterSubset;

    private final String mMessage;

    private static final SchemaCompatibilityResult COMPATIBLE = new SchemaCompatibilityResult(SchemaCompatibilityType.COMPATIBLE, 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
    Collections.<Incompatibility>emptyList()
=======
    null
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
    , null, null, null);

    private static final SchemaCompatibilityResult RECURSION_IN_PROGRESS = new SchemaCompatibilityResult(SchemaCompatibilityType.RECURSION_IN_PROGRESS, 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
    Collections.<Incompatibility>emptyList()
=======
    null
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
    , null, null, null);

    private SchemaCompatibilityResult(
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
    SchemaCompatibilityType compatibilityType
=======
    SchemaCompatibilityType type
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
    , 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
    List<Incompatibility> incompatibilities
=======
    SchemaIncompatibilityType errorDetails
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
    , Schema readerDetails, Schema writerDetails, String details) {
      this.mCompatibility = type;
      this.mSchemaIncompatibilityType = errorDetails;
      this.mReaderSubset = readerDetails;
      this.
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      mCompatibilityType
=======
      mWriterSubset
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
       = 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      compatibilityType
=======
      writerDetails
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      ;
      this.
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      mIncompatibilities
=======
      mMessage
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
       = 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      incompatibilities
=======
      details
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      ;
    }

    /**
     * Returns a details object representing a compatible schema pair.
     * @return a SchemaCompatibilityResult object with COMPATIBLE
     *         SchemaCompatibilityType, and no other state.
     */
    public static SchemaCompatibilityResult compatible() {
      return COMPATIBLE;
    }

    /**
     * Returns a details object representing a state indicating that recursion
     * is in progress.
     * @return a SchemaCompatibilityResult object with RECURSION_IN_PROGRESS
     *         SchemaCompatibilityType, and no other state.
     */
    public static SchemaCompatibilityResult recursionInProgress() {
      return RECURSION_IN_PROGRESS;
    }

    /**
     * Returns a details object representing an incompatible schema pair,
     * including error details.
     * @param incompatibilityType
     * @param readerFragment
     * @param writerFragment
     * @param message
     * @param location
     * @return a SchemaCompatibilityResult object with INCOMPATIBLE
     *         SchemaCompatibilityType, and state representing the violating
     *         parts.
     */
    public static SchemaCompatibilityResult incompatible(
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
    SchemaIncompatibilityType incompatibilityType
=======
    SchemaIncompatibilityType error
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
    , 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
    Schema readerFragment
=======
    Schema reader
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
    , 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
    Schema writerFragment
=======
    Schema writer
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
    , 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
    String message
=======
    String details
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
    , List<String> location) {
      Incompatibility incompatibility = new Incompatibility(incompatibilityType, readerFragment, writerFragment, message, location);
      return new SchemaCompatibilityResult(SchemaCompatibilityType.INCOMPATIBLE, 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      Collections.singletonList(incompatibility)
=======
      error
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      , reader, writer, details);
    }

    /**
     * Returns the SchemaCompatibilityType, always non-null.
     * @return a SchemaCompatibilityType instance, always non-null
     */
    public SchemaCompatibilityType getCompatibility() {
      return 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      mCompatibilityType
=======
      mCompatibility
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      ;
    }

    /**
     * If the compatibility is INCOMPATIBLE, returns {@link Incompatibility Incompatibilities} found, otherwise an empty
     * list.
     * @return a list of {@link Incompatibility Incompatibilities}, may be empty, never null.
     */
    public List<Incompatibility> getIncompatibilities() {
      return mIncompatibilities;
    }

    /**
     * If the compatibility is INCOMPATIBLE, returns the SchemaIncompatibilityType (first thing that
     * was incompatible), otherwise null.
     * @return a SchemaIncompatibilityType instance, or null
     */
    public SchemaIncompatibilityType getIncompatibility() {
      return mSchemaIncompatibilityType;
    }

    /**
     * If the compatibility is INCOMPATIBLE, returns the first part of the reader schema that failed
     * compatibility check.
     * @return a Schema instance (part of the reader schema), or null
     */
    public Schema getReaderSubset() {
      return mReaderSubset;
    }

    /**
     * If the compatibility is INCOMPATIBLE, returns the first part of the writer schema that failed
     * compatibility check.
     * @return a Schema instance (part of the writer schema), or null
     */
    public Schema getWriterSubset() {
      return mWriterSubset;
    }

    /**
     * If the compatibility is INCOMPATIBLE, returns a human-readable string with more details about
     * what failed. Syntax depends on the SchemaIncompatibilityType.
     * @see #getIncompatibility()
     * @return a String with details about the incompatibility, or null
     */
    public String getMessage() {
      return mMessage;
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((mMessage == null) ? 0 : mMessage.hashCode());
      result = prime * result + ((mReaderSubset == null) ? 0 : mReaderSubset.hashCode());
      result = prime * result + ((mCompatibility == null) ? 0 : mCompatibility.hashCode());
      result = prime * result + ((
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      mCompatibilityType
=======
      mSchemaIncompatibilityType
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
       == null) ? 0 : 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      mCompatibilityType
=======
      mSchemaIncompatibilityType
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      .hashCode());
      result = prime * result + ((
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      mIncompatibilities
=======
      mWriterSubset
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
       == null) ? 0 : 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      mIncompatibilities
=======
      mWriterSubset
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      .hashCode());
      return result;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object obj) {

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      if (this == obj) {
        return true;
      }
=======
      if (this == obj) {
        return true;
      }
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java


<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      if (obj == null) {
        return false;
      }
=======
      if (obj == null) {
        return false;
      }
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java


<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      if (getClass() != obj.getClass()) {
        return false;
      }
=======
      if (getClass() != obj.getClass()) {
        return false;
      }
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java

      SchemaCompatibilityResult other = (SchemaCompatibilityResult) obj;
      if (mMessage == null) {
        if (other.mMessage != null) {
          return false;
        }
      } else {
        if (!mMessage.equals(other.mMessage)) {
          return false;
        }
      }
      if (
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      mIncompatibilities
=======
      mReaderSubset
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
       == null) {

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
        if (other.mIncompatibilities != null) {
          return false;
        }
=======
        if (other.mReaderSubset != null) {
          return false;
        }
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      } else {

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
        if (!mIncompatibilities.equals(other.mIncompatibilities)) {
          return false;
        }
=======
        if (!mReaderSubset.equals(other.mReaderSubset)) {
          return false;
        }
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java

      }
      if (mCompatibility != other.mCompatibility) {
        return false;
      }

<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      if (mCompatibilityType != other.mCompatibilityType) {
        return false;
      }
=======
      if (mSchemaIncompatibilityType != other.mSchemaIncompatibilityType) {
        return false;
      }
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java

      if (mWriterSubset == null) {
        if (other.mWriterSubset != null) {
          return false;
        }
      } else {
        if (!mWriterSubset.equals(other.mWriterSubset)) {
          return false;
        }
      }
      return true;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
      return String.format(
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      "SchemaCompatibilityResult{compatibility:%s, incompatibilities:%s}"
=======
      "SchemaCompatibilityDetails{compatibility:%s, type:%s, readerSubset:%s, writerSubset:%s, message:%s}"
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      , 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      mCompatibilityType
=======
      mCompatibility
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      , 
<<<<<<< /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/left.java
      mIncompatibilities
=======
      mSchemaIncompatibilityType
>>>>>>> /usr/src/app/output/apache/avro/9e0edfa879cfd02ebb0f5864d48b2a66b05418ae/lang/java/avro/src/main/java/org/apache/avro/SchemaCompatibility.java/right.java
      , mReaderSubset, mWriterSubset, mMessage);
    }
  }

  public static final class Incompatibility {
    private final SchemaIncompatibilityType mType;

    private final Schema mReaderFragment;

    private final Schema mWriterFragment;

    private final String mMessage;

    private final List<String> mLocation;

    Incompatibility(SchemaIncompatibilityType type, Schema readerFragment, Schema writerFragment, String message, List<String> location) {
      super();
      this.mType = type;
      this.mReaderFragment = readerFragment;
      this.mWriterFragment = writerFragment;
      this.mMessage = message;
      this.mLocation = Collections.unmodifiableList(new ArrayList<String>(location));
    }

    /**
     * Returns the SchemaIncompatibilityType.
     * @return a SchemaIncompatibilityType instance.
     */
    public SchemaIncompatibilityType getType() {
      return mType;
    }

    /**
     * Returns the fragment of the reader schema that failed compatibility check.
     * @return a Schema instance (fragment of the reader schema).
     */
    public Schema getReaderFragment() {
      return mReaderFragment;
    }

    /**
     * Returns the fragment of the writer schema that failed compatibility check.
     * @return a Schema instance (fragment of the writer schema).
     */
    public Schema getWriterFragment() {
      return mWriterFragment;
    }

    /**
     * Returns a human-readable message with more details about what failed. Syntax depends on the
     * SchemaIncompatibilityType.
     * @see #getType()
     * @return a String with details about the incompatibility.
     */
    public String getMessage() {
      return mMessage;
    }

    /**
     * Returns a <a href="https://tools.ietf.org/html/draft-ietf-appsawg-json-pointer-08">JSON Pointer</a> describing
     * the node location within the schema's JSON document tree where the incompatibility was encountered.
     * @return JSON Pointer encoded as a string.
     */
    public String getLocation() {
      StringBuilder s = new StringBuilder("/");
      boolean first = true;
      for (String coordinate : mLocation.subList(1, mLocation.size())) {
        if (first) {
          first = false;
        } else {
          s.append('/');
        }
        s.append(coordinate.replace("~", "~0").replace("/", "~1"));
      }
      return s.toString();
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((mType == null) ? 0 : mType.hashCode());
      result = prime * result + ((mReaderFragment == null) ? 0 : mReaderFragment.hashCode());
      result = prime * result + ((mWriterFragment == null) ? 0 : mWriterFragment.hashCode());
      result = prime * result + ((mMessage == null) ? 0 : mMessage.hashCode());
      result = prime * result + ((mLocation == null) ? 0 : mLocation.hashCode());
      return result;
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      Incompatibility other = (Incompatibility) obj;
      if (mType != other.mType) {
        return false;
      }
      if (mReaderFragment == null) {
        if (other.mReaderFragment != null) {
          return false;
        }
      } else {
        if (!mReaderFragment.equals(other.mReaderFragment)) {
          return false;
        }
      }
      if (mWriterFragment == null) {
        if (other.mWriterFragment != null) {
          return false;
        }
      } else {
        if (!mWriterFragment.equals(other.mWriterFragment)) {
          return false;
        }
      }
      if (mMessage == null) {
        if (other.mMessage != null) {
          return false;
        }
      } else {
        if (!mMessage.equals(other.mMessage)) {
          return false;
        }
      }
      if (mLocation == null) {
        if (other.mLocation != null) {
          return false;
        }
      } else {
        if (!mLocation.equals(other.mLocation)) {
          return false;
        }
      }
      return true;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
      return String.format("Incompatibility{type:%s, location:%s, message:%s, reader:%s, writer:%s}", mType, getLocation(), mMessage, mReaderFragment, mWriterFragment);
    }
  }

  public static final class SchemaPairCompatibility {
    /** The details of this result. */
    private final SchemaCompatibilityResult mResult;

    /** Validated reader schema. */
    private final Schema mReader;

    /** Validated writer schema. */
    private final Schema mWriter;

    /** Human readable description of this result. */
    private final String mDescription;

    /**
     * Constructs a new instance.
     * @param result The result of the compatibility check.
     * @param type of the schema compatibility.
     * @param reader schema that was validated.
     * @param writer schema that was validated.
     * @param description of this compatibility result.
     */
    public SchemaPairCompatibility(SchemaCompatibilityResult result, Schema reader, Schema writer, String description) {
      mResult = result;
      mReader = reader;
      mWriter = writer;
      mDescription = description;
    }

    /**
     * Gets the type of this result.
     *
     * @return the type of this result.
     */
    public SchemaCompatibilityType getType() {
      return mResult.getCompatibility();
    }

    /**
     * Gets more details about the compatibility, in particular if getType() is INCOMPATIBLE.
     * @return the details of this compatibility check.
     */
    public SchemaCompatibilityResult getResult() {
      return mResult;
    }

    /**
     * Gets the reader schema that was validated.
     *
     * @return reader schema that was validated.
     */
    public Schema getReader() {
      return mReader;
    }

    /**
     * Gets the writer schema that was validated.
     *
     * @return writer schema that was validated.
     */
    public Schema getWriter() {
      return mWriter;
    }

    /**
     * Gets a human readable description of this validation result.
     *
     * @return a human readable description of this validation result.
     */
    public String getDescription() {
      return mDescription;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
      return String.format("SchemaPairCompatibility{result:%s, readerSchema:%s, writerSchema:%s, description:%s}", mResult, mReader, mWriter, mDescription);
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object other) {
      if ((null != other) && (other instanceof SchemaPairCompatibility)) {
        final SchemaPairCompatibility result = (SchemaPairCompatibility) other;
        return objectsEqual(result.mResult, mResult) && objectsEqual(result.mReader, mReader) && objectsEqual(result.mWriter, mWriter) && objectsEqual(result.mDescription, mDescription);
      } else {
        return false;
      }
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
      return Arrays.hashCode(new Object[] { mResult, mReader, mWriter, mDescription });
    }
  }

  /** Borrowed from Guava's Objects.equal(a, b) */
  private static boolean objectsEqual(Object obj1, Object obj2) {
    return (obj1 == obj2) || ((obj1 != null) && obj1.equals(obj2));
  }
}