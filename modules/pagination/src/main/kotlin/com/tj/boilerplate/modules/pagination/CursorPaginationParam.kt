package com.tj.boilerplate.modules.pagination

data class CursorPaginationParam(
    val size: Int,
    val nextCursor: Long,
)
