package src.wci.backend.interpreter.executors;

import src.wci.intermediate.*;
import src.wci.intermediate.symtabimpl.*;
import src.wci.backend.interpreter.*;

import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static src.wci.intermediate.symtabimpl.RoutineCodeImpl.*;
import static src.wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>CallExecutor</h1>
 *
 * <p>Execute a call to a procedure or function.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class CallExecutor extends StatementExecutor
{
    /**
     * Constructor.
     * @param the parent executor.
     */
    public CallExecutor(Executor parent)
    {
        super(parent);
    }

    /**
     * Execute procedure or function call statement.
     * @param node the root node of the call.
     * @return null.
     */
    public Object execute(ICodeNode node)
    {
        SymTabEntry routineId = (SymTabEntry) node.getAttribute(ID);
        RoutineCode routineCode =
                        (RoutineCode) routineId.getAttribute(ROUTINE_CODE);
        CallExecutor callExecutor = routineCode == DECLARED
                                    ? new CallDeclaredExecutor(this)
                                    : new CallStandardExecutor(this);

        ++executionCount;  // count the call statement
        return callExecutor.execute(node);
    }
}
