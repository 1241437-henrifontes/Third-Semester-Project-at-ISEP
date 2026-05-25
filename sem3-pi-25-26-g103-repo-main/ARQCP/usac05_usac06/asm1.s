.section .text
.global dequeueValue

# int dequeueValue(int* buffer, int length, int* nelem, int* tail, int* head, int* value);

dequeueValue:
    lw t4, 0(a2)        # t4 = *nelem (número de elementos na fila)
    lw t5, 0(a3)        # t5 = *tail (índice do primeiro elemento)

    beqz t4, fail       # Se nelem == 0, a fila está vazia → falha
    bge t5, t4, fail    # Se tail >= nelem, índice inválido → falha

    slli t0, t5, 2      # t0 = tail * 4 (índice em bytes)
    add  t0, t0, a0     # t0 = endereço de buffer[tail]
    lw   t2, 0(t0)      # t2 = buffer[tail]
    sw   t2, 0(a5)      # *value = buffer[tail]

done:
    slli t6, t5, 2
    add t6, t6, a0     
    sw x0, 0(t6)        
    
    addi t4, t4, -1     # nelem--
    sw t4, 0(a2)        # atualiza *nelem

    lw t2, 0(a4)
    beqz t2, ret

    addi t2, t2, -1
    sw t2, 0(a4)

ret:
    li a0, 1            # retorno de sucesso
    ret

fail:
    li a0, 0            # retorno de falha
    ret