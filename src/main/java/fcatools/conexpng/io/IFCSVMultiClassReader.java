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
import fcatools.conexpng.model.IFuzzyMultiClassifierContext;

public class IFCSVMultiClassReader {
	public static final String SEP = ",";
	private BufferedReader br;
	private int numObj;
	private int numClasses;
	private boolean unique;
	private IFuzzyMultiClassifierContext context;

	public IFCSVMultiClassReader(Conf state, String path) throws IllegalObjectException, IOException {
		this(state,path,1);
	}
	
	public IFCSVMultiClassReader(Conf state, String path, int numClasses) throws IllegalObjectException, IOException {
		this(state,path,numClasses,Integer.MAX_VALUE);
	}
	
	public IFCSVMultiClassReader(Conf state, String path, int numClasses, int initialRecords) throws IllegalObjectException, IOException {
		this(state,path,numClasses,true, initialRecords);
	}
	
	public IFCSVMultiClassReader(Conf state, String path, int numClasses, boolean unique) throws IllegalObjectException, IOException {
		this(state,path,numClasses,unique, Integer.MAX_VALUE);
	}
	
	public IFCSVMultiClassReader(Conf state, String path, int numClasses, boolean unique, int initialRecords) throws IllegalObjectException, IOException {
		    
		FileInputStream fis = new FileInputStream(path);
        br = new BufferedReader(new InputStreamReader(fis));
        numObj=0;
        String line;
        context = new IFuzzyMultiClassifierContext();
        this.numClasses = numClasses;
        this.unique = unique;
        line = br.readLine();
        String[] attr = line.split(SEP);
        
        for (int i = 1; i < attr.length-numClasses; i++) {
            context.addAttribute(attr[i]);
        }
  
     //   System.out.println("Attributes:" + context.getAttributes()); 
     
        while ( initialRecords>0 && (line = br.readLine()) != null) {
        	process(line);
        	initialRecords--;
        }
        
        state.setNewFile(path);
        state.newContext(context);
    
	}
        
    public boolean readNext() throws IllegalObjectException, IOException {   
    	String line = null;
        if ((line = br.readLine()) != null) 
        	  process(line);
        else 
        	return false;
        return true;
    }
    
    public void process(String line) throws IllegalObjectException {
        	
            String[] obj = line.split(SEP);
            List<String> attrForObj = new ArrayList<String>();
            List<Double> values = new ArrayList<Double>();
            Set<String> classes = new TreeSet<String>();
  
//            System.out.println("Attributes:" + context.getAttributes()); 
//            System.out.println("AttributeCount:" + context.getAttributeCount()); 
//            System.out.println(obj.length);
            int i=1;
            for (; i <= context.getAttributeCount(); i++) {
            	if (isDouble(obj[i])){
            	    attrForObj.add(context.getAttributeAtIndex(i - 1));
                    values.add(Double.parseDouble(obj[i]));
            		}
            }
            
            // for classes
            for(; i < obj.length; i++) 
            	classes.add(obj[i]);
            
//            System.out.println("Classes:" + classes); 
            System.out.println(obj[0]);
            
            if(obj.length < context.getAttributeCount() + numClasses) 
            	if(unique)
            		context.addObject(obj[0],"",attrForObj,values);
            	else
            		context.addObject(obj[0]+"_" + numObj,"",attrForObj,values);
           	else
            	if(unique)
                	context.addObject(obj[0],classes,attrForObj,values);
            	else
                	context.addObject(obj[0]+"_" + numObj,classes,attrForObj,values);

            numObj++;
    
 //         System.out.println("Classes:" + context.getClasses()); 
    }
    
    public void close() throws IOException {
    	br.close();
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
