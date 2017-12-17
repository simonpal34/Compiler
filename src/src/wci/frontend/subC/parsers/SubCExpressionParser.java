package src.wci.frontend.subC.parsers;


import static src.wci.frontend.subC.SubCErrorCode.IDENTIFIER_UNDEFINED;
import static src.wci.frontend.subC.SubCErrorCode.INCOMPATIBLE_TYPES;
import static src.wci.frontend.subC.SubCErrorCode.MISSING_RIGHT_PAREN;
import static src.wci.frontend.subC.SubCErrorCode.UNEXPECTED_TOKEN;
import static src.wci.frontend.subC.SubCTokenType.ASSIGNMENT;
import static src.wci.frontend.subC.SubCTokenType.EQUALS;
import static src.wci.frontend.subC.SubCTokenType.GREATER_EQUALS;
import static src.wci.frontend.subC.SubCTokenType.GREATER_THAN;
import static src.wci.frontend.subC.SubCTokenType.IDENTIFIER;
import static src.wci.frontend.subC.SubCTokenType.LEFT_PAREN;
import static src.wci.frontend.subC.SubCTokenType.LESS_EQUALS;
import static src.wci.frontend.subC.SubCTokenType.LESS_THAN;
import static src.wci.frontend.subC.SubCTokenType.MINUS;
import static src.wci.frontend.subC.SubCTokenType.NOT_EQUALS;
import static src.wci.frontend.subC.SubCTokenType.PLUS;
import static src.wci.frontend.subC.SubCTokenType.RIGHT_PAREN;
import static src.wci.frontend.subC.SubCTokenType.SLASH;
import static src.wci.frontend.subC.SubCTokenType.STAR;
import static src.wci.frontend.subC.SubCTokenType.STRING;
import static src.wci.frontend.subC.SubCTokenType.TRUE;
import static src.wci.intermediate.icodeimpl.ICodeKeyImpl.VALUE;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.ADD;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.BOOLEAN_CONSTANT;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.EQ;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.FLOAT_DIVIDE;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.GE;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.GT;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.INTEGER_CONSTANT;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.LE;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.LT;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.MULTIPLY;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.NE;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.NEGATE;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.REAL_CONSTANT;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.STRING_CONSTANT;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.SUBTRACT;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.UNDEFINED;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.CONSTANT_VALUE;

import java.util.EnumSet;
import java.util.HashMap;

import src.wci.frontend.Token;
import src.wci.frontend.TokenType;
import src.wci.frontend.subC.SubCErrorCode;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.frontend.subC.SubCTokenType;
import static src.wci.frontend.subC.SubCTokenType.CHAR;
import src.wci.intermediate.Definition;
import src.wci.intermediate.ICodeFactory;
import src.wci.intermediate.ICodeNode;
import src.wci.intermediate.ICodeNodeType;
import src.wci.intermediate.SymTabEntry;
import src.wci.intermediate.TypeSpec;
import src.wci.intermediate.icodeimpl.ICodeKeyImpl;
import src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.CHAR_CONSTANT;
import src.wci.intermediate.symtabimpl.DefinitionImpl;
import src.wci.intermediate.symtabimpl.Predefined;
import src.wci.intermediate.typeimpl.TypeChecker;

