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
 * <h1>PascalIfStatementParser</h1>
 *
 * <p>Parse a Pascal IF statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class PascalIfStatementParser extends PascalStatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public PascalIfStatementParser(PascalParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for THEN.
    private static final EnumSet<PascalTokenType> THEN_SET =
        PascalStatementParser.STMT_START_SET.clone();
    static {
        THEN_SET.add(THEN);
        THEN_SET.addAll(PascalStatementParser.STMT_FOLLOW_SET);
    }

    /**
     * Parse an IF statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        token = nextToken();  // consume the IF

        // Create an IF node.
        ICodeNode ifNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);

        // Parse the expression.
        // The IF node adopts the expression subtree as its first child.
        PascalExpressionParser expressionParser = new PascalExpressionParser(this);
        ICodeNode exprNode = expressionParser.parse(token);
        ifNode.addChild(exprNode);

        // Type check: The expression type must be boolean.
        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                             : Predefined.undefinedType;
        if (!TypeChecker.isBoolean(exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }

        // Synchronize at the THEN.
        token = synchronize(THEN_SET);
        if (token.getType() == THEN) {
            token = nextToken();  // consume the THEN
        }
        else {
            errorHandler.flag(token, MISSING_THEN, this);
        }

        // Parse the THEN statement.
        // The IF node adopts the statement subtree as its second child.
        PascalStatementParser statementParser = new PascalStatementParser(this);
        ifNode.addChild(statementParser.parse(token));
        token = currentToken();

        // Look for an ELSE.
        if (token.getType() == ELSE) {
            token = nextToken();  // consume the THEN

            // Parse the ELSE statement.
            // The IF node adopts the statement subtree as its third child.
            ifNode.addChild(statementParser.parse(token));
        }

        return ifNode;
    }
}
