main:
    nop
    nop
    nop
    addi x1, x0, 5
    addi x2, x0, 8
    nop
    nop
    #sw x1,0(x2)
    nop
    lw x3,0(x2)
    bnez x1,.L2
    addi x6, x0, 156
    addi x6, x0, 12
    addi x6, x0, 15
    addi x6, x0, 19
    addi x6, x0, 65
    addi x6, x0, 23
.L2:
    add x1, x1, x2
    add x2, x1, x2
    nop
    nop
    add x3, x2, x2
