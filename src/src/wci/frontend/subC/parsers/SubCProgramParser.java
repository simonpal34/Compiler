package src.wci.frontend.subC.parsers;

import src.wci.frontend.Token;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.intermediate.SymTabEntry;

/**
 * <h1>ProgramParser</h1>
 *
 * <p>Parse a Pascal program.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCProgramParser extends SubCDeclarationsParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public SubCProgramParser(SubCParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a program.
     * @param token the initial token.
     * @param parentId the symbol table entry of the parent routine's name.
     * @return null
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        /// Parse the program.
    	SubCDeclarationsParser declarationsParser = new SubCDeclarationsParser(this);
    	declarationsParser.parse(token, parentId);


        return null;
    }
}
