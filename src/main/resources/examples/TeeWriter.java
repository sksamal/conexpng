package examples;
import java.io.PrintStream;

class TeeWriter {
	
	private PrintStream pstr1, pstr2;
	
	public TeeWriter(PrintStream pstr1, PrintStream pstr2) {
		this.pstr1 = pstr1;
		this.pstr2 = pstr2;
	}
	public void println(String s) {
		if(pstr1!=null)
			pstr1.println(s);
		if(pstr2!=null)
		pstr2.println(s);
}
}
