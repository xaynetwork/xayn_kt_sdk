package xayn_kt_sdk.server

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xayn_kt_sdk.server.model.*
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import kotlin.math.min

private val json: Json by lazy {
    Json { ignoreUnknownKeys = true }
}

class Server(
    private val token: String,
    private val endpoint: String,
    private val environment: String
) {
    fun ingest(documents: Array<IngestedDocument>): Boolean {
        var index = 0
        val len = documents.size

        while (index < len) {
            val next = min(len, index + 100)
            val batch = documents.slice(IntRange(index, next - 1))

            batchIngest(batch)

            index = next
        }

        return true
    }

    fun delete(documentId: String): Boolean {
        val url = URI(endpoint).resolve("/$environment/documents/$documentId").toURL()
        val connection = url.openConnection() as HttpURLConnection

        with(connection) {
            requestMethod = "DELETE"

            setRequestProperty("authorizationToken", token)

            when (connection.responseCode) {
                204 -> return true
                400 -> throw Exception("invalid request.")
                else -> {
                    throw Exception(
                        "Status code ${connection.responseCode}: \"${
                            inputStream.bufferedReader().readText()
                        }"
                    )
                }
            }
        }
    }

    fun deleteAll(documents: Array<String>): Boolean {
        val url = URI(endpoint).resolve("/$environment/documents").toURL()
        val payload = DeleteDocumentsRequest(documents)
        val connection = url.openConnection() as HttpURLConnection

        with(connection) {
            requestMethod = "DELETE"

            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("authorizationToken", token)

            doOutput = true

            val outputStreamWriter = OutputStreamWriter(outputStream)
            outputStreamWriter.write(json.encodeToString(payload))
            outputStreamWriter.flush()

            when (connection.responseCode) {
                204 -> return true
                400 -> throw Exception("invalid document id.")
                404 -> throw Exception("document id not found.")
                else -> {
                    throw Exception(
                        "Status code ${connection.responseCode}: \"${
                            inputStream.bufferedReader().readText()
                        }"
                    )
                }
            }
        }
    }

    fun getProperties(documentId: String): Map<String, String> {
        val url = URI(endpoint).resolve("/$environment/documents/$documentId/properties").toURL()
        val connection = url.openConnection() as HttpURLConnection

        with(connection) {
            requestMethod = "GET"

            setRequestProperty("authorizationToken", token)

            when (connection.responseCode) {
                200 -> {
                    val data = inputStream.bufferedReader().readText()

                    return json.decodeFromString<Properties>(data).properties
                }
                400 -> throw Exception("invalid document id.")
                404 -> throw Exception("document id not found.")
                else -> {
                    throw Exception(
                        "Status code ${connection.responseCode}: \"${
                            inputStream.bufferedReader().readText()
                        }"
                    )
                }
            }
        }
    }

    fun updateProperties(documentId: String, properties: Map<String, String>): Boolean {
        val url = URI(endpoint).resolve("/$environment/documents/$documentId/properties").toURL()
        val connection = url.openConnection() as HttpURLConnection
        val payload = DocumentPropertiesRequest(properties)

        with(connection) {
            requestMethod = "PUT"

            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("authorizationToken", token)

            doOutput = true

            val outputStreamWriter = OutputStreamWriter(outputStream)
            outputStreamWriter.write(json.encodeToString(payload))
            outputStreamWriter.flush()

            when (connection.responseCode) {
                204 -> return true
                400 -> throw Exception("invalid document id.")
                404 -> throw Exception("document id not found.")
                else -> {
                    throw Exception(
                        "Status code ${connection.responseCode}: \"${
                            inputStream.bufferedReader().readText()
                        }"
                    )
                }
            }
        }
    }

    fun deleteProperties(documentId: String): Boolean {
        val url = URI(endpoint).resolve("/$environment/documents/$documentId/properties").toURL()
        val connection = url.openConnection() as HttpURLConnection

        with(connection) {
            requestMethod = "DELETE"

            setRequestProperty("authorizationToken", token)

            when (connection.responseCode) {
                204 -> return true
                400 -> throw Exception("invalid document id.")
                404 -> throw Exception("document id not found.")
                else -> {
                    throw Exception(
                        "Status code ${connection.responseCode}: \"${
                            inputStream.bufferedReader().readText()
                        }"
                    )
                }
            }
        }
    }

    private fun batchIngest(documentBatch: List<IngestedDocument>): Boolean {
        val url = URI(endpoint).resolve("/$environment/documents").toURL()
        val connection = url.openConnection() as HttpURLConnection
        val payload = IngestionRequest(documentBatch)

        with(connection) {
            requestMethod = "POST"

            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("authorizationToken", token)

            doOutput = true

            val outputStreamWriter = OutputStreamWriter(outputStream)
            outputStreamWriter.write(json.encodeToString(payload))
            outputStreamWriter.flush()

            when (connection.responseCode) {
                204 -> return true
                400 -> throw Exception("invalid request.")
                else -> {
                    throw Exception(
                        "Status code ${connection.responseCode}: \"${
                            inputStream.bufferedReader().readText()
                        }"
                    )
                }
            }
        }
    }
}