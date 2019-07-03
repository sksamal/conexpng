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

public class FuzzyClassifyTest1 {

	private static TeeWriter tee = null;
	public static void main(String[] args) {
		
		// Version info 
		System.out.println("FuzzyClassifyTest v14");
		try {
			Scanner sc = new Scanner(new File (".build.txt"));
			while(sc.hasNextLine()) {
				System.out.println(sc.nextLine());
			}
			sc.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
			
		String INPUTFILE = "data/exp108_coding_1.fccsv";
			
		if(args.length >=1) INPUTFILE = args[0];
	//	if(args.length >=2) imageLocation = args[1];
		double testPercentage = 0.20;
		if(args.length>=2) testPercentage = Double.parseDouble(args[1]);
		int initial = 10;
		
		System.setProperty("user.language", LocaleHandler.readLocale());
	    PrintStream pwStream = null;
	    IFuzzyMultiClassifierContext ifmc1 = null;

		Conf newState = new Conf();
		try {
			
		// Logging using tee
		pwStream = new PrintStream(INPUTFILE + ".log");
		tee = new TeeWriter(pwStream, System.out);
		
		newState.filePath = "";
		tee.println("Reading " + INPUTFILE);

		// count the number of records
		IFCSVMultiClassReader ptdgsReader = new IFCSVMultiClassReader(newState, INPUTFILE,1,false,initial); // last 1 are classes, uniqueness
		int i=0;
		while(ptdgsReader.readNext()) {
			i++;
		}
		ptdgsReader.close();

		
		// direct reading of records from the file
		Conf newState1 = new Conf();
			newState1.filePath = "";
			IFCSVMultiClassReader ptdgsReader1 = new IFCSVMultiClassReader(newState1, INPUTFILE,1,false,initial+i); // last 1 are classes, uniqueness
			System.out.println("\n**Reading " + (initial + i) + " records directly");
			ifmc1 = ((IFuzzyMultiClassifierContext)newState1.context);
			Set<Concept<String,FullObject<String,String>>> concepts1 = ifmc1.getConcepts();
			ptdgsReader1.close();

		tee.println("No of Objects: "+ ifmc1.getObjectCount());
		tee.println("No of attributes: " + ifmc1.getAttributeCount());
		tee.println("No of concepts: " + concepts1.size());

//		printConcepts(concepts1);

		}
		
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (IllegalObjectException e) {
			e.printStackTrace();
		}
		
// Get concepts for each object
//		int i=0;
//		for(FullObject<String,String> o :newState.context.getObjects()) {
//			System.out.println("Concepts containing " + o.getIdentifier() + " are:");
//			printClassedConceptProbs(((FuzzyMultiClassifierContext)(newState.context)).getConceptsOfObject(o));
//			i++;
//		} 

		// Classify all objects using k-partition
//		((FuzzyMultiClassifierContext)(newState.context)).kpartition(10);;
		ifmc1.partition(testPercentage,1);
		
		// Classify all objects
		System.out.println("Starting to classify now");
//		long ms = System.currentTimeMillis();
//		classifyAndPrint(ifmc1);
		HashMap<String,Concept<String,FullObject<String, String>>> minConceptMap = ifmc1.getMinimalConceptMap();
		System.out.println("Generated classification");
		HashMap<String,Set<String>> trainingSetMap = ifmc1.getTrainingSet();
		HashMap<String,Set<String>> testSetMap = ifmc1.getTestSet();
		
		List<Set<String>> classesSet = ifmc1.getClasses();
		for (Set<String> clazz : classesSet)
		System.out.println("Number of classes:" + clazz.size() + " " + clazz + "");
		printClassedConceptProbs(minConceptMap,classesSet,trainingSetMap,testSetMap);

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
			tee.println(i + ":" + sb + "[" + Long.toBinaryString(((LatticeConcept)c).getId()) + "]");
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
	
	    public static void classifyAndPrint(IFuzzyMultiClassifierContext ifmc) {
	    	HashMap<String,Concept<String,FullObject<String, String>>> minConceptMap = ifmc.getMinimalConceptMap();
			System.out.println("Generated classification");
			HashMap<String,Set<String>> classSetMap = ifmc.getTrainingSet();
			List<Set<String>> classesSet = ifmc.getClasses();
			for (Set<String> clazz : classesSet)
			System.out.println(clazz.size() + ":" + clazz);
			
			tee.println("\n-----------------------------------------------------------------------------------------------------------------------");
			tee.println(String.format("%6s %10s %45s %50s%10s","Object","Extent","Probabilities","Predicted","Actual"));
			tee.println(String.format("%6s %10s %65s %30s %10s","      "," Size",classesSet,"Class","Class"));
			tee.println("-----------------------------------------------------------------------------------------------------------------------");
			
			int count=0;
			for(FullObject<String, String> obj: ifmc.getObjects()) {
				String oid = obj.getIdentifier();
			//for(String oid: minConceptMap.keySet()) {
				if(minConceptMap.containsKey(oid)) {
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
				
				sb.append(String.format("%6s %9s} %65s %10s %10s",oid,"{"+fcc.getExtent().size(),psList,csList,classSetMap.get(oid)));
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
		}
			 tee.println("\nObjects classified correctly: " + count);
			 tee.println("Total objects: "+ minConceptMap.keySet().size());
			 tee.println("Success: " + count*100.0/minConceptMap.keySet().size() + "%");
			

		
	    }
		public static void printClassedConceptProbs(HashMap<String,Concept<String,FullObject<String, String>>> minConceptMap,
				List<Set<String>> classesSet, HashMap<String,Set<String>> trainingSetMap, HashMap<String,Set<String>> testSetMap) 
		{
			tee.println("\n-----------------------------------------------------------------------------------------------------------------------");
			tee.println(String.format("%8s%8s %10s %45s %10s %10s","Train/","Object","Extent","Probabilities","Predicted","Actual"));
			tee.println(String.format("%8s%8s %10s %45s %10s %10s","Test ","      "," Size",classesSet,"Class","Class"));
			tee.println("-----------------------------------------------------------------------------------------------------------------------");
			
			int count=0, trcount=0, tecount=0; 
			int trainTrueClass1=0, trainTrueClass0=0, testTrueClass1=0, testTrueClass0=0;
			int trainFalseClass1=0, trainFalseClass0=0, testFalseClass1=0, testFalseClass0=0;
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
				
				Set<String> ocls = null;
				String type = "";
				if(trainingSetMap.containsKey(oid)){ ocls = trainingSetMap.get(oid); type ="train";}
				else { ocls = testSetMap.get(oid); type="test"; }
				sb.append(String.format("%8s%8s %9s} %45s %10s %10s",type,oid,"{"+fcc.getExtent().size(),psList,csList,ocls));
				String p = (String) csList.get(0).toArray()[0];
				Double pd = Double.parseDouble(p);
				int pdi = (int) Math.round(pd);
				//System.out.println("predicted class: " + pdi);
				//System.out.println(type + "," + oid + "," + ocls);
				
				String a = ocls.toArray()[0].toString();
				Double ad = Double.parseDouble(a);
				int adi = (int)Math.round(ad);
				//System.out.println("actual class: " + adi);
				int predictedClass = pdi;
				int actualClass = adi;
				if(ocls.contains(csList.get(0).toArray(new String[0])[0])) {
//					sb.append(String.format("%8s %9s} %65s %10s %10s",oid,"{"+fcc.getExtent().size(),psList,csList,classSetMap.get(oid)));
//					if(classSetMap.get(oid).contains(csList.get(0).toArray(new String[0])[0])) {
//				
					 count++;
					 if(type.equals("train")) {
						 if(actualClass == 0 && predictedClass == 0) {
							 trainTrueClass0++; //true neg
							 //sb.append("Predicted class = 0, actual class: 0");
						 }else if (actualClass == 1 && predictedClass == 1){
							 trainTrueClass1++; //true pos
							 //sb.append("Predicted class = 1, actual class: 1");
						 }else{
							 sb.append("error");
						 }
						 trcount++;
					 }
					 if(type.equals("test")) {
						 tecount++;
						 if(actualClass == 0 && predictedClass == 0) {
							 testTrueClass0++; //true neg
							 //sb.append("Predicted class = 0, actual class: 0");
						 }else if (actualClass == 1 && predictedClass == 1){
							 testTrueClass1++; //true pos
							 //sb.append("Predicted class = 1, actual class: 1");
						 }else{
							 sb.append("error");
						 }
					 }
					 sb.append(" _/");
				}else { //mis-classified cases
					if(type.equals("train")) {
						 if(actualClass == 0 && predictedClass == 1) {
							 trainFalseClass0++; //false pos
							 //sb.append("Predicted class = 0, actual class: 0");
						 }else if (actualClass == 1 && predictedClass == 0){
							 trainFalseClass1++; //false neg
							 //sb.append("Predicted class = 1, actual class: 1");
						 }else{
							 sb.append("error");
						 }
					 }
					 if(type.equals("test")) {
						 if(actualClass == 0 && predictedClass == 1) {
							 testFalseClass0++; //false pos
							 //sb.append("Predicted class = 0, actual class: 0");
						 }else if (actualClass == 1 && predictedClass == 0){
							 testFalseClass1++; //false neg
							 //sb.append("Predicted class = 1, actual class: 1");
						 }else{
							 sb.append("error");
						 }
					 }
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
			 tee.println("Accuracy: " + count*100.0/minConceptMap.keySet().size() + "%");
			 
			 tee.println("\nTraining Objects classified correctly: " + trcount + "/" + trainingSetMap.keySet().size());
			 tee.println("Accuracy: " + trcount*100.0/trainingSetMap.keySet().size() + "%");
			 tee.println("True Positive (class1->1) = " + trainTrueClass1);
			 tee.println("True Negative (class0->0) = " + trainTrueClass0);
			 tee.println("False Positive (class0->1) = " + trainFalseClass0);
			 tee.println("False Negative (class1->0) = " + trainFalseClass1);
			 double trSensitivity = (double)trainTrueClass1 / (trainTrueClass1+trainFalseClass1);
			 double trSpecificity = (double)trainTrueClass0 / (trainTrueClass0+trainFalseClass0);
			 tee.println("Sensitivity (true positive rate) = " + trSensitivity);
			 tee.println("Specificity (true negative rate) = " + trSpecificity);
			 int trtotal = trainTrueClass1+trainTrueClass0+trainFalseClass0+trainFalseClass1;
			 if(trtotal != trainingSetMap.keySet().size()) {
				 System.err.println("ERROR in training statistics");
			 }
			 
			 tee.println("\nTest Objects classified correctly: " + tecount + "/" + testSetMap.keySet().size());
			 tee.println("Accuracy: " + tecount*100.0/testSetMap.keySet().size() + "%");
			 tee.println("True Positive (class1->1) = " + testTrueClass1);
			 tee.println("True Negative (class0->0) = " + testTrueClass0);
			 tee.println("False Positive (class0->1) = " + testFalseClass0);
			 tee.println("False Negative (class1->0) = " + testFalseClass1);
			 double teSensitivity = (double)testTrueClass1 / (testTrueClass1+testFalseClass1);
			 double teSpecificity = (double)testTrueClass0 / (testTrueClass0+testFalseClass0);			 
			 tee.println("Sensitivity (true positive rate) = " + teSensitivity);
			 tee.println("Specificity (true negative rate) = " + teSpecificity);
			 int tetotal = testTrueClass1+testTrueClass0+testFalseClass0+testFalseClass1;
			 if(tetotal != testSetMap.keySet().size()) {
				 System.err.println("ERROR in testing statistics");
			 }
		}
	
}
