plugins {
	java
}

repositories {
	mavenCentral()
	maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
	compileOnly(libs.paper.api)
}

val pat = "(?<=\\p{Lower})(?=\\p{Upper})".toRegex()
fun String.toCase(sep: String) = replace(pat, sep).lowercase()
fun String.toClassName() = split(pat).joinToString("", transform={ it.lowercase().capitalize() })

val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String by project
val pluginDescription: String by project

val pluginId = pluginName.toCase("-")

group = pluginGroup
version = pluginVersion
description = pluginDescription
base.archivesName = pluginId

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
	}
}

tasks {
	processResources {
		filesMatching("plugin.yml") {
			expand(
				"plugin" to mapOf(
					"name" to pluginName,
					"description" to pluginDescription,
					"version" to pluginVersion,
					"id" to pluginId,
					"pkg" to "${pluginGroup}.${pluginName.toCase("_")}",
					"class" to pluginName.toClassName(),
				),
				"versions" to mapOf(
					"api" to libs.versions.paper.api.get().replace("\\.\\d+-.*".toRegex(), ""),
				)
			)
		}
	}

	jar {
		from("LICENSE") {
			rename {
				"LICENSE-${pluginId}"
			}
		}
	}

	withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
	}
}
