package markslater.snowflakesquirrel

import net.sourceforge.urin.*
import net.sourceforge.urin.scheme.http.HttpQuery

object SnowflakeJdbcScheme : SchemeWithDefaultPort<Iterable<Iterable<String>>, HttpQuery, Fragment<String>>(
    "jdbc:snowflake",
    Port.port(80),
    object : MakingDecoder<Segment<Iterable<Iterable<String>>>, Iterable<Iterable<String>>, String>(
        PercentEncodingPartial.percentEncodingDelimitedValue(
            ':',
            PercentEncodingPartial.percentEncodingDelimitedValue(';')
        )
    ) {
        override fun makeOne(input: Iterable<Iterable<String>>?): Segment<Iterable<Iterable<String>>> {
            return Segment.segment(
                input,
                PercentEncodingPartial.percentEncodingDelimitedValue(
                    ':',
                    PercentEncodingPartial.percentEncodingDelimitedValue(';')
                )
            )
        }
    },
    HttpQuery.httpQueryMakingDecoder(),
    Fragment.stringFragmentMaker()
)