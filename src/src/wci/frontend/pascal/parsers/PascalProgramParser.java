package src.wci.frontend.pascal.parsers;

import java.util.EnumSet;

import src.wci.frontend.*;
import src.wci.frontend.pascal.*;
import src.wci.intermediate.*;

import static src.wci.frontend.pascal.PascalTokenType.*;
import static src.wci.frontend.pascal.PascalErrorCode.*;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;

/**
 * <h1>PascalProgramParser</h1>
 *
 * <p>Parse a Pascal program.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class PascalProgramParser extends PascalDeclarationsParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public PascalProgramParser(PascalParserTD parent)
    {
        super(parent);
    }

    // Synchronization set to start a program.
    static final EnumSet<PascalTokenType> PROGRAM_START_SET =
        EnumSet.of(PROGRAM, SEMICOLON);
    static {
        PROGRAM_START_SET.addAll(PascalDeclarationsParser.DECLARATION_START_SET);
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
        token = synchronize(PROGRAM_START_SET);

        // Parse the program.
        PascalDeclaredRoutineParser routineParser = new PascalDeclaredRoutineParser(this);
        routineParser.parse(token, parentId);

        // Look for the final period.
        token = currentToken();
        if (token.getType() != DOT) {
            errorHandler.flag(token, MISSING_PERIOD, this);
        }

        return null;
    }
}
