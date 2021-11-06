## 第二章 布尔算法运算

### HalfAdder

这个需要实现的是一个半加器，先看一下真值表：

| 输入 |      | 输出  |      |
| ---- | ---- | ----- | ---- |
| a    | b    | carry | sum  |
| 0    | 0    | 0     | 0    |
| 0    | 1    | 0     | 1    |
| 1    | 0    | 0     | 1    |
| 1    | 1    | 1     | 0    |

单独看一下carry 和 a, b，会发现carry的结果其实就是 And(a, b) 的结果，而 sum 是 Xor(a, b)，所以这个半加器的实现如下：

```hdl
/**
 * Computes the sum of two bits.
 */

CHIP HalfAdder {
    IN a, b;    // 1-bit inputs
    OUT sum,    // Right bit of a + b 
        carry;  // Left bit of a + b

    PARTS:
    // Put you code here:
    And(a=a, b=b, out=carry);
    Xor(a=a, b=b, out=sum);
}
```



### FullAdder

实现一个全加器，真值表如下：

| a    | b    | c    | carry | sum  |
| ---- | ---- | ---- | ----- | ---- |
| 0    | 0    | 0    | 0     | 0    |
| 0    | 0    | 1    | 0     | 1    |
| 0    | 1    | 0    | 0     | 1    |
| 0    | 1    | 1    | 1     | 0    |
| 1    | 0    | 0    | 0     | 1    |
| 1    | 0    | 1    | 1     | 0    |
| 1    | 1    | 0    | 1     | 0    |
| 1    | 1    | 1    | 1     | 1    |

最简单的方法是根据真值表的结果，写出对应的逻辑表达式，然后用逻辑运算实现，但是这样比较麻烦，可以用先前实现的半加器来实现。具体的思路是，对于 sum 它最终的结果是最低有效位（LSB），所以可以先用一个半加器将 a 和 b 加起来得到一个 sum1，这个 sum1 是 a + b 的最低有效位，所以如果将它再和 c 相加得到的 sum 即为最终结果的最低有效位，同理 carry 也可以这样考虑，用最原始的竖式计算大概是下面这样子

![image-20211104103001383](../pics/image-20211104103001383.png)

```hdl
/**
 * Computes the sum of three bits.
 */

CHIP FullAdder {
    IN a, b, c;  // 1-bit inputs
    OUT sum,     // Right bit of a + b + c
        carry;   // Left bit of a + b + c

    PARTS:
    // Put you code here:
    HalfAdder(a=a, b=b, sum=sum1, carry=carry1);
    HalfAdder(a=c, b=sum1, sum=sum, carry=carry2);
    HalfAdder(a=carry1, b=carry2, sum=carry, carry=carry3);
}
```

### Add16

16位的加法器，利用前面实现的全加器，然后一位一位进行运算，最开始用一个半加器，因为刚开始最低位相加的时候没有三个数，只有两个数，之后每次都有一个 sum 和一个进位 carry，依次相加

```hdl
/**
 * Adds two 16-bit values.
 * The most significant carry bit is ignored.
 */

CHIP Add16 {
    IN a[16], b[16];
    OUT out[16];

    PARTS:
   // Put you code here:
   HalfAdder(a=a[0], b=b[0], sum=out[0], carry=c1);
   FullAdder(a=a[1], b=b[1], c=c1, sum=out[1], carry=c2);
   FullAdder(a=a[2], b=b[2], c=c2, sum=out[2], carry=c3);
   FullAdder(a=a[3], b=b[3], c=c3, sum=out[3], carry=c4);
   FullAdder(a=a[4], b=b[4], c=c4, sum=out[4], carry=c5);
   FullAdder(a=a[5], b=b[5], c=c5, sum=out[5], carry=c6);
   FullAdder(a=a[6], b=b[6], c=c6, sum=out[6], carry=c7);
   FullAdder(a=a[7], b=b[7], c=c7, sum=out[7], carry=c8);
   FullAdder(a=a[8], b=b[8], c=c8, sum=out[8], carry=c9);
   FullAdder(a=a[9], b=b[9], c=c9, sum=out[9], carry=c10);
   FullAdder(a=a[10], b=b[10], c=c10, sum=out[10], carry=c11);
   FullAdder(a=a[11], b=b[11], c=c11, sum=out[11], carry=c12);
   FullAdder(a=a[12], b=b[12], c=c12, sum=out[12], carry=c13);
   FullAdder(a=a[13], b=b[13], c=c13, sum=out[13], carry=c14);
   FullAdder(a=a[14], b=b[14], c=c14, sum=out[14], carry=c15);
   FullAdder(a=a[15], b=b[15], c=c15, sum=out[15], carry=c16);
}
```

## Inc16

16位的增量器，在原数的基础上加 1，可以用16位的加法器，把最低为设为 1 ，其他位设置为 0，这样加上原数就有两个数字了，可以使用16位的加法器，直接将 b 置为 1 的话是不对的

