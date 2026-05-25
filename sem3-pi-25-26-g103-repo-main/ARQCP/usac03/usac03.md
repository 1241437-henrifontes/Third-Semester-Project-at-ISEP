# Explanation of Assembly Function `extract_data`

## Objective
The goal here is to extract two types of information from a given string based on a given **token**. Whenever a token is found, the corresponding information is extracted and returned, and that is: the unit of measurement and the value of this unit. Then, the number 1 is returned, indicating success in the extraction.
When the token is not found, the function returns an empty string, with a return value of 0, meaning failure in the extraction.

## Inputs and Outputs
- **Inputs:**
    - `a0`: Pointer to the string to be extracted (array of characters).
    - `a1`: Pointer to the token to be searched for (also a string (array of characters)).
    - `a2`: Pointer to the array where the unit of measurement is going to be placed (array of characters).
    - `a3`: Pointer to the integer where the value is going to be placed.

- **Outputs:**
    - `a0 = 1` indicates success.
    - `a0 = 0` indicates failure.

## Logic Flow
1. **Find Token:**
   - Calls str_find(str, token) to locate the token in the string.
   - Validates that the match is not partial (must be at start or preceded by #).
2. **Check Format:**
   - Computes ptr_end = ptr_token + len(token).
   - Ensures the next character is &.
3. **Extract Unit:**
   - Calls str_find(ptr_end, "&unit:").
   - Copies the unit string into the buffer using str_copy_until_delim.
4. **Extract Value:**
   - Calls str_find(ptr_end, "&value:").
   - Converts the digits after &value: into an integer using str_to_int.
   - Stores the integer at *value.
5. **Return:**
   - Returns 1 if successful, 0 otherwise.

---
# Explanation of Assembly Function `str_len`

## Objective
This function computes the length of a null‑terminated string.

## Inputs and Outputs
- **Inputs:**
    - `a0`: Pointer to the string.

- **Outputs:**
    - `a0`: - Length of the string (number of characters before '\0').

## Logic Flow
1. **Initialize counter t0 = 0.**
2. **Loop through characters until '\0'.**
3. **Increment counter for each character.**
4. **Return counter in a0.**

---
# Explanation of Assembly Function `str_find`

## Objective
This function searches for the first occurrence of a substring inside a string.

## Inputs and Outputs
- **Inputs:**
    - `a0`: Pointer to the main string.
    - `a1`: Pointer to the substring.

- **Outputs:**
    - `a0`: Pointer to the first occurrence of substring in string.
    - `a0 = 0` if not found.

## Logic Flow
1. **Outer loop: iterate through each position in the main string.**
2. **Inner loop: compare substring characters with main string characters.**
3. **If the substring ends ('\0'), returns the pointer to match.**
4. **If mismatched, continue the outer loop.**
5. **If the end of the string is reached, return 0.**

---
# Explanation of Assembly Function `str_copy_until_space`

## Objective
This function copies characters from the source string to the destination until a delimiter (&, #, or '\0') is found.

## Inputs and Outputs
- **Inputs:**
    - `a0`: Pointer to the source string.
    - `a1`: Pointer to the destination buffer.

- **Outputs:**
    - Destination buffer contains copied string, null‑terminated.

## Logic Flow
1. **Loop through the source characters.**
2. **Stop if the character is '\0', &, or #.**
3. **Copy each valid character to the destination.**
4. **Append '\0' at the end.**

---
# Explanation of Assembly Function `str_to_int`

## Objective
This function converts a string of digits into an integer.

## Inputs and Outputs
- **Inputs:**
    - `a0`: Pointer to the string containing the digits.

- **Outputs:**
    - `a0`: Integer value parsed from string.

## Logic Flow
1. **Initialize accumulator t0 = 0.**
2. **Loop through characters:**
   - Stop if not a digit or if '\0'.
   - Convert ASCII digit to numeric value.
   - Multiply the accumulator by 10 and add the digit.
3. **Return the accumulator in a0.**

---
## Completion
Together, these functions implement the parsing logic required in **USAC03**:
- `extract_data` orchestrates the whole process.
- `str_len`, `str_find`, `str_copy_until_delim`, and `str_to_int` provide the supporting string and integer operations.
