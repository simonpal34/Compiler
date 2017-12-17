package src.wci.frontend.subC.parsers;

import static src.wci.frontend.subC.SubCErrorCode.IDENTIFIER_UNDEFINED;
import static src.wci.frontend.subC.SubCErrorCode.INVALID_TYPE;
import static src.wci.frontend.subC.SubCErrorCode.NOT_TYPE_IDENTIFIER;
import static src.wci.frontend.subC.SubCTokenType.*;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.CONSTANT;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.ENUMERATION_CONSTANT;

import java.util.EnumSet;

import src.wci.frontend.Token;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.frontend.subC.SubCTokenType;
import src.wci.intermediate.Definition;
import src.wci.intermediate.SymTabEntry;
import src.wci.intermediate.TypeSpec;
import src.wci.intermediate.symtabimpl.DefinitionImpl;
import src.wci.intermediate.symtabimpl.Predefined;

/**
 * <h1>SimpleTypeParser</h1>
 *
 * <p>Parse a simple Pascal type (identifier, subrange, enumeration)
 * specification.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
class SubCSimpleTypeParser extends SubCTypeSpecificationParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    protected SubCSimpleTypeParser(SubCParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for starting a simple type specification.
    static final EnumSet<SubCTokenType> SIMPLE_TYPE_START_SET = SubCDeclarationsParser.DECLARATION_START_SET.clone();
    static {
//        SIMPLE_TYPE_START_SET.add(LEFT_PAREN);
//        SIMPLE_TYPE_START_SET.add(COMMA);
        SIMPLE_TYPE_START_SET.add(IDENTIFIER);
    }

    /**
     * Parse a simple Pascal type specification.
     * @param token the current token.
     * @return the simple type specification.
     * @throws Exception if an error occurred.
     */
    public TypeSpec parse(Token token)
        throws Exception
    {
        // Synchronize at the start of a simple type specification.
        token = synchronize(SIMPLE_TYPE_START_SET);

        switch ((SubCTokenType) token.getType()) {

            case IDENTIFIER: {
                String name = token.getText().toLowerCase();
                SymTabEntry id = symTabStack.lookup(name);

                if (id != null) {
                    Definition definition = id.getDefinition();

                    // It's either a type identifier
                    // or the start of a subrange type.
                    if (definition == DefinitionImpl.TYPE) {
                        id.appendLineNumber(token.getLineNumber());
                        token = nextToken();  // consume the identifier

                        // Return the type of the referent type.
                        return id.getTypeSpec();
                    }
                    else if ((definition != CONSTANT) &&
                             (definition != ENUMERATION_CONSTANT)) {
                        errorHandler.flag(token, NOT_TYPE_IDENTIFIER, this);
                        token = nextToken();  // consume the identifier
                        return null;
                    }
                    else {
//                        SubrangeTypeParser subrangeTypeParser =
//                            new SubrangeTypeParser(this);
//                        return subrangeTypeParser.parse(token);
                    }
                }
                else {
                    errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
                    token = nextToken();  // consume the identifier
                    return null;
                }
            }
            case INT:{
                nextToken();
                return Predefined.integerType;
            }
            
            case CHAR: {
                nextToken();
                return Predefined.charType;
            }
            case FLOAT: {
                nextToken();
                return Predefined.realType;
            }

//            case LEFT_PAREN: {
//                EnumerationTypeParser enumerationTypeParser =
//                    new EnumerationTypeParser(this);
//                return enumerationTypeParser.parse(token);
//            }

            case COMMA:
            case SEMICOLON: {
                errorHandler.flag(token, INVALID_TYPE, this);
                return null;
            }
            
            default: return null;

//            default: {
//                SubrangeTypeParser subrangeTypeParser =
//                    new SubrangeTypeParser(this);
//                return subrangeTypeParser.parse(token);
//            }
        }
    }
}