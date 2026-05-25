# Explanation of Assembly Function `decryptData`

## Objective
This function implements a **Caesar cipher decryption** in RISC-V Assembly. It shifts alphabetic characters backward by a given offset using subtraction and modulo arithmetic.

## Inputs and Outputs
- **Inputs:**
  - `a0`: Pointer to the input string.
  - `a1`: Shift value (between 1 and 26).
  - `a2`: Pointer to the output string.

- **Outputs:**
  - `a0 = 1` indicates success.
  - `a0 = 0` indicates failure.

## Initial Checks
- Verify that `a0` (input string) and `a2` (output string) are not null.
- Ensure that the shift value `a1` is within the range 1 to 26.

## Logic Flow
1. **Loop Start:**
   - Read a character from the input string.
   - If the character is the null terminator (`\0`), end the process.

2. **Character Range Validation:**
   - If the character is between `'A'` and `'Z'`, process as uppercase.
   - Otherwise, check if it is between `'a'` and `'z'` for lowercase.
   - If it does not fall into these ranges, return failure.

3. **Decryption Process:**
   - Convert the character to an index (0–25).
   - Subtract the shift value.
   - Add 26 to handle negative results.
   - Apply modulo 26 to wrap around.
   - Convert back to the corresponding character.

4. **Write to Output:**
   - Store the decrypted character in the output string.
   - Move both pointers forward and repeat until the end of the string.

## Completion
- Append a null terminator to the output string.
- Return 1 for success or 0 for failure.
