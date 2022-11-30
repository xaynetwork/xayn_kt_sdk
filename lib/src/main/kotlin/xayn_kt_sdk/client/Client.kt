package xayn_kt_sdk.client

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import xayn_kt_sdk.client.model.*
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI

private val json: Json by lazy {
    Json { ignoreUnknownKeys = true }
}

class Client(
    private val token: String,
    private val endpoint: String,
    private val environment: String,
    private val userId: String
) {
    fun likeDocument(documentId: String): Boolean {
        val url = URI(endpoint).resolve("/$environment/users/$userId/interactions").toURL()
        val payload = UserInteractionRequest(
            documents = arrayOf(
                UserInteractionData(
                    id = documentId,
                    type = UserInteractionType.POSITIVE
                )
            )
        )
        val connection = url.openConnection() as HttpURLConnection

        with(connection) {
            requestMethod = "POST"

            setRequestProperty("X-HTTP-Method-Override", "PATCH")
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("authorizationToken", token)

            doOutput = true

            val outputStreamWriter = OutputStreamWriter(outputStream)
            outputStreamWriter.write(json.encodeToString(payload))
            outputStreamWriter.flush()

            when (connection.responseCode) {
                204 -> return true
                400 -> {
                    val data = inputStream.bufferedReader().readText()
                    val error = json.decodeFromString<UserInteractionError>(data)

                    when (error.kind) {
                        "InvalidUserId" -> throw Exception("invalid request. User id is invalid.")
                        "InvalidDocumentId" -> throw Exception("invalid request. Document id is invalid.")
                        else -> {
                            throw Exception("invalid request. ${error.kind}.")
                        }
                    }
                }
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

    fun personalizedDocuments(count: Int = 10): PersonalizedDocumentsResponse {
        if (count < 1 || count > 100) {
            throw Exception("`count` should be a value between 1 and 100.");
        }

        val url = URI(endpoint).resolve("/$environment/users/$userId/personalized_documents?count=$count").toURL()
        val connection = url.openConnection() as HttpURLConnection

        with(connection) {
            requestMethod = "GET"

            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("authorizationToken", token)

            when (connection.responseCode) {
                200 -> {
                    val data = inputStream.bufferedReader().readText()

                    return json.decodeFromString(data)
                }
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
