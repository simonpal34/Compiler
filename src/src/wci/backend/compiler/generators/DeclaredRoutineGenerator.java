package src.wci.backend.compiler.generators;

import java.util.ArrayList;

import src.wci.intermediate.*;
import src.wci.intermediate.symtabimpl.*;
import src.wci.backend.compiler.*;

import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.*;
import static src.wci.backend.compiler.Directive.*;
import static src.wci.backend.compiler.Instruction.*;

/**
 * <h1>DeclaredRoutineGenerator</h1>
 *
 * <p>Generate code for a declared procedure or function.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class DeclaredRoutineGenerator extends CodeGenerator
{
    private int functionValueSlot;  // function return value slot number

    /**
     * Constructor.
     * @param the parent generator.
     */
    public DeclaredRoutineGenerator(CodeGenerator parent)
    {
        super(parent);
    }

    /**
     * Generate code for a declared procedure or function
     * @param routineId the symbol table entry of the routine's name.
     */
    public void generate(SymTabEntry routineId)
        throws PascalCompilerException
    {
        SymTab routineSymTab = (SymTab) routineId.getAttribute(ROUTINE_SYMTAB);
        localVariables = new LocalVariables(routineSymTab.maxSlotNumber());
        localStack = new LocalStack();

        // Reserve an extra variable for the function return value.
        if (routineId.getDefinition() == FUNCTION) {
            functionValueSlot = localVariables.reserve();
            routineId.setAttribute(SLOT, functionValueSlot);
        }

        generateRoutineHeader(routineId);
        generateRoutineLocals(routineId);

        // Generate code to allocate any arrays, records, and strings.
        StructuredDataGenerator structuredDataGenerator =
                                    new StructuredDataGenerator(this);
        structuredDataGenerator.generate(routineId);

        generateRoutineCode(routineId);
        generateRoutineReturn(routineId);
        generateRoutineEpilogue(routineId);
    }

    /**
     * Generate the routine header.
     */
    void generateRoutineHeader(SymTabEntry routineId)
    {
        String routineName = routineId.getName();
        ArrayList<SymTabEntry> parmIds =
            (ArrayList<SymTabEntry>) routineId.getAttribute(ROUTINE_PARMS);
        StringBuilder buffer = new StringBuilder();

        // Procedure or function name.
        buffer.append(routineName);
        buffer.append("(");

        // Parameter and return type descriptors.
        if (parmIds != null) {
            for (SymTabEntry parmId : parmIds) {
                buffer.append(typeDescriptor(parmId));
            }
        }
        buffer.append(")");
        buffer.append(typeDescriptor(routineId));

        emitBlankLine();
        emitDirective(METHOD_PRIVATE_STATIC, buffer.toString());
    }

    /**
     * Generate directives for the local variables.
     */
    void generateRoutineLocals(SymTabEntry routineId)
    {
        SymTab symTab = (SymTab) routineId.getAttribute(ROUTINE_SYMTAB);
        ArrayList<SymTabEntry> ids = symTab.sortedEntries();
        ids.sort((s1, s2) -> (int) s1.getAttribute(SLOT) - (int) s2.getAttribute(SLOT));

        emitBlankLine();

        // Loop over all the routine's identifiers and
        // emit a .var directive for each variable and formal parameter.
        for (SymTabEntry id : ids) {
            Definition defn = id.getDefinition();

            if ((defn == VARIABLE) || (defn == VALUE_PARM)
                                   || (defn == VAR_PARM)) {
                int slot = (Integer) id.getAttribute(SLOT);
                emitDirective(VAR, slot + " is " + id.getName(),
                              typeDescriptor(id));
            }
        }
        
        emitBlankLine();

        // Emit an extra .var directive for an implied function variable.
        if (routineId.getDefinition() == FUNCTION) {
            emitDirective(VAR, functionValueSlot + " is " + routineId.getName(),
                          typeDescriptor(routineId.getTypeSpec()));
        }
        
        emitBlankLine();
    }

    /**
     * Generate code for the routine's body.
     */
    void generateRoutineCode(SymTabEntry routineId)
        throws PascalCompilerException
    {
        ICode iCode = (ICode) routineId.getAttribute(ROUTINE_ICODE);
        ICodeNode root = iCode.getRoot();

        emitBlankLine();

        // Generate code for the compound statement.
        StatementGenerator statementGenerator = new StatementGenerator(this);
        statementGenerator.generate(root);
    }

    /**
     * Generate the routine's return code.
     */
    private void generateRoutineReturn(SymTabEntry routineId)
    {
        emitBlankLine();

        // Function: Return the value in the implied function variable.
        if (routineId.getDefinition() == FUNCTION) {
            TypeSpec type = routineId.getTypeSpec();

            emitLoadLocal(type, functionValueSlot);
            emitReturnValue(type);

            localStack.use(1);
        }

        // Procedure: Just return.
        else {
            emit(RETURN);
        }
    }

    /**
     * Generate the routine's epilogue.
     */
    private void generateRoutineEpilogue(SymTabEntry routineId)
    {
        emitBlankLine();
        emitDirective(LIMIT_LOCALS, localVariables.count());
        emitDirective(LIMIT_STACK,  localStack.capacity());
        emitDirective(END_METHOD);
    }
}
