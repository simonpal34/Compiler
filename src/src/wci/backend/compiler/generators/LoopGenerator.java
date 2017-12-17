package src.wci.backend.compiler.generators;

import java.util.ArrayList;

import src.wci.intermediate.*;
import src.wci.intermediate.icodeimpl.*;
import src.wci.intermediate.symtabimpl.*;
import src.wci.backend.compiler.*;

import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.*;
import static src.wci.intermediate.typeimpl.TypeFormImpl.*;
import static src.wci.intermediate.typeimpl.TypeKeyImpl.*;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static src.wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static src.wci.backend.compiler.Instruction.*;

/**
 * <h1>LoopGenerator</h1>
 *
 * <p>Generate code for a looping statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class LoopGenerator extends StatementGenerator
{
    /**
     * Constructor.
     * @param the parent executor.
     */
    public LoopGenerator(CodeGenerator parent)
    {
        super(parent);
    }

    /**
     * Generate code for a looping statement.
     * @param node the root node of the statement.
     */
    public void generate(ICodeNode node)
        throws PascalCompilerException
    {
        ArrayList<ICodeNode> loopChildren = node.getChildren();
        ExpressionGenerator expressionGenerator = new ExpressionGenerator(this);
        StatementGenerator statementGenerator = new StatementGenerator(this);
        Label loopLabel = Label.newLabel();
        Label nextLabel = Label.newLabel();

        emitLabel(loopLabel);

        // Generate code for the children of the LOOP node.
        for (ICodeNode child : loopChildren) {
            ICodeNodeTypeImpl childType = (ICodeNodeTypeImpl) child.getType();

            // TEST node: Generate code to test the boolean expression.
            if (childType == TEST) {
                ICodeNode expressionNode = child.getChildren().get(0);

                expressionGenerator.generate(expressionNode);
                emit(IFNE, nextLabel);

                localStack.decrease(1);
            }

            // Statement node: Generate code for the statement.
            else {
                statementGenerator.generate(child);
            }
        }

        emit(GOTO, loopLabel);
        emitLabel(nextLabel);
    }
}
