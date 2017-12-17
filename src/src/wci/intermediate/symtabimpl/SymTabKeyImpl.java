package src.wci.intermediate.symtabimpl;

import src.wci.intermediate.SymTabKey;

/**
 * <h1>SymTabKeyImpl</h1>
 *
 * <p>Attribute keys for a symbol table entry.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public enum SymTabKeyImpl implements SymTabKey
{
    // Constant.
    CONSTANT_VALUE,

    // Procedure or function.
    ROUTINE_CODE, ROUTINE_SYMTAB, ROUTINE_ICODE,
    ROUTINE_PARMS, ROUTINE_ROUTINES,

    // Variable or record field value.
    DATA_VALUE,

    // Local variables array slot numbers.
    SLOT, WRAP_SLOT,
    
     MAIN_METHOD_ROUTINE
}