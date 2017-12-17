package src.wci.frontend.subC.parsers;

import static src.wci.frontend.subC.SubCTokenType.FLOAT;
import static src.wci.frontend.subC.SubCTokenType.IDENTIFIER;
import static src.wci.frontend.subC.SubCTokenType.INT;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.VARIABLE;

import java.util.EnumSet;

import src.wci.frontend.EofToken;
import src.wci.frontend.Token;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.frontend.subC.SubCTokenType;
import static src.wci.frontend.subC.SubCTokenType.CHAR;
import static src.wci.frontend.subC.SubCTokenType.CONST;
import static src.wci.frontend.subC.SubCTokenType.DOUBLE;
import static src.wci.frontend.subC.SubCTokenType.VOID;
import src.wci.intermediate.SymTab;
import src.wci.intermediate.SymTabEntry;
import src.wci.intermediate.symtabimpl.DefinitionImpl;

/**
 * <h1>DeclarationsParser</h1>
 *
 * <p>Parse Pascal declarations.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCDeclarationsParser extends SubCParserTD
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public SubCDeclarationsParser(SubCParserTD parent)
    {
        super(parent);
    }

    static final EnumSet<SubCTokenType> DECLARATION_START_SET =
        EnumSet.of(INT, FLOAT, DOUBLE, CHAR, VOID);

//    static final EnumSet<SubsetCTokenType> TYPE_START_SET =
//        DECLARATION_START_SET.clone();
//    static {
//        TYPE_START_SET.remove(CONST);
//    }

//        TYPE_START_SET.clone();
//    static {
//        VAR_START_SET.remove(TYPE);
//    }

    static final EnumSet<SubCTokenType> VAR_START_SET = EnumSet.of(IDENTIFIER);
    static final EnumSet<SubCTokenType> ROUTINE_START_SET =
        VAR_START_SET.clone();
    static {
        ROUTINE_START_SET.add(VOID);
    }
  

    /**
     * Parse declarations.
     * To be overridden by the specialized declarations parser subclasses.
     * @param token the initial token.
     * @throws Exception if an error occurred.
     */
    public SymTabEntry parse(Token token, SymTabEntry entry)
        throws Exception
    {
        
        do{
            token = currentToken();
    	
            if(DECLARATION_START_SET.contains(token.getType()))
             {
                SubCVariableDeclarationsParser variableDeclarationsParser = new SubCVariableDeclarationsParser(this);
                variableDeclarationsParser.setDefinition(VARIABLE);
                variableDeclarationsParser.parse(token , entry);
             }
        }while(!(token instanceof EofToken));
        
        
        return null;
    }
    
}