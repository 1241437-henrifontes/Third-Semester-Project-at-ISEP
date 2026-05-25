    .section .text
    .global cb_push_asm
cb_push_asm:
    lw t0, 4(a0)
    lw t1, 8(a0)

    bne t0, t1, not_full

    lw t2, 12(a0)
    addi t2, t2, 1
    rem t2, t2, t0
    sw t2, 12(a0)

    addi t1, t1, -1

not_full:
    lw t3, 16(a0)
    lw t4, 0(a0)
    slli t5, t3, 2
    add t4, t4, t5
    sw a1, 0(t4)

    addi t3, t3, 1
    rem t3, t3, t0
    sw t3, 16(a0)

    addi t1, t1, 1
    sw t1, 8(a0)

    li a0, 1
    ret
