package eu.glatz.sifidraw.config

import javax.annotation.PreDestroy
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.process.extract.UserTempNaming
import de.flapdoodle.embed.mongo.Command
import de.flapdoodle.embed.process.config.IRuntimeConfig
import java.io.IOException
import javax.annotation.PostConstruct
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.config.*
import de.flapdoodle.embed.mongo.distribution.Version
import java.io.File


class MongoInMemory {
    private var port: Int
    private var host: String
    private var process: MongodProcess? = null
    private var storage: File


    constructor(port: Int, host: String, storage: File) {
        this.port = port
        this.host = host
        this.storage = storage
    }

    @PostConstruct
    @Throws(IOException::class)
    fun init() {
        val storage = Storage(
                storage.absolutePath, null, 0)

        val runtimeConfig = RuntimeConfigBuilder()
                .defaults(Command.MongoD)
                .artifactStore(ExtractedArtifactStoreBuilder()
                        .defaults(Command.MongoD)
                        .download(DownloadConfigBuilder()
                                .defaultsForCommand(Command.MongoD).build())
                        .executableNaming(UserTempNaming()))
                .build()

        val mongodConfig = MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(Net(host, port, false))
                .replication(storage)
                .build()

        val runtime = MongodStarter.getInstance(runtimeConfig)
        process = runtime.prepare(mongodConfig).start()
    }

    @PreDestroy
    fun stop() {
        process!!.stop()
    }
}
