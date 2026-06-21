package com.tj.boilerplate.modules.pagination

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

object CursorPaginationTemplate {
    fun <T> query(
        param: CursorPaginationParam,
        query: (cursor: Long, pageable: Pageable) -> List<T>,
        cursoringKey: (T) -> Long,
    ): CursorPage<T> {
        val size = param.size
        val nextCursor = param.nextCursor ?: 0
        val pageable = PageRequest.of(0, size + 1, Sort.by("id").ascending())

        val result = query(nextCursor, pageable)
        val data = result.take(size)
        val hasMore = result.size == size + 1

        return CursorPage(
            data = data,
            hasMore = hasMore,
            size = data.size,
            nextCursor = if (hasMore) result.lastOrNull()?.let(cursoringKey) else null,
        )
    }
}
