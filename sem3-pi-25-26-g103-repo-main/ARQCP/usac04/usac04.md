# Explanation of Assembly Function `format_command`

## Objective
This function normalizes and formats a command string in RISC‑V Assembly. It trims leading/trailing spaces from the input op, capitalizes the trimmed text, and then:
- If the command is GTH (exactly three letters), writes "GTH" to the output cmd.
- If the command is one of RE, YE, GE, RB (exactly two letters), writes "CMD,xx" to cmd, where xx is the two‑digit, zero‑padded representation of n.
- If the command is invalid or n is out of range for two‑letter commands, it clears cmd to an empty string and signals failure.

## Inputs and Outputs
- **Inputs:**
    - `a0`: Pointer to the input command string (op).
    - `a1`: Integer value n to be formatted as two digits (used only for 2‑letter commands).
    - `a2`: Pointer to the output string buffer (cmd).
    - `a3`: Pointer to the integer where the value is going to be placed.

- **Outputs:**
    - `a0 = 1` indicates success.
    - `a0 = 0` indicates failure.

## Initial Checks
- Verify that a0 (input command) and a2 (output buffer) are not null.
- Perform left trim and right trim on op. If the trimmed length is not 2 or 3, fail.
- For the 3‑letter case, the uppercase command must be exactly GTH.
- For the 2‑letter case, the uppercase command must be one of RE, YE, GE, or RB.
- For 2‑letter commands, ensure 0 ≤ n ≤ 99; otherwise, fail.

## Logic Flow
1. **Trim Left:**
   - Advance a cursor over spaces (' ' / ASCII 32) until the first non‑space or '\0'.
   - If '\0' is reached, fail (empty after trim).
2. **Trim Right:**
   - From the first non‑space, scan forward to '\0', tracking the last non‑space character.
   - Compute len = end − start + 1. If len ≤ 0, fail.
3. **Uppercase Conversion:**
   - Load two or three characters from the trimmed window using lbu.
   - For each character in 'a'..'z', subtract 32 to convert to uppercase.
4. **Classification:**
   - If len == 3:
        - Match against GTH. If equal, proceed; else, fail.
   - If len == 2:
        - Match against the set {RE, YE, GE, RB}. If not in the set, fail.
5. **Range Check (2‑Letter Commands):**
   - Ensure 0 ≤ n ≤ 99. If n < 0 or n ≥ 100, fail.
6. **Formatting Output:**
   - For GTH:
        - Write cmd[0]='G', cmd[1]='T', cmd[2]='H', then cmd[3]='\0'.
   - For RE|YE|GE|RB:
        - Write cmd[0]=C1, cmd[1]=C2, cmd[2]=','.
        - Compute digits: tens = n / 10, units = n % 10 (or loop‑based arithmetic if without M extension).
        - Store ASCII digits: cmd[3] = '0' + tens, cmd[4] = '0' + units, cmd[5] = '\0'.
7. **Failure Path:**
   - On any failure condition, set cmd[0] = '\0' and return 0.

## Completion
- Append the null terminator to the output string in all success paths.
- Return 1 when a valid command is formatted (GTH or one of RE|YE|GE|RB with 0 ≤ n ≤ 99).
- Return 0 and clear cmd (empty string) when inputs are invalid, the command does not match the required patterns, or n is out of range for the two‑digit format.
