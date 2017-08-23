package io.highload

import io.highload.dao.EntityDao
import io.highload.web.JsonConverter
import java.io.File
import java.util.zip.ZipFile

/**
 *
 */
class ZipExtractor(val dao: EntityDao, val converter: JsonConverter) {
    suspend fun extractResource(resource: String) = extract(this.javaClass.classLoader.getResource(resource).file)

    suspend fun extract(path: String) {
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
                        converter.parseLocations(zip.getInputStream(e)).forEach {
                            dao.insert(it)
                        }
                    } else if (name.contains("visits")) {
                        converter.parseVisits(zip.getInputStream(e)).forEach {
                            dao.insert(it)
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