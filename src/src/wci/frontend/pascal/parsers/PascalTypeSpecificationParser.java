package src.wci.frontend.pascal.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import src.wci.frontend.*;
import src.wci.frontend.pascal.*;
import src.wci.intermediate.*;
import src.wci.intermediate.symtabimpl.*;

import static src.wci.frontend.pascal.PascalTokenType.*;
import static src.wci.frontend.pascal.PascalErrorCode.*;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static src.wci.intermediate.typeimpl.TypeFormImpl.*;
import static src.wci.intermediate.typeimpl.TypeKeyImpl.*;

/**
 * <h1>PascalTypeSpecificationParser</h1>
 *
 * <p>Parse a Pascal type specification.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
class PascalTypeSpecificationParser extends PascalParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    protected PascalTypeSpecificationParser(PascalParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for starting a type specification.
    static final EnumSet<PascalTokenType> TYPE_START_SET =
        PascalSimpleTypeParser.SIMPLE_TYPE_START_SET.clone();
    static {
        TYPE_START_SET.add(PascalTokenType.ARRAY);
        TYPE_START_SET.add(PascalTokenType.RECORD);
        TYPE_START_SET.add(SEMICOLON);
    }

    /**
     * Parse a Pascal type specification.
     * @param token the current token.
     * @return the type specification.
     * @throws Exception if an error occurred.
     */
    public TypeSpec parse(Token token)
        throws Exception
    {
        // Synchronize at the start of a type specification.
        token = synchronize(TYPE_START_SET);

        switch ((PascalTokenType) token.getType()) {

            case ARRAY: {
                PascalArrayTypeParser arrayTypeParser = new PascalArrayTypeParser(this);
                return arrayTypeParser.parse(token);
            }

            case RECORD: {
                PascalRecordTypeParser recordTypeParser = new PascalRecordTypeParser(this);
                return recordTypeParser.parse(token);
            }

            default: {
                PascalSimpleTypeParser simpleTypeParser = new PascalSimpleTypeParser(this);
                return simpleTypeParser.parse(token);
            }
        }
    }
}
