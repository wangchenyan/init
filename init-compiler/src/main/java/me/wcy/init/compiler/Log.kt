package me.wcy.init.compiler

import java.io.PrintWriter
import java.io.StringWriter
import java.net.UnknownHostException
import javax.annotation.processing.Messager
import javax.tools.Diagnostic

object Log {
    private var sMessenger: Messager? = null

    fun setLogger(messager: Messager) {
        sMessenger = messager
    }

    fun i(msg: String) {
        sMessenger?.printMessage(Diagnostic.Kind.NOTE, msg + "\n")
    }

    fun w(msg: String) {
        sMessenger?.printMessage(Diagnostic.Kind.WARNING, msg + "\n")
    }

    fun e(msg: String) {
        sMessenger?.printMessage(Diagnostic.Kind.ERROR, msg + "\n")
    }

    fun e(msg: String, tr: Throwable) {
        sMessenger?.printMessage(
            Diagnostic.Kind.ERROR,
            msg + "\n" + getStackTraceString(tr) + "\n"
        )
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     * @param tr An exception to log
     */
    private fun getStackTraceString(tr: Throwable?): String {
        if (tr == null) {
            return ""
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        var t = tr
        while (t != null) {
            if (t is UnknownHostException) {
                return ""
            }
            t = t.cause
        }

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        tr.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }
}
