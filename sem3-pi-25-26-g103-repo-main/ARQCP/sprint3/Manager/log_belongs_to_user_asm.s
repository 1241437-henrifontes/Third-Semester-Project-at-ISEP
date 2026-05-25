    .section .text
    .global log_belongs_to_user_asm
log_belongs_to_user_asm:
    beqz a0, ret0
    beqz a1, ret0

    addi t0, a0, 4

loop:
    lbu t1, 0(t0)
    lbu t2, 0(a1)
    bne t1, t2, noteq
    beqz t1, eq
    addi t0, t0, 1
    addi a1, a1, 1
    j loop

noteq:
    li a0, 0
    ret

eq:
    li a0, 1
    ret

ret0:
    li a0, 0
    ret
