import dev.kasal.hackassembler.Assembler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TestAssembler {

    static void main(String[] args) {
        testAgainstGoldenFile();
    }

    private static void testAgainstGoldenFile() {
        String[] args = new String[]{"test/resources/Pong.asm"};
        Assembler.compile(args);
        compareFiles("Pong.hack", "test/resources/Pong.hack.gold");
    }

    private static void compareFiles(String pathToCompiledFile, String pathToGoldenFile) {

        File compiledFile = new File(pathToCompiledFile);

        try (var compiledReader = new BufferedReader(new FileReader(compiledFile));
             var goldenReader = new BufferedReader(new FileReader(pathToGoldenFile))) {

            String compiledLine;
            String goldenLine;
            int lineCount = 1;

            while (true) {
                compiledLine = compiledReader.readLine();
                goldenLine = goldenReader.readLine();

                if (compiledLine == null && goldenLine == null) {
                    break;
                }

                if (compiledLine == null || goldenLine == null) {
                    throw new AssertionError("Test failed: Files are not of the same size!");
                }

                if (!compiledLine.equals(goldenLine)) {
                    throw new AssertionError("Test failed on line: %d, compiled: %s; golden: %s "
                            .formatted(lineCount, compiledLine, goldenLine));
                }
                lineCount++;
            }

            System.out.println("Test completed! Output matches the golden file!");

        } catch (IOException e) {
            System.err.println("Test failed because cannot read from the test files!");
            e.printStackTrace();
            System.exit(1);
        } finally {
            compiledFile.delete();
        }

    }

}
