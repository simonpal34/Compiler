package src.wci.frontend.pascal.parsers;

import src.wci.frontend.*;
import src.wci.frontend.pascal.*;
import src.wci.intermediate.*;
import src.wci.intermediate.symtabimpl.*;
import src.wci.intermediate.typeimpl.*;

import static src.wci.frontend.pascal.PascalTokenType.*;
import static src.wci.frontend.pascal.PascalErrorCode.*;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

/**
 * <h1>PascalRepeatStatementParser</h1>
 *
 * <p>Parse a Pascal REPEAT statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class PascalRepeatStatementParser extends PascalStatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public PascalRepeatStatementParser(PascalParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a REPEAT statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        token = nextToken();  // consume the REPEAT

        // Create the LOOP and TEST nodes.
        ICodeNode loopNode = ICodeFactory.createICodeNode(LOOP);
        ICodeNode testNode = ICodeFactory.createICodeNode(TEST);

        // Parse the statement list terminated by the UNTIL token.
        // The LOOP node is the parent of the statement subtrees.
        PascalStatementParser statementParser = new PascalStatementParser(this);
        statementParser.parseList(token, loopNode, UNTIL, MISSING_UNTIL);
        token = currentToken();

        // Parse the expression.
        // The TEST node adopts the expression subtree as its only child.
        PascalExpressionParser expressionParser = new PascalExpressionParser(this);
        ICodeNode exprNode = expressionParser.parse(token);
        testNode.addChild(exprNode);
        loopNode.addChild(testNode);

        // Type check: The test expression must be boolean.
        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                             : Predefined.undefinedType;
        if (!TypeChecker.isBoolean(exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }

        return loopNode;
    }
}
