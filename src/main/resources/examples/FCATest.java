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

public class FCATest {

	private static TeeWriter tee = null;
	public static void main(String[] args) {
			
	//	String INPUTFILE = "/home/ssamal/Downloads/data-analysis-tools/data/colcan/codings2.fccsv";
	//	String INPUTFILE = "/home/ssamal/dl/201803/out/codings1000.fccsv";
		String INPUTFILE = "/home/ssamal/workspace/conexpng/pizza_onto_context.fccsv";
		if(args.length >=1) INPUTFILE = args[0];
		
		Conf state = new Conf();
		state.filePath = "/home/ssamal/data-analysis-tools/data/colcan";
	    System.setProperty("user.language", LocaleHandler.readLocale());
	    PrintStream pwStream = null;

		Conf newState = new Conf();
		try {
	
		pwStream = new PrintStream(INPUTFILE + ".log");
		
		tee = new TeeWriter(pwStream, System.out);
		newState.filePath = "";
		tee.println("Reading " + INPUTFILE);
		long currentms = System.currentTimeMillis();
		FCSVMultiClassReader ptdgsReader = new FCSVMultiClassReader(newState, INPUTFILE,0,false,10); // last 1 are classes, uniqueness
		tee.println("\n\n***Reading first 10 records***");
		tee.println("No of Objects:"+ newState.context.getObjectCount());
		tee.println("No of attributes:" + newState.context.getAttributeCount());
		Set<Concept<String,FullObject<String,String>>> concepts = newState.context.getConcepts();
		tee.println("No of concepts:" + concepts.size());
		printConcepts(concepts);
	//	System.exit(1);
	
		// Incrementally add objects
		int i=0;
		while(ptdgsReader.readNext()) {
			i++;
			if(i%1000==0) {
				tee.println("	Read " + i + " records");
			concepts = newState.context.getConcepts();
			}
			if(i==1000) break;
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
			//System.out.println("Concepts containing " + o.getIdentifier() + " are:");
			//printClassedConceptProbs(((FuzzyMultiClassifierContext)(state.context)).getConceptsOfObject(o));
//			i++;
//		} 
		// Classify all objects
	//	((FuzzyMultiClassifierContext)(newState.context)).kpartition(10);;
			
		// Classify all objects
		HashMap<String,Concept<String,FullObject<String, String>>> minConceptMap = ((FuzzyMultiClassifierContext)(newState.context)).getMinimalConceptMap();
		HashMap<String,Set<String>> classSetMap = ((FuzzyMultiClassifierContext)(newState.context)).getTrainingSet();
		List<Set<String>> classesSet = ((FuzzyMultiClassifierContext)(newState.context)).getClasses();
//		printClassedConceptProbs(minConceptMap,classesSet,classSetMap);
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
		
		
}
