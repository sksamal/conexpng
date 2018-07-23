package examples;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.gui.workers.TAssociationWorker;
import fcatools.conexpng.io.FCSVMultiClassReader;
import fcatools.conexpng.io.IFCSVMultiClassReader;
import fcatools.conexpng.io.locale.LocaleHandler;
import fcatools.conexpng.model.FuzzyFormalContext;
import fcatools.conexpng.model.FuzzyMultiClassedConcept;
import fcatools.conexpng.model.FuzzyMultiClassifierContext;
import fcatools.conexpng.model.IFuzzyMultiClassifierContext;

public class IFuzzyTest {

	private static TeeWriter tee = null;
	public static void main(String[] args) {
		
		System.out.println("IfuzzyTest v13");
			
	//	String INPUTFILE = "/home/ssamal/workspace/conexpng/pizza_onto_context.fccsv";
	//	String INPUTFILE = "/home/ssamal/workspace/conexpng/staralliance.fccsv";
		String INPUTFILE = "/home/ssamal/java_projs/conexpng/codings_1.fccsv";
	//	String INPUTFILE = "/home/ssamal/java_projs/conexpng/recipe_parser/ingsdata.fccsv";
		String imageLocation = "/home/ssamal/dl/out";
		if(args.length >=1) INPUTFILE = args[0];
	//	if(args.length >=2) imageLocation = args[1];
		int initial = 100;
		if(args.length>=2) initial = Integer.parseInt(args[1]);
		int expr = 10;
		if(args.length>=2) expr = Integer.parseInt(args[2]);
		
		Conf state = new Conf();
		state.filePath = "/home/ssamal/data-analysis-tools/data/colcan";
	    System.setProperty("user.language", LocaleHandler.readLocale());
	    PrintStream pwStream = null;
	    IFuzzyMultiClassifierContext ifmc = null;
	    IFuzzyMultiClassifierContext ifmc1 = null;

		Conf newState = new Conf();
		try {
			
		// Logging using tee
		pwStream = new PrintStream(INPUTFILE + ".log");
		HTMLWriter htmlStream = new HTMLWriter(INPUTFILE + ".html");
		htmlStream.setImageLocation(imageLocation + "/training");
		tee = new TeeWriter(pwStream, System.out);
		
		newState.filePath = "";
		tee.println("Reading " + INPUTFILE);
		long currentms = System.currentTimeMillis();
		IFCSVMultiClassReader ptdgsReader = new IFCSVMultiClassReader(newState, INPUTFILE,1,false,initial); // last 1 are classes, uniqueness
		ifmc = ((IFuzzyMultiClassifierContext)newState.context);
		tee.println("\n\n***Reading first 10 records***");
		tee.println("No of Objects:"+ ifmc.getObjectCount());
		tee.println("No of attributes:" + ifmc.getAttributeCount());
		Set<Concept<String,FullObject<String,String>>> concepts = ifmc.getConcepts();
		tee.println("No of concepts:" + concepts.size());
//		printConcepts(concepts);
//		printClassedConceptProbs(concepts);
//		System.exit(1);
	
//		for(FullObject o : ifmc.getObjects())
//			System.out.println(o.toString());
		// Incrementally add objects
		int i=0;
		long ms = System.currentTimeMillis();
//		if(ptdgsReader.readNext()) {
			
		while(ptdgsReader.readNext()) {
			i++;
			if(i%1000 == 0) {
				tee.println("	Read " + i + " records");
				long ms1 = System.currentTimeMillis();
				tee.println("1000 records took " + (ms1 - ms) + " ms");
				concepts = ifmc.getConcepts();
				ms = System.currentTimeMillis();
				tee.println("Generating concepts took " + (ms - ms1) + " ms");
				tee.println("Concepts now: " + concepts.size());
				tee.println("Objects now:" + ifmc.getObjectCount());
			}

			if(i>1000 && i%expr == 0) {
				tee.println("	Read " + i + " records");
				long ms1 = System.currentTimeMillis();
				tee.println("10 records took " + (ms1 - ms) + " ms");
				concepts = ifmc.getConcepts();
				ms = System.currentTimeMillis();
				tee.println("Generating concepts took " + (ms - ms1) + " ms");
				tee.println("Concepts now: " + concepts.size());
				tee.println("Objects now:" + ifmc.getObjectCount());
				
				Conf newState1 = new Conf();
				newState1.filePath = "";
				IFCSVMultiClassReader ptdgsReader1 = new IFCSVMultiClassReader(newState1, INPUTFILE,0,false,11+i); // last 1 are classes, uniqueness
				ifmc1 = ((IFuzzyMultiClassifierContext)newState1.context);
				tee.println("No of Objects: "+ ifmc.getObjectCount() + " (Correct:" + ifmc1.getObjectCount() + ")");
				tee.println("No of attributes: " + ifmc.getAttributeCount() + " (Correct:" + ifmc1.getAttributeCount() + ")");
				concepts = ifmc.getConcepts();
				Set<Concept<String,FullObject<String,String>>> concepts1 = ifmc1.getConcepts();
				tee.println("No of concepts: " + concepts.size() + " (Correct:" + concepts1.size() + ")");
				tee.println("Both concepts are identical?:" +areIdentical(concepts,concepts1));
				ptdgsReader1.close();

			}
//			Conf newState1 = new Conf();
//			newState1.filePath = "";
//			IFCSVMultiClassReader ptdgsReader1 = new IFCSVMultiClassReader(newState1, INPUTFILE,0,false,11+i); // last 1 are classes, uniqueness
//			ifmc1 = ((IFuzzyMultiClassifierContext)newState1.context);
//			tee.println("No of Objects: "+ ifmc.getObjectCount() + " (Correct:" + ifmc1.getObjectCount() + ")");
//			tee.println("No of attributes: " + ifmc.getAttributeCount() + " (Correct:" + ifmc1.getAttributeCount() + ")");
//			concepts = ifmc.getConcepts();
//			Set<Concept<String,FullObject<String,String>>> concepts1 = ifmc1.getConcepts();
//			tee.println("No of concepts: " + concepts.size() + " (Correct:" + concepts1.size() + ")");
//			tee.println("Both concepts are identical?:" +areIdentical(concepts,concepts1));
//			ptdgsReader1.close();
//			if(i==2) { 
//				printConcepts(concepts);
//				printConcepts(concepts1);
//				break;			
//			}
		}
		
//		printConcepts(concepts);
//		printConcepts(concepts1);
		ifmc.toXml(INPUTFILE+"_" + expr + "_context.txt");
		tee.println("	Completed reading " + i + " records");
		tee.println("No of Objects: "+ ifmc.getObjectCount());
		tee.println("No of attributes: " + ifmc.getAttributeCount());
		concepts = ifmc.getConcepts();
		tee.println("No of concepts: " + concepts.size());
		tee.println("Read time: " + (System.currentTimeMillis()-currentms) + "ms");
//	//	printClassedConcepts(concepts);
//	
		ptdgsReader.close();
	
//		printConcepts(concepts);
//		TAssociationWorker taworker = new TAssociationWorker(newState,0.5,0.5,(long) 0);
//		taworker.run();
//		taworker.print();


		}
		
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (IllegalObjectException e) {
			e.printStackTrace();
		}
		
		// Get concepts for each object
//		int i=0;
//		for(FullObject<String,String> o :state.context.getObjects()) {
//			System.out.println("Concepts containing " + o.getIdentifier() + " are:");
//			printClassedConceptProbs(((FuzzyMultiClassifierContext)(state.context)).getConceptsOfObject(o));
//			i++;
//		} 
		// Classify all objects
	//	((FuzzyMultiClassifierContext)(newState.context)).kpartition(10);;
			
		// Classify all objects
		HashMap<String,Concept<String,FullObject<String, String>>> minConceptMap = ifmc.getMinimalConceptMap();
		HashMap<String,Set<String>> classSetMap = ifmc.getTrainingSet();
		List<Set<String>> classesSet = ifmc.getClasses();
//		for (Set<String> clazz : classesSet)
//			System.out.println(clazz.size() + ":" + clazz);
//		printClassedConceptProbs(minConceptMap,classesSet,classSetMap);
	}

