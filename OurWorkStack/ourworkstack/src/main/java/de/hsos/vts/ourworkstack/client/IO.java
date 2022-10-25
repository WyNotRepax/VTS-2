package de.hsos.vts.ourworkstack.client;

import java.util.Scanner;

public class IO {
    private static final Scanner scanner = new Scanner(System.in);

    public static int readInt(String string) {
        while (true) {
            System.out.print(string);
            String result = scanner.nextLine();
            try {
                return Integer.parseInt(result);
            } catch (NumberFormatException e) {
            }
        }
    }
}
