package src.wci.frontend.subC.parsers;

import java.util.EnumSet;

import src.wci.frontend.*;
import src.wci.frontend.subC.*;
import src.wci.intermediate.*;
import src.wci.intermediate.icodeimpl.*;

import static src.wci.frontend.subC.SubCTokenType.*;
import static src.wci.frontend.subC.SubCErrorCode.*;
import static src.wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static src.wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>IfStatementParser</h1>
 *
 * <p>Parse a Pascal IF statement.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCIfStatementParser extends SubCStatementParser
{
    /**
     * Constructor.
     * @param parent the parent parser.
     */
    public SubCIfStatementParser(SubCParserTD parent)
    {
        super(parent);
    }

    // Synchronization set for THEN.
    

    /**
     * Parse an IF statement.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    @Override
	public ICodeNode parse(Token token, SymTabEntry parentId) throws Exception {
   ICodeNode controlNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.IF);

		token = nextToken(); // Consume the control statement

		if (token.getType() != LEFT_PAREN) {
			errorHandler.flag(token, SubCErrorCode.INVALID_EXPRESSION, this);
		} else {
			token = nextToken();
		}
		
		while (token.getType() != RIGHT_PAREN && token.getType() != ERROR) {
			ICodeNode testNode = controlNode;
			if (controlNode.getType() == LOOP) {
				testNode = testNode.addChild(ICodeFactory.createICodeNode(ICodeNodeTypeImpl.TEST));
			}
			SubCExpressionParser expr = new SubCExpressionParser(this);
			ICodeNode node = expr.parse(token);
			if (controlNode.getType() == LOOP) {
				ICodeNode not = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.NOT);
				not.addChild(node);
				node = not;
			}
			testNode.addChild(node);
			token = currentToken();
		}
		
		synchronize(EnumSet.of(RIGHT_PAREN));
		token = nextToken();
		
		SubCStatementParser parser;
		if (token.getType() == LEFT_BRACE) {
			parser = new SubCCompoundStatementParser(this);
		} else {
			parser = new SubCStatementParser(this);
		}
		
		controlNode.addChild(parser.parse(token, parentId));
		return controlNode;
	}
}
