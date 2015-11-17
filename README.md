cdi-test [![Build Status](https://travis-ci.org/guhilling/cdi-test.svg?branch=master)](https://travis-ci.org/guhilling/cdi-test) [![Coverage Status](https://coveralls.io/repos/guhilling/cdi-test/badge.svg?branch=master)](https://coveralls.io/r/guhilling/cdi-test?branch=master) [![Maven Central](https://img.shields.io/maven-central/v/de.hilling.junit.cdi/cdi-test.svg)](http://search.maven.org/#search|gav|1|g:"de.hilling.junit.cdi"%20AND%20a:"cdi-test")
========

junit-addon for easy and quick testing of cdi projects.

cdi-test is available under the [The Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)


## Main features:

* Plain cdi test, no classpath magic.
* Custom scopes for testing.
* Uses interceptors for on-the-fly switching between mockito-mocks, test implementation and production implementations.
* Support for some ejb features to test jee application components:
    * Inject EntityManager via ``@Inject`` or ``@PersistenceContext``
    * Injection of Stateless Beans

## Usage

### Dependencies

Use maven to pull dependency on basic features:

```xml
    <dependency>
        <groupId>de.hilling.junit.cdi</groupId>
        <artifactId>cdi-test-core</artifactId>
        <version>0.12.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.deltaspike.cdictrl</groupId>
        <artifactId>deltaspike-cdictrl-weld</artifactId>
        <version>1.5.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.jboss.weld.se</groupId>
        <artifactId>weld-se-core</artifactId>
        <version>2.2.14.Final</version>
        <scope>test</scope>
    </dependency>

```

Of course the version of weld being used should match the version you are using in your production system.
The smallest supported version is _2.1.2.Final_ however.

So if you are using an old version of JBoss or whatever server you have, you have to use a different cdi
implementation for your tests.

If you are using cdi-test with hibernate you might run into problems because hibernate uses jandex. Weld will try
to use jandex if it is present but the version used by hibernate is not suitable for Weld. To fix the problem you
just add a newer jandex version with test scope:

```xml
    <dependency>
        <groupId>org.jboss</groupId>
        <artifactId>jandex</artifactId>
        <version>1.2.2.Final</version>
        <scope>test</scope>
    </dependency>
```


### Writing Tests

In the following example, `ApplicationBean` will automatically be replaced by a mockito mock in all cdi
beans, see the full example in the code for details.

```java
@RunWith(CdiUnitRunner.class)
public class RequestScopeMockTest extends BaseTest {

    private static final String SAMPLE = "sample";

    @Mock
    private ApplicationBean applicationBean;

    @Inject
    private RequestBean requestBean;

    @Test
    public void setAttributeTransitive() {
        requestBean.setAttribute(SAMPLE);
        verify(applicationBean).setAttribute(SAMPLE);
    }

}

```

## LICENSE

 Copyright 2015 Gunnar Hilling

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


## TODO

* Project restructuring to release extensions (like cdi-test-jee) independently from cdi-test-core.
* More extensions:
    * dbunit
    * test data generator
* switchable Mock-Providers