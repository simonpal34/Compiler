package src.wci.frontend.subC.parsers;

import static src.wci.frontend.subC.SubCErrorCode.*;
import static src.wci.frontend.subC.SubCTokenType.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import src.wci.frontend.Token;
import src.wci.frontend.TokenType;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.frontend.subC.SubCTokenType;
import src.wci.intermediate.Definition;
import src.wci.intermediate.SymTabEntry;
import src.wci.intermediate.TypeSpec;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.CONSTANT;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.ENUMERATION_CONSTANT;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.PROGRAM_PARM;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.CONSTANT_VALUE;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.SLOT;

/**
 * <h1>VariableDeclarationsParser</h1>
 *
 * <p>Parse Pascal variable declarations.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCVariableDeclarationsParser extends SubCDeclarationsParser
{
    private Definition definition;  // how to define the identifier
    TypeSpec type;

    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public SubCVariableDeclarationsParser(SubCParserTD parent)
    {
        super(parent);
    }

    /**
     * Setter.
     * @param definition the definition to set.
     */
    protected void setDefinition(Definition definition)
    {
        this.definition = definition;
    }

    // Synchronization set for a variable identifier.
    static final EnumSet<SubCTokenType> IDENTIFIER_SET =
        SubCDeclarationsParser.DECLARATION_START_SET.clone();
    static {
        IDENTIFIER_SET.add(IDENTIFIER);
        IDENTIFIER_SET.add(SEMICOLON);
    }

    // Synchronization set for the start of the next definition or declaration.
    static final EnumSet<SubCTokenType> NEXT_START_SET = EnumSet.noneOf(SubCTokenType.class);
