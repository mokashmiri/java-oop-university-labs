package calc;

import java.util.Scanner;

public class IO {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void println(String s) {
        System.out.println(s);
    }

    public static void print(String s) {
        System.out.print(s);
    }

    public static String readln(String prompt) {
        if (prompt != null) {
            System.out.print(prompt);
        }
        return SCANNER.nextLine();
    }
}
