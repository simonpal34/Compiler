package src.wci.frontend.subC.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import src.wci.frontend.*;
import src.wci.frontend.subC.*;
import src.wci.intermediate.*;
import src.wci.intermediate.symtabimpl.*;

import static src.wci.frontend.subC.SubCTokenType.*;
import static src.wci.frontend.subC.SubCErrorCode.*;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static src.wci.intermediate.typeimpl.TypeFormImpl.*;
import static src.wci.intermediate.typeimpl.TypeKeyImpl.*;

/**
 * <h1>TypeSpecificationParser</h1>
 *
 * <p>Parse a Pascal type specification.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
class SubCTypeSpecificationParser extends SubCParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    protected SubCTypeSpecificationParser(SubCParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for starting a type specification.
    static final EnumSet<SubCTokenType> TYPE_START_SET = SubCSimpleTypeParser.SIMPLE_TYPE_START_SET.clone();
//        SimpleTypeParser.SIMPLE_TYPE_START_SET.clone();
    static {
//        TYPE_START_SET.add(ARRAY);
//        TYPE_START_SET.add(RECORD);
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

        switch ((SubCTokenType) token.getType()) {

//            case ARRAY: {
//                ArrayTypeParser arrayTypeParser = new ArrayTypeParser(this);
//                return arrayTypeParser.parse(token);
//            }
//
//            case RECORD: {
//                RecordTypeParser recordTypeParser = new RecordTypeParser(this);
//                return recordTypeParser.parse(token);
//            }

            default: {
                SubCSimpleTypeParser simpleTypeParser = new SubCSimpleTypeParser(this);
                return simpleTypeParser.parse(token);
            }
        }
    }
}