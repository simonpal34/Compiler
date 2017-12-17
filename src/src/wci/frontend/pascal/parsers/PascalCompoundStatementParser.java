package src.wci.frontend.pascal.parsers;

import src.wci.frontend.*;
import src.wci.frontend.pascal.*;
import src.wci.intermediate.*;

import static src.wci.frontend.pascal.PascalTokenType.*;
import static src.wci.frontend.pascal.PascalErrorCode.*;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static src.wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>PascalCompoundStatementParser</h1>
 *
 * <p>Parse a Pascal compound statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class PascalCompoundStatementParser extends PascalStatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public PascalCompoundStatementParser(PascalParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a compound statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        token = nextToken();  // consume the BEGIN

        // Create the COMPOUND node.
        ICodeNode compoundNode = ICodeFactory.createICodeNode(COMPOUND);

        // Parse the statement list terminated by the END token.
        PascalStatementParser statementParser = new PascalStatementParser(this);
        statementParser.parseList(token, compoundNode, END, MISSING_END);

        return compoundNode;
    }
}
