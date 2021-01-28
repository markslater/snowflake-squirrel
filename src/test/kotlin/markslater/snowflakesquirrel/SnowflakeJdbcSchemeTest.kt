package markslater.snowflakesquirrel

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.isA
import net.sourceforge.urin.*
import net.sourceforge.urin.scheme.http.HttpQuery
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SnowflakeJdbcSchemeTest {
    @Test
    fun `Can make sample Snowflake connection string`() {
        val urin: Urin<Iterable<Iterable<String>>, HttpQuery, Fragment<String>> = SnowflakeJdbcScheme.urin(
            Authority.authority(Host.registeredName("xy12345.snowflakecomputing.com"))
        )
        assertThat(urin, isA(has(Urin<Iterable<Iterable<String>>, HttpQuery, Fragment<String>>::asString,
            equalTo("jdbc:snowflake://xy12345.snowflakecomputing.com")))
        )
    }
}