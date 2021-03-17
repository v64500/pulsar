package ai.platon.pulsar.ql.h2.udfs

import ai.platon.pulsar.common.config.VolatileConfig
import ai.platon.pulsar.common.url.Urls
import ai.platon.pulsar.common.options.LoadOptions
import ai.platon.pulsar.ql.annotation.UDFGroup
import ai.platon.pulsar.ql.annotation.UDFunction
import ai.platon.pulsar.ql.h2.H2SessionFactory
import ai.platon.pulsar.ql.h2.Queries
import ai.platon.pulsar.ql.annotation.H2Context
import java.sql.Connection
import java.sql.ResultSet
import java.time.Duration

@UDFGroup(namespace = "META")
object MetadataFunctionTables {
    private val volatileConfig = VolatileConfig()

    @UDFunction(description = "Load a page specified by url from the database, " +
            "return the fields of the page as key-value pairs")
    @JvmStatic
    fun load(@H2Context conn: Connection, configuredUrl: String): ResultSet {
        val page = H2SessionFactory.getSession(conn).load(configuredUrl)
        return Queries.toResultSet(page)
    }

    @UDFunction(description = "Load a page specified by url from the database, " +
            "fetch it from the internet if absent or expired" +
            "return the fields of the page as key-value pairs")
    @JvmStatic
    fun fetch(@H2Context conn: Connection, configuredUrl: String): ResultSet {
        val (url, args) = Urls.splitUrlArgs(configuredUrl)
        val loadOptions = LoadOptions.parse(args, volatileConfig)
        loadOptions.expires = Duration.ZERO

        val page = H2SessionFactory.getSession(conn).load(url, loadOptions)
        return Queries.toResultSet(page)
    }
}
