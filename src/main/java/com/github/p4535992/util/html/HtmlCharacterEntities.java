package com.github.p4535992.util.html;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * A collection of all character entites defined in the HTML4 standard. The key
 * is the entity name, the property value is the decoded string.
 *
 * @author Thomas Morgner
 */
public class HtmlCharacterEntities extends Properties
{
    /**
     * The singleton instance for this entity-parser implementation.
     */
    private static CharacterEntityParser entityParser;
    private static final long serialVersionUID = 5118172339379209383L;

    /**
     * Gets the character entity parser for HTML content. The CharacterEntity
     * parser translates known characters into predefined entities.
     *
     * @return the character entity parser instance.
     */
    public static CharacterEntityParser getEntityParser()
    {
        if (entityParser == null)
        {
            entityParser = new CharacterEntityParser(new HtmlCharacterEntities());
        }
        return entityParser;
    }

    /**
     * Creates an instance.
     */
    public HtmlCharacterEntities()
    {
        setProperty("ang", "\u2220");
        setProperty("spades", "\u2660");
        setProperty("frasl", "\u2044");
        setProperty("copy", "\u00a9");
        setProperty("Upsilon", "\u03a5");
        setProperty("rsquo", "\u2019");
        setProperty("sdot", "\u22c5");
        setProperty("beta", "\u03b2");
        setProperty("egrave", "\u00e8");
        setProperty("Pi", "\u03a0");
        setProperty("micro", "\u00b5");
        setProperty("lArr", "\u21d0");
        setProperty("Beta", "\u0392");
        setProperty("eacute", "\u00e9");
        setProperty("agrave", "\u00e0");
        setProperty("sbquo", "\u201a");
        setProperty("ucirc", "\u00fb");
        setProperty("mdash", "\u2014");
        setProperty("rho", "\u03c1");
        setProperty("Nu", "\u039d");
        setProperty("ne", "\u2260");
        setProperty("nsub", "\u2284");
        setProperty("AElig", "\u00c6");
        setProperty("raquo", "\u00bb");
        setProperty("aacute", "\u00e1");
        setProperty("le", "\u2264");
        setProperty("harr", "\u2194");
        setProperty("frac34", "\u00be");
        setProperty("bdquo", "\u201e");
        setProperty("cup", "\u222a");
        setProperty("frac14", "\u00bc");
        setProperty("exist", "\u2203");
        setProperty("Ccedil", "\u00c7");
        setProperty("phi", "\u03c6");
        setProperty("Lambda", "\u039b");
        setProperty("alpha", "\u03b1");
        setProperty("sigma", "\u03c3");
        setProperty("thetasym", "\u03d1");
        setProperty("Rho", "\u03a1");
        setProperty("hArr", "\u21d4");
        setProperty("Dagger", "\u2021");
        setProperty("otilde", "\u00f5");
        setProperty("Epsilon", "\u0395");
        setProperty("iuml", "\u00ef");
        setProperty("Phi", "\u03a6");
        setProperty("prod", "\u220f");
        setProperty("Aring", "\u00c5");
        setProperty("rlm", "\u200f");
        setProperty("yen", "\u00a5");
        setProperty("emsp", "\u2003");
        setProperty("rang", "\u232a");
        setProperty("Atilde", "\u00c3");
        setProperty("Iuml", "\u00cf");
        setProperty("iota", "\u03b9");
        setProperty("deg", "\u00b0");
        setProperty("prop", "\u221d");
        setProperty("and", "\u2227");
        setProperty("para", "\u00b6");
        setProperty("darr", "\u2193");
        setProperty("curren", "\u00a4");
        setProperty("crarr", "\u21b5");
        setProperty("not", "\u00ac");
        setProperty("Iota", "\u0399");
        setProperty("aelig", "\u00e6");
        setProperty("rdquo", "\u201d");
        setProperty("Ocirc", "\u00d4");
        setProperty("ntilde", "\u00f1");
        setProperty("reg", "\u00ae");
        setProperty("zeta", "\u03b6");
        setProperty("middot", "\u00b7");
        setProperty("cent", "\u00a2");
        setProperty("quot", "\"");
        setProperty("hellip", "\u2026");
        setProperty("Zeta", "\u0396");
        setProperty("rceil", "\u2309");
        setProperty("eta", "\u03b7");
        setProperty("nbsp", "\u00a0");
        setProperty("rarr", "\u2192");
        setProperty("frac12", "\u00bd");
        setProperty("real", "\u211c");
        setProperty("mu", "\u03bc");
        setProperty("dArr", "\u21d3");
        setProperty("divide", "\u00f7");
        setProperty("cap", "\u2229");
        setProperty("chi", "\u03c7");
        setProperty("times", "\u00d7");
        setProperty("euml", "\u00eb");
        setProperty("Gamma", "\u0393");
        setProperty("loz", "\u25ca");
        setProperty("acute", "\u00b4");
        setProperty("Omega", "\u03a9");
        setProperty("ndash", "\u2013");
        setProperty("clubs", "\u2663");
        setProperty("macr", "\u00af");
        setProperty("Yacute", "\u00dd");
        setProperty("Ugrave", "\u00d9");
        setProperty("Euml", "\u00cb");
        setProperty("Eta", "\u0397");
        setProperty("sect", "\u00a7");
        setProperty("asymp", "\u2248");
        setProperty("ordm", "\u00ba");
        setProperty("rArr", "\u21d2");
        setProperty("radic", "\u221a");
        setProperty("Uacute", "\u00da");
        setProperty("omicron", "\u03bf");
        setProperty("Chi", "\u03a7");
        setProperty("aring", "\u00e5");
        setProperty("Theta", "\u0398");
        setProperty("supe", "\u2287");
        setProperty("ensp", "\u2002");
        setProperty("uml", "\u00a8");
        setProperty("ccedil", "\u00e7");
        setProperty("lambda", "\u03bb");
        setProperty("gt", "\u003e");
        setProperty("uarr", "\u2191");
        setProperty("alefsym", "\u2135");
        setProperty("auml", "\u00e4");
        setProperty("sup3", "\u00b3");
        setProperty("circ", "\u02c6");
        setProperty("lsquo", "\u2018");
        setProperty("Auml", "\u00c4");
        setProperty("dagger", "\u2020");
        setProperty("Kappa", "\u039a");
        setProperty("cong", "\u2245");
        setProperty("zwnj", "\u200c");
        setProperty("shy", "\u00ad");
        setProperty("ouml", "\u00f6");
        setProperty("diams", "\u2666");
        setProperty("uArr", "\u21d1");
        setProperty("atilde", "\u00e3");
        setProperty("THORN", "\u00de");
        setProperty("or", "\u2228");
        setProperty("Ograve", "\u00d2");
        setProperty("ocirc", "\u00f4");
        setProperty("plusm", "\u00b1");
        setProperty("Ouml", "\u00d6");
        setProperty("nabla", "\u2207");
        setProperty("psi", "\u03c8");
        setProperty("sigmaf", "\u03c2");
        setProperty("euro", "\u20ac");
        setProperty("sube", "\u2286");
        setProperty("sup2", "\u00b2");
        setProperty("laquo", "\u00ab");
        setProperty("forall", "\u2200");
        setProperty("Oacute", "\u00d3");
        setProperty("iexcl", "\u00a1");
        fillMoreEntities();
    }

