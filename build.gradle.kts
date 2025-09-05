import dev.tocraft.gradle.preprocess.tasks.PreProcessTask
import dev.tocraft.gradle.preprocess.tasks.ApplyPreProcessTask
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.extensions.IntelliJPlatformTestingExtension
import org.jetbrains.intellij.platform.gradle.tasks.RunIdeTask
import org.jetbrains.intellij.platform.gradle.tasks.BuildPluginTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("java")
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.intellij.platform)
  alias(libs.plugins.preprocessor)
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
val clionRdPlatform: String by project
val clionCidrPSIBase: String by project
// Configure IntelliJ Platform Gradle Plugin Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
  intellijPlatform {
    local(clionHome)
    plugin(libs.plugins.onedark.theme.toNotation())
  }
  compileOnly(fileTree(getIdeRelativePath(clionRadler)))
  compileOnly(files(getIdeRelativePath(clionRdIdeModels)))
  compileOnly(files(getIdeRelativePath(clionRdPlatform)))
  compileOnly(files(getIdeRelativePath(clionCidrPSIBase)))
}
fun Provider<PluginDependency>.toNotation(): Provider<String> =
    map { "${it.pluginId}:${it.version}" }
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

preprocess {
  vars["debug"] = "0"
  vars["release"] = "1"
}

tasks {
  //val projectName = project.extensionProvider.flatMap { it.projectName }
  // Set the JVM compatibility versions
  withType<JavaCompile> {
      sourceCompatibility = "21"
      targetCompatibility = "21"
  }
  withType<KotlinCompile>().configureEach {
    exclude("**/ExtendedHighlightExtHandler.kt")
    exclude("**/ExtendedHighlighterReapplier.kt")
    exclude("**/ColorSchemeSettingsListener.kt")
    
    //preprocess {
    //  vars["debug"] = "0"
    //  vars["release"] = "1"
    //}
  }
  //register<PreProcessTask>("PreProcess_Release") {
  //  group = "user"
  //  target.set(file("build/preprocessed"))
  //  sources.set(project.files("src/main/kotlin"))
  //  preprocess {
  //    vars["debug"] = "0"
  //    vars["release"] = "1"
  //  }
  //}
  //register<PreProcessTask>("PreProcess_Debug") {
  //  group = "user"
  //  target.set(file("build/preprocessed"))
  //  sources.set(project.files("src/main/kotlin"))
  //  preprocess {
  //    vars["debug"] = "1"
  //    vars["release"] = "0"
  //  }
  //}
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

    localPath.dir(clionHome)

    task(configureRunIdeTask)

  }
}
kotlin {
  sourceSets {
    main {
      kotlin {
        srcDirs("build/generated/preprocessed/main/kotlin")
      }
    }
  }
  compilerOptions {

    jvmTarget.set(JvmTarget.JVM_21)
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
