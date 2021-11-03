# 计算机系统要素 

## 第一章 布尔逻辑

项目提供了最原始的 Nand 门，也就是与非门，然后通过与非门一步步构建各种门电路。各个门电路的细节可以参考书本，这里简单的记录一下我自己实现的思路，仅供参考

### Not

书上也提示了用一个 Nand 门实现，可以想到对一个数自身的与非就可以得到相反值，即 Not(x) = Nand(x, x)

```hdl
/**
 * Not gate:
 * out = not in
 */

CHIP Not {
    IN in;
    OUT out;

    PARTS:
    // Put your code here:
    Nand(a=in, b=in, out=out);
}
```



### And 

And 门可以用一个 Not 和 Nand 即可实现，即 And(x,y) = Not(Nand(x, y))

```hdl
/**
 * And gate: 
 * out = 1 if (a == 1 and b == 1)
 *       0 otherwise
 */

CHIP And {
    IN a, b;
    OUT out;

    PARTS:
    // Put your code here:
    Nand(a=a, b=b, out=w);
    Not(in=w, out=out);
}
```



### Or

书上第10页其实已经写了一种实现方法，利用摩根定律，Or(x, y) = Nand(Nand(x, x), Nand(y, y))

```hdl
/**
 * Or gate:
 * out = 1 if (a == 1 or b == 1)
 *       0 otherwise
 */

CHIP Or {
    IN a, b;
    OUT out;

    PARTS:
    // Put your code here:
    Nand(a=a, b=a, out=w1);
    Nand(a=b, b=b, out=w2);
    Nand(a=w1, b=w2, out=out);
}
```



### Xor

这个在书上15页也写了一种实现方法，Xor(x, y) = Or(And(x, Not(y)), And(Not(x), y))

```hdl
/**
 * Exclusive-or gate:
 * out = not (a == b)
 */

CHIP Xor {
    IN a, b;
    OUT out;

    PARTS:
    // Put your code here:
    Not(in=a, out=nota);
    Not(in=b, out=notb);
    And(a=a, b=notb, out=w1);
    And(a=nota, b=b, out=w2);
    Or(a=w1, b=w2, out=out);
}
```



### Mux

基本的门电路实现后，就是稍微复杂点的符合电路，Mux 是一个选择器，根据 sel 的值来确定输出的值，实现思路：

可以想看一下真值表

| sel  | out  |
| ---- | ---- |
| 0    | a    |
| 1    | b    |

sel 为 0 的时候，输出 a，sel 为 1 的时候，输出 b，则最终结果可用 Or(And(a, Not(sel)), And(b, sel)) 表示

```hdl
/** 
 * Multiplexor:
 * out = a if sel == 0
 *       b otherwise
 */

CHIP Mux {
    IN a, b, sel;
    OUT out;

    PARTS:
    // Put your code here:
    Not(in=sel, out=notsel);
    And(a=a, b=notsel, out=w1);
    And(a=b, b=sel, out=w2);
    Or(a=w1, b=w2, out=out);
}
```



### DMux

这是一个反向的选择器，和 Mux 功能相反。因为输出的结果有两个，可以分别考虑，当 sel 为 0 的时候，a 接口输出 in，sel 为 1 的时候，b 接口输出 in，所以 a = And(in, Not(sel))，b = And(in, sel)

```
/**
 * Demultiplexor:
 * {a, b} = {in, 0} if sel == 0
 *          {0, in} if sel == 1
 */

CHIP DMux {
    IN in, sel;
    OUT a, b;

    PARTS:
    // Put your code here:
    Not(in=sel, out=notsel);
    And(a=in, b=notsel, out=a);
    And(a=in, b=sel, out=b);
}
```



### Not16

之前是一位的门电路，后面这几个都是多位的，其实也比较容易想到，把每一位单独考虑就行，只不过写起来麻烦点

