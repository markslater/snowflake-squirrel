package markslater.snowflakesquirrel

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.isA
import net.sourceforge.urin.*
import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Host.registeredName
import net.sourceforge.urin.Path.path
import net.sourceforge.urin.scheme.http.HttpQuery
import net.sourceforge.urin.scheme.http.HttpQuery.queryParameter
import net.sourceforge.urin.scheme.http.HttpQuery.queryParameters
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SnowflakeJdbcSchemeTest {
    @Test
    fun `Can make sample Snowflake connection string`() {
        val urin = SnowflakeJdbcScheme.urin(
            authority(registeredName("xy12345.snowflakecomputing.com")),
            path(Segment.empty()),
            queryParameters(
                queryParameter("user", "peter"),
                queryParameter("warehouse", "mywh"),
                queryParameter("db", "mydb"),
                queryParameter("schema", "public")
            )
        )
        assertThat(
            urin, isA(
                has(
                    Urin<Iterable<Iterable<String>>, HttpQuery, Fragment<String>>::asString,
                    equalTo("jdbc:snowflake://xy12345.snowflakecomputing.com/?user=peter&warehouse=mywh&db=mydb&schema=public")
                )
            )
        )
    }
}