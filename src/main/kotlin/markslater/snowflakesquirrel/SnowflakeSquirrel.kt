package markslater.snowflakesquirrel

import net.snowflake.client.jdbc.SnowflakeConnection
import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Host.registeredName
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
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
        val httpResponse = HttpClient.newHttpClient().send(
            HttpRequest.newBuilder(URI("https://data.cityofnewyork.us/resource/vfnx-vebw.json")).GET().build(),
            HttpResponse.BodyHandlers.ofInputStream()
        )

        connection
            .unwrap(SnowflakeConnection::class.java)
            .uploadStream(
                "~",
                "testUploadStream",
                httpResponse.body(),
                "sample.json",
                true
            )

        connection.createStatement().use { statement ->
            statement.executeUpdate("create or replace table raw_source (src variant);")
            statement.executeUpdate("copy into raw_source from @~/testUploadStream/sample.json file_format = (type = json, strip_outer_array = true);")
            statement.executeQuery("select src:primary_fur_color::string, count(*) from raw_source group by src:primary_fur_color::string").use { resultSet ->
                println("Metadata:")
                println("================================")

                val resultSetMetaData = resultSet.metaData
                println("Number of columns=${resultSetMetaData.columnCount}")
                for (columnIndex in 1..resultSetMetaData.columnCount) {
                    println("Column $columnIndex: type=${resultSetMetaData.getColumnTypeName(columnIndex)}")
                }

                // fetch data
                println()
                println("Data:")
                println("================================")
                while (resultSet.next()) {
                    println((1..resultSetMetaData.columnCount).joinToString(", ") { columnIndex ->
                        resultSet.getString(columnIndex) ?: "null"
                    })
                }
            }
        }
    }

}
