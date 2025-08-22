package org.extendedhl.cpp

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import java.nio.file.Files
import java.nio.file.Paths

@Service(Service.Level.APP)
class PluginInitializer {
  companion object {
    private val LOG = Logger.getInstance(PluginInitializer::class.java)
  }

  init {
    try {
      val logDir = Paths.get(System.getProperty("user.home"), "myplugin")
      Files.createDirectories(logDir)
      val logFilePath = logDir.resolve("custom.log").toString()
      LOG.info("Custom logging initialized to $logFilePath")
    } catch (e: Exception) {
      LOG.error("Failed to initialize custom logging", e)
    }
  }
}