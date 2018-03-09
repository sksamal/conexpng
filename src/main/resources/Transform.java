import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Transform {
	public static void main(String args[]) throws NumberFormatException, IOException {
	String path = "/home/ssamal/Downloads/data-analysis-tools/data/colcan/codings2.fccsv";
	int bins=8;
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
    
    
    //header
    StringBuffer newline = new StringBuffer(attr[0]);
    for(int i=1;i<attr.length-numClasses; i++) {
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
    	newline = new StringBuffer(values[0]);
    
    	for(int i=1;i<values.length-numClasses; i++) {
    		int binIndex = (int) ((Double.parseDouble(values[i]) - RANGE_MIN)/rangeSize);
    		for(int j=0;j<bins;j++) {
    			if(j==binIndex)
    				newline.append(SEP + String.format("%f",Double.parseDouble(values[i])));
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
