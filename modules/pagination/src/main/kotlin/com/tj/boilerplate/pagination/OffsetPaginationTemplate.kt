package com.tj.boilerplate.pagination

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

object OffsetPaginationTemplate {
    fun <T> query(
        param: OffsetPaginationParam,
        query: (pageable: Pageable) -> Page<T>,
    ): OffsetPage<T> {
        val result = query(param.toPageable())
        return OffsetPage(result)
    }
}
