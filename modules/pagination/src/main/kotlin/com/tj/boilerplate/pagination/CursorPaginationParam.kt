package com.tj.boilerplate.pagination

data class CursorPaginationParam(
    val limit: Int?,
    val nextCursor: Long?,
)
