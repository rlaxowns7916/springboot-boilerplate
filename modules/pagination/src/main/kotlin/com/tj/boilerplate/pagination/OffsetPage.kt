package com.tj.boilerplate.pagination

import org.springframework.data.domain.Page

class OffsetPage<T>(
    val page: Int,
    val size: Int,
    val totalCount: Long,
    val data: List<T>,
    val hasMore: Boolean,
) {
    fun <R> map(mapper: (T) -> R): OffsetPage<R> {
        return OffsetPage(
            size = size,
            page = page,
            data = data.map(mapper),
            totalCount = totalCount,
            hasMore = hasMore,
        )
    }
    constructor(page: Page<T>) : this(
        data = page.content,
        hasMore = page.hasNext(),
        page = page.number,
        size = page.size,
        totalCount = page.totalElements,
    )
}
