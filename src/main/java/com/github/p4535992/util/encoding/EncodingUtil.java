package com.github.p4535992.util.encoding;

import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringKit;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Class utility for encoding.
 * @author 4535992.
 * @version 2015-07-09.
 */
@SuppressWarnings("unused")
public class EncodingUtil {
    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EncodingUtil.class);
    private static Map<String,String> unicodeCodePoint = new HashMap<>();
    //private static Map<Character,Character> javaCharLiterals = new HashMap<>();
    /*private static void set() {
        javaCharLiterals.put('\u0000', '\0');
        javaCharLiterals.put('\u0001', '\0');
        javaCharLiterals.put('\u0002', '\0');
        javaCharLiterals.put('\u0003', '\0');
        javaCharLiterals.put('\u0004', '\0');
        javaCharLiterals.put('\u0005', '\0');
        javaCharLiterals.put('\u0006', '\0');
        javaCharLiterals.put('\u0007', '\0');
        javaCharLiterals.put('\u0008', '\0');
        javaCharLiterals.put('\u0009', '\0');
        javaCharLiterals.put('\u000B', '\0');
        javaCharLiterals.put('\u000C', '\0');
        javaCharLiterals.put('\u000E', '\0');
        javaCharLiterals.put('\u000F', '\0');
        javaCharLiterals.put('\u0010', '\0');
        javaCharLiterals.put('\u0011', '\0');
        javaCharLiterals.put('\u0012', '\0');
        javaCharLiterals.put('\u0013', '\0');
        javaCharLiterals.put('\u0014', '\0');
        javaCharLiterals.put('\u0015', '\0');
        javaCharLiterals.put('\u0016', '\0');
        javaCharLiterals.put('\u0017', '\0');
        javaCharLiterals.put('\u0018', '\0');
        javaCharLiterals.put('\u0019', '\0');
        javaCharLiterals.put('\u001A', '\0');
        javaCharLiterals.put('\u001B', '\0');
        javaCharLiterals.put('\u001C', '\0');
        javaCharLiterals.put('\u001D', '\0');
        javaCharLiterals.put('\u001E', '\0');
        javaCharLiterals.put('\u001F', '\0');
        javaCharLiterals.put('\u0020', ' ');
        javaCharLiterals.put('\u0021', '!');
        javaCharLiterals.put('\u0022', '\'');
        javaCharLiterals.put('\u0023', '#');
        javaCharLiterals.put('\u0024', '$');
        javaCharLiterals.put('\u0025', '%');
        javaCharLiterals.put('\u0026', '&');
        javaCharLiterals.put('\u0027', '\'');
        javaCharLiterals.put('\u0028', '(');
        javaCharLiterals.put('\u0029', ')');
        javaCharLiterals.put('\u002A', '*');
        javaCharLiterals.put('\u002B', '+');
        javaCharLiterals.put('\u002C', ',');
        javaCharLiterals.put('\u002D', '-');
        javaCharLiterals.put('\u002E', '.');
        javaCharLiterals.put('\u002F', '/');
        javaCharLiterals.put('\u0030', '0');
        javaCharLiterals.put('\u0031', '1');
        javaCharLiterals.put('\u0032', '2');
        javaCharLiterals.put('\u0033', '3');
        javaCharLiterals.put('\u0034', '4');
        javaCharLiterals.put('\u0035', '5');
        javaCharLiterals.put('\u0036', '6');
        javaCharLiterals.put('\u0037', '7');
        javaCharLiterals.put('\u0038', '8');
        javaCharLiterals.put('\u0039', '9');
        javaCharLiterals.put('\u003A', ':');
        javaCharLiterals.put('\u003B', ';');
        javaCharLiterals.put('\u003C', '<');
        javaCharLiterals.put('\u003D', '=');
        javaCharLiterals.put('\u003E', '>');
        javaCharLiterals.put('\u003F', '?');
        javaCharLiterals.put('\u0040', '@');
        javaCharLiterals.put('\u0041', 'A');
        javaCharLiterals.put('\u0042', 'B');
        javaCharLiterals.put('\u0043', 'C');
        javaCharLiterals.put('\u0044', 'D');
        javaCharLiterals.put('\u0045', 'E');
        javaCharLiterals.put('\u0046', 'F');
        javaCharLiterals.put('\u0047', 'G');
        javaCharLiterals.put('\u0048', 'H');
        javaCharLiterals.put('\u0049', 'I');
        javaCharLiterals.put('\u004A', 'J');
        javaCharLiterals.put('\u004B', 'K');
        javaCharLiterals.put('\u004C', 'L');
        javaCharLiterals.put('\u004D', 'M');
        javaCharLiterals.put('\u004E', 'N');
        javaCharLiterals.put('\u004F', 'O');
        javaCharLiterals.put('\u0050', 'P');
        javaCharLiterals.put('\u0051', 'Q');
        javaCharLiterals.put('\u0052', 'R');
        javaCharLiterals.put('\u0053', 'S');
        javaCharLiterals.put('\u0054', 'T');
        javaCharLiterals.put('\u0055', '\0');
        javaCharLiterals.put('\u0056', 'V');
        javaCharLiterals.put('\u0057', 'W');
        javaCharLiterals.put('\u0058', 'X');
        javaCharLiterals.put('\u0059', 'Y');
        javaCharLiterals.put('\u005A', 'Z');
        javaCharLiterals.put('\u005B', '[');
        javaCharLiterals.put('\u005C', '\\');
        javaCharLiterals.put('\u005D', ']');
        javaCharLiterals.put('\u005E', '^');
        javaCharLiterals.put('\u005F', '_');
        javaCharLiterals.put('\u0060', '`');
        javaCharLiterals.put('\u0061', 'a');
        javaCharLiterals.put('\u0062', 'b');
        javaCharLiterals.put('\u0063', 'c');
        javaCharLiterals.put('\u0064', 'd');
        javaCharLiterals.put('\u0065', 'e');
        javaCharLiterals.put('\u0066', 'f');
        javaCharLiterals.put('\u0067', 'g');
        javaCharLiterals.put('\u0068', 'h');
        javaCharLiterals.put('\u0069', 'i');
        javaCharLiterals.put('\u006A', 'j');
        javaCharLiterals.put('\u006B', 'k');
        javaCharLiterals.put('\u006C', 'l');
        javaCharLiterals.put('\u006D', 'm');
        javaCharLiterals.put('\u006E', 'n');
        javaCharLiterals.put('\u006F', 'o');
        javaCharLiterals.put('\u0070', 'p');
        javaCharLiterals.put('\u0071', 'q');
        javaCharLiterals.put('\u0072', 'r');
        javaCharLiterals.put('\u0073', 's');
        javaCharLiterals.put('\u0074', 't');
        javaCharLiterals.put('\u0075', 'u');
        javaCharLiterals.put('\u0076', 'v');
        javaCharLiterals.put('\u0077', 'w');
        javaCharLiterals.put('\u0078', 'x');
        javaCharLiterals.put('\u0079', 'y');
        javaCharLiterals.put('\u007A', 'z');
        javaCharLiterals.put('\u007B', '{');
        javaCharLiterals.put('\u007C', '|');
        javaCharLiterals.put('\u007D', '}');
        javaCharLiterals.put('\u007E', '~');
        javaCharLiterals.put('\u007F', '\0');
        javaCharLiterals.put('\u0080', '\0');
        javaCharLiterals.put('\u0081', '\0');
        javaCharLiterals.put('\u0082', '\0');
        javaCharLiterals.put('\u0083', '\0');
        javaCharLiterals.put('\u0084', '\0');
        javaCharLiterals.put('\u0085', '\0');
        javaCharLiterals.put('\u0087', '\0');
        javaCharLiterals.put('\u0088', '\0');
        javaCharLiterals.put('\u0089', '\0');
        javaCharLiterals.put('\u008A', '\0');
        javaCharLiterals.put('\u008C', '\0');
        javaCharLiterals.put('\u008D', '\0');
        javaCharLiterals.put('\u008E', '\0');
        javaCharLiterals.put('\u008F', '\0');
        javaCharLiterals.put('\u0090', '\0');
        javaCharLiterals.put('\u0091', '\0');
        javaCharLiterals.put('\u0092', '\0');
        javaCharLiterals.put('\u0093', '\0');
        javaCharLiterals.put('\u0094', '\0');
        javaCharLiterals.put('\u0095', '\0');
        javaCharLiterals.put('\u0096', '\0');
        javaCharLiterals.put('\u0097', '\0');
        javaCharLiterals.put('\u0098', '\0');
        javaCharLiterals.put('\u0099', '\0');
        javaCharLiterals.put('\u009A', '\0');
        javaCharLiterals.put('\u009B', '\0');
        javaCharLiterals.put('\u009C', '\0');
        javaCharLiterals.put('\u009D', '\0');
        javaCharLiterals.put('\u009E', '\0');
        javaCharLiterals.put('\u009F', '\0');
        javaCharLiterals.put('\u00A0', '\0');
        javaCharLiterals.put('\u00A1', '¡');
        javaCharLiterals.put('\u00A2', '¢');
        javaCharLiterals.put('\u00A3', '£');
        javaCharLiterals.put('\u00A4', '¤');
        javaCharLiterals.put('\u00A5', '¥');
        javaCharLiterals.put('\u00A6', '¦');
        javaCharLiterals.put('\u00A7', '§');
        javaCharLiterals.put('\u00A8', '¨');
        javaCharLiterals.put('\u00A9', '©');
        javaCharLiterals.put('\u00AA', 'ª');
        javaCharLiterals.put('\u00AB', '«');
        javaCharLiterals.put('\u00AC', '¬');
        javaCharLiterals.put('\u00AD', '­');
        javaCharLiterals.put('\u00AE', '®');
        javaCharLiterals.put('\u00AF', '¯');
        javaCharLiterals.put('\u00B0', '°');
        javaCharLiterals.put('\u00B1', '±');
        javaCharLiterals.put('\u00B2', '²');
        javaCharLiterals.put('\u00B3', '³');
        javaCharLiterals.put('\u00B4', '´');
        javaCharLiterals.put('\u00B5', 'µ');
        javaCharLiterals.put('\u00B6', '¶');
        javaCharLiterals.put('\u00B7', '·');
        javaCharLiterals.put('\u00B8', '¸');
        javaCharLiterals.put('\u00B9', '¹');
        javaCharLiterals.put('\u00BA', 'º');
        javaCharLiterals.put('\u00BB', '»');
        javaCharLiterals.put('\u00BC', '¼');
        javaCharLiterals.put('\u00BD', '½');
        javaCharLiterals.put('\u00BE', '¾');
        javaCharLiterals.put('\u00BF', '¿');
        javaCharLiterals.put('\u00C0', 'À');
        javaCharLiterals.put('\u00C1', 'Á');
        javaCharLiterals.put('\u00C2', 'Â');
        javaCharLiterals.put('\u00C3', 'Ã');
        javaCharLiterals.put('\u00C4', 'Ä');
        javaCharLiterals.put('\u00C5', 'Å');
        javaCharLiterals.put('\u00C6', 'Æ');
        javaCharLiterals.put('\u00C7', 'Ç');
        javaCharLiterals.put('\u00C8', 'È');
        javaCharLiterals.put('\u00C9', 'É');
        javaCharLiterals.put('\u00CA', 'Ê');
        javaCharLiterals.put('\u00CB', 'Ë');
        javaCharLiterals.put('\u00CC', 'Ì');
        javaCharLiterals.put('\u00CD', 'Í');
        javaCharLiterals.put('\u00CE', 'Î');
        javaCharLiterals.put('\u00CF', 'Ï');
        javaCharLiterals.put('\u00D0', 'Ð');
        javaCharLiterals.put('\u00D1', 'Ñ');
        javaCharLiterals.put('\u00D2', 'Ò');
        javaCharLiterals.put('\u00D3', 'Ó');
        javaCharLiterals.put('\u00D4', 'Ô');
        javaCharLiterals.put('\u00D5', 'Õ');
        javaCharLiterals.put('\u00D6', 'Ö');
        javaCharLiterals.put('\u00D7', '×');
        javaCharLiterals.put('\u00D8', 'Ø');
        javaCharLiterals.put('\u00D9', 'Ù');
        javaCharLiterals.put('\u00DA', 'Ú');
        javaCharLiterals.put('\u00DB', 'Û');
        javaCharLiterals.put('\u00DC', 'Ü');
        javaCharLiterals.put('\u00DD', 'Ý');
        javaCharLiterals.put('\u00DE', 'Þ');
        javaCharLiterals.put('\u00DF', 'ß');
        javaCharLiterals.put('\u00E0', 'à');
        javaCharLiterals.put('\u00E1', 'á');
        javaCharLiterals.put('\u00E2', 'â');
        javaCharLiterals.put('\u00E3', 'ã');
        javaCharLiterals.put('\u00E4', 'ä');
        javaCharLiterals.put('\u00E5', 'å');
        javaCharLiterals.put('\u00E6', 'æ');
        javaCharLiterals.put('\u00E7', 'ç');
        javaCharLiterals.put('\u00E8', 'è');
        javaCharLiterals.put('\u00E9', 'é');
        javaCharLiterals.put('\u00EA', 'ê');
        javaCharLiterals.put('\u00EB', 'ë');
        javaCharLiterals.put('\u00EC', 'ì');
        javaCharLiterals.put('\u00ED', 'í');
        javaCharLiterals.put('\u00EE', 'î');
        javaCharLiterals.put('\u00EF', 'ï');
        javaCharLiterals.put('\u00F0', 'ð');
        javaCharLiterals.put('\u00F1', 'ñ');
        javaCharLiterals.put('\u00F2', 'ò');
        javaCharLiterals.put('\u00F3', 'ó');
        javaCharLiterals.put('\u00F4', 'ô');
        javaCharLiterals.put('\u00F5', 'õ');
        javaCharLiterals.put('\u00F6', 'ö');
        javaCharLiterals.put('\u00F7', '÷');
        javaCharLiterals.put('\u00F8', 'ø');
        javaCharLiterals.put('\u00F9', 'ù');
        javaCharLiterals.put('\u00FA', 'ú');
        javaCharLiterals.put('\u00FB', 'û');
        javaCharLiterals.put('\u00FC', 'ü');
        javaCharLiterals.put('\u00FD', 'ý');
        javaCharLiterals.put('\u00FE', 'þ');
        javaCharLiterals.put('\u00FF', 'ÿ');
        javaCharLiterals.put('\uFEFF', '\0');
    }*/
    /**Map for brute force replace of all unicode escape on the text*/
    private static void setMapUnicodeEscaped(){
        unicodeCodePoint.put("U+0000","");
        unicodeCodePoint.put("U+0001","");
        unicodeCodePoint.put("U+0002","");
        unicodeCodePoint.put("U+0003","");
        unicodeCodePoint.put("U+0004","");
        unicodeCodePoint.put("U+0005","");
        unicodeCodePoint.put("U+0006","");
        unicodeCodePoint.put("U+0007","");
        unicodeCodePoint.put("U+0008","");
        unicodeCodePoint.put("U+0009","");
        unicodeCodePoint.put("U+000A","");
        unicodeCodePoint.put("U+000B","");
        unicodeCodePoint.put("U+000C","");
        unicodeCodePoint.put("U+000D","");
        unicodeCodePoint.put("U+000E","");
        unicodeCodePoint.put("U+000F","");
        unicodeCodePoint.put("U+0010","");
        unicodeCodePoint.put("U+0011","");
        unicodeCodePoint.put("U+0012","");
        unicodeCodePoint.put("U+0013","");
        unicodeCodePoint.put("U+0014","");
        unicodeCodePoint.put("U+0015","");
        unicodeCodePoint.put("U+0016","");
        unicodeCodePoint.put("U+0017","");
        unicodeCodePoint.put("U+0018","");
        unicodeCodePoint.put("U+0019","");
        unicodeCodePoint.put("U+001A","");
        unicodeCodePoint.put("U+001B","");
        unicodeCodePoint.put("U+001C","");
        unicodeCodePoint.put("U+001D","");
        unicodeCodePoint.put("U+001E","");
        unicodeCodePoint.put("U+001F","");
        unicodeCodePoint.put("U+0020"," ");
        unicodeCodePoint.put("U+0021","!");
        unicodeCodePoint.put("U+0022","\"");
        unicodeCodePoint.put("U+0023","#");
        unicodeCodePoint.put("U+0024","$");
        unicodeCodePoint.put("U+0025","%");
        unicodeCodePoint.put("U+0026","&");
        unicodeCodePoint.put("U+0027","'");
        unicodeCodePoint.put("U+0028","(");
        unicodeCodePoint.put("U+0029",")");
        unicodeCodePoint.put("U+002A","*");
        unicodeCodePoint.put("U+002B","+");
        unicodeCodePoint.put("U+002C",",");
        unicodeCodePoint.put("U+002D","-");
        unicodeCodePoint.put("U+002E",".");
        unicodeCodePoint.put("U+002F","/");
        unicodeCodePoint.put("U+0030","0");
        unicodeCodePoint.put("U+0031","1");
        unicodeCodePoint.put("U+0032","2");
        unicodeCodePoint.put("U+0033","3");
        unicodeCodePoint.put("U+0034","4");
        unicodeCodePoint.put("U+0035","5");
        unicodeCodePoint.put("U+0036","6");
        unicodeCodePoint.put("U+0037","7");
        unicodeCodePoint.put("U+0038","8");
        unicodeCodePoint.put("U+0039","9");
        unicodeCodePoint.put("U+003A",":");
        unicodeCodePoint.put("U+003B",";");
        unicodeCodePoint.put("U+003C","<");
        unicodeCodePoint.put("U+003D","=");
        unicodeCodePoint.put("U+003E",">");
        unicodeCodePoint.put("U+003F","?");
        unicodeCodePoint.put("U+0040","@");
        unicodeCodePoint.put("U+0041","A");
        unicodeCodePoint.put("U+0042","B");
        unicodeCodePoint.put("U+0043","C");
        unicodeCodePoint.put("U+0044","D");
        unicodeCodePoint.put("U+0045","E");
        unicodeCodePoint.put("U+0046","F");
        unicodeCodePoint.put("U+0047","G");
        unicodeCodePoint.put("U+0048","H");
        unicodeCodePoint.put("U+0049","I");
        unicodeCodePoint.put("U+004A","J");
        unicodeCodePoint.put("U+004B","K");
        unicodeCodePoint.put("U+004C","L");
        unicodeCodePoint.put("U+004D","M");
        unicodeCodePoint.put("U+004E","N");
        unicodeCodePoint.put("U+004F","O");
        unicodeCodePoint.put("U+0050","P");
        unicodeCodePoint.put("U+0051","Q");
        unicodeCodePoint.put("U+0052","R");
        unicodeCodePoint.put("U+0053","S");
        unicodeCodePoint.put("U+0054","T");
        unicodeCodePoint.put("U+0055","U");
        unicodeCodePoint.put("U+0056","V");
        unicodeCodePoint.put("U+0057","W");
        unicodeCodePoint.put("U+0058","X");
        unicodeCodePoint.put("U+0059","Y");
        unicodeCodePoint.put("U+005A","Z");
        unicodeCodePoint.put("U+005B","[");
        unicodeCodePoint.put("U+005C","\\");
        unicodeCodePoint.put("U+005D","]");
        unicodeCodePoint.put("U+005E","^");
        unicodeCodePoint.put("U+005F","_");
        unicodeCodePoint.put("U+0060","`");
        unicodeCodePoint.put("U+0061","a");
        unicodeCodePoint.put("U+0062","b");
        unicodeCodePoint.put("U+0063","c");
        unicodeCodePoint.put("U+0064","d");
        unicodeCodePoint.put("U+0065","e");
        unicodeCodePoint.put("U+0066","f");
        unicodeCodePoint.put("U+0067","g");
        unicodeCodePoint.put("U+0068","h");
        unicodeCodePoint.put("U+0069","i");
        unicodeCodePoint.put("U+006A","j");
        unicodeCodePoint.put("U+006B","k");
        unicodeCodePoint.put("U+006C","l");
        unicodeCodePoint.put("U+006D","m");
        unicodeCodePoint.put("U+006E","n");
        unicodeCodePoint.put("U+006F","o");
        unicodeCodePoint.put("U+0070","p");
        unicodeCodePoint.put("U+0071","q");
        unicodeCodePoint.put("U+0072","r");
        unicodeCodePoint.put("U+0073","s");
        unicodeCodePoint.put("U+0074","t");
        unicodeCodePoint.put("U+0075","u");
        unicodeCodePoint.put("U+0076","v");
        unicodeCodePoint.put("U+0077","w");
        unicodeCodePoint.put("U+0078","x");
        unicodeCodePoint.put("U+0079","y");
        unicodeCodePoint.put("U+007A","z");
        unicodeCodePoint.put("U+007B","{");
        unicodeCodePoint.put("U+007C","|");
        unicodeCodePoint.put("U+007D","}");
        unicodeCodePoint.put("U+007E","~");
        unicodeCodePoint.put("U+007F","");
        unicodeCodePoint.put("U+0080","");
        unicodeCodePoint.put("U+0081","");
        unicodeCodePoint.put("U+0082","");
        unicodeCodePoint.put("U+0083","");
        unicodeCodePoint.put("U+0084","");
        unicodeCodePoint.put("U+0085","");
        unicodeCodePoint.put("U+0086","");
        unicodeCodePoint.put("U+0087","");
        unicodeCodePoint.put("U+0088","");
        unicodeCodePoint.put("U+0089","");
        unicodeCodePoint.put("U+008A","");
        unicodeCodePoint.put("U+008C","");
        unicodeCodePoint.put("U+008D","");
        unicodeCodePoint.put("U+008E","");
        unicodeCodePoint.put("U+008F","");
        unicodeCodePoint.put("U+0090","");
        unicodeCodePoint.put("U+0091","");
        unicodeCodePoint.put("U+0092","");
        unicodeCodePoint.put("U+0093","");
        unicodeCodePoint.put("U+0094","");
        unicodeCodePoint.put("U+0095","");
        unicodeCodePoint.put("U+0096","");
        unicodeCodePoint.put("U+0097","");
        unicodeCodePoint.put("U+0098","");
        unicodeCodePoint.put("U+0099","");
        unicodeCodePoint.put("U+009A","");
        unicodeCodePoint.put("U+009B","");
        unicodeCodePoint.put("U+009C","");
        unicodeCodePoint.put("U+009D","");
        unicodeCodePoint.put("U+009E","");
        unicodeCodePoint.put("U+009F","");
        unicodeCodePoint.put("U+00A0","");
        unicodeCodePoint.put("U+00A1","¡");
        unicodeCodePoint.put("U+00A2","¢");
        unicodeCodePoint.put("U+00A3","£");
        unicodeCodePoint.put("U+00A4","¤");
        unicodeCodePoint.put("U+00A5","¥");
        unicodeCodePoint.put("U+00A6","¦");
        unicodeCodePoint.put("U+00A7","§");
        unicodeCodePoint.put("U+00A8","¨");
        unicodeCodePoint.put("U+00A9","©");
        unicodeCodePoint.put("U+00AA","ª");
        unicodeCodePoint.put("U+00AB","«");
        unicodeCodePoint.put("U+00AC","¬");
        unicodeCodePoint.put("U+00AD","­");
        unicodeCodePoint.put("U+00AE","®");
        unicodeCodePoint.put("U+00AF","¯");
        unicodeCodePoint.put("U+00B0","°");
        unicodeCodePoint.put("U+00B1","±");
        unicodeCodePoint.put("U+00B2","²");
        unicodeCodePoint.put("U+00B3","³");
        unicodeCodePoint.put("U+00B4","´");
        unicodeCodePoint.put("U+00B5","µ");
        unicodeCodePoint.put("U+00B6","¶");
        unicodeCodePoint.put("U+00B7","·");
        unicodeCodePoint.put("U+00B8","¸");
        unicodeCodePoint.put("U+00B9","¹");
        unicodeCodePoint.put("U+00BA","º");
        unicodeCodePoint.put("U+00BB","»");
        unicodeCodePoint.put("U+00BC","¼");
        unicodeCodePoint.put("U+00BD","½");
        unicodeCodePoint.put("U+00BE","¾");
        unicodeCodePoint.put("U+00BF","¿");
        unicodeCodePoint.put("U+00C0","À");
        unicodeCodePoint.put("U+00C1","Á");
        unicodeCodePoint.put("U+00C2","Â");
        unicodeCodePoint.put("U+00C3","Ã");
        unicodeCodePoint.put("U+00C4","Ä");
        unicodeCodePoint.put("U+00C5","Å");
        unicodeCodePoint.put("U+00C6","Æ");
        unicodeCodePoint.put("U+00C7","Ç");
        unicodeCodePoint.put("U+00C8","È");
        unicodeCodePoint.put("U+00C9","É");
        unicodeCodePoint.put("U+00CA","Ê");
        unicodeCodePoint.put("U+00CB","Ë");
        unicodeCodePoint.put("U+00CC","Ì");
        unicodeCodePoint.put("U+00CD","Í");
        unicodeCodePoint.put("U+00CE","Î");
        unicodeCodePoint.put("U+00CF","Ï");
        unicodeCodePoint.put("U+00D0","Ð");
        unicodeCodePoint.put("U+00D1","Ñ");
        unicodeCodePoint.put("U+00D2","Ò");
        unicodeCodePoint.put("U+00D3","Ó");
        unicodeCodePoint.put("U+00D4","Ô");
        unicodeCodePoint.put("U+00D5","Õ");
        unicodeCodePoint.put("U+00D6","Ö");
        unicodeCodePoint.put("U+00D7","×");
        unicodeCodePoint.put("U+00D8","Ø");
        unicodeCodePoint.put("U+00D9","Ù");
        unicodeCodePoint.put("U+00DA","Ú");
        unicodeCodePoint.put("U+00DB","Û");
        unicodeCodePoint.put("U+00DC","Ü");
        unicodeCodePoint.put("U+00DD","Ý");
        unicodeCodePoint.put("U+00DE","Þ");
        unicodeCodePoint.put("U+00DF","ß");
        unicodeCodePoint.put("U+00E0","à");
        unicodeCodePoint.put("U+00E1","á");
        unicodeCodePoint.put("U+00E2","â");
        unicodeCodePoint.put("U+00E3","ã");
        unicodeCodePoint.put("U+00E4","ä");
        unicodeCodePoint.put("U+00E5","å");
        unicodeCodePoint.put("U+00E6","æ");
        unicodeCodePoint.put("U+00E7","ç");
        unicodeCodePoint.put("U+00E8","è");
        unicodeCodePoint.put("U+00E9","é");
        unicodeCodePoint.put("U+00EA","ê");
        unicodeCodePoint.put("U+00EB","ë");
        unicodeCodePoint.put("U+00EC","ì");
        unicodeCodePoint.put("U+00ED","í");
        unicodeCodePoint.put("U+00EE","î");
        unicodeCodePoint.put("U+00EF","ï");
        unicodeCodePoint.put("U+00F0","ð");
        unicodeCodePoint.put("U+00F1","ñ");
        unicodeCodePoint.put("U+00F2","ò");
        unicodeCodePoint.put("U+00F3","ó");
        unicodeCodePoint.put("U+00F4","ô");
        unicodeCodePoint.put("U+00F5","õ");
        unicodeCodePoint.put("U+00F6","ö");
        unicodeCodePoint.put("U+00F7","÷");
        unicodeCodePoint.put("U+00F8","ø");
        unicodeCodePoint.put("U+00F9","ù");
        unicodeCodePoint.put("U+00FA","ú");
        unicodeCodePoint.put("U+00FB","û");
        unicodeCodePoint.put("U+00FC","ü");
        unicodeCodePoint.put("U+00FD","ý");
        unicodeCodePoint.put("U+00FE","þ");
        unicodeCodePoint.put("U+00FF","ÿ");
        unicodeCodePoint.put("U+FEFF","");
    }
        	
	//PRIVATE
	//final static String FILE_NAME = "C:\\Temp\\input.properties";
	//System.getProperty("user.dir")+"\\src\\triple_karma_output_20150214_150502.n3";
	private static String FILE_NAME;
	private static String OUTPUT_FILE_NAME;
	private static Charset ENCODING = StandardCharsets.UTF_8;
	private static String FIXED_TEXT = "But soft! what code in yonder program breaks?";

	private String quote(String aText){
           String QUOTE = "'";
	   return QUOTE + aText + QUOTE;
	}
	 	
	public EncodingUtil(){
            setMapUnicodeEscaped();
        }
        
    public EncodingUtil(String FILE_NAME,String OUTPUT_FILE_NAME,Charset ENCODING)
    {
        setMapUnicodeEscaped();
        EncodingUtil.FILE_NAME=FILE_NAME;
        EncodingUtil.OUTPUT_FILE_NAME=OUTPUT_FILE_NAME;
        EncodingUtil.ENCODING=ENCODING;
    }

    public EncodingUtil(String FILE_NAME,Charset ENCODING)
    {
        setMapUnicodeEscaped();
        EncodingUtil.FILE_NAME=FILE_NAME;
        EncodingUtil.ENCODING=ENCODING;
    }

	//For smaller files

    /**
     * Read small and large file of text
     *  Note: the javadoc of Files.readAllLines says it's intended for small
     *   files. But its implementation uses buffering, so it's likely good 
     *  even for fairly large files
     * @param aFileName string path to the file you want to read
     * @return a list of lines 
     * @throws IOException file not found
     */
    public static List<String> readSmallTextFile(String aFileName) throws IOException {
	    Path path = Paths.get(aFileName);
	    return Files.readAllLines(path, ENCODING);
    }
	  
	  
    public static void writeSmallTextFile(List<String> aLines, String aFileName) throws IOException {
	    Path path = Paths.get(aFileName);
	    Files.write(path, aLines, ENCODING);
    }
      
	
     //For larger files
	/* public static List<String> readLargerTextFile(String aFileName) throws IOException {
        List<String> list = new ArrayList<>();
	    Path path = Paths.get(aFileName);
	    try (Scanner scanner =  new Scanner(path, ENCODING.name())){
	      while (scanner.hasNextLine()){
	        //process each line in some way
	        //SystemLog.console(scanner.nextLine());
            list.add(scanner.nextLine())
	      }      
	    }
         return list;
     }*/
	  
     public static List<String> readLargerTextFile(String aFileName) throws IOException {
        List<String> list = new ArrayList<>();
        Path path = Paths.get(aFileName);
        try (Scanner scanner =  new Scanner(path, ENCODING.name())){
          while (scanner.hasNextLine()){
            //process each line in some way
            try{
              list.add(scanner.nextLine());
            }catch( java.util.NoSuchElementException e){
              if(!scanner.hasNextLine()) break;
            }
          }
        }
        return list;
     }
	  
     public static List<String> readLargerTextFileAlternate(String aFileName) throws IOException {
        List<String> list = new ArrayList<>();
	    Path path = Paths.get(aFileName);
	    try (BufferedReader reader = Files.newBufferedReader(path, ENCODING)) {
            String line;
            while ((line = reader.readLine()) != null) {
                //process each line in some way
                list.add(line);
            }
        }
	    return list;
     }
	  
     public static void writeLargerTextFile(String aFileName, List<String> aLines) throws IOException {
	    Path path = Paths.get(aFileName);
	    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
	      for(String line : aLines){
	        writer.write(line);
	        writer.newLine();
	      }
	    }catch(java.lang.NullPointerException e){
                //------DO NOTHING
        }
     }
          
     public static void writeLargerTextFileWithReplace(String aFileName, List<String> aLines) throws IOException {
	    Path path = Paths.get(aFileName);
	    try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path.toString(), true)))) {                        
	      for(String line : aLines){  
              try{
                for (Map.Entry<String, String> entry : unicodeCodePoint.entrySet())
                {
                    try{
                        String s = entry.getKey().replace("U+","\\u");
                        if(line.contains(s)){
                            line = line.replace(s, entry.getValue());
                        }
                    }catch(java.lang.NullPointerException ne){break;}
                }
                out.print(line + System.getProperty("line.separator"));
                out.flush();
              }catch(java.lang.NullPointerException ne){break;}
	      }                
                //out.close();
	    }
        catch(java.lang.NullPointerException ne){
            SystemLog.warning("Can't decode the file:"+aFileName);
        }
     }
           
     public static void writeLargerTextFileWithReplace2(String aFileName, List<String> aLines) throws IOException {
	    Path path = Paths.get(aFileName);
	    try (BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)){
	      for(String line : aLines){
                  try{
                    for (Map.Entry<String, String> entry : unicodeCodePoint.entrySet())
                      {
                          try{
                          String s = entry.getKey().replace("U+","\\u");
                          if(line.contains(s)){
                              line = line.replace(s,entry.getValue());
                          }
                          }catch(java.lang.NullPointerException ne){
                              break;
                          }
                      } //foreach entry
                  writer.write(line);
                  writer.newLine();
                }catch(java.lang.NullPointerException ne){
                    break;
                }
	      }//FOREACH LINE
	    }catch(java.lang.NullPointerException ne){
                SystemLog.warning(ne.getMessage());
        }
     }
	  
   /**
    * Template method that calls {@link #processLine(String)}.
    * @param aFileName string of the path to the file
    * @throws IOException file not found
    */
    public final void processLineByLine(String aFileName) throws IOException {
          Path path = Paths.get(aFileName);
      try (Scanner scanner =  new Scanner(path, ENCODING.name())){
        while (scanner.hasNextLine()){
          processLine(scanner.nextLine());
        }      
      }catch(java.lang.NullPointerException ne){
          SystemLog.warning("Can't read the file:"+aFileName);
      }
    }
	  
	  
	  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  /** 
	   Overridable method for processing lines in different ways.	    
	   This simple default implementation expects simple name-value pairs, separated by an 
	   '=' sign. Examples of valid input: 
	   height = 167cm
	   mass =  65kg
	   disposition =  "grumpy"
	   this is the name = this is the value
           * @param aLine string correspond to a line of the file
	  */
	  protected void processLine(String aLine){
	    //use a second Scanner to parse the content of each line 
	    Scanner scanner = new Scanner(aLine);
	    scanner.useDelimiter("=");
	    if (scanner.hasNext()){
	      //assumes the line has a certain structure
	      SystemLog.console("Name:"+scanner.next()+",Value:"+scanner.next());
	    }
	    else {
          SystemLog.console("Empty or invalid line. Unable to process.");
	    }
	  }
	  
    /** Write fixed content to the given file. */
    void write() throws IOException  {
        SystemLog.message("Try to writing to file named " + FILE_NAME + " with Encoding: " + ENCODING);
        Writer out = new OutputStreamWriter(new FileOutputStream(FILE_NAME), ENCODING);
    }
	  
    
    /**
     * Read the contents of the given file. 
     * @return the String content of the file.
     * @throws IOException file not found.
     */
    public String read() throws IOException {
        //log("Reading from file.");
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        try (Scanner scanner = new Scanner(new FileInputStream(FILE_NAME), ENCODING.name())) {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine()).append(NL);
            }
        }
        return text.toString();
    }

	 
	  /////////////////////////////////////////////////////////////////////////////////////////////////////////
	  /**
	   * Fetch the entire contents of a text file, and return it in a String.
	   * This style of implementation does not throw Exceptions to the caller.
	   *
	   * @param aFile is a file which already exists and can be read.
           * @return content of the file
	   */
	   static public String getContents(File aFile) {
	     //...checks on aFile are elided
	     StringBuilder contents = new StringBuilder();
	     try {
	       //use buffering, reading one line at a time
	       //FileReader always assumes default encoding is OK!
             try (BufferedReader input = new BufferedReader(new FileReader(aFile))) {
                 String line; //not declared within while loop
	         /*
	         * readLine is a bit quirky :
	         * it returns the content of a line MINUS the newline.
	         * it returns null only for the END of the stream.
	         * it returns an empty String if two newlines appear in a row.
	         */
                 while ((line = input.readLine()) != null) {
                     contents.append(line);
                     contents.append(System.getProperty("line.separator"));
                 }
             }
	     }
	     catch (IOException ex){
	       SystemLog.exception(ex);
	     }
	     
	     return contents.toString();
	   }

        /**
         * Reads file in UTF-8 encoding and output to STDOUT in ASCII with unicode
         * escaped sequence for characters outside of ASCII.
         * It is equivalent to: native2ascii -encoding utf-8.
         * @param UTF8 encoding of input.
         * @return ASCII encoding of output.
         * @throws IOException  file not found.
         */
        public static List<String> convertUTF8ToUnicodeEscape(File UTF8) throws IOException{
            List<String> list = new ArrayList<>();
            if (UTF8==null) {
                 System.out.println("Usage: java UTF8ToAscii <filename>");
                 return null;
             }
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(UTF8),"UTF-8" )
        )) {
            String line = r.readLine();
            
            while (line != null) {
                System.out.println(unicodeEscape(line));
                line = r.readLine();
                list.add(line);
            }
        }   
             return list;
        }

        private static final char[] hexChar = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        /**
         * Method for convert a string UTF-8 to HEX
         * @param text string of text you want to convert to HEX
         * @return the text in HEX encoding
         */
        private static String unicodeEscape(String text) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                 char c = text.charAt(i);
                 if ((c >> 7) > 0) {
                    sb.append("\\u");
                    sb.append(hexChar[(c >> 12) & 0xF]); // append the hex character for the left-most 4-bits
                    sb.append(hexChar[(c >> 8) & 0xF]); // hex for the second group of 4-bits from the left
                    sb.append(hexChar[(c >> 4) & 0xF]); // hex for the third group
                    sb.append(hexChar[c & 0xF]); // hex for the last group, e.home., the right most 4-bits
                }else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }     
        
       /**
        * Reads file with unicode escaped characters and write them out to
        * stdout in UTF-8.
        * This utility is equivalent to: native2ascii -reverse -encoding utf-8.
        * @param ASCII file of input in ASCII encoding.
        * @return UTF8 file of input in UTF8 encoding.
        * @throws IOException file not found.
        */
        public static List<String> convertUnicodeEscapeToUTF8(File ASCII) throws IOException {
            List<String> list = new ArrayList<>();
            if (ASCII == null) {
                System.out.println("Usage: java UnicodeEscape2UTF8 <filename>");
                return null;
            }
        try (BufferedReader r = new BufferedReader(new FileReader(ASCII))) {
            String line = r.readLine();
            while (line != null) {
                line = convertUnicodeEscape(line);
                byte[] bytes = line.getBytes("UTF-8");
                //System.out.write(bytes, 0, bytes.length);
                //System.out.println();
                //line = r.readLine();
                //list.add(line);
                list.add(StringKit.convertByteArrayToString(bytes));
            }
        }
            return list;
        }
        
        enum ParseState {NORMAL,ESCAPE,UNICODE_ESCAPE}
        
        /**
         *  convert unicode escapes back to char
         * @param s string to convert unicode escape.
         * @return string converted.
         */
        private static String convertUnicodeEscape(String s) {
            char[] out = new char[s.length()];
            ParseState state = ParseState.NORMAL;
            int j = 0, k = 0, unicode = 0;
            char c = ' ';
            for (int i = 0; i < s.length(); i++) {
                c = s.charAt(i);
                if (state == ParseState.ESCAPE) {
                    if (c == 'u') {
                        state = ParseState.UNICODE_ESCAPE;
                        unicode = 0;
                    }
                    else { // we don't care about other escapes
                        out[j++] = '\\';
                        out[j++] = c;
                        state = ParseState.NORMAL;
                    }
                }
                else if (state == ParseState.UNICODE_ESCAPE) {
                    if ((c >= '0') && (c <= '9')) {
                        unicode = (unicode << 4) + c - '0';
                    }
                    else if ((c >= 'a') && (c <= 'f')) {
                        unicode = (unicode << 4) + 10 + c - 'a';
                    }
                    else if ((c >= 'A') && (c <= 'F')) {
                        unicode = (unicode << 4) + 10 + c - 'A';
                    }
                    else {
                        throw new IllegalArgumentException("Malformed unicode escape");
                    }
                    k++;
                    if (k == 4) {
                        out[j++] = (char) unicode;
                        k = 0;
                        state = ParseState.NORMAL;
                    }
                }
                else if (c == '\\') {
                    state = ParseState.ESCAPE;
                }
                else {
                    out[j++] = c;
                }
            }//for
            if (state == ParseState.ESCAPE) {
                out[j++] = c;
            }
            return new String(out, 0, j);
        }

    /**
     * Method to rewrite a file in the UTF-8 encoding
     * @param fileASCII file of input in ASCII encoding
     * @throws IOException file not found
     */
    public static void rewriteTheFileToUTF8(File fileASCII) throws IOException{
        List<String> list = convertUnicodeEscapeToUTF8(fileASCII);
        String filePathASCII = fileASCII.getAbsolutePath();
        fileASCII.delete();
        //fileASCII = new File(filePathASCII);
        writeLargerTextFile(filePathASCII, list);
    }

    /**
     * Method to rewrite a file in the ASCII encoding
     * @param fileUTF8 file of input in UTF8 encoding
     * @throws IOException file not found
     */
    public static void rewriteTheFileToASCII(File fileUTF8) throws IOException{
        List<String> list = convertUTF8ToUnicodeEscape(fileUTF8);
        String filePathUTF8 = fileUTF8.getAbsolutePath();
        fileUTF8.delete();
        //fileASCII = new File(filePathASCII);
        writeLargerTextFile(filePathUTF8, list);
    }

    /**
     * Method to rewrite a file in the ASCII encoding
     * @param filePathUTF8 file of input in UTF8 encoding
     * @throws IOException file not found
     */
    public static void rewriteTheFileToASCII(String filePathUTF8) throws IOException{
        rewriteTheFileToASCII(new File(filePathUTF8));
    }

    /**
     * Method to rewrite a file in the UTF-8 encoding
     * @param filePathASCII file of input in ASCII encoding
     * @throws IOException file not found
     */
    public static void rewriteTheFileToUTF8(String filePathASCII) throws IOException{
        rewriteTheFileToUTF8(new File(filePathASCII));
    }

    /**
     * Convert byte array to Hex String
     * @param b array of byte
     * @return Hex String
     */
    private static String convertByteArrayToHexString(byte[] b) {
        int len = b.length;
        StringBuilder data = new StringBuilder();
        for (byte aB : b) {
            data.append(Integer.toHexString((aB >> 4) & 0xf));
            data.append(Integer.toHexString(aB & 0xf));
        }
        return data.toString();
    }

    //OTHER


    public static void rewriteTheFileToUTF8(String filePathInput,String filePathOutput) {
        try {
            FileOutputStream fos = new FileOutputStream(filePathInput);
            try (Writer out = new OutputStreamWriter(fos, ENCODING)) {
                out.write(filePathOutput);
            }
        } catch (IOException e) {
            SystemLog.exception(e);
        }
    }

    public static String readLargeTextFileUTF8(String filePathInput) {
        //List<String> list = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(filePathInput);
            InputStreamReader isr = new InputStreamReader(fis, ENCODING);
            try (Reader in = new BufferedReader(isr)) {
                int ch;
                while ((ch = in.read()) > -1) {
                    buffer.append((char) ch);
                }
            }
            return buffer.toString();
        } catch (IOException e) {
            SystemLog.exception(e);
            return null;
        }
    }

    // FEFF because this is the Unicode char represented by the UTF-8 byte order mark (EF BB BF).
    public static final String UTF8_BOM = "\uFEFF";

    public static void UTF8ToAnsi(File fileUTF8,File fileANSI) {
        try {
            boolean firstLine = true;
            FileInputStream fis = new FileInputStream(fileUTF8);
            try (BufferedReader r = new BufferedReader(new InputStreamReader(fis,"UTF8"))) {
                FileOutputStream fos = new FileOutputStream(fileANSI);
                try (Writer w = new BufferedWriter(new OutputStreamWriter(fos, "Cp1252"))) {
                    for (String s; (s = r.readLine()) != null;) {
                        if (firstLine) {
                            if (s.startsWith(UTF8_BOM)) {
                                s = s.substring(1);
                            }
                            firstLine = false;
                        }
                        w.write(s + System.getProperty("line.separator"));
                        w.flush();
                    }
                }
            }
            System.exit(0);
        }

        catch (Exception e) {
            SystemLog.exception(e);
            System.exit(1);
        }
    }

    public static String removeUTF8BOM(String s) {
        if (s.startsWith(UTF8_BOM)) {
            s = s.substring(1);
        }
        return s;
    }

    public final static String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();

  /*  public static String detect(InputStream is) throws IOException {
        byte[] buf = new byte[4096];
        // (1)
        org.mozilla.universalchardet.UniversalDetector detector =
                new org.mozilla.universalchardet.UniversalDetector(null);
        // (2)
        int nread;
        while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        // (3)
        detector.dataEnd();
        // (4)
        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            logger.debug("Detected encoding = " + encoding);
        } else {
            logger.debug("No encoding detected, using default: " + DEFAULT_ENCODING);
            encoding = DEFAULT_ENCODING;
        }
        // (5)
        detector.reset();
        return encoding;
    }*/

   /* public static String detect(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            String encoding = EncodingDetector.detect(fis);
            logger.info("Detected encoding for file: " + file.getName() + ": " + encoding);
            if (encoding == null) {
                encoding = DEFAULT_ENCODING;
            }
            return encoding;
        } catch(Exception e) {
            logger.debug("Exception detecting encoding, using default: " + DEFAULT_ENCODING);
        }
        return DEFAULT_ENCODING;
    }*/

    public static InputStreamReader getInputStreamReader(InputStream is, String encoding) throws IOException {
        logger.info("Reading stream: using encoding: " + encoding);
        org.apache.commons.io.input.BOMInputStream bis = new org.apache.commons.io.input.BOMInputStream(is); //So that we can remove the BOM
        return new InputStreamReader(bis, encoding);
    }

    public static InputStreamReader getInputStreamReader(File file, String encoding) throws IOException {

        FileInputStream fis = new FileInputStream(file);
        logger.info("Reading file: " + file + " using encoding: " + encoding);
        org.apache.commons.io.input.BOMInputStream bis =
                new org.apache.commons.io.input.BOMInputStream(fis); //So that we can remove the BOM
        return new InputStreamReader(bis, encoding);
    }

    public static String getString(File file, String encoding) throws IOException {
        StringWriter sw = new StringWriter();
        FileInputStream fis = new FileInputStream(file);
        logger.info("Reading file: " + file + " using encoding: " + encoding);
        org.apache.commons.io.IOUtils.copy(fis, sw, encoding);

        return sw.toString();
    }


  /*
    Normalizer.normalize(geo.getEdificio(), Normalizer.Form.NFD);
    geo.setEdificio(geo.getEdificio().replaceAll("[^\\p{ASCII}]", ""));
    geo.setIndirizzo(geo.getIndirizzo().replace("[^a-zA-Z\\d\\s:]",""));
    Normalizer.normalize(geo.getIndirizzo(), Normalizer.Form.NFD);
    geo.setIndirizzo(geo.getIndirizzo().replaceAll("[^\\p{ASCII}]", ""));
    */
}
