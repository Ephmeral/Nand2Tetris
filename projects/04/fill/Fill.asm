// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

@SCREEN
D=A 		
@1
M=D 		// 地址 1 储存 SCREEN 的地址
(CHECK)

@KBD
D=M 		// 加载 RAM[24576] 的值
@BLACK
D;JNE 		// if D > 0 goto BLACK

@WHITE
D;JEQ 		// if D = 0 goto WHITE

(BLACK)
@1
D=A 		// D = 屏幕指针的地址
@24575
D=D-A 		// D = i - 24575(最后一个像素点位置)
@END
D;JGT 		// if (i-24575) > 0 goto END

@1
D=M 		// 把地址1表示的屏幕地址储存到 D 寄存器中
A=M 		// 当前地址推入A中
M=-1 		// -1的补码为11111111111111111111，即16位全部为黑的

@1
M=D+1 		// 当前地址+1
@BLACK
0;JMP

(WHITE)
@1
D=A 		// D = 屏幕指针的地址
@SCREEN
D=M
@1
D=D-M 		// D = i - SCREEN(最后一个像素点位置)
@END
D;JLT 		// if (i-24575) < 0 goto END

@1
D=M 		// 缓存当前地址
A=M 		// 当前地址推入A中
M=0 		// 地址对应部分，清空

@1
M=D-1 		// 当前地址-1
@WHITE
0;JMP

(END)

@CHECK
0;JMP 		// 无限循环