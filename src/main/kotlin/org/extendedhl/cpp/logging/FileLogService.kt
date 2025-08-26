package org.extendedhl.cpp.logging

import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@Service(Service.Level.APP)
class FileLogService {
  private val ideaLog = Logger.getInstance(FileLogService::class.java)

  private val lock = ReentrantLock()
  private val dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
  private val logDir: Path = Paths.get(org.extendedhl.cpp.config.Const.LOG_PATH)
  private val logFile: Path = logDir.resolve("plugin.log")
  private var writer: BufferedWriter? = null

  init {
    try {
      Files.createDirectories(logDir)
      writer = BufferedWriter(
        OutputStreamWriter(
          Files.newOutputStream(logFile),
          StandardCharsets.UTF_8
        )
      )
      ideaLog.info("Custom file logging initialized at: $logFile")
    } catch (t: Throwable) {
      ideaLog.error("Failed to initialize custom file logging", t)
    }
  }

  fun log(level: String, loggerName: String, msg: String, t: Throwable? = null) {
    // Always go to IDEA log via its facade
    val m = "[$loggerName] $msg"
    when (level) {
      "DEBUG" -> ideaLog.debug(m, t)
      "INFO" -> ideaLog.info(m, t)
      "WARN" -> ideaLog.warn(m, t)
      "ERROR" -> ideaLog.error(m, t)
      else -> ideaLog.info(m, t)
    }

    // Also mirror to the plugin file
    val ts = LocalDateTime.now().format(dateFmt)
    val throwable = t?.let { "\n${it.stackTraceToString()}" }.orEmpty()
    val line = "$ts [$level] $loggerName - $msg$throwable\n"

    lock.withLock {
      try {
        writer?.apply {
          write(line)
          flush()
        }
      } catch (ignored: Throwable) {
        // Avoid recursive logging here to prevent loops; rely on idea.log already written above
      }
    }
  }
}