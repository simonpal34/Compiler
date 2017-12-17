package src.wci.frontend.subC.parsers;


import static src.wci.frontend.subC.SubCTokenType.LEFT_BRACE;
import static src.wci.frontend.subC.SubCTokenType.RIGHT_BRACE;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.COMPOUND;

import src.wci.frontend.Token;
import src.wci.frontend.TokenType;
import static src.wci.frontend.subC.SubCErrorCode.MISSING_BEGIN;
import static src.wci.frontend.subC.SubCErrorCode.MISSING_END;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.intermediate.ICodeFactory;
import src.wci.intermediate.ICodeNode;
import src.wci.intermediate.SymTabEntry;

/**
 * <h1>BlockParser</h1>
 *
 * <p>Parse a Pascal block.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCBlockParser extends SubCParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public SubCBlockParser(SubCParserTD parent)
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
    public ICodeNode parse(Token token, SymTabEntry rountineId)
        throws Exception
    {
        SubCStatementParser statementParser = new SubCStatementParser(this);
        TokenType tokenType = token.getType();
        ICodeNode rootNode = null;

        // Look for the BEGIN token to parse a compound statement.
        if (tokenType == LEFT_BRACE) {
            rootNode = statementParser.parse(token, rountineId);
        }

        // Missing BEGIN: Attempt to parse anyway if possible.
        else {
            errorHandler.flag(token, MISSING_BEGIN, this);

            if (SubCStatementParser.STMT_START_SET.contains(tokenType)) {
                rootNode = ICodeFactory.createICodeNode(COMPOUND);
                statementParser.parseList(token, rootNode, rountineId, RIGHT_BRACE, MISSING_END);
            }
        }

        return rootNode;
    }
}
