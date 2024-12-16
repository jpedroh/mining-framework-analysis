package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.Section;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Addendum;
import gov.nysenate.openleg.model.committee.Agenda;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.JsonSerializer;
import gov.nysenate.openleg.util.TranscriptFixer;
import gov.nysenate.openleg.util.XmlHelper;
import gov.nysenate.openleg.util.XmlSerializer;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.UnmergedPathException;
import org.eclipse.jgit.lib.RepositoryBuilder;


public class IngestReader {
	@SuppressWarnings("serial")
	public static class IngestException extends Exception {}

	private static Logger logger = Logger.getLogger(IngestReader.class);

	private static String WRITE_DIRECTORY;

	BasicParser basicParser;

	CalendarParser calendarParser;

	CommitteeParser committeeParser;

	ObjectMapper mapper;

	SearchEngine2 searchEngine;

	private final long THE_TIME = new Date().getTime();

	Git repo;

	public static void main(String[] args) throws IOException {
		IngestReader ir = new IngestReader();
		try {
			if (args.length < 2) {
				throw new IngestException();
			}
			String command = args[0];
			if (args.length == 2) {
				if (command.equals("-gx")) {
					XmlHelper.generateXml(args[1]);
				} else if (command.equals("-b")) {
					//In the case of bills, we also need to make sure we reindex all ammended versions
					Bill bill = ((Bill) (ir.loadObject(args[1], Bill.class)));
					if (ir.reindexAmendedVersions(((Bill) (bill)))) {
						bill.setLuceneActive(false);
					}
					ir.indexSenateObject(bill);
				} else if (command.equals("-c")) {
					ir.indexSenateObject(ir.loadObject(args[1], Calendar.class));
				} else if (command.equals("-a")) {
					ir.indexSenateObject(ir.loadObject(args[1], Agenda.class));
				} else if (command.equals("-t")) {
					ir.indexSenateObject(ir.loadObject(args[1], Transcript.class));
				} else if (command.equals("-it")) {
					//Processes, writes, and indexes a directory of transcripts
					ir.handleTranscript(new File(args[1]));
				} else {
					throw new IngestException();
				}
			} else if (args.length == 3) {
				if (command.equals("-i")) {
					WRITE_DIRECTORY = args[1];
					ir.processFile(new File(args[2]));
				} else if (command.equals("-fc")) {
					ir.fixCalendarBills(args[1], args[2]);
				} else if (command.equals("-fa")) {
					ir.fixAgendaBills(args[1], args[2]);
				} else {
					throw new IngestException();
				}
			} else if (args.length == 5) {
				if (command.equals("-pull")) {
					ir.pullSobis(args[1], args[2], args[3], args[4]);
				}
			}
		} catch (IngestReader.IngestException e) {
			System.err.println("appropriate usage is:\n" + ((((((((("\t-i <json directory> <sobi directory> (to create index)\n" + "\t-gx <sobi directory> (to generate agenda and calendar xml from sobi)\n") + "\t-fc <year> <calendar directory> (to fix calendar bills)\n") + "\t-fa <year> <agenda directory> (to fix agenda bills)\n") + "\t-b <bill json path> (to reindex single bill)\n") + "\t-c <calendar json path> (to reindex single calendar)\n") + "\t-a <agenda json path> (to reindex single agenda)\n") + "\t-t <transcript json path> (to reindex single transcript)") + "\t-it <transcript sobi path> (to reindex dir of transcripts)\n") + "\t-pull <sobi directory> <output directory> <id> <year> (get an objects referencing sobis)"));
		}
	}

	public IngestReader() {
		searchEngine = SearchEngine2.getInstance();
		mapper = new ObjectMapper();
		SerializationConfig cnfg = mapper.getSerializationConfig();
		cnfg.set(Feature.INDENT_OUTPUT, true);
		mapper.setSerializationConfig(cnfg);
		committeeParser = new CommitteeParser(this);
		basicParser = new BasicParser();
		calendarParser = new CalendarParser(this);
	}

