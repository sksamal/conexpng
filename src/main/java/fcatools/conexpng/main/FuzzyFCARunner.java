package fcatools.conexpng.main;

import java.io.IOException;

import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import fcatools.conexpng.Conf;
import fcatools.conexpng.Conf.StatusMessage;
import fcatools.conexpng.gui.MainFrame;
import fcatools.conexpng.gui.actions.OpenSaveExportAction;
import fcatools.conexpng.io.FCSVReader;
import fcatools.conexpng.io.locale.LocaleHandler;

public class FuzzyFCARunner {

	public static void main(String[] args) {
		Conf state = new Conf();
		state.filePath = "";
	    System.setProperty("user.language", LocaleHandler.readLocale());
	       
		try {
		FCSVReader fcsvr = new FCSVReader(state, args[0]);
		StatusMessage status;
		state.startCalculation(StatusMessage.LOADINGFILE);
		System.out.println(state.context.getConcepts());
		}
		
		catch(IOException e) {
			e.printStackTrace();
		} 
		catch (IllegalObjectException e) {
			e.printStackTrace();
		}
		
	}

}
