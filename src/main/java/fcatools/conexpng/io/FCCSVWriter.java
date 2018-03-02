package fcatools.conexpng.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;
import fcatools.conexpng.model.FuzzyMultiClassifierContext;

public class FCCSVWriter {

    public FCCSVWriter(Conf state, String path) throws IOException {
 
    	final String SEP = ",";

    	FuzzyMultiClassifierContext fcc = (FuzzyMultiClassifierContext)(state.context);
        FileOutputStream fos = new FileOutputStream(path);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (String attr : fcc.getAttributes()) {
            bw.append(SEP + attr);
        }
        
        System.out.println(fcc.getClassesCount());
        System.out.println(fcc.getClassesAsString());
        for(int i=1;i<=fcc.getClassesCount();i++)
        	bw.append(SEP + "class"+i);
        bw.newLine();

        for (FullObject<String, String> obj : fcc.getObjects()) {
            bw.append(obj.getIdentifier());
            for (String attr : fcc.getAttributes()) {
                bw.append(fcc.objectHasAttribute(obj, attr) ? SEP+"1" : SEP+"0");
            }
            
            for(String str : fcc.getClass(obj.getIdentifier()))
            		bw.append(SEP + str);
            bw.newLine();
        }

        bw.close();
        fos.close();
    }
}
