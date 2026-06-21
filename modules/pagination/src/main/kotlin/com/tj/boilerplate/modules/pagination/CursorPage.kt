package com.tj.boilerplate.modules.pagination

data class CursorPage<T>(
    val data: List<T>,
    val hasMore: Boolean,
    val size: Int,
    val nextCursor: Long?,
) {
    fun <R> map(mapper: (T) -> R): CursorPage<R> =
        CursorPage(
            data = data.map(mapper),
            hasMore = hasMore,
            size = size,
            nextCursor = nextCursor,
        )

    companion object {
        fun <T> empty(): CursorPage<T> =
            CursorPage(
                data = listOf(),
                hasMore = false,
                size = 0,
                nextCursor = null,
            )
    }
}
