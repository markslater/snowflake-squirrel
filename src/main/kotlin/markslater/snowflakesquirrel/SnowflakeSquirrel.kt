package markslater.snowflakesquirrel

import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Host.registeredName
import java.sql.*
import java.util.*

fun main(args: Array<String>) {
    // get connection
    println("Create JDBC connection")
    val connection: Connection = getConnection()
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

private fun getConnection(): Connection {
    return DriverManager.getConnection(
        SnowflakeJdbcScheme.urin(authority(registeredName("xy12345.eu-central-1.snowflakecomputing.com"))).asString(),
        Properties().apply {
            put("user", "") // replace "" with your user name
            put("password", "") // replace "" with your password
            put("warehouse", "") // replace "" with target warehouse name
            put("db", "") // replace "" with target database name
            put("schema", "") // replace "" with target schema name
        }
    )
}
