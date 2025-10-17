package dev.kasal.hackassembler;

/**
 * Parser util class that implements logic to parse a line of the given instruction from .asm a file.
 */
public class Parser {

    private static final String A_INSTRUCTION_PREFIX = "@";
    private static final String LABEL_PREFIX = "(";
    private static final String ASSIGNMENT_SEPARATOR = "=";
    private static final String JUMP_SEPARATOR = ";";

    private Parser() {

    }

    /**
     * Parse one given line to the instruction object.
     *
     * @param line line to parse
     * @param symbolTable {@link SymbolTable}
     * @return parsed {@link Instruction} object
     */
    public static Instruction parse(String line, SymbolTable symbolTable) {
        if (canIgnoreLine(line)) {
            return null;
        }

        return line.startsWith(A_INSTRUCTION_PREFIX) ? parseAInstruction(line, symbolTable) : parseCInstruction(line);
    }

    /**
     * Checks if the given line is blank or sign post, if yes, can be ignored.
     */
    private static boolean canIgnoreLine(String line) {
        return line.isBlank() || line.startsWith(LABEL_PREFIX);
    }

    /**
     * A-Instruction parse logic. Symbol table can be updated by parsing logic.
     *
     * @param line line to parse
     * @param symbolTable symbol table
     * @return parsed instruction as {@link Instruction}
     */
    private static Instruction parseAInstruction(String line, SymbolTable symbolTable) {
        String value = line.substring(1);
        if (!symbolTable.contains(value) && !value.matches("[0-9]+")) {
            var nextRamAddr = symbolTable.getNextAvailableRamAddress();
            symbolTable.add(value, nextRamAddr);
        }

        return new Instruction(OpCode.A, value);
    }

    /**
     * C-Instruction parse logic.
     *
     * @param line line to parse
     * @return parsed instruction as {@link Instruction}
     */
    private static Instruction parseCInstruction(String line) {
        if (line.contains(JUMP_SEPARATOR)) {
            String[] splitByJmp = line.split(JUMP_SEPARATOR);

            if (splitByJmp.length != 2)
                throw new IllegalArgumentException("Not valid jmp instruction: " + line);

            return new Instruction(OpCode.C, splitByJmp[0], splitByJmp[1]);
        } else if (line.contains(ASSIGNMENT_SEPARATOR)) {
            String[] splitByEq = line.split(ASSIGNMENT_SEPARATOR);

            if (splitByEq.length != 2)
                throw new IllegalArgumentException("Not valid assigment instruction: " + line + "!");

            return new Instruction(OpCode.C, splitByEq[1], splitByEq[0], null);
        } else {
            throw new IllegalArgumentException("Cannot parse the file, line: " + line + " is not valid C-Instruction");
        }
    }

}