```hdl
/**
 * 16-bit Not:
 * for i=0..15: out[i] = not in[i]
 */

CHIP Not16 {
    IN in[16];
    OUT out[16];

    PARTS:
    // Put your code here:
    Not(in=in[0], out=out[0]);
    Not(in=in[1], out=out[1]);
    Not(in=in[2], out=out[2]);
    Not(in=in[3], out=out[3]);
    Not(in=in[4], out=out[4]);
    Not(in=in[5], out=out[5]);
    Not(in=in[6], out=out[6]);
    Not(in=in[7], out=out[7]);
    Not(in=in[8], out=out[8]);
    Not(in=in[9], out=out[9]);
    Not(in=in[10], out=out[10]);
    Not(in=in[11], out=out[11]);
    Not(in=in[12], out=out[12]);
    Not(in=in[13], out=out[13]);
    Not(in=in[14], out=out[14]);
    Not(in=in[15], out=out[15]);
}
```



### And16

```hdl
/**
 * 16-bit bitwise And:
 * for i = 0..15: out[i] = (a[i] and b[i])
 */

CHIP And16 {
    IN a[16], b[16];
    OUT out[16];

    PARTS:
    // Put your code here:
    And(a=a[0], b=b[0], out=out[0]);
    And(a=a[1], b=b[1], out=out[1]);
    And(a=a[2], b=b[2], out=out[2]);
    And(a=a[3], b=b[3], out=out[3]);
    And(a=a[4], b=b[4], out=out[4]);
    And(a=a[5], b=b[5], out=out[5]);
    And(a=a[6], b=b[6], out=out[6]);
    And(a=a[7], b=b[7], out=out[7]);
    And(a=a[8], b=b[8], out=out[8]);
    And(a=a[9], b=b[9], out=out[9]);
    And(a=a[10], b=b[10], out=out[10]);
    And(a=a[11], b=b[11], out=out[11]);
    And(a=a[12], b=b[12], out=out[12]);
    And(a=a[13], b=b[13], out=out[13]);
    And(a=a[14], b=b[14], out=out[14]);
    And(a=a[15], b=b[15], out=out[15]);
}
```



### Or16

```hdl
/**
 * 16-bit bitwise Or:
 * for i = 0..15 out[i] = (a[i] or b[i])
 */

CHIP Or16 {
    IN a[16], b[16];
    OUT out[16];

    PARTS:
    // Put your code here:
    Or(a=a[0], b=b[0], out=out[0]);
    Or(a=a[1], b=b[1], out=out[1]);
    Or(a=a[2], b=b[2], out=out[2]);
    Or(a=a[3], b=b[3], out=out[3]);
    Or(a=a[4], b=b[4], out=out[4]);
    Or(a=a[5], b=b[5], out=out[5]);
    Or(a=a[6], b=b[6], out=out[6]);
    Or(a=a[7], b=b[7], out=out[7]);
    Or(a=a[8], b=b[8], out=out[8]);
    Or(a=a[9], b=b[9], out=out[9]);
    Or(a=a[10], b=b[10], out=out[10]);
    Or(a=a[11], b=b[11], out=out[11]);
    Or(a=a[12], b=b[12], out=out[12]);
    Or(a=a[13], b=b[13], out=out[13]);
    Or(a=a[14], b=b[14], out=out[14]);
    Or(a=a[15], b=b[15], out=out[15]);
}
```



### Mux16

```hdl
/**
 * 16-bit multiplexor: 
 * for i = 0..15 out[i] = a[i] if sel == 0 
 *                        b[i] if sel == 1
 */

CHIP Mux16 {
    IN a[16], b[16], sel;
    OUT out[16];

    PARTS:
    // Put your code here:
    Mux(a=a[0], b=b[0], sel=sel, out=out[0]);
    Mux(a=a[1], b=b[1], sel=sel, out=out[1]);
    Mux(a=a[2], b=b[2], sel=sel, out=out[2]);
    Mux(a=a[3], b=b[3], sel=sel, out=out[3]);
    Mux(a=a[4], b=b[4], sel=sel, out=out[4]);
    Mux(a=a[5], b=b[5], sel=sel, out=out[5]);
    Mux(a=a[6], b=b[6], sel=sel, out=out[6]);
    Mux(a=a[7], b=b[7], sel=sel, out=out[7]);
    Mux(a=a[8], b=b[8], sel=sel, out=out[8]);
    Mux(a=a[9], b=b[9], sel=sel, out=out[9]);
    Mux(a=a[10], b=b[10], sel=sel, out=out[10]);
    Mux(a=a[11], b=b[11], sel=sel, out=out[11]);
    Mux(a=a[12], b=b[12], sel=sel, out=out[12]);
    Mux(a=a[13], b=b[13], sel=sel, out=out[13]);
    Mux(a=a[14], b=b[14], sel=sel, out=out[14]);
    Mux(a=a[15], b=b[15], sel=sel, out=out[15]);
}
```



