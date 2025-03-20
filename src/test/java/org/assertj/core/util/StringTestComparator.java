<<<<<<< /usr/src/app/output/joel-costigliola/assertj-core/a3011e7f9eb627d717e3b5908da703b65efae0bb/src/test/java/org/assertj/core/util/StringTestComparator.java/left.java
fatal: path 'src/test/java/org/assertj/core/util/StringTestComparator.java' exists on disk, but not in '46feeb0568afe41089341ce8145c02f59b8d8c78'
||||||| /usr/src/app/output/joel-costigliola/assertj-core/a3011e7f9eb627d717e3b5908da703b65efae0bb/src/test/java/org/assertj/core/util/StringTestComparator.java/base.java
/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2016 the original author or authors.
 */
package org.assertj.core.util;

import java.util.Comparator;

final class StringTestComparator implements Comparator<String> {

  @Override
  public int compare(String s1, String s2) {
    return s1.length() - s2.length();
  }
}

final class OtherStringTestComparator implements Comparator<String> {
  
  @Override
  public int compare(String s1, String s2) {
    return s1.length() - s2.length();
  }
  
  @Override
  public String toString() {
    return "other String comparator";
  }
}

final class OtherStringTestComparatorWithAt implements Comparator<String> {

  @Override
  public int compare(String s1, String s2) {
    return s1.length() - s2.length();
  }

  @Override
  public String toString() {
    return "other String comparator with @";
  }
}

=======
/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2016 the original author or authors.
 */
package org.assertj.core.util;

import java.util.Comparator;

public final class StringTestComparator implements Comparator<String> {

  @Override
  public int compare(String s1, String s2) {
    return s1.length() - s2.length();
  }
}

>>>>>>> /usr/src/app/output/joel-costigliola/assertj-core/a3011e7f9eb627d717e3b5908da703b65efae0bb/src/test/java/org/assertj/core/util/StringTestComparator.java/right.java
