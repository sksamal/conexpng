package examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ClassifyRecipe {
	public static void main(String args[]) throws FileNotFoundException {
		String INPUTFILE = "/home/ssamal/workspace/conexpng/recipe.csv";
		Scanner sc = new Scanner(new File(INPUTFILE));
		
		while(sc.hasNext()) {
			String line= sc.nextLine();
			String data[] = line.split("\\$");
			String label = data[0];
			String image = data[1];
			String source = data[2];
			for(int i=3;i<data.length;i=i+2) {
				System.out.println("Ing:" + data[i] + " amount:" + data[i+1]);
				String tokens[] = data[i].split(" ");
			}
		}
		sc.close();
	}
}
