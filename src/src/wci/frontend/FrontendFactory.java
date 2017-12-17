package src.wci.frontend;

import src.wci.frontend.pascal.PascalParserTD;
import src.wci.frontend.pascal.PascalScanner;
import src.wci.frontend.subC.SubCParserTD;
import src.wci.frontend.subC.SubCScanner;

/**
 * <h1>FrontendFactory</h1>
 *
 * <p>A factory class that creates parsers for specific source languages.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class FrontendFactory
{
    /**
     * Create a parser.
     * @param language the name of the source language (e.g., "Pascal").
     * @param type the type of parser (e.g., "top-down").
     * @param source the source object.
     * @return the parser.
     * @throws Exception if an error occurred.
     */
    public static Parser createParser(String language, String type,
                                      Source source)
        throws Exception
    {
        if (language.equalsIgnoreCase("Pascal") &&
            type.equalsIgnoreCase("top-down"))
        {
            Scanner scanner = new PascalScanner(source);
            return new PascalParserTD(scanner);
        }
        else if(language.equalsIgnoreCase("SubC") && type.equalsIgnoreCase("top-down"))
        {
            Scanner scanner = new SubCScanner(source);
            return new SubCParserTD(scanner);
        }
        else if (!language.equalsIgnoreCase("Pascal")) {
            throw new Exception("Parser factory: Invalid language '" +
                                language + "'");
        }
        else {
            throw new Exception("Parser factory: Invalid type '" +
                                type + "'");
        }
    }
}
