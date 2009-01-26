package de.uni_koblenz.jgralabtest.schematest.domaintest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uni_koblenz.jgralab.schema.EnumDomain;
import de.uni_koblenz.jgralab.schema.QualifiedName;
import de.uni_koblenz.jgralab.schema.exception.InvalidNameException;
import de.uni_koblenz.jgralab.schema.exception.NoSuchEnumConstantException;

public class EnumDomainTest extends BasicDomainTest {

	private EnumDomain domain3;
	private EnumDomain domain4;

	@Override
	@Before
	public void init() {
		super.init();
		// Initializing DomainTest
		schema1
				.createEnumDomain(new QualifiedName(
						"package1.subpackage1.Enum1"));
		domain1 = schema1.getDomain("package1.subpackage1.Enum1");
		schema2
				.createEnumDomain(new QualifiedName(
						"package1.subpackage1.Enum1"));
		domain2 = schema2.getDomain("package1.subpackage1.Enum1");
		otherDomain1 = schema1.getDomain("Boolean");
		otherDomain2 = schema2.getDomain("String");
		expectedJavaClassName = schema1Package + ".package1.subpackage1.Enum1";
		expectedTgTypeName = "Enum1";
		expectedStringRepresentation = "domain Enum package1.subpackage1.Enum1 ()";
		expectedDirectoryName1 = "package1" + sep + "subpackage1" + sep
				+ "Enum1";
		expectedDirectoryName2 = "package1" + sep + "subpackage1" + sep
				+ "Enum1";
		expectedQualifiedName1 = "package1.subpackage1.Enum1";
		expectedQualifiedName2 = "package1.subpackage1.Enum1";
		expectedPackage1 = "package1.subpackage1";
		expectedPackage2 = "package1.subpackage1";
		expectedPathName1 = "package1" + sep + "subpackage1";
		expectedPathName2 = "package1" + sep + "subpackage1";
		expectedSimpleName = "Enum1";
		expectedUniqueName1 = "package1_subpackage1_Enum1";
		expectedUniqueName2 = "Enum1";

		// same domainname as Domain1 but in other package (for testing
		// getUniqueName)
		schema1.createEnumDomain(new QualifiedName("package1.Enum1"));
		domain3 = (EnumDomain) schema1.getDomain("package1.Enum1");
		domain3.addConst("Hugo");
		domain3.addConst("Sebastian");
		domain3.addConst("Volker");
		domain3.addConst("Kerstin");
		domain3.addConst("Sascha");
		schema1.createEnumDomain(new QualifiedName("package1.Domain2"));
		domain4 = (EnumDomain) schema1.getDomain("package1.Domain2");
	}

	@Test
	@Override
	public void testGetJavaAttributeImplementationTypeName() {
		// tests if the correct javaAttributeImplementationTypeName is returned
		assertEquals(schema1Package + ".package1.subpackage1.Enum1", domain1
				.getJavaAttributeImplementationTypeName(schema1Package));
	}

	@Test
	@Override
	public void testToString() {
		// tests if the correct string representation is returned
		super.testToString();
		assertEquals(
				"domain Enum package1.Enum1 (0: Hugo, 1: Sebastian, 2: Volker, 3: Kerstin, 4: Sascha)",
				domain3.toString());
	}

	@Test
	@Override
	public void testGetPackageName() {
		// tests if the correct packageName is returned
		super.testGetPackageName();
		assertEquals("package1", domain3.getPackageName());
		assertEquals("package1", domain4.getPackageName());
	}

	@Test
	@Override
	public void testGetUniqueName() {
		// tests if the correct uniqueName is returned
		assertEquals("package1_Enum1", domain3.getUniqueName());
		super.testGetUniqueName();
	}

	@Test
	public void testAddConst1() {
		// Test of adding a new constant in a nonempty EnumDomain
		int oldsize = domain3.getConsts().size();
		domain3.addConst("Daniel");
		assertEquals(oldsize + 1, domain3.getConsts().size());
		assertTrue(domain3.getConsts().contains("Daniel"));
		assertTrue(domain3.getConsts().contains("Hugo"));
		assertTrue(domain3.getConsts().contains("Sebastian"));
		assertTrue(domain3.getConsts().contains("Volker"));
		assertTrue(domain3.getConsts().contains("Kerstin"));
		assertTrue(domain3.getConsts().contains("Sascha"));
	}

	@Test
	public void testAddConst2() {
		// add constant into an empty EnumDomain
		EnumDomain enum1 = (EnumDomain) domain1;
		enum1.addConst("newConstant");
		assertEquals(1, enum1.getConsts().size());
		assertTrue(enum1.getConsts().contains("newConstant"));
	}

	@Test(expected = InvalidNameException.class)
	public void testAddConst3() {
		// add constant that already exists
		domain3.addConst("Sebastian");
	}

	@Test
	public void testDeleteConst1() {
		// delete last constant
		domain3.deleteConst("Sascha");
		testPostDeleteConst1(domain3, "Sascha", new String[] { "Hugo",
				"Sebastian", "Volker", "Kerstin" });
		// delete first constant
		domain3.deleteConst("Hugo");
		testPostDeleteConst1(domain3, "Hugo", new String[] { "Sebastian",
				"Volker", "Kerstin" });
		// delete constant in the middle
		domain3.deleteConst("Volker");
		testPostDeleteConst1(domain3, "Volker", new String[] { "Sebastian",
				"Kerstin" });
		domain3.deleteConst("Kerstin");
		testPostDeleteConst1(domain3, "Kerstin", new String[] { "Sebastian" });
		// delete the last existing constant
		domain3.deleteConst("Sebastian");
		testPostDeleteConst1(domain3, "Sebastian", new String[0]);
	}

	/**
	 * Tests if the EnumDomain enum1 still contains all elements of
	 * expectedConst expect the deleted one.
	 *
	 * @param enum1
	 *            the EnumDomain in which a constant was deleted
	 * @param deletedConst
	 *            the constant which was deleted
	 * @param expectedConst
	 *            the constants which should be still existing after deleting
	 *            one constant
	 */
	private void testPostDeleteConst1(EnumDomain enum1, String deletedConst,
			String[] expectedConst) {
		List<String> consts = enum1.getConsts();
		assertEquals(expectedConst.length, consts.size());
		assertFalse(consts.contains(deletedConst));
		for (String aConst : expectedConst) {
			assertTrue(consts.contains(aConst));
		}
	}

	@Test(expected = NoSuchEnumConstantException.class)
	public void testDeleteConst2() {
		// delete constant that does not exist
		domain3.deleteConst("Nonsense");
	}

	@Test
	public void testGetConsts() {
		// get constants of an EnumDomain which doesn't contain any
		assertEquals(0, domain4.getConsts().size());
		// get constants of an EnumDomain which contains several
		List<String> consts = domain3.getConsts();
		assertEquals(5, consts.size());
		assertEquals("Hugo", consts.get(0));
		assertEquals("Sebastian", consts.get(1));
		assertEquals("Volker", consts.get(2));
		assertEquals("Kerstin", consts.get(3));
		assertEquals("Sascha", consts.get(4));
	}
}
