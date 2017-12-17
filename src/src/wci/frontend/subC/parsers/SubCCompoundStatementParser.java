package src.wci.frontend.subC.parsers;

import static src.wci.frontend.subC.SubCErrorCode.MISSING_END;
import static src.wci.frontend.subC.SubCTokenType.RIGHT_BRACE;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.COMPOUND;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import src.wci.frontend.Token;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.intermediate.ICodeFactory;
import src.wci.intermediate.ICodeNode;
import src.wci.intermediate.SymTab;
import src.wci.intermediate.SymTabEntry;
import src.wci.intermediate.SymTabStack;
import src.wci.intermediate.icodeimpl.ICodeKeyImpl;
import src.wci.intermediate.symtabimpl.SymTabKeyImpl;
import src.wci.util.CrossReferencer;

/**
 * <h1>CompoundStatementParser</h1>
 *
 * <p>Parse a Pascal compound statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCCompoundStatementParser extends SubCStatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public SubCCompoundStatementParser(SubCParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a compound statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        token = nextToken();  // consume the BEGIN

        // Create the COMPOUND node.
        ICodeNode compoundNode = ICodeFactory.createICodeNode(COMPOUND);
        
        // Parse the statement list terminated by the END token.
        SubCStatementParser statementParser = new SubCStatementParser(this);
        statementParser.parseList(token, compoundNode, parentId, RIGHT_BRACE, MISSING_END);

        return compoundNode;
    }
}