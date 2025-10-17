package dev.kasal.hackassembler;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class Assembler {

    private final static String COMMENT_PREFIX = "//";
    private final static String LABEL_REGEX = "^\\(.*\\)$";

    static void main(String[] args) {
        compile(args);
    }

    public static void compile(String[] args) {
        if (args.length < 1) {
            System.err.println("ERROR: Path to a .asm file or a directory must be provided.");
            System.exit(1);
        }

        long startTime = System.nanoTime();

        File fileToCompile = new File(args[0]);
        File targetFile = new File(args.length == 2 ? args[1] : fileToCompile.getName().replace(".asm", ".hack"));
        System.out.printf("File: %s will be compiled into: %s\n", fileToCompile.getName(), targetFile.getName());

        SymbolTable symbolTable = new SymbolTable();
        Compiler compiler = new Compiler();

        // FIRST-READ to update the symbol table with labels
        firstRead(fileToCompile, symbolTable);

        // SECOND-READ to translate the instructions
        secondRead(fileToCompile, targetFile, compiler, symbolTable);

        long endTime = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        System.out.printf("Successfully compiled file in %d ms. \n", durationMs);
    }

    private static void firstRead(File fileToCompile, SymbolTable symbolTable) {
        int pc = 0;

        try (var reader = new BufferedReader(new FileReader(fileToCompile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim().replaceAll("\\s+", "");
                if (trimmedLine.startsWith(COMMENT_PREFIX) || trimmedLine.isBlank()) {
                    continue;
                }

                if (line.matches(LABEL_REGEX)) {
                    String label = line.substring(1, line.length()-1);
                    if (!symbolTable.contains(label)) {
                        symbolTable.add(label, pc);
                    } else {
                        throw new IllegalArgumentException("""
                        Could not add new label: %s to the symbol table at PC: %d, cause the label was already defined!
                        """.formatted(label, pc));
                    }
                } else {
                    pc++;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void secondRead(File fileToCompile, File targetFile, Compiler compiler, SymbolTable symbolTable) {
        try (var reader = new BufferedReader(new FileReader(fileToCompile));
             var writer = new BufferedWriter(new FileWriter(targetFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim().replaceAll("\\s+", "");
                if (!trimmedLine.startsWith(COMMENT_PREFIX)) {
                    var parsedInstruction = Parser.parse(trimmedLine, symbolTable);

                    if (parsedInstruction != null) {
                        var binaryInstruction = compiler.compileInstruction(parsedInstruction, symbolTable);
                        writer.write(binaryInstruction + "\n");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

}