	public static boolean areIdentical(Set<Concept<String, FullObject<String, String>>> concepts, Set<Concept<String, FullObject<String, String>>> concepts1) {
		
		if(concepts.size()!=concepts1.size()) return false;
		
		for(Concept<String, FullObject<String, String>> c : concepts) {
			boolean found = false;
//			System.out.println("Looking for concept "); printConcept(c);
			for(Concept<String, FullObject<String, String>> c1 : concepts1) {
	//			System.out.println("\tMatching with concept"); printConcept(c1);
				if(areEqual(c,c1))
				{ found = true; break; }
			}
			if(!found) {
//			System.out.println("\tnot found:");
			 return false; }
//			System.out.println("\tfound:");

		}
		return true;
	}
	
	public static boolean areEqual(Concept<String, FullObject<String, String>> c, Concept<String, FullObject<String, String>> c1) {
		if(c.getIntent().size() != c1.getIntent().size()) {
	//		System.out.println("Intent size doesnot match");
			return false;
		}
			if(c.getExtent().size() != c1.getExtent().size()) {
//				System.out.println("Extent size doesnot match");
			return false;
			}
//			System.out.println("Comparing with");printConcept(c1);
			
			if(c.getIntent().containsAll(c1.getIntent()) && c1.getIntent().containsAll(c.getIntent())) {
			//System.out.println("Same intents");
				}
			else
			{ //  System.out.println("intents not same");
			return false;
			}


		for(FullObject<String, String> obj : c.getExtent()) {
			boolean match = false;
			for(FullObject<String, String> obj1 : c1.getExtent()) {
				if(obj.getIdentifier().equals(obj1.getIdentifier())) 
						if(obj.getDescription().getAttributes().containsAll(obj1.getDescription().getAttributes()))
							if(obj1.getDescription().getAttributes().containsAll(obj.getDescription().getAttributes()))
								match = true;
			}
			if(!match) { //System.out.print( " but not same extents\n");
			return false; }
		}
		
//		System.out.print(" and same extents\n");
		return true;
	}
	
