package markslater.snowflakesquirrel

import net.snowflake.client.jdbc.SnowflakeConnection
import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Host.registeredName
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.sql.DriverManager
import java.util.*

fun main(args: Array<String>) {
    val propertiesFileName = args[0]
    val properties = Properties().apply {
        File(propertiesFileName).bufferedReader(UTF_8).use {
            load(it)
        }
    }
    val account = properties.getProperty("account") ?: error("Missing property \"account\"")
    val user = properties.getProperty("user") ?: error("Missing property \"user\"")
    val password = properties.getProperty("password") ?: error("Missing property \"password\"")

    DriverManager.getConnection(
        SnowflakeJdbcScheme.urin(authority(registeredName(account))).asString(),
        Properties().apply {
            put("user", user)
            put("password", password)
            put("warehouse", "MYWAREHOUSE")
            put("db", "DEMO_DB")
            put("schema", "PUBLIC")
        }
    ).use { connection ->
        connection
            .unwrap(SnowflakeConnection::class.java)
            .uploadStream(
                "~",
                "testUploadStream",
                """[{"foo":"bar"}, {"foo":"baz"}]""".byteInputStream(UTF_8),
                "sample.json",
                true
            )

        connection.createStatement().use { statement ->
            statement.executeUpdate("create or replace table raw_source (src variant);")
            statement.executeUpdate("copy into raw_source from @~/testUploadStream file_format = (type = json, strip_outer_array = true);")
            statement.executeQuery("select src:foo::string from raw_source").use { resultSet ->
                println("Metadata:")
                println("================================")

                val resultSetMetaData = resultSet.metaData
                println("Number of columns=" + resultSetMetaData.columnCount)
                for (colIdx in 0 until resultSetMetaData.columnCount) {
                    println("Column " + colIdx + ": type=" + resultSetMetaData.getColumnTypeName(colIdx + 1))
                }

                // fetch data
                println("\nData:")
                println("================================")
                val rowIdx = 0
                while (resultSet.next()) {
                    println("row " + rowIdx + ", column 0: " + resultSet.getString(1))
                }
            }
        }
    }

}

