package xayn_kt_sdk.client.model

import kotlinx.serialization.Serializable

@Serializable
data class PersonalizedDocumentData(val id: String, val score: Float, val properties: Map<String, String>)
