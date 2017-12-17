package src.wci.frontend.subC;

import src.wci.frontend.EofToken;
import src.wci.frontend.Scanner;
import src.wci.frontend.Source;
import src.wci.frontend.*;
import static src.wci.frontend.Source.EOF;
import static src.wci.frontend.Source.EOL;
import src.wci.frontend.Token;
import static src.wci.frontend.subC.SubCErrorCode.INVALID_CHARACTER;
import src.wci.frontend.subC.tokens.SubCErrorToken;
import src.wci.frontend.subC.tokens.SubCNumberToken;
import src.wci.frontend.subC.tokens.SubCSpecialSymbolToken;
import src.wci.frontend.subC.tokens.SubCStringToken;
import src.wci.frontend.subC.tokens.SubCWordToken;


/**
 * <h1>PascalScanner</h1>
 *
 * <p>The Pascal scanner.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class SubCScanner extends Scanner
{
    /**
     * Constructor
     * @param source the source to be used with this scanner.
     */
    public SubCScanner(Source source)
    {
        super(source);
    }

    /**
     * Extract and return the next Pascal token from the source.
     * @return the next token.
     * @throws Exception if an error occurred.
     */
    protected Token extractToken()
        throws Exception
    {
        skipWhiteSpace();

        Token token;
        char currentChar = currentChar();

        // Construct the next token.  The current character determines the
        // token type.
        if (currentChar == EOF) {
            token = new EofToken(source);
        }
        else if (Character.isLetter(currentChar)) {
            token = new SubCWordToken(source);
        }
        else if (Character.isDigit(currentChar)) {
            token = new SubCNumberToken(source);
            
       
        }
        else if (currentChar == '\'') {
            token = new SubCStringToken(source);
        }
        
        else if (SubCTokenType.SPECIAL_SYMBOLS
                 .containsKey(Character.toString(currentChar))) {
            token = new SubCSpecialSymbolToken(source);
        }
        else {
            token = new SubCErrorToken(source, INVALID_CHARACTER,
                                         Character.toString(currentChar));
            nextChar();  // consume character
        }

        return token;
    }

    /**
     * Skip whitespace characters by consuming them.  A comment is whitespace.
     * @throws Exception if an error occurred.
     */
    private void skipWhiteSpace()
        throws Exception
    {
        char currentChar = currentChar();
        //start a comment
        while(Character.isWhitespace(currentChar) || (currentChar == '/' && (source.peekChar() == '/' || source.peekChar() == '*')) )
        {   if(currentChar == '/' &&source.peekChar() == '*')
            {
                currentChar = nextChar();
                do{
                    currentChar = nextChar();
                } while(currentChar != '*');
                currentChar = nextChar();
                        
            
            }
            // Single Line comment
            else if (currentChar == '/' && source.peekChar() == '/') {
                currentChar = nextChar();
                do {
                    currentChar = nextChar();  // consume comment characters
                } while ((currentChar != EOL ) && (currentChar != EOF));
                currentChar = nextChar();
            }
            else if(Character.isWhitespace(currentChar) || currentChar != EOL  )
            {
                currentChar = nextChar();
            }
            
            //multiline comment
        

        }
        
        
    }
    
     
}

