
//import org.gradle.kotlin.dsl.withType

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.1.0"
  id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "org.extendedhl"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
  maven {
    url = uri("https://packages.jetbrains.team/maven/p/grazi/grazie-platform-public")
  }
  intellijPlatform {
    defaultRepositories()
  }

}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
  intellijPlatform {
    //intellijPlatformLocal("C:\\home\\arcno\\bin\\CLion")
    local("C:\\home\\arcno\\bin\\CLion")
    //jetbrainsRuntime()
    //testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    //localPlugin("C:\\home\\arcno\\src\\intellij\\clion-radler")
    //localPlugin("C:\\home\\arcno\\src\\intellij\\rider-plugins-clion-radler-cwm")
    //localPlugin("C:\\home\\arcno\\src\\intellij\\rider-plugins-cpp-debugger")
    //localPlugin(file("C:\\home\\arcno\\src\\intellij\\rd-ide-model-generated-252.23892.515.jar"))
    //runtimeOnly("com.jetbrains.rd:rd-core:2025.2.2-beta2")
    //implementation("com.jetbrains.intellij.resharper:resharper-cpp-lexer:252.23892.515")
    //bundledPlugin("com.intellij.clion")
    //pluginModule(implementation(project(":buildSrc")))
    //bundledPlugin("com.intellij.cidr.lang")
    //runtimeOnly("com.jetbrains.intellij.resharper:resharper-cpp-lexer:252.23892.515")
    //runtimeOnly("com.jetbrains.intellij.resharper:resharper-cpp-assist:252.23892.515")
    //runtimeOnly("com.jetbrains.intellij.rider:rider-cpp-core:252.23892.515")
    //runtimeOnly("com.jetbrains.intellij.rider:rider-rdclient-dotnet:252.23892.515")
    // Add necessary plugin dependencies for compilation here, example:
    // bundledPlugin("com.intellij.java")
  }
  //compileOnly("com.jetbrains.intellij.resharper:resharper-cpp-lexer:252.23892.515")
  //compileOnly("com.jetbrains.intellij.resharper:resharper-cpp-assist:252.23892.515")
  compileOnly(fileTree("C:\\home\\arcno\\src\\intellij\\clion-radler"))
  compileOnly(files("C:\\home\\arcno\\src\\intellij\\rd-ide-model-generated-252.23892.515.jar"))
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
  //withType<Test>().configureEach {
  //  // Saturate CPU: start with 1x cores; if you have heavy, memory-hungry tests, use ~0.5x cores
  //  val cores = Runtime.getRuntime().availableProcessors()
  //  maxParallelForks = maxOf(1, cores)
  //  forkEvery = 100 // periodically fork to avoid perm-mem leaks in long test suites
  //  jvmArgs("-Xms256m", "-Xmx1g", "-XX:+UseG1GC")
  //  // Optional: test filtering/avoid scan of resources repeatedly
  //  systemProperty("junit.jupiter.execution.parallel.enabled", "true")
  //}

  //composedJar {
  //  archiveBaseName.convention(projectName)
  //}

  //withType<PrepareSandboxTask> {
  //  //from("socialMedia") {
  //  //  into("${projectName.get()}/socialMedia")
  //  //  include("**/*.gif")
  //  //}
  //  doLast {
  //    val kotlinJarRe = """kotlin-(stdlib|reflect|runtime).*\.jar""".toRegex()
  //    val libraryDir = destinationDir.resolve("${projectName.get()}/lib")
  //    val kotlinStdlibJars = libraryDir.listFiles().orEmpty().filter { kotlinJarRe.matches(it.name) }
  //    check(kotlinStdlibJars.isEmpty()) {
  //      "Plugin shouldn't contain kotlin stdlib jars. Found:\n" + kotlinStdlibJars.joinToString(separator = ",\n") { it.absolutePath }
  //    }
  //  }
  //}
  //withType<RunIdeTask>().configureEach {
  //  //dependsOn(
  //  //    named("prepareSandbox")
  //  //)
  //  jvmArgs(
  //      "-Xms512m",
  //      "-Xmx1500m",
  //      "-Dide.experimental.ui=true",
  //      "-Didea.kotlin.plugin.use.k2=true",
  //      "-Dide.show.whats.new.on.startup=false"
  //  )
  //  // Supply any conditional/system-derived args lazily to keep configuration cache friendly
  //  // Replace the body with only the keys you actually need to propagate.
  //  jvmArgumentProviders += CommandLineArgumentProvider {
  //    val args = mutableListOf<String>()
  //    // Example: selectively pass through a few safe keys if present
  //    val keys = listOf(
  //      "idea.trust.all.projects",
  //      "idea.is.internal",
  //      "ide.no.platform.update"
  //    )
  //    for (k in keys) {
  //      System.getProperty(k)?.let { v -> args += "-D$k=$v" }
  //    }
  //    args
  //  }
  //}
  //withType<RunIdeTask> { autoReload = false }
  //intellijPlatformTesting {
  //  customRunIdeTask("CLion-Nova") {
  //    setClionSystemProperties(withRadler = true)
//
  //    systemProperty("idea.trust.all.projects", "true")
  //    systemProperty("ide.show.tips.on.startup.default.value", "false")
  //  }
  //}

  /*withType<RunIdeTask> {
    autoReload = false
    jvmArgs("-Xmx2g")
    jvmArgs("-Dide.experimental.ui=true")
    jvmArgs("-Didea.kotlin.plugin.use.k2=true")
    jvmArgs("-Dide.show.whats.new.on.startup=false")
    // These system properties are used by educational-ml-library
    // System properties can't be passed directly since Gradle runs the IDE process separately
    // They are not inherited by default, unlike environment variables, which should work by default
    System.getProperties()
      //.filterKeys { (it as? String)?.startsWith("educational.ml.") == true }
      .map { (key, value) -> jvmArgs("-D$key=$value") }
  }*/

  /*register<DefaultTask>("listConfigurations") {
    group = "User"
    description = "Lists all configurations in the project"
    doLast {
      project.configurations.forEach { println(it.name) }
    }
  }

  register<DefaultTask>("printCompileClasspath") {
    group = "User"
    doLast {
      var classPathStr = StringBuilder()
      classPathStr.append("Classpath: \n\"")
      project.configurations.named("compileClasspath").get().forEach {
        classPathStr.append("${it.path}:")
      }
      classPathStr.replace(classPathStr.length-1, classPathStr.length, "\"")
      println(classPathStr.toString())
    }
  }*/
}

//fun IntelliJPlatformTestingExtension.customRunIdeTask(
//  //type: IntelliJPlatformType,
//  baseTaskName: String,
//  configureRunIdeTask: RunIdeTask.() -> Unit = {},
//) {
//  val p = runIde.register("run$baseTaskName")
//  p.configure {
//    useInstaller = false
//    this.type = CLion
//    version = CLionVersion
//    // Specify custom sandbox directory to have a stable path to log file
//    sandboxDirectory = intellijPlatform.sandboxContainer.dir("${baseTaskName.lowercase()}-sandbox")
//
//    task(configureRunIdeTask)
//
//    plugins {
//      plugins(rustPlugin)
//    }
//  }
//}


kotlin {
  compilerOptions {

    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
  }
}
