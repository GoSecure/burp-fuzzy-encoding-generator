package burp

import com.esotericsoftware.minlog.Log
import java.io.IOException

class BurpExtender : IBurpExtender, IIntruderPayloadGeneratorFactory {

    lateinit var callbacks: IBurpExtenderCallbacks
    lateinit var helpers: IExtensionHelpers


    private val payloadGeneratorName = "Fuzzy Encoding Generator"


    override fun registerExtenderCallbacks(callbacks: IBurpExtenderCallbacks) {
        this.callbacks = callbacks
        helpers = callbacks.helpers

        callbacks.setExtensionName(payloadGeneratorName)


        Log.setLogger(object : Log.Logger() {
            override fun print(message: String) {
                try {
                    if (message.contains("ERROR:")) { //Not the most elegant way, but should be effective.
                        callbacks.issueAlert(message)
                    }
                    callbacks.stdout.write(message.toByteArray())
                    callbacks.stdout.write('\n'.toInt())
                } catch (e: IOException) {
                    System.err.println("Error while printing the log : " + e.message) //Very unlikely
                }

            }
        })
        Log.INFO()

        callbacks.registerIntruderPayloadGeneratorFactory(this)
    }


    override fun createNewInstance(attack: IIntruderAttack): IIntruderPayloadGenerator = FuzzyEncodingGenerator()

    override fun getGeneratorName() = payloadGeneratorName
}
