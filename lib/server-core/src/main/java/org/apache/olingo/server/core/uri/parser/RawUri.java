<<<<<<< /usr/src/app/output/apache/olingo-odata4/010642c506d593c83cfb136cd9f5ddb815bf8ba7/lib/server-core/src/main/java/org/apache/olingo/server/core/uri/parser/RawUri.java/left.java
fatal: path 'lib/server-core/src/main/java/org/apache/olingo/server/core/uri/parser/RawUri.java' does not exist in '8925274c0b5bd6936c3f6c1d3ab55608ced2cf13'
||||||| /usr/src/app/output/apache/olingo-odata4/010642c506d593c83cfb136cd9f5ddb815bf8ba7/lib/server-core/src/main/java/org/apache/olingo/server/core/uri/parser/RawUri.java/base.java
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.core.uri.parser;

import java.util.List;

public class RawUri {
  public String uri;
  public String scheme;
  public String authority;
  public String path;
  public String queryOptionString;
  public String fragment;
  public List<QueryOption> queryOptionList;
  public List<QueryOption> queryOptionListDecoded;

  public List<String> pathSegmentList;
  public List<String> pathSegmentListDecoded;

  public static class QueryOption {
    public String name;
    public String value;

    QueryOption(final String name, final String value) {
      this.name = name;
      this.value = value;
    }

  }
}
=======
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.core.uri.parser;

import java.util.List;

public class RawUri {
  public String uri;
  public String scheme;
  public String authority;
  public String path;
  public String queryOptionString;
  public String fragment;
  public List<QueryOption> queryOptionList;
  public List<QueryOption> queryOptionListDecoded;

  public List<String> pathSegmentList;
  public List<String> pathSegmentListDecoded;

  public static class QueryOption {
    public String name;
    public String value;

    QueryOption(final String name, final String value) {
      this.name = name;
      this.value = value;
    }

  }
}
>>>>>>> /usr/src/app/output/apache/olingo-odata4/010642c506d593c83cfb136cd9f5ddb815bf8ba7/lib/server-core/src/main/java/org/apache/olingo/server/core/uri/parser/RawUri.java/right.java
