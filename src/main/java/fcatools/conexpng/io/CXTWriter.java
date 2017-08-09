package fcatools.conexpng.io;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import de.tudresden.inf.tcs.fcalib.FullObject;

import fcatools.conexpng.Conf;

public class CXTWriter {

    private final String EOL = System.getProperty("line.separator");

    public CXTWriter(Conf state, String path) throws IOException {

        FileOutputStream fos = new FileOutputStream(path);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        bw.append("B" + EOL);
        bw.append("no name" + EOL); // the name of the context
        bw.append(state.context.getObjectCount() + EOL);
        bw.append(state.context.getAttributeCount() + EOL);
        for (FullObject<String, String> obj : state.context.getObjects()) {
            bw.append(obj.getIdentifier() + EOL);
        }
        for (String attr : state.context.getAttributes()) {
            bw.append(attr + EOL);
        }
        for (FullObject<String, String> obj : state.context.getObjects()) {
            for (String attr : state.context.getAttributes()) {
                bw.append(state.context.objectHasAttribute(obj, attr) ? "X" : ".");
            }
            bw.append(EOL);
        }

        bw.close();
        fos.close();

    }
}
