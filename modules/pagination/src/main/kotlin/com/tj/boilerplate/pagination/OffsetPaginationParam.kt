package com.tj.boilerplate.pagination

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class OffsetPaginationParam(
    val page: Int = DEFAULT_PAGE,
    val size: Int = DEFAULT_SIZE,
    val sorts: List<OffsetPaginationSort> = emptyList(),
) {
    fun toPageable(): PageRequest {
        val sorts =
            Sort.by(
                if (sorts.isEmpty()) {
                    sorts.map {
                        when (it) {
                            is OffsetPaginationSort.ASC -> Sort.Order.asc(it.fieldName)
                            is OffsetPaginationSort.DESC -> Sort.Order.desc(it.fieldName)
                        }
                    }
                } else {
                    listOf(Sort.Order.asc("id"))
                },
            )

        return PageRequest.of(page, size, sorts)
    }

    companion object {
        private val DEFAULT_PAGE = 0
        private val DEFAULT_SIZE = 100
    }
}
