package com.tj.boilerplate.coreapi.support.pagination

import com.tj.boilerplate.coreapi.confirguration.web.CursorPaginationArgumentResolver
import com.tj.boilerplate.pagination.CursorPaginationParam
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

class CursorArgumentResolvingTest {
    private val mockMvc =
        MockMvcBuilders.standaloneSetup(CursoringArgumentResolverTestController())
            .setCustomArgumentResolvers(CursorPaginationArgumentResolver())
            .build()

    @Test
    fun `limit와_cursor를_올바르게_Resolving_할_수_있다`() {
        mockMvc.get("/cursor-pagination-test") {
            param("size", "10")
            param("cursor", "123")
        }.andDo {
            print()
        }.andExpect {
            status { is2xxSuccessful() }
            jsonPath("$.size") { value(10) }
            jsonPath("$.nextCursor") { value(123) }
        }
    }
}

@RestController
class CursoringArgumentResolverTestController {
    @GetMapping("/cursor-pagination-test")
    fun test(
        @Cursor
        cursor: CursorPaginationRequest,
    ): CursorPaginationParam {
        val param = cursor.toParam()
        return param
    }
}
