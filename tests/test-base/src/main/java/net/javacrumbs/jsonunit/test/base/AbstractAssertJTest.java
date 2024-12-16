/**
 * Copyright 2009-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.javacrumbs.jsonunit.assertj.JsonAssert.ConfigurableJsonAssert;
import net.javacrumbs.jsonunit.core.Option;
import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;
import static java.math.BigDecimal.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.value;
import static net.javacrumbs.jsonunit.core.ConfigurationWhen.*;
import static net.javacrumbs.jsonunit.core.ConfigurationWhen.paths;
import static net.javacrumbs.jsonunit.core.Option.*;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_VALUES;
import static net.javacrumbs.jsonunit.core.internal.JsonUtils.jsonSource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.hamcrest.Matchers.greaterThan;


class Option {}