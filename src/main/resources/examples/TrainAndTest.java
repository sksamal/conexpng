package examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.io.IFCSVMultiClassReader;
import fcatools.conexpng.io.locale.LocaleHandler;
import fcatools.conexpng.model.FuzzyMultiClassedConcept;
import fcatools.conexpng.model.IFuzzyMultiClassifierContext;

import fcatools.conexpng.model.LatticeConcept;

public class TrainAndTest {

	private static TeeWriter tee = null;

	public static void main(String[] args) {

		// Version info
		System.out.println("Train and Test v14");
		try {
			Scanner sc = new Scanner(new File(".build.txt"));
			while (sc.hasNextLine()) {
				System.out.println(sc.nextLine());
			}
			sc.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		String TRAINFILE = "data/exp108_coding_1.fccsv";
		String TESTFILE = "data/exp108_coding_1.fccsv";

		if (args.length >= 1)
			TRAINFILE = args[0];
		if (args.length >= 2)
			TESTFILE = args[1];
		int initial = 10;

		System.setProperty("user.language", LocaleHandler.readLocale());
		PrintStream pwStream = null;
		IFuzzyMultiClassifierContext ifmcModel = null;

		Conf newState = new Conf();
		try {

			// Logging using tee
			pwStream = new PrintStream(TRAINFILE + ".log");
			tee = new TeeWriter(pwStream, System.out);

			newState.filePath = "";
			tee.println("Reading training data from " + TRAINFILE);

			// count the number of records
			IFCSVMultiClassReader ptdgsReader = new IFCSVMultiClassReader(newState, TRAINFILE, 1, false, initial); // last
																													// 1
																													// are
																													// classes,
																													// uniqueness
			int i = 0;
			while (ptdgsReader.readNext()) {
				i++;
			}
			ptdgsReader.close();

			// direct reading of records from the file
			ptdgsReader = new IFCSVMultiClassReader(newState, TRAINFILE, 1, false, initial + i); // last 1 are classes,
																									// uniqueness
			System.out.println("\n**Reading " + (initial + i) + " records directly");
			ifmcModel = ((IFuzzyMultiClassifierContext) newState.context);
			
			Set<Concept<String, FullObject<String, String>>> concepts = ifmcModel.getConcepts();
			ptdgsReader.close();

			tee.println("No of Objects: " + ifmcModel.getObjectCount());
			tee.println("No of attributes: " + ifmcModel.getAttributeCount());
			tee.println("No of concepts: " + concepts.size());


			// Classify all objects
			System.out.println("Created model, starting to classify training data");
//			long ms = System.currentTimeMillis();
			HashMap<String, Concept<String, FullObject<String, String>>> minConceptMap = ifmcModel.getMinimalConceptMap();
			System.out.println("Generated classification");
			HashMap<String, Set<String>> trainingSetMap = ifmcModel.getTrainingSet();
			HashMap<String, Set<String>> testSetMap = ifmcModel.getTestSet();

			List<Set<String>> classesSet = ifmcModel.getClasses();
			for (Set<String> clazz : classesSet)
				System.out.println("Number of classes:" + clazz.size() + " " + clazz + "");
			printClassedConceptProbs(minConceptMap, classesSet, trainingSetMap, testSetMap);

			// test
			classifyAndPrintProbs(newState, ifmcModel, TESTFILE, classesSet);
//		printConcepts(concepts1);

		}

		catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalObjectException e) {
			e.printStackTrace();
		}


	}