	public void processFile(File file) {
		if (file.isDirectory()) {
			for (File child : sortFilesByName(file.listFiles())) {
				processFile(child);
			}
		} else {
			try {
				logger.info("Reading file: " + file);
				ArrayList<ISenateObject> objects = new ArrayList<ISenateObject>();
				ArrayList<ILuceneObject> luceneObjects = new ArrayList<ILuceneObject>();
				long start = System.currentTimeMillis();
				if (file.getName().endsWith(".TXT")) {
					for (Bill bill : basicParser.handleBill(file.getAbsolutePath(), '-')) {
						objects.add(processSenateObject(bill, Bill.class, file, true));
					}
					basicParser.clearBills();
				} else if (file.getName().contains("-calendar-")) {
					XmlHelper.fixCalendar(file);
					for (Calendar calendar : calendarParser.doParsing(file.getAbsolutePath())) {
						objects.add(processSenateObject(calendar, Calendar.class, file, true));
					}
					calendarParser.clearCalendars();
				} else if (file.getName().contains("-agenda-")) {
					for (ISenateObject obj : committeeParser.doParsing(file)) {
						if (obj instanceof Bill) {
							objects.add(processSenateObject(obj, Bill.class, file, true));
						} else if (obj instanceof Agenda) {
							objects.add(processSenateObject(obj, Agenda.class, file, true));
						}
					}
					committeeParser.clearUpdates();
				} else {
					// This file doesn't belong here...
					throw new IngestException();
				}
				logger.warn(((System.currentTimeMillis() - start) / 1000.0) + " - Processed Objects");
				// Write the objects
				start = System.currentTimeMillis();
				for (ISenateObject obj : objects) {
					if (writeSenateObject(obj)) {
						luceneObjects.add(obj);
					}
				}
				logger.warn(((System.currentTimeMillis() - start) / 1000.0) + " - Wrote Objects");
				// Index the objects we wrote successfully
				start = System.currentTimeMillis();
				this.searchEngine.indexSenateObjects(luceneObjects, new LuceneSerializer[]{ new XmlSerializer(), new JsonSerializer() });
				logger.warn(((System.currentTimeMillis() - start) / 1000.0) + " - Indexed Objects");
				// Commit the changes made to the file system
				start = System.currentTimeMillis();
				commit(file.getName());
				logger.warn(((System.currentTimeMillis() - start) / 1000.0) + " - Committed Changes");
				logger.warn("Finished with file: " + file.getName());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IngestReader.IngestException e) {
				// TODO Auto-generated catch block
				// We don't care about this file, do nothing
				// e.printStackTrace();
			} catch (java.lang.Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void writeSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, boolean merge) {
		writeSenateObject(obj, clazz, THE_TIME, merge);
	}

	public void writeSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, long modified, boolean merge) {
		logger.info((("Writing object type: " + obj.luceneOtype()) + " with id: ") + obj.luceneOid());
		long start;
		try {
			if (obj == null) {
				return;
			}
			File newFile = new File(((((((WRITE_DIRECTORY + "/") + obj.getYear()) + "/") + obj.luceneOtype()) + "/") + obj.luceneOid()) + ".json");
			if (merge) {
				obj = mergeSenateObject(obj, clazz, newFile);
			}
			obj.setLuceneModified(modified);
			if (this.writeJsonFromSenateObject(obj, clazz, newFile)) {
				// TODO
				start = System.currentTimeMillis();
				indexSenateObject(obj);
				logger.warn("Indexing took " + ((System.currentTimeMillis() - start) / 1000.0));
			}
		} catch (java.lang.Exception e) {
			logger.warn("Exception while writing object", e);
		}
	}

	public void commit(String message) {
		//Condensed for speed, don't pull the pieces out into a "run" func
		try {
			String line;
			Process process;
			BufferedReader error;
			File repository = new File(WRITE_DIRECTORY);
			Runtime runtime = Runtime.getRuntime();
			if (!new File(WRITE_DIRECTORY + "/.git").exists()) {
				process = runtime.exec("git init", null, repository);
				error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while ((line = error.readLine()) != null) {
					logger.error(line);
				} 
			}
			process = runtime.exec("git add .", null, repository);
			error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ((line = error.readLine()) != null) {
				logger.error(line);
			} 
			process = runtime.exec(("git commit -m " + message) + "\"", null, repository);
			error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ((line = error.readLine()) != null) {
				logger.error(line);
			} 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ISenateObject loadObject(String id, String year, String type, Class<? extends ISenateObject> clazz) {
		return loadObject(WRITE_DIRECTORY + "/" + year + "/" + type + "/" + id + ".json", clazz);
	}

	/**
	 * @param path to json document
	 * @param clazz class of object to be loaded
	 * @return deserialized SenateObject of type clazz
	 */
	public ISenateObject loadObject(String path, Class<? extends ISenateObject> clazz) {
		try {
			logger.info("Loading object at: " + path);
			File file = new File(path);
			if (!file.exists()) {
				return null;
			}
			return mapper.readValue(file, clazz);
		} catch (jackson e) {
			logger.warn("could not parse json", e);
		} catch (JsonMappingException e) {
			logger.warn("could not map json", e);
		} catch (IOException e) {
			logger.warn("error with file", e);
		}
		return null;
	}

	public ISenateObject processSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, File file, boolean merge) {
		obj.addSobiReference(file.getName());
		return processSenateObject(obj, clazz, getDateFromFileName(file.getName()), merge);
	}

	public ISenateObject processSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, boolean merge) {
		return processSenateObject(obj, clazz, THE_TIME, merge);
	}

	public ISenateObject processSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, long modified, boolean merge) {
		if (clazz == Bill.class) {
			if (reindexAmendedVersions(((Bill) (obj)))) {
				obj.setLuceneActive(false);
			}
		}
		if (merge) {
			obj = mergeSenateObject(obj, clazz);
		}
		obj.setLuceneModified(modified);
		return obj;
	}

