package org.example;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

class SecurityDepartment implements Runnable {

    private final File file;
    private final String[] names = {"John", "Jane", "Bob", "Alice", "Tom", "Jerry", "Peter", "Mary", "David", "Kate"};
    private final Random rand = new Random();

    SecurityDepartment(File file) {
        this.file = file;
    }

    private void generateAccessCodes() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String name : names) {
                int accessCode = rand.nextInt(900000) + 100000;
                writer.println(name + ":" + accessCode);
            }
            System.out.println("Access codes generated.");
        } catch (IOException e) {
            System.err.println("Error generating access codes: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        if (file.exists()) {
            if (!file.delete()) {
                System.err.println("Error deleting existing file.");
                return;
            }
        }
        generateAccessCodes();
    }
}

class AccessCodeGenerator implements Runnable {

    private final File file;
    private final String name;

    AccessCodeGenerator(File file, String name) {
        this.file = file;
        this.name = name;
    }

    private void generateAccessCode() {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(":");
                if (parts[0].equals(name)) {
                    break;
                }
            }
            System.out.println("Access code generated for " + name);
        } catch (FileNotFoundException e) {
            System.err.println("Error generating access code: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        generateAccessCode();
    }
}

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("security.txt");
        Thread t1 = new Thread(new SecurityDepartment(file));
        t1.start();

        System.out.println("Loading data...");
        try {
            t1.join();
        } catch (InterruptedException e) {
            System.err.println("Error waiting for access codes to be generated: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter employee number: ");
        int index = scanner.nextInt() - 1;

        try (Scanner fileScanner = new Scanner(file)) {
            for (int i = 0; i < index; i++) {
                fileScanner.nextLine();
            }
            String line = fileScanner.nextLine();
            String[] parts = line.split(":");
            String name = parts[0];
            AccessCodeGenerator codeGenerator = new AccessCodeGenerator(file, name);
            Thread t2 = new Thread(codeGenerator);
            t2.start();
            try {
                t2.join();
                String e = null;
                throw new RuntimeException((String) null);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}