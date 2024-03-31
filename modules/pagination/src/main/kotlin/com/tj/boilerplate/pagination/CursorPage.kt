package com.tj.boilerplate.pagination

data class CursorPage<T>(
    val data: List<T>,
    val hasMore: Boolean,
    val size: Int,
    val nextCursor: Long?,
) {
    companion object {
        fun <T> empty(): CursorPage<T> {
            return CursorPage(
                data = listOf(),
                hasMore = false,
                size = 0,
                nextCursor = null,
            )
        }
    }
}
