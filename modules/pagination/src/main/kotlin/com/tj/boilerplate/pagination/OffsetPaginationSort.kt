package com.tj.boilerplate.pagination

sealed interface OffsetPaginationSort {
    data class ASC(val fieldName: String) : OffsetPaginationSort

    data class DESC(val fieldName: String) : OffsetPaginationSort
}
