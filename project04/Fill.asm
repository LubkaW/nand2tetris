// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.
(START)
@KBD
D=M
@1
M=D
@SCREEN
D=A
@0
M=D
(CHANGE)
@1
D=M
@SKIP
D;JEQ
D=0
D=!D
(SKIP)
@0
A=M
M=D
@0
M=M+1
@KBD
D=A
@0
D=M-D
@START
D;JEQ
@CHANGE
0;JMP


