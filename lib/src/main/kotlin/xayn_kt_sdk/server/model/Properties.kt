package xayn_kt_sdk.server.model

import kotlinx.serialization.Serializable

@Serializable
data class Properties(val properties: Map<String, String>)
