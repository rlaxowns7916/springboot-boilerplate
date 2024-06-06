package com.tj.boilerplate.pagination

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.data.domain.Pageable

class PaginationTemplateTest {
    @Test
    fun `결과가_지정한_Size보다_작다면_nextCursor는_null이다`() {
        val param =
            CursorPaginationParam(
                size = 100,
                nextCursor = 0L,
            )
        val query = { _: Long, _: Pageable ->
            listOf(
                SampleModel(1),
                SampleModel(2),
                SampleModel(3),
            )
        }

        val actual =
            assertDoesNotThrow { CursorPaginationTemplate.query(param, query) { it.id } }
        assertThat(actual.nextCursor).isNull()
    }

    @Test
    fun `지정한_size보다_한개_더_많다면_nextCursor를 추출해 낼 수 있다`() {
        val size = 100
        val param =
            CursorPaginationParam(
                size = size,
                nextCursor = 0L,
            )
        val query = { _: Long, _: Pageable ->
            (1..size + 1).map { SampleModel(it.toLong()) }
        }

        val actual =
            assertDoesNotThrow { CursorPaginationTemplate.query(param, query) { it.id } }

        assertThat(actual.nextCursor).isNotNull()
        assertThat(actual.size).isEqualTo(size)
        assertThat(actual.nextCursor).isEqualTo((size + 1).toLong())
    }
}

data class SampleModel(
    val id: Long,
)
