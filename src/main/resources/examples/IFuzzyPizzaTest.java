package examples;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.gui.workers.TAssociationWorker;
import fcatools.conexpng.io.FCSVMultiClassReader;
import fcatools.conexpng.io.locale.LocaleHandler;
import fcatools.conexpng.model.FuzzyMultiClassedConcept;
import fcatools.conexpng.model.FuzzyMultiClassifierContext;
import fcatools.conexpng.model.IFuzzyMultiClassifierContext;

public class IFuzzyPizzaTest {

	private static TeeWriter tee = null;
	public static void main(String[] args) {
			
		String INPUTFILE = "/home/ssamal/java_projs/conexpng/recipe_parser/ingsdata.fccsv";
//		String imageLocation = "/home/ssamal/dl/out";
		if(args.length >=1) INPUTFILE = args[0];
//		if(args.length >=2) imageLocation = args[1];
		
	    System.setProperty("user.language", LocaleHandler.readLocale());
	    PrintStream pwStream = null;

		Conf newState = new Conf();
		try {
		
		pwStream = new PrintStream(INPUTFILE + ".log");
		HTMLWriter htmlStream = new HTMLWriter(INPUTFILE + ".html");
//		htmlStream.setImageLocation(imageLocation + "/training");
		tee = new TeeWriter(pwStream, htmlStream);
		newState.filePath = "";
		tee.println("Reading " + INPUTFILE);
		long currentms = System.currentTimeMillis();
		FCSVMultiClassReader ptdgsReader = new FCSVMultiClassReader(newState, INPUTFILE,1,true,30); // last 1 are classes, uniqueness
		IFuzzyMultiClassifierContext ifmc = new IFuzzyMultiClassifierContext((FuzzyMultiClassifierContext)newState.context);
		tee.println("\n\n***Reading first 30 records***");
		tee.println("No of Objects:"+ newState.context.getObjectCount());
		tee.println("No of attributes:" + newState.context.getAttributeCount());
		Set<Concept<String,FullObject<String,String>>> concepts = newState.context.getConcepts();
		tee.println("No of concepts:" + concepts.size());
		//	printClassedConceptProbs(concepts);
	
		
		// Incrementally add objects
		tee.println("\n\n***Incrementally adding new objects ***");
		int i=0;
		while(ptdgsReader.readNext()) {
			i++;
			if(i%1000==0) {
				tee.println("	Read " + i + " records");
			concepts = newState.context.getConcepts();
			}
//			if(i==1000) break;
		}
		tee.println("	Completed reading " + i + " records");
		tee.println("No of Objects: "+ newState.context.getObjectCount());
		tee.println("No of attributes: " + newState.context.getAttributeCount());
		concepts = newState.context.getConcepts();
		tee.println("No of concepts: " + concepts.size());
		tee.println("Read time: " + (System.currentTimeMillis()-currentms) + "ms");
	//	printClassedConcepts(concepts);
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
		HashMap<String,Concept<String,FullObject<String, String>>> minConceptMap = ((FuzzyMultiClassifierContext)(newState.context)).getMinimalConceptMap();
		HashMap<String,Set<String>> classSetMap = ((FuzzyMultiClassifierContext)(newState.context)).getTrainingSet();
		List<Set<String>> classesSet = ((FuzzyMultiClassifierContext)(newState.context)).getClasses();
//		for (Set<String> clazz : classesSet)
//			System.out.println(clazz.size() + ":" + clazz);
		printClassedConceptProbs(minConceptMap,classesSet,classSetMap);
	}

	public static void printConcepts(Set<Concept<String, FullObject<String, String>>> concepts) {
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
			tee.println(String.format("%8s %10s %65s %30s %10s","         "," Size","[0  , 1   , 2   , 3   , 4   , 5   , 6   , 7   , 8   , 9   ]","Class","Class"));
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
						pspList.add(String.format("%.2f",d));
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
