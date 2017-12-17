
package src.wci.frontend.subC.parsers;

import static src.wci.frontend.subC.SubCErrorCode.INCOMPATIBLE_TYPES;
import static src.wci.frontend.subC.SubCErrorCode.INVALID_NUMBER;
import static src.wci.frontend.subC.SubCErrorCode.INVALID_VAR_PARM;
import static src.wci.frontend.subC.SubCErrorCode.MISSING_COMMA;
import static src.wci.frontend.subC.SubCErrorCode.WRONG_NUMBER_OF_PARMS;
import static src.wci.frontend.subC.SubCTokenType.COLON;
import static src.wci.frontend.subC.SubCTokenType.COMMA;
import static src.wci.frontend.subC.SubCTokenType.LEFT_PAREN;
import static src.wci.frontend.subC.SubCTokenType.RIGHT_PAREN;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.INTEGER_CONSTANT;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.PARAMETERS;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.WRITE_PARM;
import static src.wci.intermediate.symtabimpl.DefinitionImpl.VAR_PARM;
import static src.wci.intermediate.symtabimpl.RoutineCodeImpl.DECLARED;
import static src.wci.intermediate.symtabimpl.RoutineCodeImpl.FORWARD;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_CODE;
import static src.wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_PARMS;
import static src.wci.intermediate.typeimpl.TypeFormImpl.SCALAR;
import static src.wci.intermediate.typeimpl.TypeFormImpl.SUBRANGE;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Optional;

import src.wci.frontend.Token;
import src.wci.frontend.TokenType;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.frontend.subC.SubCTokenType;
import src.wci.intermediate.Definition;
import src.wci.intermediate.ICodeFactory;
import src.wci.intermediate.ICodeNode;
import src.wci.intermediate.RoutineCode;
import src.wci.intermediate.SymTabEntry;
import src.wci.intermediate.TypeForm;
import src.wci.intermediate.TypeSpec;
import src.wci.intermediate.icodeimpl.ICodeKeyImpl;
import src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl;
import src.wci.intermediate.symtabimpl.Predefined;
import src.wci.intermediate.typeimpl.TypeChecker;

