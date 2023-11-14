label:  
main:
    addi x1, x0, 10
    addi x2, x0, 20
    addi x3, x0, 20
    sw x1, 0(x3)
    sw x2, 4(x3)
    lw x5, 0(x3)
mylabel:
    lw x6, 4(x3)
    nop
    addi x6, x6, -1
    beq  x5, x6, exit
    sw x6, 4(x3)
    jal  x0, mylabel
exit:
    nop
    nop

