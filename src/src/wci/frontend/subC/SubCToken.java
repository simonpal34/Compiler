package src.wci.frontend.subC;

import src.wci.frontend.Source;
import src.wci.frontend.Token;
import src.wci.frontend.*;

public class SubCToken extends Token
{
    /**
     * Constructor.
     * @param source the source from where to fetch the token's characters.
     * @throws Exception if an error occurred.
     */
    protected SubCToken(Source source)
        throws Exception
    {
        super(source);
    }
}

