package markslater.snowflakesquirrel

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

@Throws(SQLException::class)
private fun getConnection(): Connection {

    // build connection properties
    val properties = Properties()
    properties.put("user", "") // replace "" with your user name
    properties.put("password", "") // replace "" with your password
    properties.put("warehouse", "") // replace "" with target warehouse name
    properties.put("db", "") // replace "" with target database name
    properties.put("schema", "") // replace "" with target schema name
    // properties.put("tracing", "all"); // optional tracing property

    // Replace <account> with your account, as provided by Snowflake.
    // Replace <region_id> with the name of the region where your account is located.
    // If your platform is AWS and your region ID is US West, you can omit the region ID segment.
    // Replace <platform> with your platform, for example "azure".
    // If your platform is AWS, you may omit the platform.
    // Note that if you omit the region ID or the platform, you should also omit the
    // corresponding "."  E.g. if your platform is AWS and your region is US West, then your
    // connectStr will look similar to:
    // "jdbc:snowflake://xy12345.snowflakecomputing.com";
    val connectStr = "jdbc:snowflake://<account>.<region_id>.<platform>.snowflakecomputing.com"
    return DriverManager.getConnection(connectStr, properties)
}
