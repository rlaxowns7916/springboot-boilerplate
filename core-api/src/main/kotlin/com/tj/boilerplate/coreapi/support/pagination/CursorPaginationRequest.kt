package com.tj.boilerplate.coreapi.support.pagination

import com.tj.boilerplate.pagination.CursorPaginationParam

data class CursorPaginationRequest(
    val size: Int?,
    val cursor: Long?,
) {
    fun toParam(): CursorPaginationParam {
        return CursorPaginationParam(
            size = size ?: DEFAULT_SIZE,
            nextCursor = cursor ?: DEFAULT_CURSOR,
        )
    }

    companion object {
        private const val DEFAULT_SIZE = 100
        private const val DEFAULT_CURSOR = 0L
    }
}
