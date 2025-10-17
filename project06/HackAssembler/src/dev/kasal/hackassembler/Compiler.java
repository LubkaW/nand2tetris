package dev.kasal.hackassembler;

import java.util.Map;

public class Compiler {

    private static final Map<String, String> compTableARegister = Map.ofEntries(
            Map.entry("0", "101010"),
            Map.entry("1", "111111"),
            Map.entry("-1", "111010"),
            Map.entry("D", "001100"),
            Map.entry("A", "110000"),
            Map.entry("!D", "001101"),
            Map.entry("!A", "110001"),
            Map.entry("-D", "001111"),
            Map.entry("-A", "110011"),
            Map.entry("D+1", "011111"),
            Map.entry("A+1", "110111"),
            Map.entry("D-1", "001110"),
            Map.entry("A-1", "110010"),
            Map.entry("D+A", "000010"),
            Map.entry("D-A", "010011"),
            Map.entry("A-D", "000111"),
            Map.entry("D&A", "000000"),
            Map.entry("D|A", "010101")
    );

    private static final Map<String, String> compTableMemory = Map.ofEntries(
            Map.entry("M", "110000"),
            Map.entry("!M", "110001"),
            Map.entry("-M", "110011"),
            Map.entry("M+1", "110111"),
            Map.entry("M-1", "110010"),
            Map.entry("D+M", "000010"),
            Map.entry("D-M", "010011"),
            Map.entry("M-D", "000111"),
            Map.entry("D&M", "000000"),
            Map.entry("D|M", "010101")
    );

    private static final Map<String, String> destTable = Map.ofEntries(
            Map.entry("null", "000"),
            Map.entry("M", "001"),
            Map.entry("D", "010"),
            Map.entry("MD", "011"),
            Map.entry("A", "100"),
            Map.entry("AM", "101"),
            Map.entry("AD", "110"),
            Map.entry("AMD", "111")
    );

    private static final Map<String, String> jmpTable = Map.ofEntries(
            Map.entry("null", "000"),
            Map.entry("JGT", "001"),
            Map.entry("JEQ", "010"),
            Map.entry("JGE", "011"),
            Map.entry("JLT", "100"),
            Map.entry("JNE", "101"),
            Map.entry("JLE", "110"),
            Map.entry("JMP", "111")
    );

    /**
     * Do compilation of one instruction. When compiling the A-Instruction,
     * the variable can be found and the symbol table is updated.
     *
     * @param instruction instruction to compile
     * @param symbolTable {@link SymbolTable}
     * @return compiled instruction as
     */
    public String compileInstruction(Instruction instruction, SymbolTable symbolTable) {

        String firstBit = instruction.opCode().getFirstBit();
        String compiledInstruction = switch (instruction.opCode()) {
            case A -> compileAInstruction(instruction, symbolTable);
            case C -> compileCInstruction(instruction);
        };
        return firstBit + compiledInstruction;
    }

    /**
     * Compile the A-Instruction -> set A-Register to define value (by variable or constant).
     *
     * @param instruction instruction to compile
     * @param symbolTable symbol table contains variables
     * @return compiled instruction as 15-right-most-bits.
     */
    private String compileAInstruction(Instruction instruction, SymbolTable symbolTable) {
        String ramAddress = instruction.operand();
        int intAddress;
        if (symbolTable.contains(ramAddress)) {
            intAddress = symbolTable.getAddress(ramAddress);
        } else {
            try {
                intAddress = Integer.parseInt(ramAddress);
            } catch(NumberFormatException e) {
                throw new IllegalArgumentException("""
                        Could not parse A-instruction cause address value:
                        %s is not in symbol table and also is not number!
                        """.formatted(ramAddress));
            }
        }

        // Return address and add leading zeros
        return Integer.toBinaryString((1 << 15) | intAddress).substring(1);
    }

    /**
     * compile the C-Instruction -> do some math operation, jump pc, save data to memory or register.
     *
     * @param instruction instruction to compile
     * @return compiled instruction as 15-right-most-bits.
     */
    private String compileCInstruction(Instruction instruction) {
        String operand = instruction.operand();
        if (operand.contains("A") && operand.contains("M")) {
            throw new IllegalArgumentException("""
                    This: %s is not valid instruction
                    there is no instruction that can operate on A-register and Memory at the same time!
                    """.formatted(instruction));
        }

        String aBit = operand.contains("M") ? "1" : "0";

        String dd = destTable.get(instruction.destination() == null ? "null" : instruction.destination());
        String cc = operand.contains("M") ? compTableMemory.get(operand) : compTableARegister.get(operand);
        String jj = jmpTable.get(instruction.jmp() == null ? "null" : instruction.jmp());

        return "11" + aBit + cc + dd + jj;
    }

}