/**
 * <h1>ExpressionParser</h1>
 *
 * <p>Parse a Pascal expression.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCExpressionParser extends SubCStatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public SubCExpressionParser(SubCParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse an expression.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        return parseExpression(token);
    }

    // Synchronization set for starting an expression.
    static final EnumSet<SubCTokenType> EXPR_START_SET =
        EnumSet.of(PLUS, MINUS, IDENTIFIER, STRING, LEFT_PAREN);
    
    // Set of relational operators.
    private static final EnumSet<SubCTokenType> REL_OPS =
        EnumSet.of(EQUALS, NOT_EQUALS, LESS_THAN, LESS_EQUALS,
                   GREATER_THAN, GREATER_EQUALS, ASSIGNMENT);

    // Map relational operator tokens to node types.
    private static final HashMap<SubCTokenType, ICodeNodeType>
        REL_OPS_MAP = new HashMap<>();
    static {
        REL_OPS_MAP.put(EQUALS, EQ);
        REL_OPS_MAP.put(NOT_EQUALS, NE);
        REL_OPS_MAP.put(LESS_THAN, LT);
        REL_OPS_MAP.put(LESS_EQUALS, LE);
        REL_OPS_MAP.put(GREATER_THAN, GT);
        REL_OPS_MAP.put(GREATER_EQUALS, GE);
    };

    // Set of additive operators.
    private static final EnumSet<SubCTokenType> ADD_OPS =
        EnumSet.of(PLUS, MINUS, SubCTokenType.OR);

    // Map additive operator tokens to node types.
    private static final HashMap<SubCTokenType, ICodeNodeTypeImpl>
        ADD_OPS_OPS_MAP = new HashMap<>();
    static {
        ADD_OPS_OPS_MAP.put(PLUS, ADD);
        ADD_OPS_OPS_MAP.put(MINUS, SUBTRACT);
        ADD_OPS_OPS_MAP.put(SubCTokenType.OR, ICodeNodeTypeImpl.OR);
    };

    // Set of multiplicative operators.
    private static final EnumSet<SubCTokenType> MULT_OPS =
        EnumSet.of(STAR, SLASH, SubCTokenType.MOD, SubCTokenType.AND);

    // Map multiplicative operator tokens to node types.
    private static final HashMap<SubCTokenType, ICodeNodeType>
        MULT_OPS_OPS_MAP = new HashMap<>();
    static {
        MULT_OPS_OPS_MAP.put(STAR, MULTIPLY);
        MULT_OPS_OPS_MAP.put(SLASH, FLOAT_DIVIDE);
        MULT_OPS_OPS_MAP.put(SubCTokenType.MOD, ICodeNodeTypeImpl.MOD);
        MULT_OPS_OPS_MAP.put(SubCTokenType.AND, ICodeNodeTypeImpl.AND);
    };
    
    /**
     * Parse an expression.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseExpression(Token token)
        throws Exception
    {
        // Parse a simple expression and make the root of its tree
        // the root node.
        ICodeNode rootNode = parseSimpleExpression(token);
        TypeSpec resultType = rootNode != null ? rootNode.getTypeSpec()
                                               : Predefined.undefinedType;

        token = currentToken();
        TokenType tokenType = token.getType();

        // Look for a relational operator.
        if (REL_OPS.contains(tokenType)) {

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = REL_OPS_MAP.get(tokenType);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            token = nextToken();  // consume the operator

            // Parse the second simple expression.  The operator node adopts
            // the simple expression's tree as its second child.
            ICodeNode simExprNode = parseSimpleExpression(token);
            opNode.addChild(simExprNode);

            // The operator node becomes the new root node.
            rootNode = opNode;

            // Type check: The operands must be comparison compatible.
            TypeSpec simExprType = simExprNode != null
                                       ? simExprNode.getTypeSpec()
                                       : Predefined.undefinedType;
            if (TypeChecker.areComparisonCompatible(resultType, simExprType)) {
                resultType = Predefined.integerType;
            }
            else {
                errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                resultType = Predefined.undefinedType;
            }
        }

        if (rootNode != null) {
            rootNode.setTypeSpec(resultType);
        }

        return rootNode;
    }

    // Set of additive operators

    /**
     * Parse a simple expression.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseSimpleExpression(Token token)
        throws Exception
    {
        Token signToken = null;
        TokenType signType = null;  // type of leading sign (if any)

        // Look for a leading + or - sign.
        TokenType tokenType = token.getType();
        if ((tokenType == PLUS) || (tokenType == MINUS)) {
            signType = tokenType;
            signToken = token;
            token = nextToken();  // consume the + or -
        }

        // Parse a term and make the root of its tree the root node.
        ICodeNode rootNode = parseTerm(token);
        TypeSpec resultType = rootNode != null ? rootNode.getTypeSpec()
                                               : Predefined.undefinedType;

        // Type check: Leading sign.
        if ((signType != null) && (!TypeChecker.isIntegerOrReal(resultType))) {
            errorHandler.flag(signToken, INCOMPATIBLE_TYPES, this);
        }

        // Was there a leading - sign?
        if (signType == MINUS) {

            // Create a NEGATE node and adopt the current tree
            // as its child. The NEGATE node becomes the new root node.
            ICodeNode negateNode = ICodeFactory.createICodeNode(NEGATE);
            negateNode.addChild(rootNode);
            negateNode.setTypeSpec(rootNode.getTypeSpec());
            rootNode = negateNode;
        }

        token = currentToken();
        tokenType = token.getType();

        // Loop over additive operators.
        while (ADD_OPS.contains(tokenType)) {
            TokenType operator = tokenType;

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = ADD_OPS_OPS_MAP.get(operator);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            token = nextToken();  // consume the operator

            // Parse another term.  The operator node adopts
            // the term's tree as its second child.
            ICodeNode termNode = parseTerm(token);
            opNode.addChild(termNode);
            TypeSpec termType = termNode != null ? termNode.getTypeSpec()
                                                 : Predefined.undefinedType;

            // The operator node becomes the new root node.
            rootNode = opNode;

            // Determine the result type.
            switch ((SubCTokenType) operator) {

                case PLUS:
                case MINUS: {
                    // Both operands integer ==> integer result.
                    if (TypeChecker.areBothInteger(resultType, termType)) {
                        resultType = Predefined.integerType;
                    }

                    // Both real operands or one real and one integer operand
                    // ==> real result.
                    else if (TypeChecker.isAtLeastOneReal(resultType,
                                                          termType)) {
                        resultType = Predefined.realType;
                    }

                    else {
                        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                    }

                    break;
                }

                case OR: {
                    // Both operands boolean ==> boolean result.
                    if (TypeChecker.areBothBoolean(resultType, termType)) {
                        resultType = Predefined.booleanType;
                    }
                    else {
                        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                    }

                    break;
                }
            }

            rootNode.setTypeSpec(resultType);

            token = currentToken();
            tokenType = token.getType();
        }

        return rootNode;
    }

    /**
     * Parse a term.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseTerm(Token token)
        throws Exception
    {
        // Parse a factor and make its node the root node.
        ICodeNode rootNode = parseFactor(token);
        TypeSpec resultType = rootNode != null ? rootNode.getTypeSpec()
                                               : Predefined.undefinedType;

        token = currentToken();
        TokenType tokenType = token.getType();

        // Loop over multiplicative operators.
        while (MULT_OPS.contains(tokenType)) {
            TokenType operator = tokenType;

            // Create a new operator node and adopt the current tree
            // as its first child.
            ICodeNodeType nodeType = MULT_OPS_OPS_MAP.get(operator);
            ICodeNode opNode = ICodeFactory.createICodeNode(nodeType);
            opNode.addChild(rootNode);

            token = nextToken();  // consume the operator

            // Parse another factor.  The operator node adopts
            // the term's tree as its second child.
            ICodeNode factorNode = parseFactor(token);
            opNode.addChild(factorNode);
            TypeSpec factorType = factorNode != null ? factorNode.getTypeSpec()
                                                     : Predefined.undefinedType;

            // The operator node becomes the new root node.
            rootNode = opNode;

            // Determine the result type.
            switch ((SubCTokenType) operator) {

                case STAR: {
                    // Both operands integer ==> integer result.
                    if (TypeChecker.areBothInteger(resultType, factorType)) {
                        resultType = Predefined.integerType;
                    }

                    // Both real operands or one real and one integer operand
                    // ==> real result.
                    else if (TypeChecker.isAtLeastOneReal(resultType,
                                                          factorType)) {
                        resultType = Predefined.realType;
                    }

                    else {
                        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                    }

                    break;
                }

                case SLASH: {
                    // All integer and real operand combinations
                    // ==> real result.
                    if (TypeChecker.areBothInteger(resultType, factorType) ||
                        TypeChecker.isAtLeastOneReal(resultType, factorType))
                    {
                        resultType = Predefined.realType;
                    }
                    else {
                        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                    }

                    break;
                }

                case MOD: {
                    // Both operands integer ==> integer result.
                    if (TypeChecker.areBothInteger(resultType, factorType)) {
                        resultType = Predefined.integerType;
                    }
                    else {
                        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                    }

                    break;
                }

                case AND: {
                    // Both operands boolean ==> boolean result.
                    if (TypeChecker.areBothBoolean(resultType, factorType)) {
                        resultType = Predefined.booleanType;
                    }
                    else {
                        errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                    }

                    break;
                }
            }

            rootNode.setTypeSpec(resultType);

            token = currentToken();
            tokenType = token.getType();
        }

        return rootNode;
    }

    /**
     * Parse a factor.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseFactor(Token token)
        throws Exception
    {
        TokenType tokenType = token.getType();
        ICodeNode rootNode = null;

        switch ((SubCTokenType) tokenType) {

            case IDENTIFIER: {
                return parseIdentifier(token);
            }

            case INTEGER: {
                // Create an INTEGER_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();  // consume the number

                rootNode.setTypeSpec(Predefined.integerType);
                break;
            }

            case FLOAT: {
                // Create an REAL_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(REAL_CONSTANT);
                rootNode.setAttribute(VALUE, token.getValue());

                token = nextToken();  // consume the number

                rootNode.setTypeSpec(Predefined.realType);
                break;
            }

            case CHAR: {
                String value = (String) token.getValue();

                // Create a STRING_CONSTANT node as the root node.
                rootNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
                rootNode.setAttribute(VALUE, value);
                rootNode.setTypeSpec(Predefined.charType);  

                token = nextToken();  // consume the string

                break;
            }


            
            case TRUE:
            case FALSE: {
            	rootNode = ICodeFactory.createICodeNode(BOOLEAN_CONSTANT);
            	rootNode.setAttribute(VALUE, tokenType == TRUE ? true : false);
            	
            	token = nextToken();
            	break;
            }

            case LEFT_PAREN: {
                token = nextToken();      // consume the (

                // Parse an expression and make its node the root node.
                rootNode = parseExpression(token);
                TypeSpec resultType = rootNode != null
                                          ? rootNode.getTypeSpec()
                                          : Predefined.undefinedType;

                // Look for the matching ) token.
                token = currentToken();
                if (token.getType() == RIGHT_PAREN) {
                    token = nextToken();  // consume the )
                }
                else {
                    errorHandler.flag(token, MISSING_RIGHT_PAREN, this);
                }

                rootNode.setTypeSpec(resultType);
                break;
            }

            default: {
                errorHandler.flag(token, UNEXPECTED_TOKEN, this);
            }
        }

        return rootNode;
    }

    /**
     * Parse an identifier.
     * @param token the current token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseIdentifier(Token token)
        throws Exception
    {
        ICodeNode rootNode = null;

        // Look up the identifier in the symbol table stack.
        String name = token.getText().toLowerCase();
        SymTabEntry id = symTabStack.lookup(name);

        // Undefined.
        if (id == null) {
            errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
            id = symTabStack.enterLocal(name);
            id.setDefinition(UNDEFINED);
            id.setTypeSpec(Predefined.undefinedType);
        }

        Definition defnCode = id.getDefinition();

        switch ((DefinitionImpl) defnCode) {

            case CONSTANT: {
                Object value = id.getAttribute(CONSTANT_VALUE);
                TypeSpec type = id.getTypeSpec();

                if (value instanceof Integer) {
                    rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
                    rootNode.setAttribute(VALUE, value);
                }
                else if (value instanceof Float) {
                    rootNode = ICodeFactory.createICodeNode(REAL_CONSTANT);
                    rootNode.setAttribute(VALUE, value);
                }
                else if (value instanceof String) {
                    rootNode = ICodeFactory.createICodeNode(STRING_CONSTANT);
                    rootNode.setAttribute(VALUE, value);
                }

                id.appendLineNumber(token.getLineNumber());
                token = nextToken();  // consume the constant identifier

                if (rootNode != null) {
                    rootNode.setTypeSpec(type);
                }

                break;
            }

            case ENUMERATION_CONSTANT: {
                Object value = id.getAttribute(CONSTANT_VALUE);
                TypeSpec type = id.getTypeSpec();

                rootNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
                rootNode.setAttribute(VALUE, value);

                id.appendLineNumber(token.getLineNumber());
                token = nextToken();  // consume the enum constant identifier

                rootNode.setTypeSpec(type);
                break;
            }

            case FUNCTION: {
                SubCCallParser callParser = new SubCCallParser(this);
                rootNode = callParser.parse(token);
                break;
            }
            
            case PROCEDURE: {
            	errorHandler.flag(token, SubCErrorCode.INVALID_ASSIGMENT_VOID, this);
            	synchronize(STMT_FOLLOW_SET);
            	break;
            }

            default: {
            	rootNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.VARIABLE);
            	rootNode.setAttribute(ICodeKeyImpl.ID, id);
            	rootNode.setTypeSpec(id.getTypeSpec());
                TypeSpec t = id.getTypeSpec();
            	token = nextToken();
                break;
            }
        }

        return rootNode;
    }
}