/**
 * <h1>CallParser</h1>
 *
 * <p>Parse a called to a procedure or function.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCCallParser extends SubCStatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public SubCCallParser(SubCParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a call to a declared procedure or function.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token)
        throws Exception
    {
        SymTabEntry pfId = symTabStack.lookup(token.getText());
        RoutineCode routineCode = (RoutineCode) pfId.getAttribute(ROUTINE_CODE);
        SubCCallParser callParser = (routineCode == DECLARED) ||
                (routineCode == FORWARD)
                    ? new SubCCallDeclaredParser(this)
                    : new SubCCallStandardParser(this);

        return callParser.parse(token);
    }

    // Synchronization set for the , token.
    private static final EnumSet<SubCTokenType> COMMA_SET = EnumSet.of(COMMA, RIGHT_PAREN);
    
    /**
     * Parse the actual parameters of a procedure or function call.
     * @param token the current token.
     * @param pfId the symbol table entry of the procedure or function name.
     * @param isDeclared true if parsing actual parms of a declared routine.
     * @param isReadReadln true if parsing actual parms of read or readln.
     * @param isWriteWriteln true if parsing actual parms of write or writeln.
     * @return the PARAMETERS node, or null if there are no actual parameters.
     * @throws Exception if an error occurred.
     */
    protected ICodeNode parseActualParameters(Token token, SymTabEntry pfId,
                                              boolean isDeclared,
                                              boolean isReadReadln,
                                              boolean isWriteWriteln)
        throws Exception
    {
        SubCExpressionParser expressionParser = new SubCExpressionParser(this);
        ICodeNode parmsNode = ICodeFactory.createICodeNode(PARAMETERS);
        ArrayList<SymTabEntry> formalParms = null;
        int parmCount = 0;
        int parmIndex = -1;

        if (isDeclared) {
            formalParms =
                (ArrayList<SymTabEntry>) pfId.getAttribute(ROUTINE_PARMS);
            parmCount = formalParms != null ? formalParms.size() : 0;
        }

        if (token.getType() != LEFT_PAREN) {
            if (parmCount != 0) {
                errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
            }

            return null;
        }

        token = nextToken();  // consume opening (

        // Loop to parse each actual parameter.
        while (token.getType() != RIGHT_PAREN) {
            ICodeNode actualNode = expressionParser.parse(token);

            // Declared procedure or function: Check the number of actual
            // parameters, and check each actual parameter against the
            // corresponding formal parameter.
            if (isDeclared) {
                if (++parmIndex < parmCount) {
                    SymTabEntry formalId = formalParms.get(parmIndex);
                    checkActualParameter(token, formalId, actualNode);
                }
                else if (parmIndex == parmCount) {
                    errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
                }
            }

            // read or readln: Each actual parameter must be a variable that is
            //                 a scalar, boolean, or subrange of integer.
            else if (isReadReadln) {
                TypeSpec type = actualNode.getTypeSpec();
                TypeForm form = type.getForm();

                if (! (   (actualNode.getType() == ICodeNodeTypeImpl.VARIABLE)
                       && ( (form == SCALAR) ||
                            (type == Predefined.booleanType) ||
                            ( (form == SUBRANGE) &&
                              (type.baseType() == Predefined.integerType) ) )
                      )
                   )
                {
                    errorHandler.flag(token, INVALID_VAR_PARM, this);
                }
            }

            // write or writeln: The type of each actual parameter must be a
            // scalar, boolean, or a Pascal string. Parse any field width and
            // precision.
            else if (isWriteWriteln) {

                // Create a WRITE_PARM node which adopts the expression node.
                ICodeNode exprNode = actualNode;
                actualNode = ICodeFactory.createICodeNode(WRITE_PARM);
                actualNode.addChild(exprNode);

                TypeSpec type = exprNode.getTypeSpec().baseType();
                TypeForm form = type.getForm();

                if (! ( (form == SCALAR) || (type == Predefined.booleanType) ||
                        (type.isPascalString())
                      )
                   )
                {
                    errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
                }

                // Optional field width.
                token = currentToken();
                actualNode.addChild(parseWriteSpec(token));

                // Optional precision.
                token = currentToken();
                actualNode.addChild(parseWriteSpec(token));
            }

            parmsNode.addChild(actualNode);
            token = synchronize(COMMA_SET);
            TokenType tokenType = token.getType();

            // Look for the comma.
            if (tokenType == COMMA) {
                token = nextToken();  // consume ,
            }
            else if (SubCExpressionParser.EXPR_START_SET.contains(tokenType)) {
                errorHandler.flag(token, MISSING_COMMA, this);
            }
            else if (tokenType != RIGHT_PAREN) {
                token = synchronize(SubCExpressionParser.EXPR_START_SET);
            }
        }

        token = nextToken();  // consume closing )

        if (isDeclared && parmsNode.getChildren().size() != parmCount)
        {
            errorHandler.flag(token, WRONG_NUMBER_OF_PARMS, this);
        }

        return parmsNode;
    }

    /**
     * Check an actual parameter against the corresponding formal parameter.
     * @param token the current token.
     * @param formalId the symbol table entry of the formal parameter.
     * @param actualNode the parse tree node of the actual parameter.
     */
    private void checkActualParameter(Token token, SymTabEntry formalId,
                                      ICodeNode actualNode)
    {
        Definition formalDefn = formalId.getDefinition();
        TypeSpec formalType = formalId.getTypeSpec();
        TypeSpec actualType = Optional.ofNullable(((SymTabEntry)actualNode.getAttribute(ICodeKeyImpl.ID))).map(SymTabEntry::getTypeSpec).orElse(actualNode.getTypeSpec());

        // VAR parameter: The actual parameter must be a variable of the same
        //                type as the formal parameter.
        if (formalDefn == VAR_PARM) {
            if ((actualNode.getType() != ICodeNodeTypeImpl.VARIABLE) ||
                (actualType != formalType))
            {
                errorHandler.flag(token, INVALID_VAR_PARM, this);
            }
        }

        // Value parameter: The actual parameter must be assignment-compatible
        //                  with the formal parameter.
        else if (!TypeChecker.areAssignmentCompatible(formalType, actualType)) {
            errorHandler.flag(token, INCOMPATIBLE_TYPES, this);
        }
    }

    /**
     * Parse the field width or the precision for an actual parameter
     * of a call to write or writeln.
     * @param token the current token.
     * @return the INTEGER_CONSTANT node or null
     * @throws Exception if an error occurred.
     */
    private ICodeNode parseWriteSpec(Token token)
        throws Exception
    {
        if (token.getType() == COLON) {
            token = nextToken();  // consume :

            SubCExpressionParser expressionParser = new SubCExpressionParser(this);
            ICodeNode specNode = expressionParser.parse(token);

            if (specNode.getType() == INTEGER_CONSTANT) {
                return specNode;
            }
            else {
                errorHandler.flag(token, INVALID_NUMBER, this);
                return null;
            }
        }
        else {
            return null;
        }
    }
}
