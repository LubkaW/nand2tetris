package dev.kasal.hackassembler;

/**
 * Base structure to define parsed instruction from the .asm file.
 *
 * @param opCode if instruction is A or C type.
 * @param operand first operand, in terms of A-Instruction defines the address, in terms of C-Instruction defines operation
 * @param destination destination of the compute result
 * @param jmp jmp instruction
 */
public record Instruction(OpCode opCode, String operand, String destination, String jmp) {

    public Instruction(OpCode opCode, String operand) {
        this (opCode, operand, null, null);
    }

    public Instruction(OpCode opCode, String operand, String jmp) {
        this(opCode, operand, null, jmp);
    }

}
