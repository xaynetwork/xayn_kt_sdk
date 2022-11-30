package xayn_kt_sdk.server.model

import kotlinx.serialization.Serializable

@Serializable
data class IngestedDocument(val id: String, val snippet: String, val properties: Map<String, String>?);