/*
 * Copyright 2010-2012 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.ning.billing.recurly.model;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.testng.annotations.BeforeMethod;

public abstract class TestModelBase {

    protected XmlMapper xmlMapper;

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
<<<<<<< /usr/src/app/output/killbilling/recurly-java-library/500781acecc44ce57590effaab9a7369c2358035/src/test/java/com/ning/billing/recurly/model/TestModelBase.java/left.java
        xmlMapper = RecurlyObject.newXmlMapper();
||||||| /usr/src/app/output/killbilling/recurly-java-library/500781acecc44ce57590effaab9a7369c2358035/src/test/java/com/ning/billing/recurly/model/TestModelBase.java/base.java
        xmlMapper = new XmlMapper();

        final AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
        final AnnotationIntrospector secondary = new JaxbAnnotationIntrospector();
        final AnnotationIntrospector pair = new AnnotationIntrospector.Pair(primary, secondary);
        xmlMapper.setAnnotationIntrospector(pair);
        xmlMapper.registerModule(new JodaModule());
        xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
=======
        xmlMapper = new XmlMapper();

        final AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
        final AnnotationIntrospector secondary = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
        final AnnotationIntrospector pair = new AnnotationIntrospectorPair(primary, secondary);
        xmlMapper.setAnnotationIntrospector(pair);
        xmlMapper.registerModule(new JodaModule());
        xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
>>>>>>> /usr/src/app/output/killbilling/recurly-java-library/500781acecc44ce57590effaab9a7369c2358035/src/test/java/com/ning/billing/recurly/model/TestModelBase.java/right.java
    }
}
