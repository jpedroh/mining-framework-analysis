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
<<<<<<< /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/left.java
  
    String[] lines = tomlString.split("[\\n\\r]");
    int lastKeyLine = 1;
    StringBuilder multilineBuilder = new StringBuilder();
    Multiline multiline = Multiline.NONE;
||||||| /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/base.java
  
    String[] lines = tomlString.split("[\\n\\r]");
    StringBuilder multilineBuilder = new StringBuilder();
    Multiline multiline = Multiline.NONE;
=======
>>>>>>> /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/right.java
    
<<<<<<< /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/left.java
    String key = null;
    String value = null;

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];

      if (line != null && multiline.isTrimmable()) {
        line = line.trim();
      }

      if (isComment(line) || line.isEmpty()) {
        continue;
      }

      if (isTableArray(line)) {
        String tableName = Keys.getTableArrayName(line);
        if (tableName != null) {
          results.startTableArray(tableName);
        } else {
          results.errors.invalidTableArray(line, i + 1);
        }

        continue;
      }

      if (multiline.isNotMultiline() && isTable(line)) {
        String tableName = Keys.getTableName(line);
        if (tableName != null) {
          results.startTables(tableName);
        } else {
          results.errors.invalidTable(line.trim(), i + 1);
        }

        continue;
      }
      
      if (multiline.isNotMultiline() && !line.contains("=")) {
        results.errors.invalidKey(line, i + 1);
        continue;
      }

      String[] pair = line.split("=", 2);

      if (multiline.isNotMultiline() && MULTILINE_ARRAY_REGEX.matcher(pair[1].trim()).matches()) {
        multiline = Multiline.ARRAY;
        key = pair[0].trim();
        multilineBuilder.append(removeComment(pair[1]));
        continue;
      }

      if (multiline.isNotMultiline() && pair[1].trim().startsWith("\"\"\"")) {
        multiline = Multiline.STRING;
        multilineBuilder.append(pair[1]);
        key = pair[0].trim();

        if (pair[1].trim().indexOf("\"\"\"", 3) > -1) {
          multiline = Multiline.NONE;
          pair[1] = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          if (multilineBuilder.toString().trim().length() > 3) {
            multilineBuilder.append('\n');
          }
          continue;
        }
      }
      
      if (multiline.isNotMultiline() && pair[1].trim().startsWith(STRING_LITERAL_DELIMITER)) {
        multiline = Multiline.STRING_LITERAL;
        multilineBuilder.append(pair[1]);
        key = pair[0].trim();

        if (pair[1].trim().indexOf(STRING_LITERAL_DELIMITER, 3) > -1) {
          multiline = Multiline.NONE;
          pair[1] = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          if (multilineBuilder.toString().trim().length() > 3) {
            multilineBuilder.append('\n');
          }
          continue;
        }
      }
      
      if (multiline == Multiline.ARRAY) {
        String lineWithoutComment = removeComment(line);
        multilineBuilder.append(lineWithoutComment);
        if (MULTILINE_ARRAY_REGEX_END.matcher(lineWithoutComment).matches()) {
          multiline = Multiline.NONE;
          value = multilineBuilder.toString();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          continue;
        }
      } else if (multiline == Multiline.STRING) {
        multilineBuilder.append(line);
        if (line.contains("\"\"\"")) {
          multiline = Multiline.NONE;
          value = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          multilineBuilder.append('\n');
          continue;
        }
      } else if (multiline == Multiline.STRING_LITERAL) {
        multilineBuilder.append(line);
        if (line.contains(STRING_LITERAL_DELIMITER)) {
          multiline = Multiline.NONE;
          value = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          multilineBuilder.append('\n');
          continue;
        }
      } else {
        key = Keys.getKey(pair[0]);
        if (key == null) {
          results.errors.invalidKey(pair[0], i + 1);
          continue;
        }
        value = pair[1].trim();
      }

      lastKeyLine = i + 1;
      Object convertedValue = VALUE_ANALYSIS.convert(value);

      if (convertedValue != INVALID) {
        results.addValue(key, convertedValue);
      } else {
        results.errors.invalidValue(key, value, i + 1);
      }
    }
