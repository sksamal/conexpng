package examples;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

public class Partition {
	
	public static void main(String args[]) throws IOException {
		String file = "Z:/data-analysis-tools/data/colcan/codings.fccsv";
		//String file = "/home/ssamal/Downloads/data-analysis-tools/data/colcan/codings.fccsv";
		int numClasses = 1;
		String SEP = ",";
		double trainP = 0.6;
		
		// to find the number of records
		Path path = Paths.get(file);
		long records = Files.lines(path).count() - 1;
		
		// generate test indices
		long testSize = (int)(records*(1-trainP));
		Set<Long> test = new TreeSet<Long>();
		System.out.println("TrainSize=" + (records - testSize));
		System.out.println("TestSize=" + testSize);
		while(test.size() < testSize) {
			long number =(long)(Math.random()*records); 
			test.add(number);
		}
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		PrintWriter pw = new PrintWriter(new File(path + "_" + trainP));
		// print the header
		String line = br.readLine();
		pw.println(line);
		
		long index=0;
		while((line = br.readLine()) != null) {
			if(test.contains(index)) {
				String values[] = line.split(SEP);
				for(int i=0;i<numClasses;i++) {
					values[values.length-numClasses + i] = "";
				}
				String newLine = String.join(",",values);
				pw.println(newLine);
			 }
			else
				pw.println(line);
			index++;
		}
		br.close();
		pw.close();
	}
}
