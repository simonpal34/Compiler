package src.wci.frontend.subC.parsers;

import static src.wci.frontend.subC.SubCTokenType.ELSE;
import static src.wci.frontend.subC.SubCTokenType.FLOAT;
import static src.wci.frontend.subC.SubCTokenType.IDENTIFIER;
import static src.wci.frontend.subC.SubCTokenType.INT;
import static src.wci.frontend.subC.SubCTokenType.SEMICOLON;
import static src.wci.frontend.subC.SubCTokenType.WHILE;
import static src.wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

import java.util.EnumSet;

import src.wci.frontend.EofToken;
import src.wci.frontend.Token;
import src.wci.frontend.subC.SubCErrorCode;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.frontend.subC.SubCTokenType;
import static src.wci.frontend.subC.SubCTokenType.CHAR;
import static src.wci.frontend.subC.SubCTokenType.DOUBLE;
import static src.wci.frontend.subC.SubCTokenType.LEFT_BRACE;
import static src.wci.frontend.subC.SubCTokenType.RIGHT_BRACE;
import static src.wci.frontend.subC.SubCTokenType.VOID;
import src.wci.intermediate.ICodeFactory;
import src.wci.intermediate.ICodeNode;
import src.wci.intermediate.SymTab;
import src.wci.intermediate.SymTabEntry;
import src.wci.intermediate.symtabimpl.DefinitionImpl;
import src.wci.intermediate.symtabimpl.SymTabEntryImpl;
import src.wci.intermediate.symtabimpl.SymTabKeyImpl;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.SLOT;

