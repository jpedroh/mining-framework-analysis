package org.imixs.workflow.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import org.imixs.workflow.ItemCollection;
import org.junit.Test;

import org.junit.Assert;

/**
 * Test class reads the "/document-example.xml" file via the
 * XMLItemCollectionAdapter and verifies the content. Than the test case writes
 * the data back into a new file "/document-example.xml" on the same file
 * system.
 * 
 * 
 * @author rsoika
 */
public class TestReadWriteXMLData {


	/**
	 * Read /document-example.xml
	 */
	@Test
	public void testRead() {
		List<ItemCollection> col = null;

		try {
			col = XMLDataCollectionAdapter
					.readCollectionFromInputStream(getClass().getResourceAsStream("/document-example.xml"));
		} catch (JAXBException e) {
			Assert.fail();
		} catch (IOException e) {
			Assert.fail();
		}

		Assert.assertEquals(2, col.size());
		
		// test values
		ItemCollection workitem=col.get(0);
		Assert.assertEquals("1.0.1", workitem.getItemValueString("$modelversion"));
		Assert.assertEquals(55.123, workitem.getItemValueDouble("amount"),0);

	}

	/**
	 * reads /document-example.xml input file and writes the jaxb object back into a new xml file
	 */
	@Test
	// @Ignore
	public void testWrite() {
		List<ItemCollection> col = null;
		// read default content
		try {
			col = XMLDataCollectionAdapter
					.readCollectionFromInputStream(getClass().getResourceAsStream("/document-example.xml"));
		} catch (JAXBException e) {
			Assert.fail();
		} catch (IOException e) {
			Assert.fail();
		}

		// create JAXB object
		XMLDataCollection xmlCol = null;
		try {
			xmlCol = XMLDataCollectionAdapter.getDataCollection(col);
		} catch (Exception e1) {

			e1.printStackTrace();
			Assert.fail();
		}

		// now write back to file
		File file = null;
		try {

			file = new File("src/test/resources/export-test.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(XMLDataCollection.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(xmlCol, file);
			jaxbMarshaller.marshal(xmlCol, System.out);

		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail();
		}
		Assert.assertNotNull(file);
	}

}
