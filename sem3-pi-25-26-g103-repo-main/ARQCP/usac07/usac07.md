# Explanation of Assembly Function `move_n_to_array`

## Objective
This function removes the `n` oldest elements from a circular buffer and copies them into a destination array. It updates the buffer's `tail` and `nelem` pointers accordingly. If there are fewer than `n` elements available, the function fails and returns `0`.

## Inputs and Outputs
- **Inputs:**
    - `a0`: Pointer to the circular buffer (`int* buffer`)
    - `a1`: Buffer length (`int length`)
    - `a2`: Pointer to the number of elements currently in the buffer (`int* nelem`)
    - `a3`: Pointer to the tail index (`int* tail`)
    - `a4`: Pointer to the head index (`int* head`) — not used in this function
    - `a5`: Number of elements to remove (`int n`)
    - `a6`: Pointer to the destination array (`int* array`)

- **Outputs:**
    - `a0 = 1` indicates success (n elements moved)
    - `a0 = 0` indicates failure (not enough elements)

## Initial Checks
- Load `*nelem` and compare with `n`.
- If `*nelem < n`, skip the loop and return `0`.

## Logic Flow
1. **Initialize Loop Counter:**
    - Set `i = 0` to begin iteration over `n` elements.

2. **Loop Until `i == n`:**
    - For each iteration:
        - Load `*tail` to get the index of the oldest element.
        - Compute `buffer[tail]` and load the value.
        - Compute `array[i]` and store the value.
        - Update `*tail = (tail + 1) % length` using `rem`.
        - Decrement `*nelem`.

3. **Update Pointers:**
    - After each iteration, increment `i`.
    - Continue until `i == n`.

4. **Success Path:**
    - After moving all `n` elements, return `1` in `a0`.

5. **Failure Path:**
    - If `*nelem < n`, return `0` in `a0` and leave `array` unchanged.

## Completion
- The function preserves the circular buffer structure by wrapping `tail` using modulo (`rem`).
- It ensures that `*nelem` reflects the new count after removal.
- The destination array receives the values in order.
- The function uses only valid RISC‑V temporaries (`t0`–`t6`) and saves/restores callee-saved registers (`s0`–`s3`, `ra`).
