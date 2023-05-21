plugins {
    `java-library`
    `maven-publish`
}

group = "com.runicrealms.plugin"
version = "1.0-SNAPSHOT"
val artifactName = "restart"

dependencies {
    compileOnly(commonLibs.acf)
    compileOnly(commonLibs.spigot)
    compileOnly(commonLibs.mythicmobs)
    compileOnly(commonLibs.paper)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.runicrealms.plugin"
            artifactId = artifactName
            version = "1.0-SNAPSHOT"
            from(components["java"])
        }
    }
}