### Or8Way

下面几个是多通道逻辑门，比前面的复杂一点，Or8Way 这个门电路就是把所有的输出进行或运算，最终得到一个结果输出，可以两两进行或运算，或者一个一个进行或运算

```hdl
/**
 * 8-way Or: 
 * out = (in[0] or in[1] or ... or in[7])
 */

CHIP Or8Way {
    IN in[8];
    OUT out;

    PARTS:
    // Put your code here:
    Or(a=in[0], b=in[7], out=w1);
    Or(a=in[1], b=in[6], out=w2);
    Or(a=in[2], b=in[5], out=w3);
    Or(a=in[3], b=in[4], out=w4);
    Or(a=w1, b=w4, out=r1);
    Or(a=w2, b=w3, out=r2);
    Or(a=r1, b=r2, out=out);
    
    /* 其实感觉差不多
    Or(a=in[0], b=in[1], out=w1);
    Or(a=w1, b=in[2], out=w2);
    Or(a=w2, b=in[3], out=w3);
    Or(a=w3, b=in[4], out=w4);
    Or(a=w4, b=in[5], out=w5);
    Or(a=w5, b=in[6], out=w6);
    Or(a=w6, b=in[7], out=w7);
    Or(a=w7, b=in[8], out=out);
    */
}
```



### Mux4Way16

先看一下真值表：

| sel[1] | sel[0] | out  |
| ------ | ------ | ---- |
| 0      | 0      | a    |
| 0      | 1      | b    |
| 1      | 0      | c    |
| 1      | 1      | d    |

可以两两一组考虑，对于 a 和 b，sel[1] 均为0，即考虑 sel[0]，sel[0] 为 0 输出 a，为 1 输出 b，而前面实现的 Mux16（因为这里要求的是 16 位，所以不能用 Mux）刚好可以实现这个需求，从两个输入当中，根据一个判断位，得到最后的输出结果；同理对于 c 和 d，也可以得到一个输出，再把 a 和 b 的输出看成一个输入， c 和 d 的输出看成一个输入，sel[1] 作为选择位，即可得到最终的结果

```hdl
/**
 * 4-way 16-bit multiplexor:
 * out = a if sel == 00
 *       b if sel == 01
 *       c if sel == 10
 *       d if sel == 11
 */

CHIP Mux4Way16 {
    IN a[16], b[16], c[16], d[16], sel[2];
    OUT out[16];

    PARTS:
    // Put your code here:
    Mux16(a=a, b=b, sel=sel[0], out=opt1);
    Mux16(a=c, b=d, sel=sel[0], out=opt2);
    Mux16(a=opt1, b=opt2, sel=sel[1], out=out);
}
```



### Mux8Way16

上面实现了 4 通道的选择，这里则是 8 个输入，也是同样的思路，不过这次可以直接使用已经实现的 Mux4Way16，即先对 a, b, c, d 进行 Mux4Way16，然后是 e, f, g, h 进行 Mux4Way16，得到两个输出后，再用 Mux16 进行选择即可，当然也可以两两一组用 Mux16 来实现

这里要注意的就是提供的 sel 是 3 位，如果要取出前两位，可以用 sel[0..1] 这样的写法

