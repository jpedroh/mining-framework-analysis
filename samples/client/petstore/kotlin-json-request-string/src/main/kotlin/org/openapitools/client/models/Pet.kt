/**
 * OpenAPI Petstore
 *
 * This is a sample server Petstore server. For this sample, you can use the api key `special-key` to test the authorization filters.
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.models

import org.openapitools.client.models.Category
import org.openapitools.client.models.Tag

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A pet for sale in the pet store
 *
 * @param name 
 * @param photoUrls 
 * @param id 
 * @param category 
 * @param tags 
 * @param status pet status in the store
 */
@Parcelize
@Serializable
data class Pet (

    @SerialName(value = "name")
    val name: kotlin.String,

    @SerialName(value = "photoUrls")
    val photoUrls: kotlin.collections.List<kotlin.String>,

    @SerialName(value = "id")
    val id: kotlin.Long? = null,

    @SerialName(value = "category")
    val category: Category? = null,

    @SerialName(value = "tags")
    val tags: kotlin.collections.List<Tag>? = null,

    /* pet status in the store */
    @SerialName(value = "status")
    val status: Pet.Status? = null

) : Parcelable {

    /**
     * pet status in the store
     *
     * Values: available,pending,sold,unknownDefaultOpenApi
     */
    @Serializable(with = PetSerializer::class)
    enum class Status(val value: kotlin.String) {
        @SerialName(value = "available") available("available"),
        @SerialName(value = "pending") pending("pending"),
        @SerialName(value = "sold") sold("sold"),
        @SerialName(value = "unknown_default_open_api") unknownDefaultOpenApi("unknown_default_open_api");
    }

    @Serializer(forClass = Status::class)
    internal object StatusSerializer : KSerializer<Status> {
        override val descriptor = kotlin.String.serializer().descriptor

        override fun deserialize(decoder: Decoder): Status {
            val value = decoder.decodeSerializableValue(kotlin.String.serializer())
            return Status.values().firstOrNull { it.value == value }
                ?: Status.unknownDefaultOpenApi
        }

        override fun serialize(encoder: Encoder, value: Status) {
            encoder.encodeSerializableValue(kotlin.String.serializer(), value.value)
        }
    }
}

