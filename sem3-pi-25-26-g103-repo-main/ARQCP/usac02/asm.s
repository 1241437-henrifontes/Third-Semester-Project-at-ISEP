.section .text
.global decryptData

decryptData:
    beqz a0, fail
    beqz a2, fail

    li t0, 1
    blt a1, t0, fail
    li t0, 26
    bgt a1, t0, fail

loopStart:
    lb t1, 0(a0)
    beqz t1, done

    li t2, 'A'
    li t3, 'Z'

    blt t1, t2, fail
    bgt t1, t3, lowerCase
    j decryptChar

lowerCase:
    li t2, 'a'
    li t3, 'z'
    blt t1, t2, fail
    bgt t1, t3, fail

decryptChar:
    sub t4, t1, t2
    sub t4, t4, a1
    li t5, 26
    add t4, t4, t5
    rem t4, t4, t5
    add t4, t4, t2

    sb t4, 0(a2)

    addi a0, a0, 1
    addi a2, a2, 1
    j loopStart

done:
    sb zero, 0(a2)
    li a0, 1
    ret

fail:
    beqz a2, ret_fail
    sb zero, 0(a2)
ret_fail:
    li a0, 0
    ret
