    .section .text
    .global str_copy_until_delim

str_copy_until_delim:
    addi sp, sp, -8
    sw   ra, 4(sp)

copy_loop:
    lbu t0, 0(a0)
    beqz t0, end_copy
    li  t1, '&'
    beq t0, t1, end_copy
    li  t1, '#'
    beq t0, t1, end_copy

    sb  t0, 0(a1)
    addi a0, a0, 1
    addi a1, a1, 1
    j    copy_loop

end_copy:
    sb  zero, 0(a1)
    lw  ra, 4(sp)
    addi sp, sp, 8
    ret
