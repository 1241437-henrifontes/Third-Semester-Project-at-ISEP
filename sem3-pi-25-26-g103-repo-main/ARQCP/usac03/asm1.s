    .section .data

    unit_label:
        .asciz "&unit:"
    value_label:
        .asciz "&value:"

    .section .text
    .globl extract_data

extract_data:
    addi sp, sp, -36
    sw   ra, 32(sp)
    sw   s0, 28(sp)
    sw   s1, 24(sp)
    sw   s2, 20(sp)
    sw   s3, 16(sp)
    sw   s4, 12(sp)

    mv s0, a0
    mv s1, a1
    mv s2, a2
    mv s3, a3

    mv a0, s0
    mv a1, s1
    call str_find
    beqz a0, not_found

    mv s4, a0

    beq s4, s0, token_ok
    addi t1, s4, -1
    lbu  t2, 0(t1)
    li   t3, '#'
    bne  t2, t3, not_found

token_ok:
    mv a0, s1
    call str_len
    mv t4, a0

    add  t5, s4, t4

    lbu t6, 0(t5)
    li  t1, '&'
    bne t6, t1, not_found

    mv a0, t5
    la a1, unit_label
    call str_find
    beqz a0, not_found
    addi a0, a0, 6
    mv a1, s2
    call str_copy_until_delim

    mv a0, t5
    la a1, value_label
    call str_find
    beqz a0, not_found
    addi a0, a0, 7
    call str_to_int
    sw a0, 0(s3)

    li a0, 1
    j end_func

not_found:
    sb zero, 0(s2)
    sw zero, 0(s3)
    li a0, 0

end_func:
    lw ra, 32(sp)
    lw s0, 28(sp)
    lw s1, 24(sp)
    lw s2, 20(sp)
    lw s3, 16(sp)
    lw s4, 12(sp)
    addi sp, sp, 36
    ret
