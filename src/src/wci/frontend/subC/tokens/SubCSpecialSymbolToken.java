package src.wci.frontend.subC.tokens;

import src.wci.frontend.Source;
import src.wci.frontend.*;
import static src.wci.frontend.Source.EOL;
import src.wci.frontend.pascal.*;

import static src.wci.frontend.subC.SubCTokenType.*;
import static src.wci.frontend.subC.SubCErrorCode.*;
import src.wci.frontend.subC.SubCToken;

public class SubCSpecialSymbolToken extends SubCToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    public SubCSpecialSymbolToken(Source source)
        throws Exception
    {
        super(source);
    }

    /**
     * Extract a Pascal special symbol token from the source.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
        char currentChar = currentChar();

        text = Character.toString(currentChar);
        type = null;

        switch (currentChar) {

            // Single-character special symbols.
            case '+':  case '-':  case '*':  case '/':  case ',':
            case '\'': case ':':  case '(':  case ')':
            case '[':  case ']':  case '"':  case '{':  case '}':  case '^': {
                nextChar();  // consume character
                break;
            }
            case ';':{
                    currentChar = nextChar();
            }
            // : or :=
            case '=': {
                currentChar = nextChar();  // consume ':';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            // < or <= or <>
            case '<': {
                currentChar = nextChar();  // consume '<';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }
            case '!':{
                currentChar = nextChar();
                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }
                
                break;
            }

            // > or >=
            case '>': {
                currentChar = nextChar();  // consume '>';

                if (currentChar == '=') {
                    text += currentChar;
                    nextChar();  // consume '='
                }

                break;
            }

            // . or ..
            case '.': {
                currentChar = nextChar();  // consume '.';

                if (currentChar == '.') {
                    text += currentChar;
                    nextChar();  // consume '.'
                }

                break;
            }

            default: {
                nextChar();  // consume bad character
                type = ERROR;
                value = INVALID_CHARACTER;
            }
        }

        // Set the type if it wasn't an error.
        if (type == null) {
            type = SPECIAL_SYMBOLS.get(text);
        }
    }
}
