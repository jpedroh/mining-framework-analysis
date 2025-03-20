package com.moandjiezana.toml;

import java.util.concurrent.atomic.AtomicInteger;

class NumberConverter implements ValueConverter {
  static final NumberConverter NUMBER_PARSER = new NumberConverter();
  
  @Override
  public boolean canConvert(String s) {
    char firstChar = s.charAt(0);
    
    return firstChar == '+' || firstChar == '-' || Character.isDigit(firstChar);
  }

  @Override
  public Object convert(String s, AtomicInteger index, Context context) {
    boolean signable = true;
    boolean dottable = false;
    boolean exponentable = false;
<<<<<<< /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/left.java
    boolean terminatable = false;
||||||| /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/base.java
=======
    boolean underscorable = false;
>>>>>>> /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/right.java
    String type = "";
    StringBuilder sb = new StringBuilder();

    for (int i = index.get(); i < s.length(); i = index.incrementAndGet()) {
      char c = s.charAt(i);
      boolean notLastChar = chars.length > i + 1;

      if (Character.isDigit(c)) {
        sb.append(c);
        signable = false;
        terminatable = true;
        if (type.isEmpty()) {
          type = "integer";
          dottable = true;
        }
        underscorable = notLastChar;
        exponentable = !type.equals("exponent");
<<<<<<< /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/left.java
      } else if ((c == '+' || c == '-') && signable && s.length() > i + 1) {
||||||| /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/base.java
      } else if ((c == '+' || c == '-') && signable && chars.length > i + 1) {
=======
      } else if ((c == '+' || c == '-') && signable && notLastChar) {
>>>>>>> /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/right.java
        signable = false;
        terminatable = false;
        if (c == '-') {
          sb.append('-');
        }
        underscorable = false;
<<<<<<< /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/left.java
      } else if (c == '.' && dottable && s.length() > i + 1) {
||||||| /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/base.java
      } else if (c == '.' && dottable && chars.length > i + 1) {
=======
      } else if (c == '.' && dottable && notLastChar) {
>>>>>>> /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/right.java
        sb.append('.');
        type = "float";
        terminatable = false;
        dottable = false;
        exponentable = false;
        underscorable = false;
<<<<<<< /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/left.java
      } else if ((c == 'E' || c == 'e') && exponentable && s.length() > i + 1) {
||||||| /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/base.java
      } else if ((c == 'E' || c == 'e') && exponentable && chars.length > i + 1) {
=======
      } else if ((c == 'E' || c == 'e') && exponentable && notLastChar) {
>>>>>>> /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/right.java
        sb.append('E');
        type = "exponent";
        terminatable = false;
        signable = true;
        dottable = false;
        exponentable = false;
        underscorable = false;
<<<<<<< /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/left.java
      } else {
        if (!terminatable) {
          type = "";
        }
        index.decrementAndGet();
        break;
      }
||||||| /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/base.java
      } else if (Character.isWhitespace(c)) {
        whitespace = true;
      } else if (whitespace && c == '#') {
        break;
      } else {
        type = "";
        break;
      }
=======
      } else if (c == '_' && underscorable && notLastChar && Character.isDigit(chars[i + 1])) {
        underscorable = false;
      } else if (Character.isWhitespace(c)) {
        whitespace = true;
      } else if (whitespace && c == '#') {
        break;
      } else {
        type = "";
        break;
      }
>>>>>>> /usr/src/app/output/mwanji/toml4j/a3edb55e9c0c40f0620c3b8c4480864a2f07f46c/src/main/java/com/moandjiezana/toml/NumberConverter.java/right.java
    }

    if (type.equals("integer")) {
      return Long.valueOf(sb.toString());
    } else if (type.equals("float")) {
      return Double.valueOf(sb.toString());
    } else if (type.equals("exponent")) {
      String[] exponentString = sb.toString().split("E");
      
      return Double.parseDouble(exponentString[0]) * Math.pow(10, Double.parseDouble(exponentString[1]));
    } else {
      Results.Errors errors = new Results.Errors();
      errors.invalidValue(context.identifier.getName(), sb.toString(), context.line.get());
      return errors;
    }
  }
}
