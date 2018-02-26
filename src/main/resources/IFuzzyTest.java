import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.Conf.StatusMessage;
import fcatools.conexpng.io.FCSVMultiClassReader;
import fcatools.conexpng.io.locale.LocaleHandler;
import fcatools.conexpng.model.FuzzyMultiClassedConcept;
import fcatools.conexpng.model.FuzzyMultiClassifierContext;
import fcatools.conexpng.model.IFuzzyMultiClassifierContext;

public class IFuzzyTest {

	public static void main(String[] args) {
		
		final String INPUTFILE = "/home/ssamal/Downloads/data-analysis-tools/data/colcan/codings.fccsv";
		Conf state = new Conf();
		state.filePath = "/home/ssamal/Downloads/data-analysis-tools/data/colcan";
	    System.setProperty("user.language", LocaleHandler.readLocale());
	       
		try {
			
		
		FCSVMultiClassReader ptdgsReader = new FCSVMultiClassReader(state, INPUTFILE,1); // last 2 are classes
		state.startCalculation(StatusMessage.LOADINGFILE);
		System.out.println("***Reading entire file***");
		System.out.println("No of Objects:"+ state.context.getObjectCount());
		System.out.println("No of attributes:" + state.context.getAttributeCount());
		long millis = System.currentTimeMillis();
		Set<Concept<String,FullObject<String,String>>> concepts = state.context.getConcepts();
//		System.out.println(state.context.getConcepts());
		System.out.println("Directly generating concepts took " + (System.currentTimeMillis() - millis) + "ms");
		System.out.println("No of concepts:" + concepts.size());
		System.out.println("No of classSets:" + ((FuzzyMultiClassifierContext)(state.context)).getClassesCount());
		System.out.println("Classes:" + ((FuzzyMultiClassifierContext)(state.context)).getClassesAsString());
//		printClassedConceptProbs(concepts);

		ptdgsReader.close();

//		printConcepts(concepts);
	
//		Conf newState = new Conf();
//		newState.filePath = "";
//		ptdgsReader = new FCSVMultiClassReader(newState, INPUTFILE,1,10); // last 1 are classes
//		IFuzzyMultiClassifierContext ifmc = new IFuzzyMultiClassifierContext((FuzzyMultiClassifierContext)newState.context);
//		System.out.println("\n\n***Reading first 10 records***");
//		System.out.println("No of Objects:"+ newState.context.getObjectCount());
//		System.out.println("No of attributes:" + newState.context.getAttributeCount());
//		concepts = newState.context.getConcepts();
//		System.out.println("No of concepts:" + concepts.size());
////		printClassedConceptProbs(concepts);
	
//		// Incrementally add objects
//		int i=0;
//		while(ptdgsReader.readNext()) {
//			i++;
//		}
//		System.out.println("	Read next " + i + " records");
//		System.out.println("No of Objects:"+ newState.context.getObjectCount());
//		System.out.println("No of attributes:" + newState.context.getAttributeCount());
//		concepts = newState.context.getConcepts();
//		System.out.println("No of concepts:" + concepts.size());
////		printClassedConcepts(concepts);
//	
//		ptdgsReader.close();
//		
//				
		}
		
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (IllegalObjectException e) {
			e.printStackTrace();
		}
		
		// Get concepts for each object
		int i=0;
		for(FullObject<String,String> o :state.context.getObjects()) {
			System.out.println("Concepts containing " + o.getIdentifier() + " are:");
			printClassedConceptProbs(((FuzzyMultiClassifierContext)(state.context)).getConceptsOfObject(o));
			if(i==5)
			break;
			i++;
		} 
		
		// Classify all objects
//		HashMap<String,Set<String>> classifiedMap = ((FuzzyMultiClassifierContext)(state.context)).classify();
	//	for(String o: classifiedMap.keySet())
		//	System.out.println(o + " " + classifiedMap.get(o).toString());
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
			System.out.println(i + ":" + sb);
			i++;
	}
	}
		
	public static void printClassedConcepts(Set<? extends Concept<String, FullObject<String, String>>> concepts) {
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
				System.out.println(i + ":" + sb);
				i++;
		}
}
	
	public static void printClassedConceptProbs(Set<? extends Concept<String, FullObject<String, String>>> concepts) {
		int i=1;
		for(Concept<String, FullObject<String, String>> c : concepts) {
			FuzzyMultiClassedConcept fcc = (FuzzyMultiClassedConcept) c;
			StringBuffer sb = new StringBuffer();
			sb.append("<{");
	//		for(FullObject<String, String> o : fcc.getExtent()) 
	//			sb.append(o.getIdentifier() + ",");
			sb.append("},{");
			/*	for(String attr : fcc.getIntent())
				sb.append(attr + ",");
			sb.append("},{"); */
			sb.append(fcc.getProbsList());
			sb.append("-->");
			sb.append(fcc.getProbClass());
			sb.append("}>");
			System.out.println(i + ":" + sb);
			i++;
	}
}
}