    /**
     * Externalized initialization method to make CheckStyle happy.
     */
    private void fillMoreEntities()
    {
        setProperty("piv", "\u03d6");
        setProperty("minus", "\u2212");
        setProperty("zwj", "\u200d");
        setProperty("tau", "\u03c4");
        setProperty("Mu", "\u039c");
        setProperty("gamma", "\u03b3");
        setProperty("sup", "\u2283");
        setProperty("Psi", "\u03a8");
        setProperty("omega", "\u03c9");
        setProperty("Oslash", "\u00d8");
        setProperty("weierp", "\u2118");
        setProperty("Igrave", "\u00cc");
        setProperty("OElig", "\u0152");
        setProperty("sup1", "\u00b9");
        setProperty("cedil", "\u00b8");
        setProperty("upsilon", "\u03c5");
        setProperty("equiv", "\u2261");
        setProperty("isin", "\u2208");
        setProperty("Delta", "\u0394");
        setProperty("yacute", "\u00fd");
        setProperty("ugrave", "\u00f9");
        setProperty("ge", "\u2265");
        setProperty("Iacute", "\u00cd");
        setProperty("brvbar", "\u00a6");
        setProperty("Tau", "\u03a4");
        setProperty("Prime", "\u2033");
        setProperty("rfloor", "\u22a7");
        setProperty("Ecirc", "\u00ca");
        setProperty("ETH", "\u00d0");
        setProperty("int", "\u222b");
        setProperty("xi", "\u03be");
        setProperty("uacute", "\u00fa");
        setProperty("bull", "\u2022");
        setProperty("Scaron", "\u0160");
        setProperty("theta", "\u03b8");
        setProperty("yuml", "\u00ff");
        setProperty("oplus", "\u2295");
        setProperty("part", "\u2202");
        setProperty("ldquo", "\u201c");
        setProperty("Icirc", "\u00ce");
        setProperty("Yuml", "\u0178");
        setProperty("eth", "\u00f0");
        setProperty("Acirc", "\u00c2");
        setProperty("sub", "\u2282");
        setProperty("lceil", "\u2308");
        setProperty("Egrave", "\u00c8");
        setProperty("tilde", "\u02dc");
        setProperty("pi", "\u03c0");
        setProperty("rsaquo", "\u203a");
        setProperty("kappa", "\u03ba");
        setProperty("upsih", "\u03d2");
        setProperty("Omicron", "\u039f");
        setProperty("otimes", "\u2297");
        setProperty("ni", "\u220b");
        setProperty("amp", "\u0026");
        setProperty("Eacute", "\u00c9");
        setProperty("nu", "\u03bd");
        setProperty("Ucirc", "\u00db");
        setProperty("uuml", "\u00fc");
        setProperty("oslash", "\u00f8");
        setProperty("thorn", "\u00fe");
        setProperty("trade", "\u2122");
        setProperty("epsilon", "\u03b5");
        setProperty("ograve", "\u00f2");
        setProperty("hearts", "\u2665");
        setProperty("iquest", "\u00bf");
        setProperty("Uuml", "\u00dc");
        setProperty("empty", "\u2205");
        setProperty("lowast", "\u2217");
        setProperty("sum", "\u2211");
        setProperty("lfloor", "\u22a6");
        setProperty("lrm", "\u200e");
        setProperty("oacute", "\u00f3");
        setProperty("image", "\u2111");
        setProperty("Agrave", "\u00c0");
        setProperty("oline", "\u203e");
        setProperty("oelig", "\u0153");
        setProperty("Sigma", "\u03a3");
        setProperty("permil", "\u2030");
        setProperty("perp", "\u22a5");
        setProperty("lt", "\u003c");
        setProperty("Aacute", "\u00c1");
        setProperty("acirc", "\u00e2");
        setProperty("lang", "\u2329");
        setProperty("delta", "\u03b4");
        setProperty("infin", "\u221e");
        setProperty("igrave", "\u00ec");
        setProperty("ordf", "\u00aa");
        setProperty("lsaquo", "\u2039");
        setProperty("prime", "\u2032");
        setProperty("ecirc", "\u00ea");
        setProperty("there4", "\u2234");
        setProperty("iacute", "\u00ed");
        setProperty("sim", "\u223c");
        setProperty("Alpha", "\u0391");
        setProperty("pound", "\u00a3");
        setProperty("notin", "\u2209");
        setProperty("Ntilde", "\u00d1");
        setProperty("Xi", "\u039e");
        setProperty("thinsp", "\u2009");
        setProperty("Otilde", "\u00d5");
        setProperty("icirc", "\u00ee");
        setProperty("scaron", "\u0161");
        setProperty("szlig", "\u00df");
        setProperty("larr", "\u2190");
    }
}
/**
 * The character entity parser replaces all known occurrences of an entity in
 * the format &amp;entityname;.
 *
 * @author Thomas Morgner
 */
