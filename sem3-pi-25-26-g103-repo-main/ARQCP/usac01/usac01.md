# Explanation of Assembly Code `encryptData`

## Objective
This function implements a **Caesar cipher** in RISC-V Assembly, shifting alphabetic characters by a given offset.

## Inputs & Outputs
- **Inputs:**
  - `a0`: Pointer to input string.
  - `a1`: Shift value (1 to 26).
  - `a2`: Pointer to output string.

- **Outputs:**
  - `a0 = 1` Success.
  - `a0 = 0` Failure.

## Initial Checks
- Ensure `a0` and `a2` are not null.
- Validate that `a1` is between 1 and 26.

## Logic Flow
1. **Loop (`loopStart`):**
   - Read a character.
   - If null terminator (`\0`), finish.

2. **Character Range Check:**
   - If between `'A'` and `'Z'` → Uppercase.
   - Else check `'a'` to `'z'` → Lowercase.
   - Otherwise → Fail.

3. **Caesar Cipher (`encryptChar`):**
   - Convert char to index (0-25).
   - Add shift.
   - Apply modulo 26.
   - Convert back to character.

4. **Write to output and move pointers.**

## Completion
- Add null terminator to output.
- Return success or failure.