	public static void printConcepts(Set<Concept<String, FullObject<String, String>>> concepts) {
		// concept (A,B)
		// A is set of objects which is extent
		// B is set of attributes which is intent
		int i=1;
		for(Concept<String, FullObject<String, String>> c : concepts) {
			StringBuffer sb = new StringBuffer();
			sb.append("<{");
			for(FullObject<String, String> o : c.getExtent()) 
				sb.append(o.getIdentifier() + ",");
			sb.append("},{");
			for(String attr : c.getIntent())
				sb.append(attr + ",");
			sb.append("}>");
			tee.println(i + ":" + sb);
			i++;
	}
	}
	
	public static void printConcept(Concept<String, FullObject<String, String>> c) {
		// concept (A,B)
		// A is set of objects which is extent
		// B is set of attributes which is intent
			StringBuffer sb = new StringBuffer();
			sb.append("<{");
			for(FullObject<String, String> o : c.getExtent()) 
				sb.append(o.getIdentifier() + ",");//  "[" + o.getDescription().getAttributes() + "]" + ",");
			sb.append("},{");
			for(String attr : c.getIntent())
				sb.append(attr + ",");
			sb.append("}>");
			tee.println(sb.toString());
	}
		
	public static void printClassedConcepts(Set<? extends Concept<String, FullObject<String, String>>> concepts,HashMap<String,Set<String>> classSetMap) {
			int i=1;
			for(Concept<String, FullObject<String, String>> c : concepts) {
				FuzzyMultiClassedConcept fcc = (FuzzyMultiClassedConcept) c;
				StringBuffer sb = new StringBuffer();
				sb.append("<{");
				for(FullObject<String, String> o : fcc.getExtent()) 
					sb.append(o.getIdentifier() + ",");
				sb.append("},{");
				for(String attr : fcc.getIntent())
					sb.append(attr + ",");
				sb.append("},{");
				sb.append(fcc.getProbsList());
				sb.append("}>");
				tee.println(i + ":" + sb);
				i++;
		}
}
	
