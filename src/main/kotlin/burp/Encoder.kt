package burp

import com.esotericsoftware.minlog.Log
import org.apache.commons.lang3.StringUtils
import org.unbescape.html.HtmlEscape
import org.unbescape.html.HtmlEscapeLevel
import org.unbescape.html.HtmlEscapeType
import org.unbescape.javascript.JavaScriptEscape
import org.unbescape.javascript.JavaScriptEscapeLevel
import org.unbescape.javascript.JavaScriptEscapeType
import java.net.URLEncoder
import java.util.ArrayList

interface Encoder {
    fun encoder(input:String):String
}

class EncodingUtils {
    fun ascii2hex(str: String, separator: String): String {
        var output = StringBuilder()
        var hex = ""
        for (i in 0 until str.length) {
            try {
                hex = Integer.toHexString(Character.codePointAt(str, i))
                if (hex.length % 2 != 0) {
                    hex = "0$hex"
                }
                output.append(hex)
                if (separator.isNotEmpty() && i < str.length - 1) {
                    output.append(separator)
                }
            } catch (e: NumberFormatException) {
                Log.error(e.message+" with $str")
            }

        }
        return output.toString()
    }
}




/**
 * bien%20re%C3%A7u
 */
class URLEncoderSimple:Encoder {
    override fun encoder(input: String): String {
        return URLEncoder.encode(input, "UTF-8").replace(Regex("\\+"),"%20");
    }

}

/**
 * %62%69%65%6E%20%72%65%E7%75
 */
class URLEncoderAll:Encoder {

    override fun encoder(input: String): String {
        val converted = StringBuilder()
        for (i in 0 until input.length) {
            val codePoint = Character.codePointAt(input, i)
            if (codePoint <= 0xff) {
                converted.append("%" + String.format("%02X", codePoint))
            } else {
                converted.append(URLEncoder.encode(Character.toString(input[i])), "UTF-8")
            }
        }
        return converted.toString()
    }
}


/**
 * bien&#x20;re&ccedil;u
 */
class HtmlEntitiesNamedHexadecimal : Encoder {
    override fun encoder(input: String): String {
        return HtmlEscape.escapeHtml(input, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_HEXA, HtmlEscapeLevel.LEVEL_3_ALL_NON_ALPHANUMERIC);
    }
}

/**
 * bien&#32;re&ccedil;u
 */
class HtmlEntitiesNamedDecimal : Encoder {
    override fun encoder(input: String): String {
        return HtmlEscape.escapeHtml(input, HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL, HtmlEscapeLevel.LEVEL_3_ALL_NON_ALPHANUMERIC);
    }
}

/**
 * bien&#x20;re&#xe7;u
 */
class HtmlEntitiesHexadecimalOnly : Encoder {
    override fun encoder(input: String): String {
        return HtmlEscape.escapeHtml(input, HtmlEscapeType.HEXADECIMAL_REFERENCES, HtmlEscapeLevel.LEVEL_3_ALL_NON_ALPHANUMERIC);
    }
}

/**
 * bien&#32;re&#231;u
 */
class HtmlEntitiesDecimalOnly : Encoder {
    override fun encoder(input: String): String {
        return HtmlEscape.escapeHtml(input, HtmlEscapeType.DECIMAL_REFERENCES, HtmlEscapeLevel.LEVEL_3_ALL_NON_ALPHANUMERIC);
    }
}

/**
 * \u0062\u0069\u0065\u006E\u0020\u0072\u0065\u00E7\u0075
 */
class BackslashUnicodeHexadecimal : Encoder {
    override fun encoder(input: String): String {
        return JavaScriptEscape.escapeJavaScript(input, JavaScriptEscapeType.UHEXA, JavaScriptEscapeLevel.LEVEL_4_ALL_CHARACTERS);
    }
}

/**
 * \x62\x69\x65\x6E\x20\x72\x65\xE7\x75
 */
class BackslashHexadecimal : Encoder {
    override fun encoder(input: String): String {
        return JavaScriptEscape.escapeJavaScript(input, JavaScriptEscapeType.XHEXA_DEFAULT_TO_UHEXA, JavaScriptEscapeLevel.LEVEL_4_ALL_CHARACTERS);
    }
}
/**
 * chr(98).chr(105).chr(101).chr(110).chr(32).chr(114).chr(101).chr(231).chr(117)
 */
class PhpChar : Encoder {
    override fun encoder(input: String): String {
        val output = ArrayList<String>()
        for (i in 0 until input.length) {
            output.add("chr(" + Character.codePointAt(input, i) + ")")
        }
        return StringUtils.join(output, ".")
    }
}

/**
 * 5468697320697320612074657374
 */
class AsciToHexadecimal : Encoder {
    override fun encoder(input: String): String {
        return EncodingUtils().ascii2hex(input,"")
    }
}

/**
 * 0x5468697320697320612074657374
 */
class AsciToZeroHexadecimal : Encoder {
    override fun encoder(input: String): String {
        return "0x"+EncodingUtils().ascii2hex(input,"")
    }
}

class Capitalize : Encoder {
    override fun encoder(input: String): String = StringUtils.capitalize(input)
}
class Lowercase : Encoder {
    override fun encoder(input: String): String = StringUtils.lowerCase(input)
}
class UpperCase : Encoder {
    override fun encoder(input: String) = StringUtils.upperCase(input)
}


class ReplaceSpacesBySlash:Encoder {
    override fun encoder(input: String): String  = input.replace(Regex(" "),"/")
}

class ReplaceSpacesByTabs:Encoder {
    override fun encoder(input: String): String = input.replace(Regex(" "),"\t")
}

class ReplaceSpacesByComments:Encoder {
    override fun encoder(input: String): String  = input.replace(Regex(" "),"/**/")
}

class ReplaceSpacesByNewLine:Encoder {
    override fun encoder(input: String): String = input.replace(Regex(" "),"\n")
}

class CDataEncapsulate:Encoder {
    override fun encoder(input: String): String = "<![CDATA[$input]]>"
}

