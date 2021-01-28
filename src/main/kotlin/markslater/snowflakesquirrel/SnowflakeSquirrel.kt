package markslater.snowflakesquirrel

import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Fragment
import net.sourceforge.urin.Fragment.stringFragmentMaker
import net.sourceforge.urin.Host.registeredName
import net.sourceforge.urin.MakingDecoder
import net.sourceforge.urin.Path.path
import net.sourceforge.urin.PercentEncodingPartial.percentEncodingDelimitedValue
import net.sourceforge.urin.Port.port
import net.sourceforge.urin.SchemeWithDefaultPort
import net.sourceforge.urin.Segment
import net.sourceforge.urin.scheme.http.HttpQuery
import net.sourceforge.urin.scheme.http.HttpQuery.*

fun main() {
    println(SnowflakeJdbcScheme.urin(
        authority(registeredName("<account_name>.snowflakecomputing.com")),
        path(Segment.empty()),
        queryParameters(queryParameter("hello"))
    ).asString())
}

object SnowflakeJdbcScheme : SchemeWithDefaultPort<Iterable<Iterable<String>>, HttpQuery, Fragment<String>>(
    "jdbc:snowflake",
    port(80),
    object : MakingDecoder<Segment<Iterable<Iterable<String>>>, Iterable<Iterable<String>>, String>(percentEncodingDelimitedValue(':', percentEncodingDelimitedValue(';'))
    ) {
        override fun makeOne(input: Iterable<Iterable<String>>?): Segment<Iterable<Iterable<String>>> {
            return Segment.segment(input, percentEncodingDelimitedValue(':', percentEncodingDelimitedValue(';')))
        }
    },
    httpQueryMakingDecoder(),
    stringFragmentMaker()
)
