package com.tj.boilerplate.coreapi.support.pagination

import com.tj.boilerplate.pagination.CursorPaginationParam

data class CursorPaginationRequest(
    val limit: Int?,
    val cursor: String?,
) {
    fun toParam(): CursorPaginationParam {
        return CursorPaginationParam(
            limit = limit,
            nextCursor = cursor?.toLongOrNull(),
        )
    }
}
