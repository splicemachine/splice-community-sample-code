/**
 * Syntax:
   RPAD (input_string, length, padding_character)
   Example Usage:
   The SQL query below pads the string 'PSOUG' with the character 'X' up to 10 characters.
    SQL> SELECT RPAD('PSOUG',10,'X') FROM DUAL;
    PSOUGXXXXX
    The SQL query shows that the padded length takes precedence over the actual length of the original string.
    SQL> SELECT RPAD('PSOUG', 3) FROM DUAL;
    PSO
    *************************************************************************************************************************************************
    POWER( base , exponent )
    Example Usage:
    The SQL query would provide the function POWER(2,3) as result 8
    
 */
/**
 * @author amitmudgal
 *
 */
package com.splicemachine.utils;