plugins {
	kotlin("jvm") version "1.4.21"
}

repositories {
	mavenCentral()
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(15))
	}
}
