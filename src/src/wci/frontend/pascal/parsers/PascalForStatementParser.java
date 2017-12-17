package src.wci.frontend.pascal.parsers;

import java.util.EnumSet;

import src.wci.frontend.*;
import src.wci.frontend.pascal.*;
import src.wci.intermediate.*;
import src.wci.intermediate.symtabimpl.*;
import src.wci.intermediate.typeimpl.*;

import static src.wci.frontend.pascal.PascalTokenType.*;
import static src.wci.frontend.pascal.PascalErrorCode.*;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static src.wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static src.wci.intermediate.typeimpl.TypeFormImpl.ENUMERATION;

/**
 * <h1>PascalForStatementParser</h1>
 *
 * <p>Parse a FOR statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class PascalForStatementParser extends PascalStatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public PascalForStatementParser(PascalParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for TO or DOWNTO.
    private static final EnumSet<PascalTokenType> TO_DOWNTO_SET =
        PascalExpressionParser.EXPR_START_SET.clone();
    static {
        TO_DOWNTO_SET.add(TO);
        TO_DOWNTO_SET.add(DOWNTO);
        TO_DOWNTO_SET.addAll(PascalStatementParser.STMT_FOLLOW_SET);
    }

    // Synchronization set for DO.
    private static final EnumSet<PascalTokenType> DO_SET =
        PascalStatementParser.STMT_START_SET.clone();
    static {
        DO_SET.add(DO);
        DO_SET.addAll(PascalStatementParser.STMT_FOLLOW_SET);
    }

    /**
     * Parse the FOR statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        token = nextToken();  // consume the FOR
        Token targetToken = token;

        // Create the loop COMPOUND, LOOP, and TEST nodes.
        ICodeNode compoundNode = ICodeFactory.createICodeNode(COMPOUND);
        ICodeNode loopNode = ICodeFactory.createICodeNode(LOOP);
        ICodeNode testNode = ICodeFactory.createICodeNode(TEST);

        // Parse the embedded initial assignment.
        PascalAssignmentStatementParser assignmentParser =
            new PascalAssignmentStatementParser(this);
        ICodeNode initAssignNode = assignmentParser.parse(token);
        TypeSpec controlType = initAssignNode != null
                                   ? initAssignNode.getTypeSpec()
                                   : Predefined.undefinedType;

        // Set the current line number attribute.
        setLineNumber(initAssignNode, targetToken);

        // Type check: The control variable's type must be integer
        //             or enumeration.
        if (!TypeChecker.isInteger(controlType) &&
            (controlType.getForm() != ENUMERATION))
        {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }

        // The COMPOUND node adopts the initial ASSIGN and the LOOP nodes
        // as its first and second children.
        compoundNode.addChild(initAssignNode);
        compoundNode.addChild(loopNode);

        // Synchronize at the TO or DOWNTO.
        token = synchronize(TO_DOWNTO_SET);
        TokenType direction = token.getType();

        // Look for the TO or DOWNTO.
        if ((direction == TO) || (direction == DOWNTO)) {
            token = nextToken();  // consume the TO or DOWNTO
        }
        else {
            direction = TO;
            errorHandler.flag(token, MISSING_TO_DOWNTO, this);
        }

        // Create a relational operator node: GT for TO, or LT for DOWNTO.
        ICodeNode relOpNode = ICodeFactory.createICodeNode(direction == TO
                                                           ? GT : LT);
        relOpNode.setTypeSpec(Predefined.booleanType);

        // Copy the control VARIABLE node. The relational operator
        // node adopts the copied VARIABLE node as its first child.
        ICodeNode controlVarNode = initAssignNode.getChildren().get(0);
        relOpNode.addChild(controlVarNode.copy());

        // Parse the termination expression. The relational operator node
        // adopts the expression as its second child.
        PascalExpressionParser expressionParser = new PascalExpressionParser(this);
        ICodeNode exprNode = expressionParser.parse(token);
        relOpNode.addChild(exprNode);

        // Type check: The termination expression type must be assignment
        //             compatible with the control variable's type.
        TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec()
                                             : Predefined.undefinedType;
        if (!TypeChecker.areAssignmentCompatible(controlType, exprType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }

        // The TEST node adopts the relational operator node as its only child.
        // The LOOP node adopts the TEST node as its first child.
        testNode.addChild(relOpNode);
        loopNode.addChild(testNode);

        // Synchronize at the DO.
        token = synchronize(DO_SET);
        if (token.getType() == DO) {
            token = nextToken();  // consume the DO
        }
        else {
            errorHandler.flag(token, MISSING_DO, this);
        }

        // Parse the nested statement. The LOOP node adopts the statement
        // node as its second child.
        PascalStatementParser statementParser = new PascalStatementParser(this);
        loopNode.addChild(statementParser.parse(token));

        // Create an assignment with a copy of the control variable
        // to advance the value of the variable.
        ICodeNode nextAssignNode = ICodeFactory.createICodeNode(ASSIGN);
        nextAssignNode.setTypeSpec(controlType);
        nextAssignNode.addChild(controlVarNode.copy());

        // Create the arithmetic operator node:
        // ADD for TO, or SUBTRACT for DOWNTO.
        ICodeNode arithOpNode = ICodeFactory.createICodeNode(direction == TO
                                                             ? ADD : SUBTRACT);
        arithOpNode.setTypeSpec(Predefined.integerType);

        // The next operator node adopts a copy of the loop variable as its
        // first child and the value 1 as its second child.
        arithOpNode.addChild(controlVarNode.copy());
        ICodeNode oneNode = ICodeFactory.createICodeNode(INTEGER_CONSTANT);
        oneNode.setAttribute(VALUE, 1);
        oneNode.setTypeSpec(Predefined.integerType);
        arithOpNode.addChild(oneNode);

        // The next ASSIGN node adopts the arithmetic operator node as its
        // second child. The loop node adopts the next ASSIGN node as its
        // third child.
        nextAssignNode.addChild(arithOpNode);
        loopNode.addChild(nextAssignNode);

        // Set the current line number attribute.
        setLineNumber(nextAssignNode, targetToken);

        return compoundNode;
    }
}
