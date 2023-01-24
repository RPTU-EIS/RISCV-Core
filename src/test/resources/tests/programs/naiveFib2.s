main:
	addi	sp,sp,-16
    mv	s0,s0
	sw	ra,12(sp)
	sw	s0,8(sp)
	addi	s0,sp,16
	li	a0,6
	call	f
	mv	a5,a0
	mv	a0,a5
	lw	ra,12(sp)
	lw	s0,8(sp)
	addi	sp,sp,16
	jr	ra
f:
	addi	sp,sp,-32
	sw	ra,28(sp)
    mv	fp,fp
	sw	s0,24(sp)
	sw	s1,20(sp)
	addi	s0,sp,32
	sw	a0,-20(s0)
	lw	a5,-20(s0)
    nop
    nop
    nop
    done
