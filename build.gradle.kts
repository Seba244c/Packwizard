plugins {
    id("java")
    id("io.freefair.lombok") version "8.1.0"
}

val imguiVersion = "1.86.10"
val lwjglVersion = "3.3.2"
val lwjglNatives = "natives-windows"

group = "dk.sebsa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-nfd")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-nfd", classifier = lwjglNatives)

    implementation("io.github.spair:imgui-java-app:${imguiVersion}")
    implementation("org.json:json:20230618")
}
