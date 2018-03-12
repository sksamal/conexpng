import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

public class HTMLWriter extends PrintStream {

public HTMLWriter(String fileName) throws FileNotFoundException {
		super(fileName);
	}

public HTMLWriter(PrintStream out) {
	super(out);
}

public void println(String str) {
	print(formatHtml(str));
	super.print("<br>");
}

public void print(String str) {
	super.print(formatHtml(str));
}

public static void main(String args[]) throws Exception {
	HTMLWriter pw = new HTMLWriter(System.out);
	pw.println("This is a book");
	pw.print("sdjkskd jksdjks\tsjkdksk");
	pw.close();
}

public String formatHtml(String str) {

	return str//.replace(" ", "&nbsp;")
			.replace("\n", "<br>")
			.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
	
}

}