	private static void classifyAndPrintProbs(Conf newState, IFuzzyMultiClassifierContext ifmcModel, String TESTFILE,
			List<Set<String>> classesSet) {
		
		// test file
		IFCSVMultiClassReader ptdgsReader1 = null;
		HashMap<String, Set<String>> classifyMap  = null;
		tee.println(
				"\n-----------------------------------------------------------------------------------------------------------------------");
		tee.println(String.format("%8s%8s %10s %45s %10s %10s", "Train/", "Object", "Extent", "Probabilities",
				"Predicted", "Actual"));
		tee.println(
				String.format("%8s%8s %10s %45s %10s %10s", "Test ", "      ", " Size", classesSet, "Class", "Class"));
		tee.println(
				"-----------------------------------------------------------------------------------------------------------------------");

		int count = 0;
		tee.println("Reading " + TESTFILE);
		tee.println("Starting to classify test data");

		try {
			ptdgsReader1 = new IFCSVMultiClassReader(newState, ifmcModel, TESTFILE, 1, false, 0);
		
		
		IFuzzyMultiClassifierContext ifmcTest = ((IFuzzyMultiClassifierContext) newState.context);
		int i=0;
	 	String nextObj = "";
		while ((nextObj = ptdgsReader1.readReturnNext())!=null) {
			Set<Concept<String, FullObject<String, String>>> concepts1 = ifmcTest.getConcepts();
//		tee.println("No of Objects: " + ifmcTest.getObjectCount());
//		tee.println("No of attributes: " + ifmcTest.getAttributeCount());
//		tee.println("No of concepts: " + concepts1.size());
		HashMap<String,Concept<String, FullObject<String, String>>> minConcMap = ifmcTest.getMinimalConceptMap();
	//	System.out.println("Generated classification");
		Concept<String, FullObject<String, String>> concept = minConcMap.get(nextObj);
		FuzzyMultiClassedConcept fcc = (FuzzyMultiClassedConcept) concept;
		StringBuffer sb = new StringBuffer();

		// Convert class indices to string representation
		int j = 0;
		List<List<String>> csList = new ArrayList<List<String>>();
		for (List<Integer> ccList : fcc.getProbClass()) {
			List<String> cscList = new ArrayList<String>();
			for (Integer ic : ccList) {
				cscList.add(classesSet.get(j).toArray(new String[0])[ic]);
				j++;
			}
			csList.add(cscList);
		}

		List<List<String>> psList = new ArrayList<List<String>>();
		for (List<Double> ppList : fcc.getProbsList()) {
			List<String> pspList = new ArrayList<String>();
			for (Double d : ppList) {
				pspList.add(String.format("%2.2f", d));
			}
			psList.add(pspList);
		}

		classifyMap = ifmcTest.getTrainingSet();
		Set<String> ocls = classifyMap.get(nextObj);
		String type = "test";
		sb.append(String.format("%8s%8s %9s} %45s %10s %10s", type, nextObj, "{" + fcc.getExtent().size(), psList,
				csList, ocls));
		if (ocls.contains(csList.get(0).toArray(new String[0])[0])) {
//				sb.append(String.format("%8s %9s} %65s %10s %10s",nextObj,"{"+fcc.getExtent().size(),psList,csList,classSetMap.get(oid)));
//				if(classSetMap.get(oid).contains(csList.get(0).toArray(new String[0])[0])) {
//			
			count++;
			sb.append(" _/");
		}
	
		tee.println(sb.toString());
		ptdgsReader1.remove(nextObj);
		i++;
		}
		ptdgsReader1.close();
		tee.println("\nTest Objects classified correctly: " + count + "/" + i);
		tee.println("Accuracy: " + count * 100.0 / i + "%");

		} catch (IllegalObjectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} // last
	}

	public static void printConcepts(Set<Concept<String, FullObject<String, String>>> concepts) {
		// concept (A,B)
		// A is set of objects which is extent
		// B is set of attributes which is intent
		int i = 1;
		for (Concept<String, FullObject<String, String>> c : concepts) {
			StringBuffer sb = new StringBuffer();
			sb.append("<{");
			for (FullObject<String, String> o : c.getExtent())
				sb.append(o.getIdentifier() + ",");
			sb.append("},{");
			for (String attr : c.getIntent())
				sb.append(attr + ",");
			sb.append("}>");
			tee.println(i + ":" + sb + "[" + Long.toBinaryString(((LatticeConcept) c).getId()) + "]");
			i++;
		}
	}

	public static void printConcept(Concept<String, FullObject<String, String>> c) {
		// concept (A,B)
		// A is set of objects which is extent
		// B is set of attributes which is intent
		StringBuffer sb = new StringBuffer();
		sb.append("<{");
		for (FullObject<String, String> o : c.getExtent())
			sb.append(o.getIdentifier() + ",");// "[" + o.getDescription().getAttributes() + "]" + ",");
		sb.append("},{");
		for (String attr : c.getIntent())
			sb.append(attr + ",");
		sb.append("}>");
		tee.println(sb.toString());
	}

	public static void printClassedConcepts(Set<? extends Concept<String, FullObject<String, String>>> concepts,
			HashMap<String, Set<String>> classSetMap) {
		int i = 1;
		for (Concept<String, FullObject<String, String>> c : concepts) {
			FuzzyMultiClassedConcept fcc = (FuzzyMultiClassedConcept) c;
			StringBuffer sb = new StringBuffer();
			sb.append("<{");
			for (FullObject<String, String> o : fcc.getExtent())
				sb.append(o.getIdentifier() + ",");
			sb.append("},{");
			for (String attr : fcc.getIntent())
				sb.append(attr + ",");
			sb.append("},{");
			sb.append(fcc.getProbsList());
			sb.append("}>");
			tee.println(i + ":" + sb);
			i++;
		}
	}

