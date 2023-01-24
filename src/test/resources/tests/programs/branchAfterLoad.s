main:
    li x1, 4
    li a5, 10    
    sw x1, 100(zero)
    lw x2, 100(zero)
    bnez x2, .L2
    li a5, 1
    j .L3
.L2:
    mv a0, a5
    nop
    nop
.L3:
    nop
    nop
    done
