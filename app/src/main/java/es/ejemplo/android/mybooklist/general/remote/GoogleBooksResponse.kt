package es.ejemplo.android.mybooklist.general.remote

data class GoogleBooksResponse(
    val items: List<BookItem>?
)

data class BookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    val pageCount: Int?,
    val categories: List<String>?,
    val imageLinks: ImageLinks?,
    val industryIdentifiers: List<IndustryIdentifier>?
)

data class ImageLinks(
    val thumbnail: String?
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)
