package fcatools.conexpng.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.FuzzyConf;
import fcatools.conexpng.model.FuzzyFormalContext1;
import fcatools.conexpng.model.FuzzyObject;

public class FCSVReader {
	public static final String SEP = ",";
//	public static double TRESH = 0.8;

	public FCSVReader(FuzzyConf state, String path) throws IllegalObjectException, IOException {
        FileInputStream fis = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        FuzzyFormalContext1 context = new FuzzyFormalContext1();

        line = br.readLine();
        String[] attr = line.split(SEP);
        for (int i = 1; i < attr.length; i++) {
            context.addAttribute(attr[i]);
        }
        while ((line = br.readLine()) != null) {
            String[] obj = line.split(SEP);
            Set<String> attrForObj = new TreeSet<>();
            for (int i = 1; i < obj.length; i++) {
//                if (obj[i].equals("1"))
              try{
 //               if(Double.parseDouble(obj[i]) > TRESH)
            	  	FuzzyObject<String,Double> fObj = new FuzzyObject<String,Double>(obj[0],Double.parseDouble(obj[i]));
                    attrForObj.add(context.getAttributeAtIndex(i - 1));
                    context.addObject(new FullObject<>(fObj, attrForObj));
              }
              catch(NumberFormatException e) {
            	  
              }
            }
        }
        br.close();

        state.setNewFile(path);
        state.newContext(context);
    }
}
