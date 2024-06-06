package com.tj.boilerplate.pagination

data class OffsetPaginationParam(
    val page: Int = DEFAULT_PAGE,
    val size: Int = DEFAULT_SIZE,
) {

    companion object {
        private val DEFAULT_PAGE = 0
        private val DEFAULT_SIZE = 100
    }
}
