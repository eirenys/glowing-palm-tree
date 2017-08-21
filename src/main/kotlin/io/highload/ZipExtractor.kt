package io.highload

import io.highload.dao.EntityDao
import io.highload.json.JsonConverter
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.util.zip.ZipFile
import kotlin.coroutines.experimental.CoroutineContext

/**
 *
 */
class ZipExtractor(val dao: EntityDao, val converter: JsonConverter, val context: CoroutineContext) {
    fun extract(path: String) = extract(File(path))

    fun extractResource(resource: String) = extract(this.javaClass.classLoader.getResource(resource).file)

    fun extract(file: File) {
        val zip = ZipFile(file)
        try {
            for (e in zip.entries()) {
                val name = e.name
                if (name.endsWith(".json")) {
                    if (name.contains("users")) {
                        val list = converter.parseUsers(zip.getInputStream(e))
                        launch(context) {
                            list.users.forEach {
                                dao.insert(it)
                            }
                        }
                    } else if (name.contains("locations")) {
                        val list = converter.parseLocations(zip.getInputStream(e))
                        launch(context) {
                            list.locations.forEach {
                                dao.insert(it)
                            }
                        }
                    } else if (name.contains("visits")) {
                        val list = converter.parseVisits(zip.getInputStream(e))
                        launch(context) {
                            list.visits.forEach {
                                dao.insert(it)
                            }
                        }
                    }
                }
            }
        } catch (e: Throwable) {
        } finally {
            zip.close()
        }
    }
}