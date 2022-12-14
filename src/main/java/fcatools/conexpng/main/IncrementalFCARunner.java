package fcatools.conexpng.main;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.Conf.StatusMessage;
import fcatools.conexpng.io.CSVReader;
import fcatools.conexpng.io.locale.LocaleHandler;
import fcatools.conexpng.model.FuzzyFormalContext;
import fcatools.conexpng.model.IncrementalFormalContext;

public class IncrementalFCARunner {

	public static void main(String[] args) {
		Conf state = new Conf();
		state.filePath = "";
	    System.setProperty("user.language", LocaleHandler.readLocale());
	       
		try {
		CSVReader csvr = new CSVReader(state, args[0]);
		StatusMessage status;
		state.startCalculation(StatusMessage.LOADINGFILE);
//		System.out.println(state.context.getConcepts());
//		printConcepts(state.context.getConcepts());
		
		IncrementalFormalContext ifc = new IncrementalFormalContext(state.context);
		printConcepts(ifc.getConcepts());
		
		//add a new object
	     Set<String> attrForObj = new TreeSet<>();
	     attrForObj.add(ifc.getAttributeAtIndex(0));
	     attrForObj.add(ifc.getAttributeAtIndex(1));
	     attrForObj.add(ifc.getAttributeAtIndex(2));
	     ifc.addObject(new FullObject<String, String>("t4", attrForObj));

	 	printConcepts(ifc.getConcepts());
		
		}
		
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (IllegalObjectException e) {
			e.printStackTrace();
		}
		
	}

	public static void printConcepts(Set<Concept<String, FullObject<String, String>>> concepts) {
		int i=1;
		for(Concept<String, FullObject<String, String>> c : concepts) {
			StringBuffer sb = new StringBuffer();
			sb.append("<{");
			for(FullObject<String, String> o : c.getExtent()) 
				sb.append(o.getIdentifier() + ",");
			sb.append("},");
			sb.append(c.getIntent());
			sb.append(">");
			System.out.println(i + ":" + sb);
			i++;
	}
}
}
