package com.tj.boilerplate.pagination

data class CursorPaginationParam(
    val size: Int,
    val nextCursor: Long,
)
