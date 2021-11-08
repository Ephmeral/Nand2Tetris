@7
D=A
@SP
A=M 		// 写入栈中 M[A]
M=D
@SP
M=M+1 		// 栈指针 + 1

@8
D=A
@SP
A=M 		// 写入栈中
M=D
@SP
M=M+1 		// 栈指针 + 1


// 进行add操作

@SP
AM=M-1 		// 栈指针-1
D=M 		// 取出栈顶元素
A=A-1
M=M+D