class CharacterEntityParser {
    private String[] charMap;

    /**
     * the entities, keyed by entity name.
     */
    private final HashMap<Object,Object> entities;

    /**
     * Creates a new CharacterEntityParser and initializes the parser with the
     * given set of entities.
     *
     * @param characterEntities the entities used for the parser
     */
    @SuppressWarnings("rawtypes")
    public CharacterEntityParser(final Properties characterEntities) {
        if (characterEntities == null) {
            throw new NullPointerException("CharacterEntities must not be null");
        }

        entities = new HashMap<>(characterEntities);
        charMap = new String[65536];

        final Iterator<Entry<Object,Object>> entries = entities.entrySet().iterator();
        while (entries.hasNext()) {
            final Map.Entry entry = (Map.Entry) entries.next();
            final String value = (String) entry.getValue();
            final String entityName = (String) entry.getKey();
            if (value.length() != 1) {
                throw new IllegalStateException();
            }
            charMap[value.charAt(0)] = entityName;
        }
    }

    /**
     * Creates a new CharacterEntityParser and initializes the parser with the
     * given set of entities.
     *
     * @param characterEntities the entities used for the parser
     */
    @SuppressWarnings({"rawtypes","unchecked"})
    public CharacterEntityParser(final HashMap<Object,Object> characterEntities) {
        if (characterEntities == null) {
            throw new NullPointerException("CharacterEntities must not be null");
        }

        entities = (HashMap<Object, Object>) characterEntities.clone();
        charMap = new String[65536];

        final Iterator<Entry<Object,Object>> entries = entities.entrySet().iterator();
        while (entries.hasNext()) {
            final Map.Entry entry = (Map.Entry) entries.next();
            final String value = (String) entry.getValue();
            final String entityName = (String) entry.getKey();
            if (value.length() != 1) {
                throw new IllegalStateException();
            }
            charMap[value.charAt(0)] = entityName;
        }
    }

