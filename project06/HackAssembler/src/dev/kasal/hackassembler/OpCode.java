package dev.kasal.hackassembler;

public enum OpCode {
    A("0"), // A - instruction
    C("1");  // C - instruction

    private final String firstBit;

    OpCode(String firstBit) {
        this.firstBit = firstBit;
    }

    public String getFirstBit() {
        return this.firstBit;
    }

}
