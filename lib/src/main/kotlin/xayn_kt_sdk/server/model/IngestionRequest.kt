package xayn_kt_sdk.server.model

import kotlinx.serialization.Serializable

@Serializable
data class IngestionRequest(val documents: List<IngestedDocument>)