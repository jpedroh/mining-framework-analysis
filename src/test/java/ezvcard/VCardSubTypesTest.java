package ezvcard;

import static ezvcard.util.TestUtils.assertIntEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

/*
 Copyright (c) 2013, Michael Angstadt
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met: 

 1. Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer. 
 2. Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution. 

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 The views and conclusions contained in the software and documentation are those
 of the authors and should not be interpreted as representing official policies, 
 either expressed or implied, of the FreeBSD Project.
 */

/**
 * @author Michael Angstadt
 */
public class VCardSubTypesTest {
	private VCardSubTypes subTypes;

	@Before
	public void before() {
		subTypes = new VCardSubTypes();
	}

	@Test
	public void case_insensitive() {
		//tests to make sure sanitizeKey() is implemented correctly
		//ListMultimapTest tests the rest of the get/put/remove methods
		subTypes.put("NUMBERS", "1");
		assertEquals("1", subTypes.first("numbers"));
	}

	@Test
	public void getPref() {
		assertNull(subTypes.getPref());
		subTypes.put("PREF", "1");
		assertIntEquals(1, subTypes.getPref());
	}

	@Test(expected = IllegalStateException.class)
	public void getPref_malformed() {
		subTypes.put("PREF", "invalid");
		subTypes.getPref();
	}

	@Test
	public void setPref() {
		subTypes.setPref(1);
		assertEquals("1", subTypes.first("PREF"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void setPref_too_low() {
		subTypes.setPref(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setPref_too_high() {
		subTypes.setPref(101);
	}

	/**
	 * Make sure it handles GEO values correctly.
	 */
	@Test
	public void geo() {
		assertNull(subTypes.getGeo());
		subTypes.setGeo(-10.98887888, 20.12344111);

		//make sure it builds the correct text value
		{
			String expected = "geo:-10.988879,20.123441"; //it should round to 6 decimal places
			String actual = subTypes.first("GEO");
			assertEquals(expected, actual);
		}

		//make sure it unmarshals the text value correctly
		{
			double[] expected = new double[] { -10.988879, 20.123441 };
			double[] actual = subTypes.getGeo();
			assertArrayEquals(expected, actual, .00001);
		}
	}

	@Test(expected = IllegalStateException.class)
	public void geo_malformed() {
		subTypes.put("GEO", "invalid");
		subTypes.getGeo();
	}

	/**
	 * Make sure it handles PID values correctly.
	 */
	@Test
	public void pid() {
		assertTrue(subTypes.getPids().isEmpty());
		subTypes.addPid(1);
		subTypes.addPid(2, 1);

		//make sure it builds the correct string values
		assertEquals(Arrays.asList("1", "2.1"), subTypes.get("PID"));

		//make sure it unmarshals the string values correctly
		Iterator<Integer[]> it = subTypes.getPids().iterator();
		Integer[] pid = it.next();
		assertIntEquals(1, pid[0]);
		assertNull(pid[1]);
		pid = it.next();
		assertIntEquals(2, pid[0]);
		assertIntEquals(1, pid[1]);
		assertFalse(it.hasNext());
	}

	@Test(expected = IllegalStateException.class)
	public void pid_malformed() {
		subTypes.put("PID", "1.1");
		subTypes.put("PID", "invalid");
		subTypes.getPids();
	}

	@Test
	public void getIndex() {
		assertNull(subTypes.getIndex());
		subTypes.put("INDEX", "1");
		assertIntEquals(1, subTypes.getIndex());
	}

	@Test(expected = IllegalStateException.class)
	public void getIndex_malformed() {
		subTypes.put("INDEX", "invalid");
		subTypes.getIndex();
	}

	@Test
	public void setIndex() {
		subTypes.setIndex(1);
		assertEquals("1", subTypes.first("INDEX"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void setIndex_negative() {
		subTypes.setIndex(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setIndex_zero() {
		subTypes.setIndex(0);
	}

	@Test
	public void sortAs() {
		assertTrue(subTypes.getSortAs().isEmpty());

		subTypes = new VCardSubTypes();
		subTypes.setSortAs("one", "two");
		assertEquals(Arrays.asList("one", "two"), subTypes.getSortAs());
		assertEquals(Arrays.asList("one", "two"), subTypes.get("SORT-AS"));

		subTypes = new VCardSubTypes();
		subTypes.put("SORT-AS", "one");
		subTypes.put("SORT-AS", "two");
		assertEquals(Arrays.asList("one", "two"), subTypes.getSortAs());

		subTypes = new VCardSubTypes();

		subTypes.setSortAs("one", "three");
		assertEquals(Arrays.asList("one", "three"), subTypes.getSortAs());

		subTypes.setSortAs();
		assertTrue(subTypes.getSortAs().isEmpty());

		subTypes.setSortAs("one", "two");
		subTypes.setSortAs((String[]) null);
		assertTrue(subTypes.getSortAs().isEmpty());

		subTypes.setSortAs("one", "two");
		subTypes.setSortAs((String) null);
		assertEquals(Arrays.asList((String) null), subTypes.getSortAs());
	}
}
