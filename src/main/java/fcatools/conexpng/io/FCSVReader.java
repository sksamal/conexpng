package fcatools.conexpng.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import fcatools.conexpng.Conf;
import fcatools.conexpng.model.FuzzyFormalContext;

public class FCSVReader {
	public static final String SEP = ",";
//	public static double TRESH = 0.8;

	public FCSVReader(Conf state, String path) throws IllegalObjectException, IOException {
        FileInputStream fis = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        FuzzyFormalContext context = new FuzzyFormalContext();

        line = br.readLine();
        String[] attr = line.split(SEP);
        for (int i = 1; i < attr.length; i++) {
            context.addAttribute(attr[i]);
        }
        while ((line = br.readLine()) != null) {
            String[] obj = line.split(SEP);
            String[] attrForObj = new String[attr.length-1];
            double[] values = new double[attr.length-1];
            for (int i = 1; i < obj.length; i++) {
              try{
                    attrForObj[i-1]=context.getAttributeAtIndex(i - 1);
                    values[i-1] = Double.parseDouble(obj[i]);
              }
              catch(NumberFormatException e) {
            	  
              }
            }
            context.addObject(obj[0],attrForObj,values);
            System.out.println(context);

        }
        br.close();
       state.setNewFile(path);
        state.newContext(context);
    }
}