	public static void printClassedConceptProbs(Set<? extends Concept<String, FullObject<String, String>>> concepts) {
		int i=1;
		for(Concept<String, FullObject<String, String>> c : concepts) {
			FuzzyMultiClassedConcept fcc = (FuzzyMultiClassedConcept) c;
			StringBuffer sb = new StringBuffer();
			sb.append("<{");
			for(FullObject<String, String> o : fcc.getExtent()) 
				sb.append(o.getIdentifier() + ",");
			sb.append("},{");
//				for(String attr : fcc.getIntent())
//				sb.append(attr + ",");
//			sb.append("},{"); 
			sb.append(fcc.getProbsList());
	//		sb.append("-->");
	//		sb.append(fcc.getProbClass());
			sb.append("}>");
			tee.println(i + ":" + sb);
			i++;
	}	
	}	
		public static void printClassedConceptProbs(HashMap<String,Concept<String,FullObject<String, String>>> minConceptMap,
				List<Set<String>> classesSet, HashMap<String,Set<String>> classSetMap) 
		{
			tee.println("\n-----------------------------------------------------------------------------------------------------------------------");
			tee.println(String.format("%8s %10s %45s %50s%10s","Object","Extent","Probabilities","Predicted","Actual"));
			tee.println(String.format("%8s %10s %65s %30s %10s","         "," Size",classesSet,"Class","Class"));
			tee.println("-----------------------------------------------------------------------------------------------------------------------");
			
			int count=0;
			for(String oid: minConceptMap.keySet()) {
				Concept<String,FullObject<String, String>> concept = minConceptMap.get(oid);
				FuzzyMultiClassedConcept fcc = (FuzzyMultiClassedConcept) concept;
				StringBuffer sb = new StringBuffer();
	
				// Convert class indices to string representation
				int i=0;
				List<List<String>> csList = new ArrayList<List<String>>();
				for(List<Integer> ccList : fcc.getProbClass()) {
					List<String> cscList = new ArrayList<String>();
					for(Integer ic : ccList) {
						cscList.add(classesSet.get(i).toArray(new String[0])[ic]);
					i++;
					}
					csList.add(cscList);
				}	
				
				List<List<String>> psList = new ArrayList<List<String>>();
				for(List<Double> ppList : fcc.getProbsList()) {
					List<String> pspList = new ArrayList<String>();
					for(Double d : ppList) {
						pspList.add(String.format("%2.2f",d));
					}
					psList.add(pspList);
				}
				
				sb.append(String.format("%8s %9s} %65s %10s %10s",oid,"{"+fcc.getExtent().size(),psList,csList,classSetMap.get(oid)));
				if(classSetMap.get(oid).contains(csList.get(0).toArray(new String[0])[0])) {
					 count++;
					 sb.append(" _/");
				}
		//s		sb.append("\t mnistImage=o" + (int)(Double.parseDouble(oid.substring(3,oid.length()))) + ".png");
				
			//	sb.append("\t mnistImage="+ (int)(Double.parseDouble(classSetMap.get(oid).toArray(new String[0])[0])) + "/o" + (int)(Double.parseDouble(oid)) + ".png");
				//		sb.append("<{");
				//for(FullObject<String, String> o : fcc.getExtent()) 
				//	sb.append(o.getIdentifier() + ",");
			//	sb.append(fcc.getExtent().size());
			//	sb.append("},{");
				/*	for(String attr : fcc.getIntent())
					sb.append(attr + ",");
				sb.append("},{"); */
//				sb.append(fcc.getProbsList());
//				sb.append("-->");
//				sb.append(fcc.getProbClass());
//				sb.append("}>");
				tee.println(sb.toString());

		}
			 tee.println("\nObjects classified correctly: " + count);
			 tee.println("Total objects: "+ minConceptMap.keySet().size());
			 tee.println("Success: " + count*100.0/minConceptMap.keySet().size() + "%");
			
		}
	
}

class TeeWriter {
	
	private PrintStream pstr1, pstr2;
	
	public TeeWriter(PrintStream pstr1, PrintStream pstr2) {
		this.pstr1 = pstr1;
		this.pstr2 = pstr2;
	}
	public void println(String s) {
		if(pstr1!=null)
			pstr1.println(s);
		if(pstr2!=null)
		pstr2.println(s);
}
}
