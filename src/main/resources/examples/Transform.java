package examples;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Transform {
	public static void main(String args[]) throws NumberFormatException, IOException {
	//String path = "/home/ssamal/dl/201803/codings.fccsv";
	String path = "/home/ssamal/workspace/conexpng/floating_codings.csv";
	int bins=2;
	boolean noattr = true;
	if(args.length==1)	path = args[0];
	if(args.length==2)  bins = Integer.parseInt(args[1]);
	if(args.length==3)  noattr = false;
	double RANGE_MIN = 0.0, RANGE_MAX = 1.0;
	int numClasses = 1;
	String SEP = ",";
	FileInputStream fis = new FileInputStream(path);
	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	
	String rawpath = path.substring(0, path.lastIndexOf('.'));
	String ext = path.substring(path.lastIndexOf('.')+1,path.length());
	PrintWriter pw = new PrintWriter(new File(rawpath + "_" + bins + "." + ext));
	
	
	String line = br.readLine();
	String[] attr = line.split(SEP);
	   
	if(noattr) {
		for(int i=0;i<attr.length - numClasses;i++)
			attr[i] = "feat"+ i;
		for(int i=0;i<numClasses;i++)
			attr[attr.length - numClasses] = "class"+ i;
		br.close();
		br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
	}
    
    //header - when no objs present
    StringBuffer newline = new StringBuffer("objs");
    for(int i=0;i<attr.length-numClasses; i++) {
    	for(int j=0;j<bins;j++) {
    		newline.append(SEP + attr[i]+ "_" + j);
    	}
    }
    
    for(int i=0;i<numClasses;i++) {
    	 newline.append(SEP + attr[attr.length-numClasses + i]);
    }
    pw.println(newline);
    
    // Actual data
    double rangeSize = (RANGE_MAX - RANGE_MIN)/bins;
    while((line = br.readLine()) != null) {
    	String values[] = line.split(SEP);
    	
   // 	newline = new StringBuffer(values[0]);  // not always an object
    //	If no object id exists, use the class as objectid
    	newline = new StringBuffer("o" + values[values.length-1]);
    
    	// start from 1 if first field is object, else from 0
    	for(int i=0;i<values.length-numClasses; i++) {
    		int binIndex = (int) ((Double.parseDouble(values[i]) - RANGE_MIN)/rangeSize);
    		for(int j=0;j<bins;j++) {
    			if(j==binIndex)
    				newline.append(SEP + String.format("%.4f",Double.parseDouble(values[i])));
    			else
    				newline.append(SEP + 0.0);
    		} 
    }
    	
    for(int i=0;i<numClasses;i++) {
      	 newline.append(SEP + values[values.length-numClasses + i]);
     }
    pw.println(newline);
    }
    br.close();
    pw.close();
	}
}
