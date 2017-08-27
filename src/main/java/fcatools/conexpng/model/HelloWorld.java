package fcatools.conexpng.model;

import java.util.Scanner;

public class HelloWorld {
    public static void main(String args[]) {
        int number =0;
        int i = 0;
        int sum = 0;

 
        System.out.print("Type 1 to 10 on same line (separated by space): ");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        in = new Scanner(input);
 //       for(i = 0; i <= 9; i++)
        while(in.hasNext())
        {
            number = in.nextInt();
        	System.out.println(number);

            if((number%2) == 1){
            	sum +=number;
            }
        }        System.out.println("The sum is " + sum);

    }
        }
