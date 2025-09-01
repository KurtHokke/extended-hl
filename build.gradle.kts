import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.extensions.IntelliJPlatformTestingExtension
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask

//import org.gradle.kotlin.dsl.withType

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.1.0"
  id("org.jetbrains.intellij.platform") version "2.7.2"
}

group = "org.extendedhl"
version = "1.0-SNAPSHOT"

repositories {
  maven { setUrl("https://cache-redirector.jetbrains.com/maven-central") }
  intellijPlatform {
    defaultRepositories()
  }
}
val clionHome: String by project
val clionRadler: String by project
val clionRdIdeModels: String by project
val clionCidrPSIBase: String by project
// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
  intellijPlatform {
    local(clionHome)
    plugin(id = "com.jetbrains.gerryPurpleTheme", version =  "2025.1.0820")
  }
  compileOnly(fileTree(getIdeRelativePath(clionRadler)))
  compileOnly(files(getIdeRelativePath(clionRdIdeModels)))
  compileOnly(files(getIdeRelativePath(clionCidrPSIBase)))
}
fun getIdeRelativePath(path: String): String {
  return intellijPlatform.platformPath.resolve(path).toString()
}


intellijPlatform {
  pluginConfiguration {
    //version.set("RD-2025.2")
    ideaVersion {
      sinceBuild = "252"
    }

    changeNotes = """
      Initial version
    """.trimIndent()
  }
}

tasks {
  //val projectName = project.extensionProvider.flatMap { it.projectName }
  // Set the JVM compatibility versions
  withType<JavaCompile> {
      sourceCompatibility = "21"
      targetCompatibility = "21"
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    exclude("**/ExtendedHighlightExtHandler.kt")
    exclude("**/ExtendedHighlighterReapplier.kt")
    exclude("**/ColorSchemeSettingsListener.kt")
  }
  withType<RunIdeTask> {
    autoReload = false
    //System.getProperties()

  }
  intellijPlatformTesting {
    customRunIdeTask {
      setClionRadlerSystemProperties()
    }
  }
}
fun IntelliJPlatformTestingExtension.customRunIdeTask(
    configureRunIdeTask: RunIdeTask.() -> Unit = {},
) {
  val p = runIde.register("run clion nova")
  p.configure {
    useInstaller = false
    localPath.dir(clionHome)

    task(configureRunIdeTask)

  }
}
kotlin {
  compilerOptions {

    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
  }
}
fun JavaForkOptions.setClionRadlerSystemProperties() {
  val mode = "radler"
  val suppressedPlugins = listOfNotNull(
        "com.intellij.cidr.lang",
        "com.intellij.cidr.lang.clangdBridge",
        "com.intellij.c.performanceTesting",
        "org.jetbrains.plugins.cidr-intelliLang",
        "com.intellij.cidr.grazie",
        "com.intellij.cidr.markdown",
  )
  systemProperty("idea.suppressed.plugins.set.selector", mode) // possible values: `classic` and `radler`
  systemProperty("idea.suppressed.plugins.set.$mode", suppressedPlugins.joinToString(","))
}