```hdl
/**
 * 16-bit incrementer:
 * out = in + 1 (arithmetic addition)
 */

CHIP Inc16 {
    IN in[16];
    OUT out[16];

    PARTS:
   // Put you code here:
   Add16(a=in, b[0]=true, b[1..15]=false, out=out);
}
```



### ALU

算数逻辑单元，这个应该是这章最难的一个，步骤较多，可以一步一步根据书上的提示来写。

首先把输入的 x 和 y 值根据提示处理一下，当 nx = 1 时，x 会取反，当 zx = 1 时，x 会置零，可以用第一章的 16 位选择器来实现这个处理操作，如下

```hdl
// 把x置0并取反
Mux16(a=x, b=false, sel=zx, out=x1);
Not16(in=x1, out=notx);
Mux16(a=x1, b=notx, sel=nx, out=x2);
```

同理，y 也可以进行这样的预处理，接下来是根据功能码 f，当 f = 1 时，x 和 y 进行 Add 相加操作，f = 0 时，x 和 y 进行 And 与操作，也是先分别计算出两个操作的值，然后利用一个选择器进行选择，注意进行操作的数为上面预处理过的数据，如下：

```hdl
// 根据功能码f，进行计算
And16(a=x2, b=y2, out=andxy);
Add16(a=x2, b=y2, out=addxy);
Mux16(a=andxy, b=addxy, sel=f, out=o1);
```

还有一个控制是 no，它是将输出取反，也是前面的思路，先算出取反的结果，然后利用选择器进行选择，如下：

```hdl
// 根据 no 来判断时候需要取反
Not16(in=o1, out=noto1);
Mux16(a=o1, b=noto1, sel=no, out=out, out=o2);
```

最后需要输出的两个是 zr 和 ng，zr 当输出的结果为 0 时，为true，否则为false，问题是如何判断一个 16 位的数字为 0？可以用 Or 运算，把16位依次进行 Or 运算，最后的结果即可判断是否为0，考虑第一章实现了一个多通道 Or8Way，这里是 16 位，所以可以另外写一个 O16Way 来进行辅助判断，

```hdl
/**
 * 16-way Or: 
 * out = (in[0] or in[1] or ... or in[15])
 */

CHIP Or16Way {
    IN in[16];
    OUT out;

    PARTS:
    Or8Way(in=in[0..7], out=o1);
    Or8Way(in=in[8..15], out=o2);
    Or(a=o1, b=o2, out=out);
}
```

注意这里输出的结果刚好和 zr 相反，需要再进行一下取反的操作，结果如下：

```hdl
Or16Way(in=o2, out=zr1);
Not(in = zr1, out = zr);
```

接着就是 ng 的判断，负数在补码存储中，最高位为 1，根据这个即可判断，注意的是不能直接在内部使用输出的结果，比如说，下面这样写会报错，可能是系统的限制，我也不太确定

```hdl
Or(a=o2[15], b=false, out=out);
```

正确做法是在外面重新写一个判断负值的芯片，如下：

```hdl
 /**
 * IsNeg gate:
 * out = 1 if (in[15] == 1 )
 *       0 otherwise
 */

CHIP IsNeg {
    IN in[16];
    OUT out;

    PARTS:
    // Put your code here:
    Or(a=in[15], b=false, out=out);
}
```

如此，完整的 ALU 芯片就写完了，完整代码如下：

```hdl
CHIP ALU {
    IN  
        x[16], y[16],  // 16-bit inputs        
        zx, // zero the x input?
        nx, // negate the x input?
        zy, // zero the y input?
        ny, // negate the y input?
        f,  // compute out = x + y (if 1) or x & y (if 0)
        no; // negate the out output?

    OUT 
        out[16], // 16-bit output
        zr, // 1 if (out == 0), 0 otherwise
        ng; // 1 if (out < 0),  0 otherwise

    PARTS:
   // Put you code here:
   // 把x置0并取反
   Mux16(a=x, b=false, sel=zx, out=x1);
   Not16(in=x1, out=notx);
   Mux16(a=x1, b=notx, sel=nx, out=x2);


   // 把y置0并取反
   Mux16(a=y, b=false, sel=zy, out=y1);
   Not16(in=y1, out=noty);
   Mux16(a=y1, b=noty, sel=ny, out=y2);

   // 实现 f 功能码
   And16(a=x2, b=y2, out=andxy);
   Add16(a=x2, b=y2, out=addxy);
   Mux16(a=andxy, b=addxy, sel=f, out=o1);

   Not16(in=o1, out=noto1);
   Mux16(a=o1, b=noto1, sel=no, out=out, out=o2);
   
   // 判断是否为 0
   Or16Way(in=o2, out=zr1);
   Not(in = zr1, out = zr);
   
   // 判断是否为负数
   IsNeg(in=o2, out=ng);
}
```

参考：[woai3c/nand2tetris: 计算机系统要素-从零开始构建现代计算机 (github.com)](https://github.com/woai3c/nand2tetris)