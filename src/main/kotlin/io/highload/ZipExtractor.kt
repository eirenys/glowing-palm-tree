package io.highload

import io.highload.dao.StubDao
import io.highload.web.JsonConverter
import java.io.File
import java.util.zip.ZipFile

/**
 *
 */
class ZipExtractor(val dao: StubDao, val converter: JsonConverter) {
    fun extractResource(resource: String) = extract(this.javaClass.classLoader.getResource(resource).file)

    fun extract(path: String) {
        val zip = ZipFile(File(path))
        try {
            for (e in zip.entries()) {
                val name = e.name
                if (name.endsWith(".json")) {
                    if (name.contains("users")) {
                        zip.getInputStream(e).use {
                            converter.parseUsers(it).forEach {
                                dao.insert(it)
                            }
                        }
                    } else if (name.contains("locations")) {
                        zip.getInputStream(e).use {
                            converter.parseLocations(it).forEach {
                                dao.insert(it)
                            }
                        }
                    } else if (name.contains("visits")) {
                        zip.getInputStream(e).use {
                            converter.parseVisits(it).forEach {
                                dao.insert(it)
                            }
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            zip.close()
        }
    }
}