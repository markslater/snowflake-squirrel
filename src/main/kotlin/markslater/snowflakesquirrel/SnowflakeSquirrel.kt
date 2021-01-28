package markslater.snowflakesquirrel

import net.sourceforge.urin.Authority.authority
import net.sourceforge.urin.Host.registeredName
import net.sourceforge.urin.Path.path
import net.sourceforge.urin.Segment
import net.sourceforge.urin.scheme.http.HttpQuery.*

fun main() {
    println(SnowflakeJdbcScheme.urin(
        authority(registeredName("<account_name>.snowflakecomputing.com")),
        path(Segment.empty()),
        queryParameters(queryParameter("hello"))
    ).asString())
}

