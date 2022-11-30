package xayn_kt_sdk.client.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInteractionData(val id: String, val type: UserInteractionType)
