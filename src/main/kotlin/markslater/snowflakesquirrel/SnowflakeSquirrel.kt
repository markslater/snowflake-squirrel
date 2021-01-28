package markslater.snowflakesquirrel

import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Host.registeredName
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.sql.*
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

    // create statement
    println("Create JDBC statement")
    val statement: Statement = connection.createStatement()
    println("Done creating JDBC statement\n")

    // create a table
    println("Create demo table")
    statement.executeUpdate("create or replace table demo(c1 string)")
    println("Done creating demo table\n")

    // insert a row
    println("Insert 'hello world'")
    statement.executeUpdate("insert into demo values ('hello world')")
    println("Done inserting 'hello world'\n")

    // query the data
    println("Query demo")
    val resultSet: ResultSet = statement.executeQuery("select * from demo")
    println("Metadata:")
    println("================================")

    // fetch metadata
    val resultSetMetaData = resultSet.metaData
    println("Number of columns=" + resultSetMetaData.columnCount)
    for (colIdx in 0 until resultSetMetaData.columnCount) {
        println(
            "Column " + colIdx + ": type=" + resultSetMetaData.getColumnTypeName(colIdx + 1)
        )
    }

    // fetch data
    println("\nData:")
    println("================================")
    val rowIdx = 0
    while (resultSet.next()) {
        println("row " + rowIdx + ", column 0: " + resultSet.getString(1))
    }
    resultSet.close()
    statement.close()
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
