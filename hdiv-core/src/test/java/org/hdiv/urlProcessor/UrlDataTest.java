/**
<<<<<<< LEFT
 * Copyright 2005-2016 hdiv.org
=======
 * Copyright 2005-2015 hdiv.org
>>>>>>> RIGHT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hdiv.urlProcessor;

import org.hdiv.util.Method;
import org.junit.Assert;
import org.junit.Test;


public class UrlDataTest {
	@Test
	public void testShortURLIsJS() {
		UrlData data = new UrlData("short", Method.GET);
		Assert.assertFalse(data.isJS());
		data = new UrlData("javascript:", Method.GET);
		Assert.assertTrue(data.isJS());
	}
}