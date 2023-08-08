package com.charleex.vidgenius.datasource.feature.open_ai.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentInfo(
    @SerialName("en-US") val enUS: Content = Content(),
    @SerialName("es") val es: Content = Content(),
    @SerialName("fr") val fr: Content = Content(),
    @SerialName("pt") val pt: Content = Content(),
    @SerialName("hi") val hi: Content = Content(),
    @SerialName("tags") val tags: List<String> = emptyList(),
)

@Serializable
data class Content(
    @SerialName("title")val title: String = "",
    @SerialName("description") val description: String = "",
)

//{
//    "en-US": {
//    "title": "generated title here",
//    "description": "generated description here"
//},
//    "es": {
//    "title": "generated title here",
//    "description": "generated description here"
//},
//    "zh": {
//    "title": "generated title here",
//    "description": "generated description here"
//},
//    "pt": {
//    "title": "generated title here",
//    "description": "generated description here"
//},
//    "hi": {
//    "title": "generated title here",
//    "description": "generated description here"
//},
//    "tags": [
//    "generated tag with no hashtag",
//    "generated tag with no hashtag",
//    "generated tag with no hashtag",
//    "generated tag with no hashtag",
//    "generated tag with no hashtag",
//    ]
//}
//
// languages = "en-US", "es", "zh", "pt", "hi"

