package src.wci.frontend.pascal.parsers;

import src.wci.frontend.*;
import src.wci.frontend.pascal.*;
import src.wci.intermediate.*;

import static src.wci.frontend.pascal.PascalTokenType.*;
import static src.wci.frontend.pascal.PascalErrorCode.*;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static src.wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>PascalBlockParser</h1>
 *
 * <p>Parse a Pascal block.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class PascalBlockParser extends PascalParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public PascalBlockParser(PascalParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a block.
     * @param token the initial token.
     * @param routineId the symbol table entry of the routine name.
     * @return the root node of the parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, SymTabEntry routineId)
        throws Exception
    {
        PascalDeclarationsParser declarationsParser = new PascalDeclarationsParser(this);
        PascalStatementParser statementParser = new PascalStatementParser(this);

        // Parse any declarations.
        declarationsParser.parse(token, routineId);

        token = synchronize(PascalStatementParser.STMT_START_SET);
        TokenType tokenType = token.getType();
        ICodeNode rootNode = null;

        // Look for the BEGIN token to parse a compound statement.
        if (tokenType == BEGIN) {
            rootNode = statementParser.parse(token);
        }

        // Missing BEGIN: Attempt to parse anyway if possible.
        else {
            errorHandler.flag(token, MISSING_BEGIN, this);

            if (PascalStatementParser.STMT_START_SET.contains(tokenType)) {
                rootNode = ICodeFactory.createICodeNode(COMPOUND);
                statementParser.parseList(token, rootNode, END, MISSING_END);
            }
        }

        return rootNode;
    }
}
