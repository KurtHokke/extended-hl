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
        local("C:\\home\\arcno\\bin\\CLion")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        localPlugin("C:\\home\\arcno\\src\\intellij\\clion-radler")
        //implementation("com.jetbrains.intellij.resharper:resharper-cpp-lexer:252.23892.515")
        //bundledPlugin("com.intellij.clion")
        bundledPlugin("com.intellij.cidr.lang")
        //runtimeOnly("com.jetbrains.intellij.resharper:resharper-cpp-lexer:252.23892.515")
        //runtimeOnly("com.jetbrains.intellij.resharper:resharper-cpp-assist:252.23892.515")

        //compileOnly("com.jetbrains.intellij.rider:rider-cpp-core:252.23892.515")
        //runtimeOnly("com.jetbrains.intellij.rider:rider-rdclient-dotnet:252.23892.515")
        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        version.set("RD-2025.2")
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            Initial version
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