	public void processSenateObjects(ArrayList<ISenateObject> objects, Class<? extends ISenateObject> clazz, long modified, boolean merge) {
		// Process all of the objects into completed LuceneObjects for indexing
		ArrayList<ILuceneObject> luceneObjects = new ArrayList<ILuceneObject>();
		for (ISenateObject obj : objects) {
			//Prepare the objects for writing
			if (merge) {
				obj = mergeSenateObject(obj, clazz);
			}
			obj.setLuceneModified(modified);
			// If the write is successful, slot the object for indexing
			if (writeSenateObject(obj) == true) {
				luceneObjects.add(obj);
			}
		}
		//Attempt to index all the objects in one go!
		try {
			long start = System.currentTimeMillis();
			searchEngine.indexSenateObjects(luceneObjects, new LuceneSerializer[]{ new XmlSerializer(), new JsonSerializer() });
			logger.warn("Indexing took " + ((System.currentTimeMillis() - start) / 1000.0));
		} catch (IOException e) {
			logger.warn("Exception while indexing object", e);
		}
	}

	public ISenateObject mergeSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz) {
		File file = new File(((((((WRITE_DIRECTORY + "/") + obj.getYear()) + "/") + obj.luceneOtype()) + "/") + obj.luceneOid()) + ".json");
		if (file.exists()) {
			logger.info("Merging object with id: " + obj.luceneOid());
			try {
				ISenateObject oldObject = ((ISenateObject) (mapper.readValue(file, clazz)));
				oldObject.setLuceneActive(obj.getLuceneActive());
				oldObject.merge(obj);
				obj = oldObject;
			} catch (JsonGenerationException e) {
				logger.warn("could not parse json", e);
			} catch (JsonMappingException e) {
				logger.warn("could not parse json", e);
			} catch (IOException e) {
				logger.warn("error reading file", e);
			}
		}
		return obj;
	}

	public boolean writeSenateObject(ISenateObject obj) {
		File yearDir = new File((WRITE_DIRECTORY + "/") + obj.getYear());
		File typeDir = new File((((WRITE_DIRECTORY + "/") + obj.getYear()) + "/") + obj.luceneOtype());
		File newFile = new File(((((((WRITE_DIRECTORY + "/") + obj.getYear()) + "/") + obj.luceneOtype()) + "/") + obj.luceneOid()) + ".json");
		if (!yearDir.exists()) {
			logger.info("creating directory: " + yearDir.getAbsolutePath());
			yearDir.mkdir();
		}
		if (!typeDir.exists()) {
			logger.info("creating directory: " + typeDir.getAbsolutePath());
			typeDir.mkdir();
		}
		logger.info("Writing json to path: " + newFile.getAbsolutePath());
		try {
			BufferedOutputStream osw = new BufferedOutputStream(new FileOutputStream(newFile));
			JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(osw, JsonEncoding.UTF8);
			generator.setPrettyPrinter(new DefaultPrettyPrinter());
			mapper.writeValue(generator, obj);
			osw.close();
			return true;
		} catch (JsonGenerationException e) {
			logger.warn("could not parse json", e);
		} catch (JsonMappingException e) {
			logger.warn("could not parse json", e);
		} catch (IOException e) {
			logger.warn("error reading file", e);
		}
		return false;
	}

	public boolean writeJsonFromSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, File file) {
		logger.info("Writing json to path: " + file.getAbsolutePath());
		long start = System.currentTimeMillis();
		if (file == null) {
			file = new File(((((((WRITE_DIRECTORY + "/") + obj.getYear()) + "/") + obj.luceneOtype()) + "/") + obj.luceneOid()) + ".json");
		}
		File dir = new File((WRITE_DIRECTORY + "/") + obj.getYear());
		if (!dir.exists()) {
			logger.info("creating directory: " + dir.getAbsolutePath());
			dir.mkdir();
		}
		dir = new File((((WRITE_DIRECTORY + "/") + obj.getYear()) + "/") + obj.luceneOtype());
		if (!dir.exists()) {
			logger.info("creating directory: " + dir.getAbsolutePath());
			dir.mkdir();
		}
		try {
			BufferedOutputStream osw = new BufferedOutputStream(new FileOutputStream(file));
			JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(osw, JsonEncoding.UTF8);
			generator.setPrettyPrinter(new DefaultPrettyPrinter());
			mapper.writeValue(generator, obj);
			osw.close();
			logger.warn("Writing took " + ((System.currentTimeMillis() - start) / 1000.0));
			return true;
		} catch (JsonGenerationException e) {
			logger.warn("could not parse json", e);
		} catch (JsonMappingException e) {
			logger.warn("could not parse json", e);
		} catch (IOException e) {
			logger.warn("error reading file", e);
		}
		return false;
	}

	public ISenateObject mergeSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, File file) {
		if (file == null) {
			file = new File(((((((WRITE_DIRECTORY + "/") + obj.getYear()) + "/") + obj.luceneOtype()) + "/") + obj.luceneOid()) + ".json");
		}
		if (file.exists()) {
			logger.info("Merging object with id: " + obj.luceneOid());
			ISenateObject oldObject = null;
			try {
				oldObject = ((ISenateObject) (mapper.readValue(file, clazz)));
			} catch (JsonGenerationException e) {
				logger.warn("could not parse json", e);
			} catch (JsonMappingException e) {
				logger.warn("could not parse json", e);
			} catch (IOException e) {
				logger.warn("error reading file", e);
			}
			if (oldObject != null) {
				oldObject.setLuceneActive(obj.getLuceneActive());
				oldObject.merge(obj);
				obj = oldObject;
			}
		}
		return obj;
	}

	//TODO this is pretty bad
	public void handleTranscript(File file) {
		if (file.isDirectory()) {
			for (File temp : file.listFiles()) {
				handleTranscript(temp);
			}
		} else {
			Transcript trans = null;
			//transcripts often come incorrectly formatted..
			//this attempts to reprocess and save the raw text
			//if there is a parsing error, and then attempts
			//parsing one more time
			try {
				trans = basicParser.handleTranscript(file.getAbsolutePath());
			} catch (java.lang.Exception e) {
				try {
					List<String> in;
					TranscriptFixer fixer = new TranscriptFixer();
					if ((in = fixer.readContents(file)) != null) {
						List<String> ret = fixer.fix(in);
						BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
						for (String s : ret) {
							bw.write(s);
							bw.newLine();
						}
						bw.close();
						trans = basicParser.handleTranscript(file.getAbsolutePath());
					}
				} catch (java.lang.Exception e2) {
					e2.printStackTrace();
					return;// We couldn't get a good read on the transcript

				}
			}
			//Conduct general processing, writing, and indexing
			processSenateObject(trans, Transcript.class, true);
			if (writeSenateObject(trans)) {
				indexSenateObject(trans);
			}
		}
	}

	public boolean deleteSenateObject(ISenateObject so) {
		try {
			searchEngine.deleteSenateObject(so);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deleteFile(so.luceneOid(), so.getYear() +"", so.luceneOtype());
	}

	public boolean deleteFile(String id, String year, String type) {
		return deleteFile(WRITE_DIRECTORY + "/" + year + "/" + type + "/" + id + ".json");
	}

	public boolean deleteFile(String path) {
		logger.info("Deleting file at: " + path);
		
		File file = new File(path);
		return file.delete();
	}

	/* TODO
	 * INDEXING
	 */

	public void indexSenateObject(ISenateObject obj) {
		try {
			searchEngine.indexSenateObjects(
					new ArrayList<ILuceneObject>(
						Arrays.asList(obj)), 
						new LuceneSerializer[]{
							new XmlSerializer(), 
							new JsonSerializer()});
		} catch (IOException e) {
			logger.warn("Exception while indexing object", e);
		}
	}

	/**
	 * desirable to hide old versions of an amended bill from the default search
	 * this appends "active:false" as a field to any old versions of bills
	 *
	 * to avoid constantly rewriting amended versions of bills this does a query
	 * to lucene to check if they've already been hidden, if they haven't then
	 * they are sent to reindexInactiveBill
	 *
	 * @param bill
	 * 		returns true if current bill isn't searchable, false otherwise
	 */
	public boolean reindexAmendedVersions(Bill bill) {
		int idx = bill.getSenateBillNo().indexOf("-");
		char c = bill.getSenateBillNo().charAt(idx - 1);
		String[] strings = bill.getSenateBillNo().split("-");
		String query = null;
		if ((c >= 65) && (c < 90)) {
			query = strings[0].substring(0, strings[0].length() - 1);
		} else {
			query = strings[0];
		}
		try {
			// oid:(S418-2009 OR [S418A-2009 TO S418Z-2009]) AND year:2009
			query = ((((((((((((((("otype:bill AND oid:((" + query) + "-") + strings[1]) + " OR [") + query) + "A-") + strings[1]) + " TO ") + query) + "Z-") + strings[1]) + "]) AND ") + query) + "*-") + strings[1]) + ")";
			// caches recent searches, if s1, s1a and s1b are added in close succession
			//it's possible they s1a won't be picked up.. closing the searcher
			//fixes that for the time being
			searchEngine.closeSearcher();
			SenateResponse sr = searchEngine.search(query, "json", 0, 100, null, false);
			// if there aren't any results this is a new bill
			if (sr.getResults().isEmpty()) {
				return false;
			}
			// create a list and store bill numbers from oldest to newest
			ArrayList<String> billNumbers = new ArrayList<String>();
			for (Result result : sr.getResults()) {
				billNumbers.add(result.getOid());
			}
			if (!billNumbers.contains(bill)) {
				billNumbers.add(bill.getSenateBillNo());
			}
			Collections.sort(billNumbers);
			String newest = billNumbers.get(billNumbers.size() - 1);
			// if bill being stored isn't the newest we can assume
			// that the newest bill has already reindexed older bills
			if (!bill.getSenateBillNo().equals(newest)) {
				return true;
			}
			billNumbers.remove(newest);
			billNumbers.remove(bill.getSenateBillNo());
			for (Result result : sr.getResults()) {
				if (billNumbers.contains(result.getOid())) {
					if (result.getActive().equals("true")) {
						reindexInactiveBill(result.getOid(), bill.getYear() + "");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (lucene e) {
			e.printStackTrace();
		}
		return false;
	}

	private void reindexInactiveBill(String senateBillNo, String year) {
		Bill temp = (Bill)this.loadObject(senateBillNo,
				year,
				"bill",
				Bill.class);
		
		if(temp != null) {
			temp.setLuceneActive(false);
			
			this.indexSenateObject(temp);
		}
	}

	/* TODO
	 * UTILITIES
	 */

	/*
	 * fixCalendarBills(year,path) and fixAgendaBills(year,path) can be
	 * executed to update the two document types with the latest bill information.
	 * This solves an issue where occasionally calendars or agendas
	 * would be missing relevant information that SHOULD be available to them.
	 */

	public void fixCalendarBills(String year, String path) {
		File file = new File(path);
		
		if(!file.exists())
			return;
		
		if(file.isDirectory()) {
			for(File temp:file.listFiles()) {
				fixCalendarBills(year, temp.getAbsolutePath());
			}
		}
		else {
			Calendar cal = (Calendar) this.loadObject(file.getAbsolutePath(), Calendar.class);
			
			if(cal == null) 
				return;
			
			if(cal.getSupplementals() != null) {
				for(Supplemental sup:cal.getSupplementals()) {
					if(sup.getSections() != null) {
						for(Section section:sup.getSections()) {
							for(CalendarEntry ce:section.getCalendarEntries()) {
								ce.setBill(
									(Bill)this.loadObject(
										ce.getBill().getSenateBillNo(),
										year,
										"bill",
										Bill.class)
								);
							}
						}
					}
					
					if(sup.getSequence() != null) {
						for(CalendarEntry ce:sup.getSequence().getCalendarEntries()) {
							if(ce.getBill() != null) {							
								ce.setBill(
									(Bill)this.loadObject(
										ce.getBill().getSenateBillNo(),
										year,
										"bill",
										Bill.class)
								);
							}
						}
					}
				}
			}
			this.writeSenateObject(cal, Calendar.class, false);
		}
	}

	public void fixAgendaBills(String year, String path) {
		File file = new File(path);
		
		if(!file.exists()){
			return;
		}
		
		if(file.isDirectory()) {
			for(File temp:file.listFiles()) {
				fixAgendaBills(year, temp.getAbsolutePath());
			}
		}
		else {
			Agenda agenda = (Agenda) this.loadObject(file.getAbsolutePath(), Agenda.class);
			
			if(agenda == null) {
				return;
			}
			
			if(agenda.getAddendums() != null) {
				for(Addendum addendum:agenda.getAddendums()) {
					if(addendum.getMeetings() != null) {
						for(Meeting meeting:addendum.getMeetings()) {
							if(meeting.getBills() ==  null) {
								continue;
							}
							
							for(int i = 0; i < meeting.getBills().size(); i++) {
								meeting.getBills().set(i,
									(Bill)this.loadObject(
										meeting.getBills().get(i).getSenateBillNo(),
										year,
										"bill",
										Bill.class)
								);
							}
						}
					}
				}
			}
			this.writeSenateObject(agenda, Agenda.class, false);
		}
	}

	public long getDateFromFileName(String fileName) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		
		fileName = fileName.replaceAll("(SOBI\\.D|\\.TXT.*$)", "");
		
		if(fileName.length() == 14) {
			cal.set(Integer.parseInt(fileName.substring(0,2)) + 2000,
					Integer.parseInt(fileName.substring(2,4))-1,
					Integer.parseInt(fileName.substring(4,6)),
					Integer.parseInt(fileName.substring(8,10)),
					Integer.parseInt(fileName.substring(10,12)),
					Integer.parseInt(fileName.substring(12,14)));
		}
				
		return cal.getTimeInMillis();
	}

	public void pullSobis(String id, String year, String sobiDirectory, String writeDirectory) {
		Bill bill = (Bill) this.loadObject(id, year, "bill", Bill.class);
		for(String sobi:bill.getSobiReferenceList()) {
			File sobiFile = new File(sobiDirectory + System.getProperty("file.separator") + sobi);
			if(sobiFile.exists()) {
				File copySobi = new File(writeDirectory + System.getProperty("file.separator") + sobi);
				try {
					copySobi.createNewFile();
				} catch (IOException e) {
					logger.warn(e);
				}
				
				if(!copySobi.exists()) 
					continue;
				
				FileChannel source = null;
				FileChannel destination = null;
				
				try {
					source = new FileInputStream(sobiFile).getChannel();
					destination = new FileOutputStream(copySobi).getChannel();
					destination.transferFrom(source, 0, source.size());
				} catch (FileNotFoundException e) {
					logger.warn(e);
				} catch (IOException e) {
					logger.warn(e);
				}
				finally {
					if(source != null) {
						try {
							source.close();
						} catch (IOException e) {
							logger.warn(e);
						}
					}
					if(destination != null) {
						try {
							destination.close();
						} catch (IOException e) {
							logger.warn(e);
						}
					}
				}
			}
		}
	}

	public static File[] sortFilesByName(File[] fList) {
		Arrays.sort(fList, new Comparator<File>() {
			@Override
			public int compare(File one, File two) {
				return one.getName().compareTo(two.getName());
			}
		});
		
		return fList;
	}

	/* 
	 * 
	 * JGit has been retired for now
	 * 
	 * */
	public Git getRepo(String workingDrive) {
		if (repo == null) {
			try {
				if (!new File(workingDrive + ".git/").exists()) {
					Git.init().setDirectory(new File(workingDrive)).call();
				}
				repo = new Git(new RepositoryBuilder().setWorkTree(new File(workingDrive)).build());
			} catch (IOException e) {
				logger.error(e);
				System.exit(0);
			}
		}
		return repo;
	}

	public void gitCommit(String message) {
		Git git = getRepo(WRITE_DIRECTORY);
		try {
			git.add().addFilepattern(".").call();
			git.commit().setMessage(message).setAuthor("Tester", "notmyfault@nysenate.gov").call();
		} catch(WrongRepositoryStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnmergedPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConcurrentRefUpdateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JGitInternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}