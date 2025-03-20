<<<<<<< /usr/src/app/output/chat-wane/lseq/916d8a5efc2654df3f31715ed05eea52f7d09957/src/main/java/alma/fr/App2.java/left.java
package alma.fr;

import java.util.ArrayList;

public class App2 {
	public static void main(String[] args) {

		boolean pngExport = false;
		boolean histoPrompt = true;
		String prefix = "en";
		Integer nRev = 1000; // wikipedia returns 500 revs max
		ArrayList<String> pageList = new ArrayList<String>();

		if (args.length < 2) {
			System.err.println("Wrong number of arguments");
			System.out.println("wikiExtractor [pageName]+");
			System.out.println("-n=N extract from revision 1 to N");
			System.out.println("-o to set the png export on");
			System.out.println("-Dhttp.proxyHost=adress to specify the proxy");
			System.out.println("-Dhttp.proxyPort=P with P the proxy port");
			System.out
					.println("-pre=prefix set the prefix of wikipedia's page, default(en)");
			System.out.println("-nh no histogramme prompted ");
			System.exit(0);
		} else {
			for (int i = 0; i < args.length; i++) {
				if (args[i].contains("-n=")) {
					nRev = Integer.parseInt(args[i].substring(3).trim());
				} else if (args[i].contains("-o")) {
					pngExport = true;
				} else if (args[i].startsWith("-D")) {
					if (args[i].startsWith("-Dhttp.proxyHost=")) {
						System.setProperty("http.proxyHost",
								args[i].substring(17).trim());
					}
					if (args[i].startsWith("-Dhttp.proxyPort=")) {
						System.setProperty("http.proxyPort",
								args[i].substring(17).trim());
					}
					// ignore
				} else if (args[i].contains("-pre=")) {
					prefix = args[i].substring(5).trim();
				} else if (args[i].contains("-nh")) {
					histoPrompt = false;
				} else {
					pageList.add(args[i]);
				}
			}

		}

		RunWiki rw = new RunWiki();
		for (String pageName : pageList) {

			System.out.println("Calling " + pageName);

			rw.nRev = nRev;
			rw.pageName = pageName;
			rw.prefix = prefix;

			rw.run();

		}
	}
}
||||||| /usr/src/app/output/chat-wane/lseq/916d8a5efc2654df3f31715ed05eea52f7d09957/src/main/java/alma/fr/App2.java/base.java
package alma.fr;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import alma.fr.basecomponents.IBase;
import alma.fr.data.Positions;
import alma.fr.documentgenerator.WikipediaDocument;
import alma.fr.logootenginecomponents.LogootEngine;
import alma.fr.logootenginecomponents.Replica;
import alma.fr.modules.DoubleModule;
import alma.fr.modules.GreedDoubleModule;
import alma.fr.strategychoicecomponents.FakeListNode;
import alma.fr.strategychoicecomponents.IStrategyChoice;
import alma.fr.view.MultipleChartView;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class App2 {
	public static void main(String[] args) {
		DecimalFormat df = new DecimalFormat("#.##");

		Float[] result;
		Injector injector;
		Injector injector2;

		LogootEngine logootEngine;
		LogootEngine logootEngine2;
		WikipediaDocument wd = new WikipediaDocument();
		WikipediaDocument wd2 = new WikipediaDocument();

		/*********************************/

		// injector = Guice.createInjector(new WeissModule());
		// injector = Guice.createInjector(new GreedModule());
		// injector = Guice.createInjector(new DoubleModule());
		// injector = Guice.createInjector(new GreedDoubleModule());
		injector = Guice.createInjector(new DoubleModule());
		injector2 = Guice.createInjector(new GreedDoubleModule());

		logootEngine = injector.getInstance(LogootEngine.class);
		logootEngine2 = injector2.getInstance(LogootEngine.class);

		logootEngine.setReplica(new Replica());
		logootEngine2.setReplica(new Replica());

		String prefix = "en";
		String pageName = "Template_talk:Did_you_know";
		//String pageName="Liste_des_bureaux_de_poste_français_classés_par_oblitération_Petits_Chiffres";
		//String pageName="Résultats_des_recensements_linguistiques_des_communes_à_facilités_linguistiques";
		Integer nRev = 200;
		URL url;

		// Uncomment and fill these if behind proxy
		//System.setProperty("http.proxyHost", +proxyIp);
		//System.setProperty("http.proxyPort", +proxyPort);

		try {
			url = new URL(
					"http://"
							+ prefix
							+ ".wikipedia.org/w/api.php?action=query&prop=revisions&titles="
							+ pageName
							+ "&rvprop=timestamp|ids|user|comment|content&rvdir=newer&format=xml&rvlimit="
							+ nRev);

			wd.setUrl(url);
			wd.run(logootEngine);

			System.out.println("==== " + logootEngine.getIdTable().size()
					+ " ====");
			result = avgAndMaxSize(logootEngine.getIdTable());
			System.out.println("EditingModule avg : " + result[0]);
			System.out.println("EditingModule max : " + result[1]);
			result = avgAndMaxBitSize(logootEngine.getIdTable());
			System.out.println("EditingModule avg (bit) : " + result[0]);
			System.out.println("EditingModule max (bit) : " + result[1]);

			IStrategyChoice wsc = logootEngine.getStrategyChoice();
			Positions first = logootEngine.getIdTable().get(1);
			int i = 1;

			ArrayList<String> curveNames = new ArrayList<String>();
			result = avgAndMaxSize(logootEngine.getIdTable());
//			curveNames.add("Id-length (m=" + df.format(result[0]) + ")");
			result = avgAndMaxBitSizeDouble(logootEngine.getIdTable(),
					logootEngine.getBase());
			curveNames.add("Weiss-bitLength (m=" + result[0] + ")");

			ArrayList<Integer> dateList = new ArrayList<Integer>();
			ArrayList<Integer> longueurList = new ArrayList<Integer>();
			ArrayList<Integer> longueurBitList = new ArrayList<Integer>();

			while (wsc.getSpectrum().get(first).getNext() != null) {
				longueurList.add(logootEngine.getIdTable().get(i).getD().bitLength());

				Integer tempsum = 0;
				for (int j = 0; j < first.getD().bitLength(); ++j) {
					tempsum += logootEngine.getBase().getBase(j).bitLength();
				}
				longueurBitList.add(tempsum);

				dateList.add(wsc.getSpectrum().get(first).getDate());

				first = wsc.getSpectrum().get(first).getNext();
				++i;
			}

			longueurList.add(logootEngine.getIdTable().get(i).size());

			Integer tempsum = 0;
			for (int j = 0; j < first.size(); ++j) {
				tempsum += logootEngine.getBase().getBase(j).bitLength();
			}
			longueurBitList.add(tempsum);

			dateList.add(wsc.getSpectrum().get(first).getDate());

			MultipleChartView cv = new MultipleChartView();
			//cv.getLengths().add(longueurList);
			cv.getLengths().add(longueurBitList);
			cv.setDatas(dateList);

			// ////////////////////
			// 2nd Logoot engine to compare

			wd2.setUrl(url);
			wd2.run(logootEngine2);

			System.out.println("==== " + logootEngine2.getIdTable().size()
					+ " ====");
			result = avgAndMaxSize(logootEngine2.getIdTable());
			System.out.println("EditingModule avg : " + result[0]);
			System.out.println("EditingModule max : " + result[1]);
			result = avgAndMaxBitSize(logootEngine2.getIdTable(),
					logootEngine2.getBase());
			System.out.println("EditingModule avg (bit) : " + result[0]);
			System.out.println("EditingModule max (bit) : " + result[1]);

			wsc = logootEngine2.getStrategyChoice();
			first = logootEngine2.getIdTable().get(1);
			i = 1;

			result = avgAndMaxSize(logootEngine2.getIdTable());
			//curveNames.add("Id-length (m=" + df.format(result[0]) + ")");
			result = avgAndMaxBitSizeDouble(logootEngine2.getIdTable(),
					logootEngine2.getBase());
			curveNames.add("Double-bitLength (m=" + result[0] + ")");

			dateList = new ArrayList<Integer>();
			longueurList = new ArrayList<Integer>();
			longueurBitList = new ArrayList<Integer>();

			while (wsc.getSpectrum().get(first).getNext() != null) {
				longueurList.add(logootEngine2.getIdTable().get(i).size());

				tempsum = 0;
				for (int j = 0; j < first.size(); ++j) {
					tempsum += logootEngine2.getBase().getBase(j).bitLength();
				}
				longueurBitList.add(tempsum);

				dateList.add(wsc.getSpectrum().get(first).getDate());

				first = wsc.getSpectrum().get(first).getNext();
				++i;
			}

			longueurList.add(logootEngine2.getIdTable().get(i).size());

			tempsum = 0;
			for (int j = 0; j < first.size(); ++j) {
				tempsum += logootEngine2.getBase().getBase(j).bitLength();
			}
			longueurBitList.add(tempsum);

			//dateList.add(wsc.getSpectrum().get(first).getDate());

			//cv.getLengths().add(longueurList);
			cv.getLengths().add(longueurBitList);
			//cv.setDatas(dateList);

			cv.setOpName(pageName);
			cv.setLegend(curveNames);
			cv.setNote("base : 2^64 (Weiss), 2^(4+depth) (Double);\n" +
					"strategy : boundary+ (both);\n" +
					"boundary : 1,000,000 (Weiss), 10 (Double).");
			
			cv.run();
			//cv.export();
			cv.setVisible(true);
			

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	
	// / / / / ///// / / // / // / / // / / // / // / / /
	public static Float[] avgAndMaxBitSize(List<Positions> logootIdTable) {
		BigInteger[] tempResult = { BigInteger.ZERO, BigInteger.ZERO };
		Float[] result = { 0f, 0f };
		for (Positions p : logootIdTable) {
			tempResult[0] = tempResult[0].add(BigInteger.valueOf(p.getD()
					.bitLength()));

			if (p.getD().bitLength() > result[1]) {
				result[1] = (float) p.getD().bitLength();
			}
		}
		tempResult = tempResult[0].divideAndRemainder(BigInteger
				.valueOf(logootIdTable.size()));
		result[0] = tempResult[0].intValue() + (float) tempResult[1].intValue()
				/ logootIdTable.size();
		return result;
	}

	// / / // // / / // / // / / / /// / // / // // / //
	public static void writeFile(String name, ArrayList<Positions> idTable,
			HashMap<Positions, FakeListNode> spectrum) {
		String page = name.replace("/", "_").replace("\\", "_");
		try {
			PrintWriter out = new PrintWriter(new FileWriter(page + ".dat"));
			int docSize = idTable.size();
			for (int i = 0; i < docSize; ++i) {
				String lineS = new String();
				lineS = spectrum.get(idTable.get(i)).getDate() + " "
						+ idTable.get(i).getD().bitLength();
				out.println(lineS);
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
=======
fatal: path 'src/main/java/alma/fr/App2.java' exists on disk, but not in '86932f013055275c558f7f964225bf8e70b98281'
>>>>>>> /usr/src/app/output/chat-wane/lseq/916d8a5efc2654df3f31715ed05eea52f7d09957/src/main/java/alma/fr/App2.java/right.java
