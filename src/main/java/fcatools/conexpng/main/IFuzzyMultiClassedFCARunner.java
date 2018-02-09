package fcatools.conexpng.main;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import de.tudresden.inf.tcs.fcaapi.Concept;
import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.Conf.StatusMessage;
import fcatools.conexpng.io.FCSVMultiClassReader;
import fcatools.conexpng.io.locale.LocaleHandler;
import fcatools.conexpng.model.FuzzyMultiClassifierContext;
import fcatools.conexpng.model.IFuzzyMultiClassifierContext;

public class IFuzzyMultiClassedFCARunner {

	public static void main(String[] args) {
		Conf state = new Conf();
		state.filePath = "";
	    System.setProperty("user.language", LocaleHandler.readLocale());
	       
		try {
		FCSVMultiClassReader fccsvr = new FCSVMultiClassReader(state, args[0],0);
		state.startCalculation(StatusMessage.LOADINGFILE);
//		System.out.println(state.context.getConcepts());
		
		IFuzzyMultiClassifierContext ifmc = new IFuzzyMultiClassifierContext((FuzzyMultiClassifierContext)state.context);
		System.out.println("Original concepts");
		printConcepts(ifmc.getConcepts());
		
		//add a new object
	     Set<String> attrForObj = new TreeSet<>();
	     attrForObj.add(ifmc.getAttributeAtIndex(0));
	     attrForObj.add(ifmc.getAttributeAtIndex(1));
	     attrForObj.add(ifmc.getAttributeAtIndex(2));
	     ifmc.addObject(new FullObject<String, String>("t4", attrForObj));

	 	System.out.println("\n New concepts after adding t4");
		printConcepts(ifmc.getConcepts());
		
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
