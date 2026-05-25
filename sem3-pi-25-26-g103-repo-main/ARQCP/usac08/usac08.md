# Explanation of Assembly Function `sort_array`

## Objective
This function sorts an integer array either in ascending or descending order depending on the `order` parameter. It uses a nested loop structure (similar to bubble sort) to repeatedly compare and swap adjacent elements. If the array length is less than or equal to zero, the function fails and returns `0`.

## Inputs and Outputs
- **Inputs:**
    - `a0`: Pointer to the array (`int* vec`)
    - `a1`: Array length (`int length`)
    - `a2`: Order flag (`char order`)
        - `1` → ascending order
        - `0` → descending order

- **Outputs:**
    - `a0 = 1` indicates success (array sorted)
    - `a0 = 0` indicates failure (length ≤ 0)

## Initial Checks
- If `length < 0` or `length == 0`, branch to `empty` and return `0`.

## Logic Flow
1. **Setup:**
    - Save registers on the stack.
    - Copy arguments into saved registers (`s0` = vec, `s1` = length, `s2` = order).
    - Initialize outer loop counter `t0 = 0`.

2. **Outer Loop (`firstloop`):**
    - Compute `t1 = length - 1`.
    - If `t0 < t1`, enter inner loop; otherwise, finish.

3. **Inner Loop (`secondloop`):**
    - Initialize inner counter `t2 = 0`.
    - Compare `t2` with `(t1 - t0)` to control iterations.
    - Load adjacent elements:
        - `t5 = vec[t2]`
        - `t6 = vec[t2+1]`

4. **Comparison:**
    - If `order == 1` (ascending):
        - Swap if `t5 > t6`.
    - If `order == 0` (descending):
        - Swap if `t5 < t6`.

5. **Swap:**
    - Exchange values in memory when condition is met.

6. **Loop Progression:**
    - Increment `t2` for inner loop.
    - When inner loop ends, increment `t0` for outer loop.
    - Repeat until all passes are complete.

7. **Success Path (`end`):**
    - Restore registers.
    - Return `1` in `a0`.

8. **Failure Path (`empty`):**
    - Restore registers.
    - Return `0` in `a0`.

## Completion
- The function implements a bubble sort algorithm in RISC‑V assembly.
- It handles both ascending and descending order based on the `order` flag.
- It ensures proper stack discipline by saving and restoring callee-saved registers.
- Returns `1` when sorting is successful, `0` when the array length is invalid.
