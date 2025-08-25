package org.extendedhl.cpp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger

inline fun <reified T> logger(): PluginLogger = PluginLogger(T::class.java)

class PluginLogger(private val clazz: Class<*>) {
  private val ideaLog: Logger = Logger.getInstance(clazz)

  private fun fileLogService(): FileLogService? =
    ApplicationManager.getApplication().getService(FileLogService::class.java)

  fun debug(message: String, t: Throwable? = null) {
    ideaLog.debug(message, t)
    fileLogService()?.log("DEBUG", clazz.name, message, t)
  }

  fun info(message: String, t: Throwable? = null) {
    ideaLog.info(message, t)
    fileLogService()?.log("INFO", clazz.name, message, t)
  }

  fun warn(message: String, t: Throwable? = null) {
    ideaLog.warn(message, t)
    fileLogService()?.log("WARN", clazz.name, message, t)
  }

  fun error(message: String, t: Throwable? = null) {
    ideaLog.error(message, t)
    fileLogService()?.log("ERROR", clazz.name, message, t)
  }
}