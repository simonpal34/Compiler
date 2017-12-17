package src.wci.frontend.subC.tokens;

import src.wci.frontend.Source;
import src.wci.frontend.subC.SubCErrorCode;
import src.wci.frontend.subC.SubCToken;
import static src.wci.frontend.subC.SubCTokenType.ERROR;
import src.wci.frontend.*;
import src.wci.frontend.pascal.*;

import static src.wci.frontend.subC.SubCTokenType.*;

public class SubCErrorToken extends SubCToken
{
    /**
     * Constructor.
     * @param source the source from where to fetch subsequent characters.
     * @param errorCode the error code.
     * @param tokenText the text of the erroneous token.
     * @throws Exception if an error occurred.
     */
    public SubCErrorToken(Source source, SubCErrorCode errorCode,
                            String tokenText)
        throws Exception
    {
        super(source);

        this.text = tokenText;
        this.type = ERROR;
        this.value = errorCode;
    }

    /**
     * Do nothing.  Do not consume any source characters.
     * @throws Exception if an error occurred.
     */
    protected void extract()
        throws Exception
    {
    }
}
