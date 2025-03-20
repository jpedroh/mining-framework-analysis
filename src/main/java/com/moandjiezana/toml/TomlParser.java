package com.moandjiezana.toml;
import static com.moandjiezana.toml.IdentifierConverter.IDENTIFIER_CONVERTER;
import static com.moandjiezana.toml.ValueConverterUtils.INVALID;
import java.util.concurrent.atomic.AtomicInteger;
import com.moandjiezana.toml.ValueConverterUtils.Unterminated;

class TomlParser {
  Results run(String tomlString) {
    final Results results = new Results();
    if (tomlString.isEmpty()) {
      return results;
    }
    char[] chars = tomlString.toCharArray();

<<<<<<< /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/left.java
    int lastKeyLine = 1;
=======
    AtomicInteger index = new AtomicInteger();
>>>>>>> /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/right.java

    boolean inComment = false;
    AtomicInteger line = new AtomicInteger(1);
    Identifier identifier = null;
    Object value = null;
    for (int i = index.get(); i < chars.length; i = index.incrementAndGet()) {

<<<<<<< /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/left.java
      if (isTableArray(line)) {
        String tableName = Keys.getTableArrayName(line);
        if (tableName != null) {
          results.startTableArray(tableName);
        } else {
          results.errors.invalidTableArray(line, i + 1);
        }
        continue;
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/left.java
      if (multiline.isNotMultiline() && isTable(line)) {
        String tableName = Keys.getTableName(line);
        if (tableName != null) {
          results.startTables(tableName);
        } else {
          results.errors.invalidTable(line.trim(), i + 1);
        }
        continue;
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.


<<<<<<< /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/left.java
      if (multiline.isNotMultiline() && !line.contains("=")) {
        results.errors.invalidKey(line, i + 1);
        continue;
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.

      char c = chars[i];
      if (c == '#' && !inComment) {
        inComment = true;
      } else {
        if (!Character.isWhitespace(c) && !inComment && identifier == null) {
          Identifier id = IDENTIFIER_CONVERTER.convert(chars, index);
          if (id.isValid()) {
            char next = chars[index.get()];
            if (index.get() < chars.length - 1 && !id.acceptsNext(next)) {
              results.errors.invalidTextAfterIdentifier(id, next, line.get());
            } else {
              if (id.isKey()) {
                identifier = id;
              } else {
                if (id.isTable()) {
                  results.startTables(Keys.getTableName(id.getName()));
                } else {
                  if (id.isTableArray()) {
                    results.startTableArray(Keys.getTableArrayName(id.getName()));
                  }
                }
              }
            }
            inComment = next == '#';
          } else {
            results.errors.invalidIdentifier(id, line.get());
          }
        } else {
          if (c == '\n') {
            inComment = false;
            identifier = null;
            value = null;
            line.incrementAndGet();
          } else 
<<<<<<< /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/left.java
          {
            key = Keys.getKey(pair[0]);
            if (key == null) {
              results.errors.invalidKey(pair[0], i + 1);
              continue;
            }
            value = pair[1].trim();
          }
=======
          if (!inComment && identifier != null && identifier.isKey() && value == null && !Character.isWhitespace(c)) {
            int startIndex = index.get();
            Object converted = ValueConverters.CONVERTERS.convert(tomlString, index);
            value = converted;
            if (converted == INVALID) {
              results.errors.invalidValue(identifier.getName(), tomlString.substring(startIndex, Math.min(index.get(), tomlString.length() - 1)), line.get());
            } else {
              if (converted instanceof Unterminated) {
                results.errors.unterminated(identifier.getName(), ((Unterminated) converted).payload, line.get());
              } else {
                results.addValue(identifier.getName(), converted);
              }
            }
          } else {
            if (value != null && !inComment && !Character.isWhitespace(c)) {
              results.errors.invalidTextAfterIdentifier(identifier, c, line.get());
            }
          }
>>>>>>> /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/right.java

        }
      }
      lastKeyLine = i + 1;

<<<<<<< /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/left.java
      if (convertedValue != INVALID) {
        results.addValue(key, convertedValue);
      } else {
        results.errors.invalidValue(key, value, i + 1);
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.
    }

<<<<<<< /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/left.java
    if (multiline != Multiline.NONE) {
      results.errors.unterminated(key, multilineBuilder.toString().trim(), lastKeyLine);
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    return results;
  }
}