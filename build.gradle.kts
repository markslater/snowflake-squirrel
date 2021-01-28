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
	implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
	implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8")
	implementation(group = "net.snowflake", name = "snowflake-jdbc", version = "3.12.17")
	implementation(group = "net.sourceforge.urin", name = "urin", version = "3.12")

	testImplementation(group = "org.junit.jupiter", name= "junit-jupiter", version = "5.7.0")
	testImplementation(group = "com.natpryce", name= "hamkrest", version = "1.8.0.1")

	testRuntimeOnly(group = "org.junit.jupiter", name= "junit-jupiter-engine", version = "5.7.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}