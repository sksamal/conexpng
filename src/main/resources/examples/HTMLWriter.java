package examples;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;

public class HTMLWriter extends PrintStream {

String imageLocation = "./";

public HTMLWriter(String fileName) throws FileNotFoundException {
		super(fileName);
	}

public HTMLWriter(PrintStream out) {
	super(out);
}

public void setImageLocation(String pathStr) {
	this.imageLocation = pathStr;
}
public void println(String str) {
	print(str);
	super.print("<br>");
}

public void print(String str) {
	super.print(formatHtml(str));
}

public static void main(String args[]) throws Exception {
	HTMLWriter pw = new HTMLWriter(System.out);
	pw.println("This is a book");
	pw.print("     8.0       {14}    [[0.42, 0.14, 0.00, 0.00, 0.00, 0.21, 0.00, 0.21, 0.00, 0.00]]    [[0.0]]      [0.0] _/	 mnistImage=0/8.png");
	pw.close();
}

public String formatHtml(String str) {
	
	str= str.replace(" ", "&nbsp;")
			.replace("\n", "<br>")
			.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
	
	str = str.replace("mnistImage=", "<img src=\"" + imageLocation + "/")
			.replace(".png", ".png\">");
	
	if(str.contains("_/")) {
		str = "<font color = 'red' > " + str + "</font>";
	}
	return str;
}

}