||||||| /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/base.java
    String key = null;
    String value = null;

    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];

      if (line != null && multiline.isTrimmable()) {
        line = line.trim();
      }

      if (isComment(line) || line.isEmpty()) {
        continue;
      }

      if (isTableArray(line)) {
        String tableName = Keys.getTableArrayName(line);
        if (tableName != null) {
          results.startTableArray(tableName);
        } else {
          results.errors.append("Invalid table array definition: " + line + "\n\n");
        }

        continue;
      }

      if (multiline.isNotMultiline() && isTable(line)) {
        String tableName = Keys.getTableName(line);
        if (tableName != null) {
          results.startTables(tableName);
        } else {
          results.errors.append("Invalid table definition: " + line + "\n\n");
        }

        continue;
      }
      
      if (multiline.isNotMultiline() && !line.contains("=")) {
        results.errors.append("Invalid key definition: " + line);
        continue;
      }

      String[] pair = line.split("=", 2);

      if (multiline.isNotMultiline() && MULTILINE_ARRAY_REGEX.matcher(pair[1].trim()).matches()) {
        multiline = Multiline.ARRAY;
        key = pair[0].trim();
        multilineBuilder.append(removeComment(pair[1]));
        continue;
      }

      if (multiline.isNotMultiline() && pair[1].trim().startsWith("\"\"\"")) {
        multiline = Multiline.STRING;
        multilineBuilder.append(pair[1]);
        key = pair[0].trim();

        if (pair[1].trim().indexOf("\"\"\"", 3) > -1) {
          multiline = Multiline.NONE;
          pair[1] = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          if (multilineBuilder.toString().trim().length() > 3) {
            multilineBuilder.append('\n');
          }
          continue;
        }
      }
      
      if (multiline.isNotMultiline() && pair[1].trim().startsWith(STRING_LITERAL_DELIMITER)) {
        multiline = Multiline.STRING_LITERAL;
        multilineBuilder.append(pair[1]);
        key = pair[0].trim();

        if (pair[1].trim().indexOf(STRING_LITERAL_DELIMITER, 3) > -1) {
          multiline = Multiline.NONE;
          pair[1] = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          if (multilineBuilder.toString().trim().length() > 3) {
            multilineBuilder.append('\n');
          }
          continue;
        }
      }
      
      if (multiline == Multiline.ARRAY) {
        String lineWithoutComment = removeComment(line);
        multilineBuilder.append(lineWithoutComment);
        if (MULTILINE_ARRAY_REGEX_END.matcher(lineWithoutComment).matches()) {
          multiline = Multiline.NONE;
          value = multilineBuilder.toString();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          continue;
        }
      } else if (multiline == Multiline.STRING) {
        multilineBuilder.append(line);
        if (line.contains("\"\"\"")) {
          multiline = Multiline.NONE;
          value = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          multilineBuilder.append('\n');
          continue;
        }
      } else if (multiline == Multiline.STRING_LITERAL) {
        multilineBuilder.append(line);
        if (line.contains(STRING_LITERAL_DELIMITER)) {
          multiline = Multiline.NONE;
          value = multilineBuilder.toString().trim();
          multilineBuilder.delete(0, multilineBuilder.length());
        } else {
          multilineBuilder.append('\n');
          continue;
        }
      } else {
        key = Keys.getKey(pair[0]);
        if (key == null) {
          results.errors.append("Invalid key name: " + pair[0] + "\n");
          continue;
        }
        value = pair[1].trim();
      }


      Object convertedValue = VALUE_ANALYSIS.convert(value);

      if (convertedValue != INVALID) {
        results.addValue(key, convertedValue);
      } else {
        results.errors.append("Invalid key/value: " + key + " = " + value + "\n");
      }
    }
=======
    char[] chars = tomlString.toCharArray();
    AtomicInteger index = new AtomicInteger();
    boolean inComment = false;
    AtomicInteger line = new AtomicInteger(1);
    Identifier identifier = null;
    Object value = null;
>>>>>>> /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/right.java
    
<<<<<<< /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/left.java
    if (multiline != Multiline.NONE) {
      results.errors.unterminated(key, multilineBuilder.toString().trim(), lastKeyLine);
||||||| /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/base.java
    if (multiline != Multiline.NONE) {
      results.errors.append("Unterminated multiline " + multiline.toString().toLowerCase().replace('_', ' ') + "\n");
=======
    for (int i = index.get(); i < chars.length; i = index.incrementAndGet()) {
      char c = chars[i];
      
      if (c == '#' && !inComment) {
        inComment = true;
      } else if (!Character.isWhitespace(c) && !inComment && identifier == null) {
        Identifier id = IDENTIFIER_CONVERTER.convert(chars, index);
        
        if (id.isValid()) {
          char next = chars[index.get()];
          if (index.get() < chars.length -1 && !id.acceptsNext(next)) {
            results.errors.invalidTextAfterIdentifier(id, next, line.get());
          } else if (id.isKey()) {
            identifier = id;
          } else if (id.isTable()) {
            results.startTables(Keys.getTableName(id.getName()));
          } else if (id.isTableArray()) {
            results.startTableArray(Keys.getTableArrayName(id.getName()));
          }
          inComment = next == '#';
        } else {
          results.errors.invalidIdentifier(id, line.get());
        }
      } else if (c == '\n') {
        inComment = false;
        identifier = null;
        value = null;
        line.incrementAndGet();
      } else if (!inComment && identifier != null && identifier.isKey() && value == null && !Character.isWhitespace(c)) {
        int startIndex = index.get();
        Object converted = ValueConverters.CONVERTERS.convert(tomlString, index);
        value = converted;
        
        if (converted == INVALID) {
          results.errors.invalidValue(identifier.getName(), tomlString.substring(startIndex, Math.min(index.get(), tomlString.length() - 1)), line.get());
        } else if (converted instanceof Unterminated) {
          results.errors.unterminated(identifier.getName(), ((Unterminated) converted).payload, line.get());
        } else {
          results.addValue(identifier.getName(), converted);
        }
      } else if (value != null && !inComment && !Character.isWhitespace(c)) {
        results.errors.invalidTextAfterIdentifier(identifier, c, line.get());
      }
>>>>>>> /usr/src/app/output/mwanji/toml4j/c4027ed2d5a685734a83306fdd1a4ed41f473f60/src/main/java/com/moandjiezana/toml/TomlParser.java/right.java
    }

    return results;
  }
}
