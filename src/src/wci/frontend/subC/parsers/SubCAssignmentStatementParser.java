package src.wci.frontend.subC.parsers;

import java.util.EnumSet;
import static src.wci.frontend.subC.SubCErrorCode.INCOMPATIBLE_TYPES;
import static src.wci.frontend.subC.SubCErrorCode.MISSING_COLON_EQUALS;
import static src.wci.frontend.subC.SubCErrorCode.MISSING_SEMICOLON;
import static src.wci.frontend.subC.SubCTokenType.EQUALS;
import static src.wci.frontend.subC.SubCTokenType.FLOAT;
import static src.wci.frontend.subC.SubCTokenType.INT;
import static src.wci.frontend.subC.SubCTokenType.SEMICOLON;
import static src.wci.intermediate.icodeimpl.ICodeKeyImpl.ID;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.ASSIGN;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.VARIABLE;
import src.wci.frontend.Token;
import src.wci.frontend.subC.SubCErrorCode;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.frontend.subC.SubCTokenType;
import static src.wci.frontend.subC.SubCTokenType.ASSIGNMENT;
import src.wci.intermediate.ICodeFactory;
import src.wci.intermediate.ICodeNode;
import src.wci.intermediate.SymTabEntry;
import src.wci.intermediate.TypeSpec;
import src.wci.intermediate.symtabimpl.Predefined;
import src.wci.intermediate.typeimpl.TypeChecker;

/**
 * <h1>AssignmentStatementParser</h1>
 *
 * <p>Parse a Pascal assignment statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCAssignmentStatementParser extends SubCStatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public SubCAssignmentStatementParser(SubCParserTD parent)
    {
        super(parent);
    }

    
    private static final EnumSet<SubCTokenType> EQUALS_SET = SubCExpressionParser.EXPR_START_SET.clone();
    static{
        EQUALS_SET.add(EQUALS);
        EQUALS_SET.addAll(SubCExpressionParser.STMT_FOLLOW_SET);
    }
    /**
     * Parse an assignment statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        // Create the ASSIGN node.
        ICodeNode assignNode = ICodeFactory.createICodeNode(ASSIGN);

        SubCVariableParser variableParser = new SubCVariableParser(this);
        
        ICodeNode targetNode = variableParser.parse(token);
                
        TypeSpec targetType = targetNode != null ? targetNode.getTypeSpec()
                : Predefined.undefinedType;
        
        // Look up the target identifer in the symbol table stack.
        // Enter the identifier into the table if it's not found.
        String targetName = token.getText();
        SymTabEntry targetId = symTabStack.lookup(targetName);
        if (targetId != null) {
            targetId.appendLineNumber(token.getLineNumber());
        } else {
        	errorHandler.flag(token, SubCErrorCode.IDENTIFIER_UNDEFINED, this);
        }

        token = currentToken();

        // Create the variable node and set its name attribute.
        ICodeNode variableNode = ICodeFactory.createICodeNode(VARIABLE);
        variableNode.setAttribute(ID, targetId);
        variableNode.setTypeSpec(targetId.getTypeSpec());

        // The ASSIGN node adopts the variable node as its first child.
        assignNode.addChild(variableNode);

        // Look for the := token.
        if (token.getType() == ASSIGNMENT) {
            token = nextToken();  // consume the :=
        }
        else {
            errorHandler.flag(token, MISSING_COLON_EQUALS, this);
        }

        // Parse the expression.  The ASSIGN node adopts the expression's
        // node as its second child.
        SubCExpressionParser expressionParser = new SubCExpressionParser(this);        
        ICodeNode exprNode = expressionParser.parse(token);
        assignNode.addChild(exprNode);
        
        // Type check: Assignment compatible?
        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                             : Predefined.undefinedType;
        if (!TypeChecker.areAssignmentCompatible(targetType, exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }
        
        token = currentToken();
        if (token.getType() == SEMICOLON) {
        	token = nextToken();
        } else {
        	errorHandler.flag(token, MISSING_SEMICOLON, this);
        }

        assignNode.setTypeSpec(targetType);
        return assignNode;
    }
}