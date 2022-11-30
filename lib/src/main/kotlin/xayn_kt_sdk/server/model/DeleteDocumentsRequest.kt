package xayn_kt_sdk.server.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteDocumentsRequest(val documents: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeleteDocumentsRequest

        if (!documents.contentEquals(other.documents)) return false

        return true
    }

    override fun hashCode(): Int {
        return documents.contentHashCode()
    }
}
