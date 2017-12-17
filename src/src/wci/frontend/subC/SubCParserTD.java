package src.wci.frontend.subC;

import java.util.ArrayList;
import java.util.EnumSet;
import src.wci.frontend.EofToken;
import src.wci.frontend.Parser;
import src.wci.frontend.Scanner;
import src.wci.frontend.Token;
import src.wci.frontend.*;
import static src.wci.frontend.subC.SubCErrorCode.IO_ERROR;
import static src.wci.frontend.subC.SubCTokenType.ERROR;
import static src.wci.frontend.subC.SubCTokenType.IDENTIFIER;
import src.wci.message.Message;
import static src.wci.message.MessageType.*;
import src.wci.intermediate.*;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;

import static src.wci.frontend.subC.SubCTokenType.*;
import static src.wci.frontend.subC.SubCErrorCode.*;
import src.wci.frontend.subC.parsers.SubCBlockParser;
import src.wci.frontend.subC.parsers.SubCProgramParser;
import src.wci.frontend.subC.parsers.SubCStatementParser;
import src.wci.intermediate.symtabimpl.DefinitionImpl;
import src.wci.intermediate.symtabimpl.Predefined;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import src.wci.intermediate.symtabimpl.Predefined;

public class SubCParserTD extends Parser
{
    protected static SubCErrorHandler errorHandler = new SubCErrorHandler();
    protected static StringBuilder nestedStacks = new StringBuilder();
    
    private SymTabEntry routineId;  // name of the routine being parsed

    /**
     * Constructor.
     * @param scanner the scanner to be used with this parser.
     */
    public SubCParserTD(Scanner scanner)
    {
        super(scanner);
    }
    
    public SubCParserTD(SubCParserTD parent)
    {
        super(parent.getScanner());
    }

    public SymTabEntry getRoutineId()
    {
        return routineId;
    }
    
    /**
     * Parse a Pascal source program and generate the symbol table
     * and the intermediate code.
     */
    public void parse()
        throws Exception
    {
        long startTime = System.currentTimeMillis();
        Predefined.initialize(symTabStack);

        try {
            Token token = nextToken();
            
            ICode iCode = ICodeFactory.createICode();

            routineId = symTabStack.getProgramId();
            
            // Push a new symbol table onto the symbol table stack and set
            // the routine's symbol table and intermediate code.
            routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
            routineId.setAttribute(ROUTINE_ICODE, iCode);
            routineId.setAttribute(ROUTINE_ROUTINES, new ArrayList<>());

            // Parse a program.
            SubCProgramParser programParser = new SubCProgramParser(this);
            programParser.parse(token, routineId);
            token = currentToken();

            // Send the parser summary message.
            float elapsedTime = (System.currentTimeMillis() - startTime)/1000f;
            System.out.println(nestedStacks);
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
