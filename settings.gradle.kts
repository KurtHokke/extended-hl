
rootProject.name = "cpp"

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
      url = uri("https://packages.jetbrains.team/maven/p/grazi/grazie-platform-public")
    }
  }
}

//gradle.settingsEvaluated {
//  val ignores = listOf(".git", ".gradle", "build", "out", ".idea", "node_modules")
//  System.setProperty("org.gradle.vfs.watch.ignore.regex", ignores.joinToString("|") { ".*/$it(/.*)?" })
//}
