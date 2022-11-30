package xayn_kt_sdk.client.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInteractionRequest(val documents: Array<UserInteractionData>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserInteractionRequest

        if (!documents.contentEquals(other.documents)) return false

        return true
    }

    override fun hashCode(): Int {
        return documents.contentHashCode()
    }
}
