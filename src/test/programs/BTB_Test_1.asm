0   00000013    addi x0, x0, 0
4   00000013    addi x0, x0, 0
8   00900113    addi x2, x0, 9
12  00a00313    addi x6, x0, 10 // x6 = 10
16  01400293    addi x5, x0, 20 // x5 = 20, it's the iterator
20  00028a63    beq x5, x0, 20  // while(x5>0)
24  0062d463    bge x5, x6, 8       // if(x5<10)
28  00140413    addi x8, x8, 1          // Increment x8
32  fff28293    addi x5, x5, -1     // Decrement x5             
36  ff1ff06f    jal x0, -16         // Go to while(x5>0) (next iteration)
40  00240663    beq x8, x2, 12  // if(x8 != 9)
44  00000513    addi x10, x0, 0     // Test Failed!
48  0080006F    jal x0, 8           // Go to NOPs
52  00100513    addi x10, x0, 1 // else: Test Succeeds!
56  00000013    addi x0, x0, 0
60  00000013    addi x0, x0, 0
64  00000013    addi x0, x0, 0
