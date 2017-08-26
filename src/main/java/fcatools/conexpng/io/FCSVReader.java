package fcatools.conexpng.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import de.tudresden.inf.tcs.fcalib.FullObject;
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
            Set<String> attrForObj = new TreeSet<>();
            double[] values = new double[attr.length];
            for (int i = 1; i < obj.length; i++) {
//                if (obj[i].equals("1"))
              try{
 //               if(Double.parseDouble(obj[i]) > TRESH)
                    attrForObj.add(context.getAttributeAtIndex(i - 1));
                    values[i] = Double.parseDouble(obj[i]);
              }
              catch(NumberFormatException e) {
            	  
              }
            }
            System.out.println(context);
            context.addObject(new FullObject<>(obj[0],attrForObj),values);

        }
        br.close();
       state.setNewFile(path);
        state.newContext(context);
    }
}
