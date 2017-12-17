package src.wci.frontend.subC;

import java.util.Hashtable;
import java.util.HashSet;

import src.wci.frontend.TokenType;

/**
 * <h1>PascalTokenType</h1>
 *
 * <p>Pascal token types.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public enum SubCTokenType implements TokenType
{
    // Reserved words.
    AND, ARRAY, BEGIN,BREAK,AUTO, CASE,CONTINUE, CONST, DIV, DO,DEFAULT, DOWNTO, ELSE, END,
    FILE, FOR, FUNCTION, GOTO, IF, IN, LABEL, MOD, NIL, NOT,
    OF, OR, PACKED, STATIC, PROCEDURE, PROGRAM, REPEAT, SET,
    THEN, TO, TYPE, UNTIL, VAR, WHILE, PRINTF, RETURN, VOID, TRUE, FALSE, INT,CHAR, DOUBLE, FLOAT, WITH,

    // Special symbols.
    PLUS("+"), MINUS("-"), STAR("*"), SLASH("/"), EQUIVILENCE("=="),
    DOT("."), COMMA(","), SEMICOLON(";"), COLON(":"), QUOTE("\""),
    ASSIGNMENT("="), NOT_EQUALS("!="), LESS_THAN("<"), LESS_EQUALS("<="),EXCLIMATION("!"),
    GREATER_EQUALS(">="), EQUALS("=="), GREATER_THAN(">"), LEFT_PAREN("("), RIGHT_PAREN(")"),
    LEFT_BRACKET("["), RIGHT_BRACKET("]"), LEFT_BRACE("{"), RIGHT_BRACE("}"),
    UP_ARROW("^"),

    IDENTIFIER, INTEGER, REAL, STRING,
    ERROR, END_OF_FILE;

    private static final int FIRST_RESERVED_INDEX = AND.ordinal();
    private static final int LAST_RESERVED_INDEX  = WITH.ordinal();

    private static final int FIRST_SPECIAL_INDEX = PLUS.ordinal();
    private static final int LAST_SPECIAL_INDEX  = UP_ARROW.ordinal();

    private String text;  // token text

    /**
     * Constructor.
     */
    SubCTokenType()
    {
        this.text = this.toString().toLowerCase();
    }

    /**
     * Constructor.
     * @param text the token text.
     */
    SubCTokenType(String text)
    {
        this.text = text;
    }

    /**
     * Getter.
     * @return the token text.
     */
    public String getText()
    {
        return text;
    }

    // Set of lower-cased Pascal reserved word text strings.
    public static HashSet<String> RESERVED_WORDS = new HashSet<String>();
    static {
        SubCTokenType values[] = SubCTokenType.values();
        for (int i = FIRST_RESERVED_INDEX; i <= LAST_RESERVED_INDEX; ++i) {
            RESERVED_WORDS.add(values[i].getText().toLowerCase());
        }
    }

    // Hash table of Pascal special symbols.  Each special symbol's text
    // is the key to its Pascal token type.
    public static Hashtable<String, SubCTokenType> SPECIAL_SYMBOLS =
        new Hashtable<String, SubCTokenType>();
    static {
        SubCTokenType values[] = SubCTokenType.values();
        for (int i = FIRST_SPECIAL_INDEX; i <= LAST_SPECIAL_INDEX; ++i) {
            SPECIAL_SYMBOLS.put(values[i].getText(), values[i]);
        }
    }
}

