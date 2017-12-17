package src.wci.frontend.pascal.parsers;

import java.util.EnumSet;

import src.wci.frontend.*;
import src.wci.frontend.pascal.*;
import src.wci.intermediate.*;
import src.wci.intermediate.symtabimpl.*;

import static src.wci.frontend.pascal.PascalTokenType.*;
import static src.wci.frontend.pascal.PascalErrorCode.*;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.*;
import static src.wci.intermediate.typeimpl.TypeFormImpl.RECORD;
import static src.wci.intermediate.typeimpl.TypeKeyImpl.*;

/**
 * <h1>PascalRecordTypeParser</h1>
 *
 * <p>Parse a Pascal record type specification.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
class PascalRecordTypeParser extends PascalTypeSpecificationParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    protected PascalRecordTypeParser(PascalParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for the END.
    private static final EnumSet<PascalTokenType> END_SET =
        PascalDeclarationsParser.VAR_START_SET.clone();
    static {
        END_SET.add(END);
        END_SET.add(SEMICOLON);
    }

    /**
     * Parse a Pascal record type specification.
     * @param token the current token.
     * @return the record type specification.
     * @throws Exception if an error occurred.
     */
    public TypeSpec parse(Token token)
        throws Exception
    {
        TypeSpec recordType = TypeFactory.createType(RECORD);
        token = nextToken();  // consume RECORD

        // Push a symbol table for the RECORD type specification.
        recordType.setAttribute(RECORD_SYMTAB, symTabStack.push());

        // Parse the field declarations.
        PascalVariableDeclarationsParser variableDeclarationsParser =
            new PascalVariableDeclarationsParser(this);
        variableDeclarationsParser.setDefinition(FIELD);
        variableDeclarationsParser.parse(token, null);

        // Pop off the record's symbol table.
        symTabStack.pop();

        // Synchronize at the END.
        token = synchronize(END_SET);

        // Look for the END.
        if (token.getType() == END) {
            token = nextToken();  // consume END
        }
        else {
            errorHandler.flag(token, MISSING_END, this);
        }

        return recordType;
    }
}
