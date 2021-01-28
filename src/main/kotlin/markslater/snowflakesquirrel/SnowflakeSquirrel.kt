package markslater.snowflakesquirrel

import net.snowflake.client.jdbc.SnowflakeConnection
import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Host.registeredName
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*


fun main(args: Array<String>) {
    val propertiesFileName = args[0]
    println(propertiesFileName)
    val properties = Properties().apply {
        File(propertiesFileName).bufferedReader(UTF_8).use {
            load(it)
        }
    }
    val account = properties.getProperty("account") ?: error("Missing property \"account\"")
    val user = properties.getProperty("user") ?: error("Missing property \"user\"")
    val password = properties.getProperty("password") ?: error("Missing property \"password\"")

    // get connection
    println("Create JDBC connection")
    val connection: Connection = getConnection(account, user, password)
    println("Done creating JDBC connection\n")

    connection.unwrap(SnowflakeConnection::class.java).uploadStream(
        "~", "testUploadStream",
        """[{"foo":"bar"}, {"foo":"baz"}]""".byteInputStream(UTF_8), "sample.json", true
    )

    // create statement
    println("Create JDBC statement")
    val statement: Statement = connection.createStatement()
    println("Done creating JDBC statement\n")

    // create a table
    println("Create demo table")
    statement.executeUpdate("create or replace table raw_source (\n" +
            "  src variant);")
    println("Done creating demo table\n")

    // insert a row
    println("Load data")
    statement.executeUpdate("copy into raw_source\n" +
            "  from @~/testUploadStream\n" +
            "  file_format = (type = json, strip_outer_array = true);")
    println("Done loading data'\n")

    connection.close()
}

private fun getConnection(account: String, user: String, password: String): Connection {
    return DriverManager.getConnection(
        SnowflakeJdbcScheme.urin(authority(registeredName(account))).asString(),
        Properties().apply {
            put("user", user)
            put("password", password)
            put("warehouse", "MYWAREHOUSE")
            put("db", "DEMO_DB")
            put("schema", "PUBLIC")
        }
    )
}