//        DeclarationsParser.ROUTINE_START_SET.clone();
    static {
        NEXT_START_SET.add(IDENTIFIER);
        NEXT_START_SET.add(SEMICOLON);
    }
    
     // Synchronization set for starting a constant.
    static final EnumSet<SubCTokenType> CONSTANT_START_SET =
        EnumSet.of(IDENTIFIER, INTEGER, REAL, PLUS, MINUS, CHAR, COMMA);
    

    /**
     * Parse variable declarations.
     * @param token the initial token.
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        type = parseTypeSpec(token);
        
        token = synchronize(IDENTIFIER_SET);
        
         char peek = token.peekChar();
        if (peek == '(') {
                
        	SubCDeclaredRoutineParser routineParser = new SubCDeclaredRoutineParser(this);
        	routineParser.setReturnType(type);
        	return routineParser.parse(token, parentId);
        }

        
        this.setDefinition(VARIABLE);
        // Loop to parse a sequence of variable declarations
        // separated by semicolons.
        if (token.getType() == IDENTIFIER) {
            
            // Parse the identifier sublist and its type specification.
            parseIdentifierSublist(token, type);
            token = currentToken();
        }

        // Look for one or more semicolons after a definition.
        if (token.getType() == SEMICOLON) {
            while (token.getType() == SEMICOLON) {
                token = nextToken();  // consume the ;
            }
        }
        // If at the start of the next definition or declaration,
        // then missing a semicolon.
        else {
            errorHandler.flag(token, MISSING_SEMICOLON, this);
        }
        return parentId;
        
    }

    // Synchronization set to start a sublist identifier.
    static final EnumSet<SubCTokenType> IDENTIFIER_START_SET =
        EnumSet.of(IDENTIFIER, COMMA);
    
    static final EnumSet<SubCTokenType> IDENTFIER_FOLLOW_SET = 
        EnumSet.of(COLON, SEMICOLON);

    // Synchronization set for the , token.
     // Synchronization set for the , token.
    private static final EnumSet<SubCTokenType> COMMA_SET =
        EnumSet.of(COMMA, SEMICOLON, RIGHT_PAREN);

    /**
     * Parse a sublist of identifiers and their type specification.
     * @param token the current token.
     * @param followSet the synchronization set to follow an identifier.
     * @return the sublist of identifiers in a declaration.
     * @throws Exception if an error occurred.
     */
    protected ArrayList<SymTabEntry> parseIdentifierSublist(
                                         Token token,
                                         TypeSpec type)
        throws Exception
    {
        ArrayList<SymTabEntry> sublist = new ArrayList<SymTabEntry>();

        do {
//            token = synchronize(IDENTIFIER_START_SET);
            SymTabEntry id = parseIdentifier(token);
            token = currentToken();
            if (token.getType() == IDENTIFIER) token = nextToken();

            if (id != null) {
                sublist.add(id);
            }

            token = synchronize(COMMA_SET);
            TokenType tokenType = token.getType();

            // Look for the comma.
            if (tokenType == COMMA) {
                token = nextToken();  // consume the comma

                if (IDENTFIER_FOLLOW_SET.contains(token.getType())) {
                    errorHandler.flag(token, MISSING_IDENTIFIER, this);
                }
            }
            else if (IDENTIFIER_START_SET.contains(tokenType)) {
                errorHandler.flag(token, MISSING_COMMA, this);
            }
        } while (!IDENTFIER_FOLLOW_SET.contains(token.getType()));

        if (definition != PROGRAM_PARM) {

            // Assign the type specification to each identifier in the list.
            for (SymTabEntry variableId : sublist) {
                variableId.setTypeSpec(type);
            }
        }

        return sublist;
    }
    
    /**
     * Parse an identifier.
     * @param token the current token.
     * @return the symbol table entry of the identifier.
     * @throws Exception if an error occurred.
     */
        public SymTabEntry parseIdentifier(Token token)
        throws Exception
    {
        SymTabEntry id = null;

        if (token.getType() == IDENTIFIER) {
            String name = token.getText().toLowerCase();
            id = symTabStack.lookupLocal(name);

            // Enter a new identifier into the symbol table.
            if (id == null) {
                id = symTabStack.enterLocal(name);
                id.setDefinition(definition);
                id.appendLineNumber(token.getLineNumber());
                
                // Set its slot number in the local variables array.
                int slot = id.getSymTab().nextSlotNumber();
                id.setAttribute(SLOT, slot);
            }
            else {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
            }
        }
        else {
            errorHandler.flag(token, MISSING_IDENTIFIER, this);
        }

        return id;
    }

    // Synchronization set for the : token.
    private static final EnumSet<SubCTokenType> COLON_SET =
        EnumSet.of(COLON, SEMICOLON);

    
    
    /**
     * Parse an identifier constant.
     * @param token the current token.
     * @param sign the sign, if any.
     * @return the constant value.
     * @throws Exception if an error occurred.
     */
    protected Object parseIdentifierConstant(Token token, TokenType sign)
        throws Exception
    {
        String name = token.getText().toLowerCase();
        SymTabEntry id = symTabStack.lookup(name);

        nextToken();  // consume the identifier

        // The identifier must have already been defined
        // as an constant identifier.
        if (id == null) {
            errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
            return null;
        }

        Definition definition = id.getDefinition();

        if (definition == CONSTANT) {
            Object value = id.getAttribute(CONSTANT_VALUE);
            id.appendLineNumber(token.getLineNumber());

            if (value instanceof Integer) {
                return sign == MINUS ? -((Integer) value) : value;
            }
            else if (value instanceof Float) {
                return sign == MINUS ? -((Float) value) : value;
            }
            else if (value instanceof String) {
                if (sign != null) {
                    errorHandler.flag(token, INVALID_CONSTANT, this);
                }

                return value;
            }
            else {
                return null;
            }
        }
        else if (definition == VARIABLE) {
            Object value = id.getAttribute(CONSTANT_VALUE);
            id.appendLineNumber(token.getLineNumber());

            if (sign != null) {
                errorHandler.flag(token, INVALID_CONSTANT, this);
            }

            return value;
        }
        else if (definition == null) {
            errorHandler.flag(token, NOT_CONSTANT_IDENTIFIER, this);
            return null;
        }
        else {
            errorHandler.flag(token, INVALID_CONSTANT, this);
            return null;
        }
    }
    
    
    /**
     * Parse the type specification.
     * @param token the current token.
     * @return the type specification.
     * @throws Exception if an error occurs.
     */
    protected TypeSpec parseTypeSpec(Token token)
        throws Exception
    {
        // Parse the type specification.
        SubCTypeSpecificationParser typeSpecificationParser =
            new SubCTypeSpecificationParser(this);
        TypeSpec type = typeSpecificationParser.parse(token);

        return type;
    }
}