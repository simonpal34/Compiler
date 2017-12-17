package src.wci.frontend.pascal.parsers;

import java.util.EnumSet;

import src.wci.frontend.*;
import src.wci.frontend.pascal.*;
import src.wci.intermediate.*;

import static src.wci.frontend.pascal.PascalTokenType.*;
import static src.wci.frontend.pascal.PascalErrorCode.*;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.*;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;

/**
 * <h1>PascalDeclarationsParser</h1>
 *
 * <p>Parse Pascal declarations.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class PascalDeclarationsParser extends PascalParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public PascalDeclarationsParser(PascalParserTD parent)
    {
        super(parent);
    }

    static final EnumSet<PascalTokenType> DECLARATION_START_SET =
        EnumSet.of(CONST, TYPE, VAR, PROCEDURE, FUNCTION, BEGIN);

    static final EnumSet<PascalTokenType> TYPE_START_SET =
        DECLARATION_START_SET.clone();
    static {
        TYPE_START_SET.remove(CONST);
    }

    static final EnumSet<PascalTokenType> VAR_START_SET =
        TYPE_START_SET.clone();
    static {
        VAR_START_SET.remove(TYPE);
    }

    static final EnumSet<PascalTokenType> ROUTINE_START_SET =
        VAR_START_SET.clone();
    static {
        ROUTINE_START_SET.remove(VAR);
    }

    /**
     * Parse declarations.
     * To be overridden by the specialized declarations parser subclasses.
     * @param token the initial token.
     * @param parentId the symbol table entry of the parent routine's name.
     * @return null
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        token = synchronize(DECLARATION_START_SET);

        if (token.getType() == CONST) {
            token = nextToken();  // consume CONST

            PascalConstantDefinitionsParser constantDefinitionsParser =
                new PascalConstantDefinitionsParser(this);
            constantDefinitionsParser.parse(token, null);
        }

        token = synchronize(TYPE_START_SET);

        if (token.getType() == TYPE) {
            token = nextToken();  // consume TYPE

            PascalTypeDefinitionsParser typeDefinitionsParser =
                new PascalTypeDefinitionsParser(this);
            typeDefinitionsParser.parse(token, null);
        }

        token = synchronize(VAR_START_SET);

        if (token.getType() == VAR) {
            token = nextToken();  // consume VAR

            PascalVariableDeclarationsParser variableDeclarationsParser =
                new PascalVariableDeclarationsParser(this);
            variableDeclarationsParser.setDefinition(VARIABLE);
            variableDeclarationsParser.parse(token, null);
        }

        token = synchronize(ROUTINE_START_SET);
        TokenType tokenType = token.getType();

        while ((tokenType == PROCEDURE) || (tokenType == FUNCTION)) {
            PascalDeclaredRoutineParser routineParser =
                new PascalDeclaredRoutineParser(this);
            routineParser.parse(token, parentId);

            // Look for one or more semicolons after a definition.
            token = currentToken();
            if (token.getType() == SEMICOLON) {
                while (token.getType() == SEMICOLON) {
                    token = nextToken();  // consume the ;
                }
            }

            token = synchronize(ROUTINE_START_SET);
            tokenType = token.getType();
        }

        return null;
    }
}