	public static void printClassedConceptProbs(Set<? extends Concept<String, FullObject<String, String>>> concepts) {
		int i = 1;
		for (Concept<String, FullObject<String, String>> c : concepts) {
			FuzzyMultiClassedConcept fcc = (FuzzyMultiClassedConcept) c;
			StringBuffer sb = new StringBuffer();
			sb.append("<{");
			for (FullObject<String, String> o : fcc.getExtent())
				sb.append(o.getIdentifier() + ",");
			sb.append("},{");
//				for(String attr : fcc.getIntent())
//				sb.append(attr + ",");
//			sb.append("},{"); 
			sb.append(fcc.getProbsList());
			// sb.append("-->");
			// sb.append(fcc.getProbClass());
			sb.append("}>");
			tee.println(i + ":" + sb);
			i++;
		}
	}

	public static void printClassedConceptProbs(
			HashMap<String, Concept<String, FullObject<String, String>>> minConceptMap, List<Set<String>> classesSet,
			HashMap<String, Set<String>> trainingSetMap, HashMap<String, Set<String>> testSetMap) {
		tee.println(
				"\n-----------------------------------------------------------------------------------------------------------------------");
		tee.println(String.format("%8s%8s %10s %45s %10s %10s", "Train/", "Object", "Extent", "Probabilities",
				"Predicted", "Actual"));
		tee.println(
				String.format("%8s%8s %10s %45s %10s %10s", "Test ", "      ", " Size", classesSet, "Class", "Class"));
		tee.println(
				"-----------------------------------------------------------------------------------------------------------------------");

		int count = 0, trcount = 0, tecount = 0;
		for (String oid : minConceptMap.keySet()) {
			Concept<String, FullObject<String, String>> concept = minConceptMap.get(oid);
			FuzzyMultiClassedConcept fcc = (FuzzyMultiClassedConcept) concept;
			StringBuffer sb = new StringBuffer();

			// Convert class indices to string representation
			int i = 0;
			List<List<String>> csList = new ArrayList<List<String>>();
			for (List<Integer> ccList : fcc.getProbClass()) {
				List<String> cscList = new ArrayList<String>();
				for (Integer ic : ccList) {
					cscList.add(classesSet.get(i).toArray(new String[0])[ic]);
					i++;
				}
				csList.add(cscList);
			}

			List<List<String>> psList = new ArrayList<List<String>>();
			for (List<Double> ppList : fcc.getProbsList()) {
				List<String> pspList = new ArrayList<String>();
				for (Double d : ppList) {
					pspList.add(String.format("%2.2f", d));
				}
				psList.add(pspList);
			}

			Set<String> ocls = null;
			String type = "";
			if (trainingSetMap.containsKey(oid)) {
				ocls = trainingSetMap.get(oid);
				type = "train";
			} else {
				ocls = testSetMap.get(oid);
				type = "test";
			}
			sb.append(String.format("%8s%8s %9s} %45s %10s %10s", type, oid, "{" + fcc.getExtent().size(), psList,
					csList, ocls));
			if (ocls.contains(csList.get(0).toArray(new String[0])[0])) {
//					sb.append(String.format("%8s %9s} %65s %10s %10s",oid,"{"+fcc.getExtent().size(),psList,csList,classSetMap.get(oid)));
//					if(classSetMap.get(oid).contains(csList.get(0).toArray(new String[0])[0])) {
//				
				count++;
				if (type.equals("train"))
					trcount++;
				if (type.equals("test"))
					tecount++;
				sb.append(" _/");
			}
			tee.println(sb.toString());

		}
		tee.println("\nObjects classified correctly: " + count);
		tee.println("Total objects: " + minConceptMap.keySet().size());
		tee.println("Accuracy: " + count * 100.0 / minConceptMap.keySet().size() + "%");

		tee.println("\nTraining Objects classified correctly: " + trcount + "/" + trainingSetMap.keySet().size());
		tee.println("Accuracy: " + trcount * 100.0 / trainingSetMap.keySet().size() + "%");

//		tee.println("\nTest Objects classified correctly: " + tecount + "/" + testSetMap.keySet().size());
//		tee.println("Accuracy: " + tecount * 100.0 / testSetMap.keySet().size() + "%");

	}

}
