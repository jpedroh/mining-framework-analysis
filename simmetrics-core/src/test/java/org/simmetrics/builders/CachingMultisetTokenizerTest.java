<<<<<<< /usr/src/app/output/simmetrics/simmetrics/26ac3158598828d1bbacf766272aa32bf4a1d370/simmetrics-core/src/test/java/org/simmetrics/builders/CachingMultisetTokenizerTest.java/left.java
/*
 * #%L
 * Simmetrics Core
 * %%
 * Copyright (C) 2014 - 2016 Simmetrics Authors
 * %%
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
 * #L%
 */

package org.simmetrics.builders;

import org.simmetrics.builders.StringMetricBuilder.CachingMultisetTokenizer;
import org.simmetrics.tokenizers.Tokenizer;
import com.google.common.cache.Cache;
import com.google.common.collect.Multiset;

@SuppressWarnings("javadoc")
public class CachingMultisetTokenizerTest extends CachingTokenizerTest<Multiset<String>> {

	
	
	@Override
	protected final boolean supportsTokenizeToList() {
		return false;
	}
	
	@Override
	protected boolean supportsTokenizeToSet() {
		return false;
	}

	@Override
	public Tokenizer getTokenizer(Cache<String, Multiset<String>> cache, Tokenizer tokenizer) {
		return new CachingMultisetTokenizer(cache, tokenizer);
	}
}
||||||| /usr/src/app/output/simmetrics/simmetrics/26ac3158598828d1bbacf766272aa32bf4a1d370/simmetrics-core/src/test/java/org/simmetrics/builders/CachingMultisetTokenizerTest.java/base.java
/*
 * #%L
 * Simmetrics Core
 * %%
 * Copyright (C) 2014 - 2015 Simmetrics Authors
 * %%
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
 * #L%
 */

package org.simmetrics.builders;

import org.simmetrics.builders.StringMetricBuilder.CachingMultisetTokenizer;
import org.simmetrics.tokenizers.Tokenizer;
import com.google.common.cache.Cache;
import com.google.common.collect.Multiset;

@SuppressWarnings("javadoc")
public class CachingMultisetTokenizerTest extends CachingTokenizerTest<Multiset<String>> {

	
	
	@Override
	protected final boolean supportsTokenizeToList() {
		return false;
	}
	
	@Override
	protected boolean supportsTokenizeToSet() {
		return false;
	}

	@Override
	public Tokenizer getTokenizer(Cache<String, Multiset<String>> cache, Tokenizer tokenizer) {
		return new CachingMultisetTokenizer(cache, tokenizer);
	}
}
=======
fatal: path 'simmetrics-core/src/test/java/org/simmetrics/builders/CachingMultisetTokenizerTest.java' exists on disk, but not in '53f9bf59fd1bd05606e52f2155e2cde1052a5931'
>>>>>>> /usr/src/app/output/simmetrics/simmetrics/26ac3158598828d1bbacf766272aa32bf4a1d370/simmetrics-core/src/test/java/org/simmetrics/builders/CachingMultisetTokenizerTest.java/right.java
