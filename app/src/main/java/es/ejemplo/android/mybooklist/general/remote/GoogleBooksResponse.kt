package es.ejemplo.android.mybooklist.general.remote

data class GoogleBooksResponse(
    val items: List<BookItem>? = null
)

data class BookItem(
    val id: String? = null,
    val volumeInfo: VolumeInfo? = null
)

data class VolumeInfo(
    val title: String? = null,
    val authors: List<String>? = null,
    val description: String? = null,
    val pageCount: Int? = null,
    val categories: List<String>? = null,
    val imageLinks: ImageLinks? = null,
    val industryIdentifiers: List<IndustryIdentifier>? = null,
    val publishedDate: String? = null
)

data class ImageLinks(
    val thumbnail: String? = null
)

data class IndustryIdentifier(
    val type: String? = null,
    val identifier: String? = null
)
