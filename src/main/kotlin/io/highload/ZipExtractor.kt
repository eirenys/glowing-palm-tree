package io.highload

import io.highload.dao.StubDao
import io.highload.metrics.MetricsAggregator
import io.highload.web.JsonConverter
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.util.zip.ZipFile

/**
 *
 */
class ZipExtractor(val dao: StubDao, val converter: JsonConverter) {
    suspend fun extractResource(resource: String) = extract(this.javaClass.classLoader.getResource(resource).file)

    suspend fun extract(path: String) {
        val zip = ZipFile(File(path))
        try {
            val list = mutableListOf<Job>()

            for (e in zip.entries()) {
                val name = e.name
                if (name.endsWith(".json")) {
                    val job = if (name.contains("users")) {
                        launch(CommonPool) {
                            MetricsAggregator.startedImp.incrementAndGet()
                            try {
                                zip.getInputStream(e).use {
                                    converter.parseUsers(it).forEach {
                                        dao.insert(it)
                                    }
                                }
                            } catch (e: Throwable) {
                                println(e.message)
                            } finally {
                                MetricsAggregator.endedImp.incrementAndGet()
                            }
                        }
                    } else if (name.contains("locations")) {
                        launch(CommonPool) {
                            MetricsAggregator.startedImp.incrementAndGet()
                            try {
                                zip.getInputStream(e).use {
                                    converter.parseLocations(it).forEach {
                                        dao.insert(it)
                                    }
                                }
                            } catch (e: Throwable) {
                                println(e.message)
                            } finally {
                                MetricsAggregator.endedImp.incrementAndGet()
                            }
                        }
                    } else if (name.contains("visits")) {
                        launch(CommonPool) {
                            MetricsAggregator.startedImp.incrementAndGet()
                            try {
                                zip.getInputStream(e).use {
                                    converter.parseVisits(it).forEach {
                                        dao.insert(it)
                                    }
                                }
                            } catch (e: Throwable) {
                                println(e.message)
                            } finally {
                                MetricsAggregator.endedImp.incrementAndGet()
                            }
                        }
                    } else {
                        null
                    }
                    if (job != null) {
                        list.add(job)
                    }
                }
            }

            list.forEach { it.join() }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            zip.close()
        }
    }
}