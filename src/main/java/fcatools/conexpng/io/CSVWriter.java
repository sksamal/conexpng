package fcatools.conexpng.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import de.tudresden.inf.tcs.fcalib.FullObject;
import fcatools.conexpng.Conf;

public class CSVWriter {

    public CSVWriter(Conf state, String path) throws IOException {
 
    	final String SEP = ",";

        FileOutputStream fos = new FileOutputStream(path);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (String attr : state.context.getAttributes()) {
            bw.append(SEP + attr);
        }
        bw.newLine();

        for (FullObject<String, String> obj : state.context.getObjects()) {
            bw.append(obj.getIdentifier());
            for (String attr : state.context.getAttributes()) {
                bw.append(state.context.objectHasAttribute(obj, attr) ? SEP+"1" : SEP+"0");
            }
            bw.newLine();
        }

        bw.close();
        fos.close();
    }
}
