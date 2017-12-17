package src.wci.frontend.pascal;

import java.util.EnumSet;

import src.wci.frontend.*;
import src.wci.frontend.pascal.parsers.*;
import src.wci.intermediate.*;
import src.wci.intermediate.symtabimpl.*;
import src.wci.intermediate.typeimpl.*;
import src.wci.message.*;

import static src.wci.frontend.pascal.PascalTokenType.*;
import static src.wci.frontend.pascal.PascalErrorCode.*;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static src.wci.intermediate.typeimpl.TypeFormImpl.*;
import static src.wci.message.MessageType.PARSER_SUMMARY;

/**
 * <h1>PascalParserTD</h1>
 *
 * <p>The top-down Pascal parser.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class PascalParserTD extends Parser
{
    protected static PascalErrorHandler errorHandler = new PascalErrorHandler();

    private SymTabEntry routineId;  // name of the routine being parsed

    /**
     * Constructor.
     * @param scanner the scanner to be used with this parser.
     */
    public PascalParserTD(Scanner scanner)
    {
        super(scanner);
    }

    /**
     * Constructor for subclasses.
     * @param parent the parent parser.
     */
    public PascalParserTD(PascalParserTD parent)
    {
        super(parent.getScanner());
    }

    /**
     * Getter.
     * @return the routine identifier's symbol table entry.
     */
    public SymTabEntry getRoutineId()
    {
        return routineId;
    }

    /**
     * Getter.
     * @return the error handler.
     */
    public PascalErrorHandler getErrorHandler()
    {
        return errorHandler;
    }

    /**
     * Parse a Pascal source program and generate the symbol table
     * and the intermediate code.
     * @throws Exception if an error occurred.
     */
    public void parse()
        throws Exception
    {
        long startTime = System.currentTimeMillis();
        Predefined.initialize(symTabStack);

        try {
            Token token = nextToken();

            // Parse a program.
            PascalProgramParser programParser = new PascalProgramParser(this);
            programParser.parse(token, null);
            token = currentToken();

            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
            sendMessage(new Message(PARSER_SUMMARY,
                                    new Number[] {token.getLineNumber(),
                                                  getErrorCount(),
                                                  elapsedTime}));
        }
        catch (java.io.IOException ex) {
            errorHandler.abortTranslation(IO_ERROR, this);
        }
    }

    /**
     * Return the number of syntax errors found by the parser.
     * @return the error count.
     */
    public int getErrorCount()
    {
        return errorHandler.getErrorCount();
    }

    /**
     * Synchronize the parser.
     * @param syncSet the set of token types for synchronizing the parser.
     * @return the token where the parser has synchronized.
     * @throws Exception if an error occurred.
     */
    public Token synchronize(EnumSet syncSet)
        throws Exception
    {
        Token token = currentToken();

        // If the current token is not in the synchronization set,
        // then it is unexpected and the parser must recover.
        if (!syncSet.contains(token.getType())) {

            // Flag the unexpected token.
            errorHandler.flag(token, UNEXPECTED_TOKEN, this);

            // Recover by skipping tokens that are not
            // in the synchronization set.
            do {
                token = nextToken();
            } while (!(token instanceof EofToken) &&
                     !syncSet.contains(token.getType()));
       }

       return token;
    }
}