/**
 * <h1>StatementParser</h1>
 *
 * <p>Parse a Pascal statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCStatementParser extends SubCParserTD
{
	/**
     * Constructor.
     * @param parent the parent parser.
     */
    public SubCStatementParser(SubCParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for starting a statement.
    protected static final EnumSet<SubCTokenType> STMT_START_SET =
        EnumSet.of(INT, VOID, FLOAT, WHILE, SubCTokenType.IF, WHILE, IDENTIFIER, SEMICOLON, CHAR, DOUBLE, LEFT_BRACE);

    // Synchronization set for following a statement.
    protected static final EnumSet<SubCTokenType> STMT_FOLLOW_SET =
        EnumSet.of(SEMICOLON, ELSE);
    
    /**
     * Parse a statement.
     * To be overridden by the specialized statement parser subclasses.
     * @param token the initial token.
     * @param parentId 
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token, SymTabEntry parentId)
        throws Exception
    {
        ICodeNode statementNode = null;

        switch ((SubCTokenType) token.getType()) {

            case LEFT_BRACE: {
                SubCCompoundStatementParser compoundParser =
                    new SubCCompoundStatementParser(this);
                statementNode = compoundParser.parse(token, parentId);
                break;
            }
            
            case IF:
            {
             SubCIfStatementParser ifParser = new SubCIfStatementParser(this);
            	statementNode = ifParser.parse(token, parentId);
            	break;   
            }
            case WHILE: {
            	SubCWhileStatementParser whileParser = new SubCWhileStatementParser(this);
            	statementNode = whileParser.parse(token, parentId);
            	break;
            }
            case INT:{
                SubCVariableDeclarationsParser variableDeclarationsParser = new SubCVariableDeclarationsParser(this);
                variableDeclarationsParser.parse(token, parentId);
                statementNode = null;
                break;
            }
            case FLOAT:{
                SubCVariableDeclarationsParser variableDeclarationsParser = new SubCVariableDeclarationsParser(this);
                variableDeclarationsParser.parse(token, parentId);
                statementNode = null;
                break;
            }
            case CHAR: {
                SubCVariableDeclarationsParser variableDeclarationsParser = new SubCVariableDeclarationsParser(this);
                variableDeclarationsParser.parse(token,parentId);
                statementNode = null;
                break;
            }
            
            case IDENTIFIER: {
            	SymTabEntry entry = symTabStack.lookup(token.getText());
            	if (entry == null) {
            		errorHandler.flag(token, SubCErrorCode.IDENTIFIER_UNDEFINED, this);
            		synchronize(STMT_FOLLOW_SET);
            		break;
            	}
            	switch((DefinitionImpl) entry.getDefinition()) {
            	case FUNCTION:
            	case PROCEDURE:
            		// Call parsing
            		SubCCallParser callParser = new SubCCallParser(this);
            	    statementNode = callParser.parse(token);
            		break;
            	default:
            		SubCAssignmentStatementParser assignmentParser = new SubCAssignmentStatementParser(this);
            		statementNode = assignmentParser.parse(token);
            	}
            	break;
            }
            
            case RETURN: {
                ICodeNode assignNode = ICodeFactory.createICodeNode(ASSIGN);
                assignNode.setTypeSpec(parentId.getTypeSpec());
            	SymTabEntry targetId = new SymTabEntryImpl(parentId.getName(), symTabStack.getLocalSymTab());
            	targetId.setDefinition(DefinitionImpl.VARIABLE);
            	targetId.setTypeSpec(parentId.getTypeSpec());
                
            	// Set its slot number in the local variables array.
                int slot = targetId.getSymTab().maxSlotNumber() + 1;
                targetId.setAttribute(SLOT, slot);

                // Create the variable node and set its name attribute.
                ICodeNode variableNode = ICodeFactory.createICodeNode(VARIABLE);
                variableNode.setAttribute(ID, targetId);
                variableNode.setTypeSpec(parentId.getTypeSpec());

                // The ASSIGN node adopts the variable node as its first child.
                assignNode.addChild(variableNode);
                token = nextToken(); // Consume RETURN
                
                if (token.getType() != SEMICOLON) {
	            	SubCExpressionParser expressionParser = new SubCExpressionParser(this);
	                assignNode.addChild(expressionParser.parse(token));
	                
	                if (parentId.getDefinition() == DefinitionImpl.PROCEDURE) {
	                	errorHandler.flag(token, SubCErrorCode.INVALID_ASSIGMENT_VOID, this);
	                }
                } else if (parentId.getDefinition() == DefinitionImpl.FUNCTION) {
                	errorHandler.flag(token, SubCErrorCode.INVALID_ASSIGMENT_VOID, this);
                }

                statementNode = assignNode;
                token = nextToken(); // Consume semicolon
                break;
            }

            default: {
                statementNode = ICodeFactory.createICodeNode(NO_OP);
                token = nextToken();
                break;
            }
        }
        
        if (currentToken().getType() == SEMICOLON) {
        	token = nextToken();
        }

        // Set the current line number as an attribute.
        setLineNumber(statementNode, token);
        return statementNode;
    }

    /**
     * Set the current line number as a statement node attribute.
     * @param node ICodeNode
     * @param token Token
     */
    protected void setLineNumber(ICodeNode node, Token token)
    {
        if (node != null) {
            node.setAttribute(LINE, token.getLineNumber());
        }
    }

    /**
     * Parse a statement list.
     * @param token the curent token.
     * @param parentNode the parent node of the statement list.
     * @param routineId 
     * @param terminator the token type of the node that terminates the list.
     * @param errorCode the error code if the terminator token is missing.
     * @throws Exception if an error occurred.
     */
    protected void parseList(Token token, ICodeNode parentNode, SymTabEntry rountineId,                     
                             SubCTokenType terminator,
                             SubCErrorCode errorCode)
        throws Exception
    {
        // Loop to parse each statement until the END token
        // or the end of the source file.
        while (!(token instanceof EofToken) &&
               (token.getType() != terminator)) {

            // Parse a statement.  The parent node adopts the statement node.
            ICodeNode statementNode = parse(token, rountineId);
            parentNode.addChild(statementNode);

            token = currentToken();
        }

        // Look for the terminator token.
        if (token.getType() == terminator) {
            token = nextToken();  // consume the terminator token
        }
        else {
            errorHandler.flag(token, errorCode, this);
        }
    }
}