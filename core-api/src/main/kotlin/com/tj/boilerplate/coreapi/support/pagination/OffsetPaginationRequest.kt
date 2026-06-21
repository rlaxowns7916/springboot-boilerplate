package com.tj.boilerplate.coreapi.support.pagination

import com.tj.boilerplate.pagination.OffsetPaginationParam
import com.tj.boilerplate.pagination.OffsetPaginationSort
import java.util.Locale

data class OffsetPaginationRequest(
    val page: Int?,
    val size: Int?,
    val sorts: List<String>,
) {
    fun toParam(): OffsetPaginationParam =
        OffsetPaginationParam(
            page = page ?: DEFAULT_PAGE,
            size = size ?: DEFAULT_SIZE,
            sorts = toOffsetPaginationSort(),
        )

    private fun toOffsetPaginationSort(): List<OffsetPaginationSort> {
        return sorts.mapNotNull {
            val parts = it.split(":")
            if (parts.size != 2) {
                return@mapNotNull null
            }

            val (fieldName, order) = parts
            when (order.lowercase(Locale.getDefault())) {
                "asc" -> OffsetPaginationSort.ASC(fieldName)
                "desc" -> OffsetPaginationSort.DESC(fieldName)
                else -> null
            }
        }
    }

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 100
    }
}
