package com.tj.boilerplate.coreapi.support.pagination

import com.tj.boilerplate.pagination.OffsetPaginationParam

data class OffsetPaginationRequest(
    val page: Int?,
    val size: Int?,
) {
    fun toParam(): OffsetPaginationParam {
        return OffsetPaginationParam(
            page = page ?: DEFAULT_PAGE,
            size = size ?: DEFAULT_SIZE,
        )
    }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 100
    }
}
