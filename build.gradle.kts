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

dependencies {
	implementation(group = "net.snowflake", name = "snowflake-jdbc", version = "3.12.17")
}