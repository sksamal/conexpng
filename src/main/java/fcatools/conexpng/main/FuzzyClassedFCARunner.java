package fcatools.conexpng.main;

import java.io.IOException;

import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import fcatools.conexpng.Conf;
import fcatools.conexpng.Conf.StatusMessage;
import fcatools.conexpng.io.FCSVClassReader;
import fcatools.conexpng.io.locale.LocaleHandler;

public class FuzzyClassedFCARunner {

	public static void main(String[] args) {
		Conf state = new Conf();
		state.filePath = "";
	    System.setProperty("user.language", LocaleHandler.readLocale());
	       
		try {
		FCSVClassReader fccsvr = new FCSVClassReader(state, args[0]);
		StatusMessage status;
		state.startCalculation(StatusMessage.LOADINGFILE);
		System.out.println(state.context.getConcepts());
//		((FuzzyClassifierContext)(state.context)).setThreshold(1.0);
		System.out.println(state.context);
		
		}
		
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (IllegalObjectException e) {
			e.printStackTrace();
		}
		
	}

}
