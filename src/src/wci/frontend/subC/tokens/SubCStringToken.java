package src.wci.frontend.subC.tokens;

import src.wci.frontend.Source;
import src.wci.frontend.*;
import src.wci.frontend.pascal.*;

import static src.wci.frontend.Source.EOL;
import static src.wci.frontend.Source.EOF;
import static src.wci.frontend.subC.SubCTokenType.*;
import static src.wci.frontend.subC.SubCErrorCode.*;
import src.wci.frontend.subC.SubCToken;

/**
 * <h1>PascalStringToken</h1>
 *
 * <p> Pascal string tokens.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCStringToken extends SubCToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public SubCStringToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a Pascal string token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        StringBuilder textBuffer = new StringBuilder();
        StringBuilder valueBuffer = new StringBuilder();

        char currentChar = nextChar();  // consume initial quote
        textBuffer.append('\'');

        // Get string characters.
        do {
            // Replace any whitespace character with a blank.
            if (Character.isWhitespace(currentChar)) {
                currentChar = ' ';
            }

            if ((currentChar != '\'') && (currentChar != EOF)) {
                textBuffer.append(currentChar);
                valueBuffer.append(currentChar);
                currentChar = nextChar();  // consume character
            }

            // Quote?  Each pair of adjacent quotes represents a single-quote.
            if (currentChar == '\'') {
                while ((currentChar == '\'') && (peekChar() == '\'')) {
                    textBuffer.append("\'\'");
                    valueBuffer.append(currentChar); // append single-quote
                    currentChar = nextChar();        // consume pair of quotes
                    currentChar = nextChar();
                }
            }
        } while ((currentChar != '\'') && (currentChar != EOF));

        if (currentChar == '\'') {
            nextChar();  // consume final quote
            textBuffer.append('\'');

            type = CHAR;
            value = valueBuffer.toString();
        }
        else {
            type = ERROR;
            value = UNEXPECTED_EOF;
        }

        text = textBuffer.toString();
    }
}
