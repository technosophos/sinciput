package com.technosophos.rhizome.web.xml.rhtml;

//import java.util.ArrayList;

/**
 * Provide support for character entities.
 * <p><strong>This class is experiemental and evolving.</strong> The purpose of this class is
 * to provide support for all standard XHTML entities to RHTML documents. However, the data 
 * structures in this class are evolving to make this class flexible enough to serve other
 * purposes as well.
 */
public final class RHTMLEntities {
	
	public static class CharNameCode {
		private String cname = null;
		private int ccode = 0;
		public CharNameCode(String name, int code) {
			cname = name;
			ccode = code;
		}
		
		public String getName() { return cname; } 
		public int getCode() { return ccode; } 
		public void writeDef( StringBuilder sb ) {
			sb.append("<!ENTITY ");
			sb.append(cname);
			sb.append(" \"&#");
			sb.append(ccode);
			sb.append(";\">");
		}
		public void formatReference( StringBuilder sb) {
			sb.append('&');
			sb.append(cname);
			sb.append(";: ");
			sb.append((char)ccode);
			sb.append('\n');
		}
	}
	
	/*public static final char[] DONT_ESCAPE = {
		'!', 
		'.', 
		',', 
		';', 
		'\'',
		'"',
		']',
		'[',
		'(',
		')',
		'+',
		'/',
		'?',
		'@',
		'=',
		'\\',
		'^',
		'_',
		'`',
		'{'
	};*/
	
