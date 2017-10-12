package fcatools.conexpng.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import fcatools.conexpng.Conf;
import fcatools.conexpng.model.FuzzyClassifierContext;

public class FCSVClassReader {
	public static final String SEP = ",";

	public FCSVClassReader(Conf state, String path) throws IllegalObjectException, IOException {
        FileInputStream fis = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        FuzzyClassifierContext context = new FuzzyClassifierContext();

        line = br.readLine();
        String[] attr = line.split(SEP);
        for (int i = 1; i < attr.length-1; i++) {
            context.addAttribute(attr[i]);
        }
        while ((line = br.readLine()) != null) {
            String[] obj = line.split(SEP);
            /* last attribute is class */
            String[] attrForObj = new String[attr.length-2];
            double[] values = new double[attr.length-2];
            for (int i = 1; i < obj.length-1; i++) {
              try{
                    attrForObj[i-1]=context.getAttributeAtIndex(i - 1);
                    values[i-1] = Double.parseDouble(obj[i]);
              }
              catch(NumberFormatException e) {
            	  
              }
            }
            if(obj.length < attr.length)
            	context.addObject(obj[0],null,attrForObj,values);
            else
            	context.addObject(obj[0],obj[obj.length-1],attrForObj,values);
//            System.out.println(context);
        }
        br.close();
       state.setNewFile(path);
        state.newContext(context);
    }
}
