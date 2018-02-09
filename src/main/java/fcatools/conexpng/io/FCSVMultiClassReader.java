package fcatools.conexpng.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.tudresden.inf.tcs.fcaapi.exception.IllegalObjectException;
import fcatools.conexpng.Conf;
import fcatools.conexpng.model.FuzzyMultiClassifierContext;

public class FCSVMultiClassReader {
	public static final String SEP = ",";

	public FCSVMultiClassReader(Conf state, String path) throws IllegalObjectException, IOException {
		this(state,path,2);
	}
	public FCSVMultiClassReader(Conf state, String path, int numClasses) throws IllegalObjectException, IOException {
        FileInputStream fis = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        String line;
        FuzzyMultiClassifierContext context = new FuzzyMultiClassifierContext();

        line = br.readLine();
        String[] attr = line.split(SEP);
        
        for (int i = 1; i < attr.length-numClasses; i++) {
            context.addAttribute(attr[i]);
        }
  
      //  System.out.println("Attributes:" + context.getAttributes()); 
        
        int numObj=0;
        while ((line = br.readLine()) != null) {
        	
            String[] obj = line.split(SEP);
            List<String> attrForObj = new ArrayList<String>();
            List<Double> values = new ArrayList<Double>();
            Set<String> classes = new TreeSet<String>();
            
            int i=1;
            for (; i < attr.length - numClasses; i++) {
            	if (isDouble(obj[i])){
            	    attrForObj.add(context.getAttributeAtIndex(i - 1));
                    values.add(Double.parseDouble(obj[i]));
            		}
            }
            
            // for classes
            for(; i < attr.length; i++) 
            	classes.add(obj[i]);
            
//            System.out.println("Classes:" + classes); 

            
            if(obj.length < attr.length) 
                context.addObject(obj[0]+"_" + numObj,"",attrForObj,values);
           	else
            	context.addObject(obj[0]+"_" + numObj,classes,attrForObj,values);
            numObj++;
     //         System.out.println(context);
        }
        br.close();
 //       System.out.println("Classes:" + context.getClasses()); 
        state.setNewFile(path);
        state.newContext(context);
    }

	public boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
