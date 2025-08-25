rootProject.name = "cpp"

gradle.settingsEvaluated {
  val ignores = listOf(".git", ".gradle", "build", "out", ".idea", "node_modules")
  System.setProperty("org.gradle.vfs.watch.ignore.regex", ignores.joinToString("|") { ".*/$it(/.*)?" })
}
