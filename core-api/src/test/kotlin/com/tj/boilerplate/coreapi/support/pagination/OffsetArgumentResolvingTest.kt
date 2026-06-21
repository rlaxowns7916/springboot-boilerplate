package com.tj.boilerplate.coreapi.support.pagination

import com.tj.boilerplate.coreapi.confirguration.web.OffsetPaginationArgumentResolver
import com.tj.boilerplate.pagination.OffsetPaginationParam
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

class OffsetArgumentResolvingTest {
    private val mockMvc =
        MockMvcBuilders
            .standaloneSetup(OffsetArgumentResolverTestController())
            .setCustomArgumentResolvers(OffsetPaginationArgumentResolver())
            .build()

    @Test
    fun `limit와_cursor를_올바르게_Resolving_할_수_있다`() {
        mockMvc
            .get("/offset-pagination-test") {
                param("page", "0")
                param("size", "200")
                param("sorts", "id:asc", "name:desc", "createdAt:asc")
            }.andDo {
                print()
            }.andExpect {
                status { is2xxSuccessful() }
                jsonPath("$.page") { value(0) }
                jsonPath("$.size") { value(200) }
            }
    }
}

@RestController
class OffsetArgumentResolverTestController {
    @GetMapping("/offset-pagination-test")
    fun test(
        @Offset
        offset: OffsetPaginationRequest,
    ): OffsetPaginationParam {
        val param = offset.toParam()
        return param
    }
}
