package xayn_kt_sdk.client.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserInteractionType(val typeName: String) {
    @SerialName(value = "positive")
    POSITIVE("positive")
}