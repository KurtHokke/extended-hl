import org.gradle.api.Project
import org.gradle.process.JavaForkOptions
import kotlin.reflect.KProperty

val Project.CLionVersion: String by Properties
val Project.CLionHome: String by Properties
val Project.rustPlugin: String by Properties
val Project.psiViewerPlugin: String by Properties

fun JavaForkOptions.setClionSystemProperties(withRadler: Boolean = false) {
  val (mode, suppressedPlugins) = if (withRadler) {
    val radlerSuppressedPlugins = listOfNotNull(
      "com.intellij.cidr.lang",
      "com.intellij.cidr.lang.clangdBridge",
      "com.intellij.c.performanceTesting",
      "org.jetbrains.plugins.cidr-intelliLang",
      "com.intellij.cidr.grazie",
      "com.intellij.cidr.markdown",
    )
    "radler" to radlerSuppressedPlugins
  }
  else {
    val classicSuppressedPlugins = listOf(
      "org.jetbrains.plugins.clion.radler",
      "intellij.rider.cpp.debugger",
      "intellij.rider.plugins.clion.radler.cwm"
    )
    "classic" to classicSuppressedPlugins
  }
  systemProperty("idea.suppressed.plugins.set.selector", mode) // possible values: `classic` and `radler`
  systemProperty("idea.suppressed.plugins.set.$mode", suppressedPlugins.joinToString(","))
  //systemProperty("ide.show.whats.new.on.startup", false)
}

private object Properties {
  operator fun getValue(thisRef: Project, property: KProperty<*>): String = thisRef.prop(property.name)
}
fun Project.prop(name: String): String = findProperty(name) as? String ?: error("Property `$name` is not defined in gradle.properties")