```hdl
/**
 * 8-way 16-bit multiplexor:
 * out = a if sel == 000
 *       b if sel == 001
 *       etc.
 *       h if sel == 111
 */

CHIP Mux8Way16 {
    IN a[16], b[16], c[16], d[16],
       e[16], f[16], g[16], h[16],
       sel[3];
    OUT out[16];

    PARTS:
    // Put your code here:
    Mux4Way16(a=a, b=b, c=c, d=d, sel=sel[0..1], out=opt1);
    Mux4Way16(a=e, b=f, c=g, d=h, sel=sel[0..1], out=opt2);
    Mux16(a=opt1, b=opt2, sel=sel[2], out=out);
    
    /* 较为繁琐的实现方法
    PARTS:
    // Put your code here:
    Mux16(a=a, b=b, sel=sel[0], out=opt1);
    Mux16(a=c, b=d, sel=sel[0], out=opt2);
    Mux16(a=e, b=f, sel=sel[0], out=opt3);
    Mux16(a=g, b=h, sel=sel[0], out=opt4);
    Mux16(a=opt1, b=opt2, sel=sel[1], out=res1);
    Mux16(a=opt3, b=opt4, sel=sel[1], out=res2);
    Mux16(a=res1, b=res2, sel=sel[2], out=out);
    */
}
```



### DMux4Way

这是一个 4 通道的反向选择器，先看一下真值表：

| sel[1] | sel[0] | a    | b    | c    | d    |
| ------ | ------ | ---- | ---- | ---- | ---- |
| 0      | 0      | in   | 0    | 0    | 0    |
| 0      | 1      | 0    | in   | 0    | 0    |
| 1      | 0      | 0    | 0    | in   | 0    |
| 1      | 1      | 0    | 0    | 0    | in   |

先以 sel[0] 为选择位进行考虑，当 sel[0] = 0 时，输出 in 的可能为 a 或者 c，sel[0] = 1 的时候，输出 in 的可能为 b 或者 d，先用 DMux 对 sel[0] 进行判断输出，得到 o1 和 o2，然后对于 o1 来说，又可以作为一个输入，此时以 sel[1] 为选择位，这样就可以区分出输出 in 的是 a 还是 c，同理 o2 也是如此，内部大概就是下面这种结构（画的比较丑）

<img src="..\pics\image-20211103151252589.png" alt="image-20211103151252589" style="zoom:67%;" />

```hdl
CHIP DMux4Way {
    IN in, sel[2];
    OUT a, b, c, d;

    PARTS:
    // Put your code here:
    DMux(in=in, sel=sel[0], a=o1, b=o2);
    DMux(in=o1, sel=sel[1], a=a, b=c);
    DMux(in=o2, sel=sel[1], a=b, b=d);
}
```



### DMux8Way

这个的思路，和上面有点类似，可以先用一个 4 通道反向选择器，得到四个输出，然后用最后一个选择位，对每个输出进行选择，即可实现 8 通道的反向选择器

```hdl
/**
 * 8-way demultiplexor:
 * {a, b, c, d, e, f, g, h} = {in, 0, 0, 0, 0, 0, 0, 0} if sel == 000
 *                            {0, in, 0, 0, 0, 0, 0, 0} if sel == 001
 *                            etc.
 *                            {0, 0, 0, 0, 0, 0, 0, in} if sel == 111
 */

CHIP DMux8Way {
    IN in, sel[3];
    OUT a, b, c, d, e, f, g, h;

    PARTS:
    // Put your code here:
    DMux4Way(in=in, sel=sel[0..1], a=o1, b=o2, c=o3, d=o4);
    DMux(in=o1, sel=sel[2], a=a, b=e);
    DMux(in=o2, sel=sel[2], a=b, b=f);
    DMux(in=o3, sel=sel[2], a=c, b=g);
    DMux(in=o4, sel=sel[2], a=d, b=h);
}
```



自此，第一章的门电路就都实现了，可以看到就是每个门电路都由前面的经过一系列的组合构成，由简单到复杂。最开始有的地方我也卡了挺久，但是仔细想想发现还是可以解决的，争取把后面的项目都做一下