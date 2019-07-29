package burp

import com.esotericsoftware.minlog.Log
import java.nio.charset.Charset

class FuzzyEncodingGenerator : IIntruderPayloadGenerator {

    val encoders = arrayOf(
            URLEncoderSimple(), URLEncoderAll(),


            HtmlEntitiesHexadecimalOnly(),HtmlEntitiesDecimalOnly(),
            HtmlEntitiesNamedHexadecimal(), HtmlEntitiesNamedDecimal(),

            BackslashUnicodeHexadecimal(),
            BackslashHexadecimal(),

            PhpChar(),

            AsciToZeroHexadecimal(),
            AsciToHexadecimal(),


            ReplaceSpacesBySlash(), ReplaceSpacesByTabs(), ReplaceSpacesByComments(), ReplaceSpacesByNewLine(),

            CDataEncapsulate(),

            Capitalize(),Lowercase(),UpperCase())


    var currentEncoder = 0;

    init {
        Log.info("${encoders.size} encoders loaded")
    }


    override fun getNextPayload(baseValueBytes: ByteArray): ByteArray {
        val baseValue = String(baseValueBytes, Charset.defaultCharset())

        val res =  encoders[currentEncoder].encoder(baseValue)
        currentEncoder++
        return res.toByteArray(Charset.defaultCharset())
    }

    override fun reset() {
        currentEncoder = 0
    }

    override fun hasMorePayloads(): Boolean {
        return currentEncoder < encoders.size
    }


}