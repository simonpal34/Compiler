package src.wci.frontend.pascal.parsers;

import java.util.EnumSet;

import src.wci.frontend.*;
import src.wci.frontend.pascal.*;
import src.wci.intermediate.*;
import src.wci.intermediate.icodeimpl.*;
import src.wci.intermediate.symtabimpl.*;
import src.wci.intermediate.typeimpl.*;

import static src.wci.frontend.pascal.PascalTokenType.*;
import static src.wci.frontend.pascal.PascalErrorCode.*;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

/**
 * <h1>PascalWhileStatementParser</h1>
 *
 * <p>Parse a Pascal WHILE statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class PascalWhileStatementParser extends PascalStatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public PascalWhileStatementParser(PascalParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for DO.
    private static final EnumSet<PascalTokenType> DO_SET =
        PascalStatementParser.STMT_START_SET.clone();
    static {
        DO_SET.add(DO);
        DO_SET.addAll(PascalStatementParser.STMT_FOLLOW_SET);
    }

    /**
     * Parse a WHILE statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        token = nextToken();  // consume the WHILE

        // Create LOOP, TEST, and NOT nodes.
        ICodeNode loopNode = ICodeFactory.createICodeNode(LOOP);
        ICodeNode breakNode = ICodeFactory.createICodeNode(TEST);
        ICodeNode notNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);

        // The LOOP node adopts the TEST node as its first child.
        // The TEST node adopts the NOT node as its only child.
        loopNode.addChild(breakNode);
        breakNode.addChild(notNode);

        // Parse the expression.
        // The NOT node adopts the expression subtree as its only child.
        PascalExpressionParser expressionParser = new PascalExpressionParser(this);
        ICodeNode exprNode = expressionParser.parse(token);
        notNode.addChild(exprNode);

        // Type check: The test expression must be boolean.
        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                             : Predefined.undefinedType;
        if (!TypeChecker.isBoolean(exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }

        // Synchronize at the DO.
        token = synchronize(DO_SET);
        if (token.getType() == DO) {
            token = nextToken();  // consume the DO
        }
        else {
            errorHandler.flag(token, MISSING_DO, this);
        }

        // Parse the statement.
        // The LOOP node adopts the statement subtree as its second child.
        PascalStatementParser statementParser = new PascalStatementParser(this);
        loopNode.addChild(statementParser.parse(token));

        return loopNode;
    }
}