	/**
	 * This is taken from the W3C XHTML spec.
	 * <p>The array contains definitions from the Latin1, Special, and Symbol 
	 * entity tables for XHTML 1.0 Transitional.</p>
	 * <p>The original DTD came with the following notice: 
	 * <code>
	 * Portions (C) International Organization for Standardization 1986
     * Permission to copy in any form is granted for use with
     * conforming SGML systems and applications as defined in
     * ISO 8879, provided this notice is included in all copies.
     * </code>
     * @see http://www.w3.org/TR/xhtml1/dtds.html#a_dtd_XHTML-1.0-Transitional
	 */
	public static final CharNameCode[] RHTML_ENTITIES = {
		/*
		 * From the original entities DTD file, get all of the entity declarations:
		 *  $ awk '{print $2 " " $3}' entities.txt | grep '&' | perl -p -e 's/>//g' > entities.clean
		 * Then format for Java and output. (You could, of course, do this all on one line...)
		 *  $ awk '{print "new String[]{\"" $1 "\", " $2 "}," }' entities.clean
		 */
		new CharNameCode("nbsp",160),
		new CharNameCode("iexcl",161),
		new CharNameCode("cent",162),
		new CharNameCode("pound",163),
		new CharNameCode("curren",164),
		new CharNameCode("yen",165),
		new CharNameCode("brvbar",166),
		new CharNameCode("sect",167),
		new CharNameCode("uml",168),
		new CharNameCode("copy",169),
		new CharNameCode("ordf",170),
		new CharNameCode("laquo",171),
		new CharNameCode("not",172),
		new CharNameCode("shy",173),
		new CharNameCode("reg",174),
		new CharNameCode("macr",175),
		new CharNameCode("deg",176),
		new CharNameCode("plusmn",177),
		new CharNameCode("sup2",178),
		new CharNameCode("sup3",179),
		new CharNameCode("acute",180),
		new CharNameCode("micro",181),
		new CharNameCode("para",182),
		new CharNameCode("middot",183),
		new CharNameCode("cedil",184),
		new CharNameCode("sup1",185),
		new CharNameCode("ordm",186),
		new CharNameCode("raquo",187),
		new CharNameCode("frac14",188),
		new CharNameCode("frac12",189),
		new CharNameCode("frac34",190),
		new CharNameCode("iquest",191),
		new CharNameCode("Agrave",192),
		new CharNameCode("Aacute",193),
		new CharNameCode("Acirc",194),
		new CharNameCode("Atilde",195),
		new CharNameCode("Auml",196),
		new CharNameCode("Aring",197),
		new CharNameCode("AElig",198),
		new CharNameCode("Ccedil",199),
		new CharNameCode("Egrave",200),
		new CharNameCode("Eacute",201),
		new CharNameCode("Ecirc",202),
		new CharNameCode("Euml",203),
		new CharNameCode("Igrave",204),
		new CharNameCode("Iacute",205),
		new CharNameCode("Icirc",206),
		new CharNameCode("Iuml",207),
		new CharNameCode("ETH",208),
		new CharNameCode("Ntilde",209),
		new CharNameCode("Ograve",210),
		new CharNameCode("Oacute",211),
		new CharNameCode("Ocirc",212),
		new CharNameCode("Otilde",213),
		new CharNameCode("Ouml",214),
		new CharNameCode("times",215),
		new CharNameCode("Oslash",216),
		new CharNameCode("Ugrave",217),
		new CharNameCode("Uacute",218),
		new CharNameCode("Ucirc",219),
		new CharNameCode("Uuml",220),
		new CharNameCode("Yacute",221),
		new CharNameCode("THORN",222),
		new CharNameCode("szlig",223),
		new CharNameCode("agrave",224),
		new CharNameCode("aacute",225),
		new CharNameCode("acirc",226),
		new CharNameCode("atilde",227),
		new CharNameCode("auml",228),
		new CharNameCode("aring",229),
		new CharNameCode("aelig",230),
		new CharNameCode("ccedil",231),
		new CharNameCode("egrave",232),
		new CharNameCode("eacute",233),
		new CharNameCode("ecirc",234),
		new CharNameCode("euml",235),
		new CharNameCode("igrave",236),
		new CharNameCode("iacute",237),
		new CharNameCode("icirc",238),
		new CharNameCode("iuml",239),
		new CharNameCode("eth",240),
		new CharNameCode("ntilde",241),
		new CharNameCode("ograve",242),
		new CharNameCode("oacute",243),
		new CharNameCode("ocirc",244),
		new CharNameCode("otilde",245),
		new CharNameCode("ouml",246),
		new CharNameCode("divide",247),
		new CharNameCode("oslash",248),
		new CharNameCode("ugrave",249),
		new CharNameCode("uacute",250),
		new CharNameCode("ucirc",251),
		new CharNameCode("uuml",252),
		new CharNameCode("yacute",253),
		new CharNameCode("thorn",254),
		new CharNameCode("yuml",255),
		
		// Specials
		new CharNameCode("quot", 34),
		new CharNameCode("amp", 38), // We had to hard-wire these into the parser
		new CharNameCode("lt", 60),  // ^^
		new CharNameCode("gt", 62),
		new CharNameCode("apos", 39),
		new CharNameCode("OElig", 338),
		new CharNameCode("oelig", 339),
		new CharNameCode("Scaron", 352),
		new CharNameCode("scaron", 353),
		new CharNameCode("Yuml", 376),
		new CharNameCode("circ", 710),
		new CharNameCode("tilde", 732),
		new CharNameCode("ensp", 8194),
		new CharNameCode("emsp", 8195),
		new CharNameCode("thinsp", 8201),
		new CharNameCode("zwnj", 8204),
		new CharNameCode("zwj", 8205),
		new CharNameCode("lrm", 8206),
		new CharNameCode("rlm", 8207),
		new CharNameCode("ndash", 8211),
		new CharNameCode("mdash", 8212),
		new CharNameCode("lsquo", 8216),
		new CharNameCode("rsquo", 8217),
		new CharNameCode("sbquo", 8218),
		new CharNameCode("ldquo", 8220),
		new CharNameCode("rdquo", 8221),
		new CharNameCode("bdquo", 8222),
		new CharNameCode("dagger", 8224),
		new CharNameCode("Dagger", 8225),
		new CharNameCode("permil", 8240),
		new CharNameCode("lsaquo", 8249),
		new CharNameCode("rsaquo", 8250),
		new CharNameCode("euro", 8364),
		
		// Symbols
		new CharNameCode("fnof", 402),
		new CharNameCode("Alpha", 913),
		new CharNameCode("Beta", 914),
		new CharNameCode("Gamma", 915),
		new CharNameCode("Delta", 916),
		new CharNameCode("Epsilon", 917),
		new CharNameCode("Zeta", 918),
		new CharNameCode("Eta", 919),
		new CharNameCode("Theta", 920),
		new CharNameCode("Iota", 921),
		new CharNameCode("Kappa", 922),
		new CharNameCode("Lambda", 923),
		new CharNameCode("Mu", 924),
		new CharNameCode("Nu", 925),
		new CharNameCode("Xi", 926),
		new CharNameCode("Omicron", 927),
		new CharNameCode("Pi", 928),
		new CharNameCode("Rho", 929),
		new CharNameCode("Sigma", 931),
		new CharNameCode("Tau", 932),
		new CharNameCode("Upsilon", 933),
		new CharNameCode("Phi", 934),
		new CharNameCode("Chi", 935),
		new CharNameCode("Psi", 936),
		new CharNameCode("Omega", 937),
		new CharNameCode("alpha", 945),
		new CharNameCode("beta", 946),
		new CharNameCode("gamma", 947),
		new CharNameCode("delta", 948),
		new CharNameCode("epsilon", 949),
		new CharNameCode("zeta", 950),
		new CharNameCode("eta", 951),
		new CharNameCode("theta", 952),
		new CharNameCode("iota", 953),
		new CharNameCode("kappa", 954),
		new CharNameCode("lambda", 955),
		new CharNameCode("mu", 956),
		new CharNameCode("nu", 957),
		new CharNameCode("xi", 958),
		new CharNameCode("omicron", 959),
		new CharNameCode("pi", 960),
		new CharNameCode("rho", 961),
		new CharNameCode("sigmaf", 962),
		new CharNameCode("sigma", 963),
		new CharNameCode("tau", 964),
		new CharNameCode("upsilon", 965),
		new CharNameCode("phi", 966),
		new CharNameCode("chi", 967),
		new CharNameCode("psi", 968),
		new CharNameCode("omega", 969),
		new CharNameCode("thetasym", 977),
		new CharNameCode("upsih", 978),
		new CharNameCode("piv", 982),
		new CharNameCode("bull", 8226),
		new CharNameCode("hellip", 8230),
		new CharNameCode("prime", 8242),
		new CharNameCode("Prime", 8243),
		new CharNameCode("oline", 8254),
		new CharNameCode("frasl", 8260),
		new CharNameCode("weierp", 8472),
		new CharNameCode("image", 8465),
		new CharNameCode("real", 8476),
		new CharNameCode("trade", 8482),
		new CharNameCode("alefsym", 8501),
		new CharNameCode("larr", 8592),
		new CharNameCode("uarr", 8593),
		new CharNameCode("rarr", 8594),
		new CharNameCode("darr", 8595),
		new CharNameCode("harr", 8596),
		new CharNameCode("crarr", 8629),
		new CharNameCode("lArr", 8656),
		new CharNameCode("uArr", 8657),
		new CharNameCode("rArr", 8658),
		new CharNameCode("dArr", 8659),
		new CharNameCode("hArr", 8660),
		new CharNameCode("forall", 8704),
		new CharNameCode("part", 8706),
		new CharNameCode("exist", 8707),
		new CharNameCode("empty", 8709),
		new CharNameCode("nabla", 8711),
		new CharNameCode("isin", 8712),
		new CharNameCode("notin", 8713),
		new CharNameCode("ni", 8715),
		new CharNameCode("prod", 8719),
		new CharNameCode("sum", 8721),
		new CharNameCode("minus", 8722),
		new CharNameCode("lowast", 8727),
		new CharNameCode("radic", 8730),
		new CharNameCode("prop", 8733),
		new CharNameCode("infin", 8734),
		new CharNameCode("ang", 8736),
		new CharNameCode("and", 8743),
		new CharNameCode("or", 8744),
		new CharNameCode("cap", 8745),
		new CharNameCode("cup", 8746),
		new CharNameCode("int", 8747),
		new CharNameCode("there4", 8756),
		new CharNameCode("sim", 8764),
		new CharNameCode("cong", 8773),
		new CharNameCode("asymp", 8776),
		new CharNameCode("ne", 8800),
		new CharNameCode("equiv", 8801),
		new CharNameCode("le", 8804),
		new CharNameCode("ge", 8805),
		new CharNameCode("sub", 8834),
		new CharNameCode("sup", 8835),
		new CharNameCode("nsub", 8836),
		new CharNameCode("sube", 8838),
		new CharNameCode("supe", 8839),
		new CharNameCode("oplus", 8853),
		new CharNameCode("otimes", 8855),
		new CharNameCode("perp", 8869),
		new CharNameCode("sdot", 8901),
		new CharNameCode("lceil", 8968),
		new CharNameCode("rceil", 8969),
		new CharNameCode("lfloor", 8970),
		new CharNameCode("rfloor", 8971),
		new CharNameCode("lang", 9001),
		new CharNameCode("rang", 9002),
		new CharNameCode("loz", 9674),
		new CharNameCode("spades", 9824),
		new CharNameCode("clubs", 9827),
		new CharNameCode("hearts", 9829),
		new CharNameCode("diams", 9830),
	};
	
	public static String getEntitiesDTD() {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE doc[ ");
		getEntities(sb);
		sb.append("]>");
		return sb.toString();
	}
	
	public static String getEntities() {
		return getEntities(new StringBuilder()).toString();
	}
	
	public static StringBuilder getEntities(StringBuilder sb) {
		for (CharNameCode c : RHTML_ENTITIES) {
			c.writeDef(sb);
		}
		return sb;
	}
	
	public static String summaryString() {
		StringBuilder sb = new StringBuilder();
		for (CharNameCode c : RHTML_ENTITIES) {
			c.formatReference(sb);
		}
		return sb.toString();
	}
}
