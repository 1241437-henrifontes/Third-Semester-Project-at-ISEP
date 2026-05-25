    .section .text
    .global format_command

format_command:
    beqz a0, fail
    beqz a2, fail

    mv   t0, a0

1:  lbu  t1, 0(t0)
    beqz t1, fail
    li   t2, 32
    beq  t1, t2, 2f
    j    3f

2:  addi t0, t0, 1
    j    1b

3:
    mv   a3, t0
    mv   a4, a3

4:  lbu  t1, 0(t0)
    beqz t1, 5f
    li   t2, 32
    beq  t1, t2, 6f
    mv   a4, t0

6:  addi t0, t0, 1
    j    4b

5:
    blt  a4, a3, fail

    sub  t3, a4, a3
    addi t3, t3, 1

    li   t2, 3
    beq  t3, t2, check_GTH
    li   t2, 2
    beq  t3, t2, check_2
    j    fail

check_GTH:
    lbu  t4, 0(a3)
    lbu  t5, 1(a3)
    lbu  t6, 2(a3)

    li   t2, 'a'
    li   t3, 'z'
    blt  t4, t2, 7f
    blt  t3, t4, 7f
    addi t4, t4, -32
7:
    li   t2, 'a'
    li   t3, 'z'
    blt  t5, t2, 8f
    blt  t3, t5, 8f
    addi t5, t5, -32
8:
    li   t2, 'a'
    li   t3, 'z'
    blt  t6, t2, 9f
    blt  t3, t6, 9f
    addi t6, t6, -32
9:
    li   t2, 'G'
    li   t3, 'T'
    li   t0, 'H'
    bne  t4, t2, fail
    bne  t5, t3, fail
    bne  t6, t0, fail

    sb   t2, 0(a2)
    sb   t3, 1(a2)
    sb   t0, 2(a2)
    sb   x0, 3(a2)
    li   a0, 1
    ret

check_2:
    lbu  t4, 0(a3)
    lbu  t5, 1(a3)
    li   t2, 'a'
    li   t3, 'z'
    blt  t4, t2, 10f
    blt  t3, t4, 10f
    addi t4, t4, -32
10:
    li   t2, 'a'
    li   t3, 'z'
    blt  t5, t2, 11f
    blt  t3, t5, 11f
    addi t5, t5, -32
11:
    li   t0, 'R'
    li   t1, 'E'
    li   t2, 'B'
    beq  t4, t0, is_R
    li   t3, 'Y'
    beq  t4, t3, is_Y
    li   t3, 'G'
    beq  t4, t3, is_G
    j    fail

is_R:
    beq  t5, t1, build_cmd_two
    beq  t5, t2, build_cmd_two
    j    fail

is_Y:
    beq  t5, t1, build_cmd_two
    j    fail

is_G:
    beq  t5, t1, build_cmd_two
    j    fail

build_cmd_two:
    blt  a1, x0, fail
    li   t1, 100
    bge  a1, t1, fail

    sb   t4, 0(a2)
    sb   t5, 1(a2)
    li   t0, ','
    sb   t0, 2(a2)

    li   t3, 10
    div  t4, a1, t3
    rem  t5, a1, t3

    li   t6, '0'
    add  t4, t4, t6
    add  t5, t5, t6

    sb   t4, 3(a2)
    sb   t5, 4(a2)
    sb   x0, 5(a2)

    li   a0, 1
    ret

fail:
    sb   x0, 0(a2)
    li   a0, 0
    ret