    /**
     * create a new Character entity parser and initializes the parser with the
     * entities defined in the XML standard.
     *
     * @return the CharacterEntityParser initialized with XML entities.
     */
    public static CharacterEntityParser createXMLEntityParser() {
        final HashMap<Object,Object> entities = new HashMap<>();
        entities.put("amp", "&");
        entities.put("quot", "\"");
        entities.put("lt", "<");
        entities.put("gt", ">");
        entities.put("apos", "\u0027");
        return new CharacterEntityParser(entities);
    }

    /**
     * returns the entities used in the parser.
     *
     * @return the properties for this parser.
     */
    private HashMap<Object,Object> getEntities() {
        return entities;
    }

    /**
     * Looks up the character for the entity name specified in <code>key</code>.
     *
     * @param key the entity name
     * @return the character as string with a length of 1
     */
    private String lookupCharacter(final String key) {
        return (String) getEntities().get(key);
    }

    /**
     * Encode the given String, so that all known entites are encoded. All
     * characters represented by these entites are now removed from the string.
     *
     * @param value the original string
     * @return the encoded string.
     */
    public String encodeEntities(final String value) {
        if (value == null) {
            throw new NullPointerException();
        }

        final int length = value.length();
        final StringBuffer writer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            final char character = value.charAt(i);
            final String lookup = charMap[character];
            if (lookup == null) {
                writer.append(character);
            } else {
                writer.append('&');
                writer.append(lookup);
                writer.append(';');
            }
        }
        return writer.toString();
    }

    /**
     * Decode the string, all known entities are replaced by their resolved
     * characters.
     *
     * @param value the string that should be decoded.
     * @return the decoded string.
     */
    public String decodeEntities(final String value) {
        if (value == null) {
            throw new NullPointerException();
        }

        int parserIndex = 0;
        int subStart = value.indexOf('&', parserIndex);
        if (subStart == -1) {
            return value;
        }
        int subEnd = value.indexOf(';', subStart);
        if (subEnd == -1) {
            return value;
        }

        final StringBuffer bufValue = new StringBuffer(value.substring(0, subStart));
        do {
            // at this point we know, that there is at least one entity ..
            if (value.charAt(subStart + 1) == '#') {
                final int subValue = parseInt(value.substring(subStart + 2, subEnd), 0);
                if ((subValue >= 1) && (subValue <= 65536)) {
                    final char[] chr = new char[1];
                    chr[0] = (char) subValue;
                    bufValue.append(chr);
                } else {
                    // invalid entity, do not decode ..
                    bufValue.append(value.substring(subStart, subEnd));
                }
            } else {
                final String entity = value.substring(subStart + 1, subEnd);
                final String replaceString = lookupCharacter(entity);
                if (replaceString != null) {
                    bufValue.append(decodeEntities(replaceString));
                } else {
                    bufValue.append('&');
                    bufValue.append(entity);
                    bufValue.append(';');
                }
            }
            parserIndex = subEnd + 1;
            subStart = value.indexOf('&', parserIndex);
            if (subStart == -1) {
                bufValue.append(value.substring(parserIndex));
                subEnd = -1;
            } else {
                subEnd = value.indexOf(';', subStart);
                if (subEnd == -1) {
                    bufValue.append(value.substring(parserIndex));
                } else {
                    bufValue.append(value.substring(parserIndex, subStart));
                }
            }
        }
        while (subStart != -1 && subEnd != -1);

        return bufValue.toString();
    }

    /**
     * Parses the given string into an int-value. On errors the default value
     * is returned.
     *
     * @param s          the string
     * @param defaultVal the default value that should be used in case of errors
     * @return the parsed int or the default value.
     */
    private int parseInt(final String s, final int defaultVal) {
        if (s == null) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            // ignored ..
        }
        return defaultVal;
    }
}
