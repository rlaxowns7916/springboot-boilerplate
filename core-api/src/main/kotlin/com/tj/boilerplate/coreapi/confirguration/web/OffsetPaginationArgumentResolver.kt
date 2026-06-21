package com.tj.boilerplate.coreapi.confirguration.web

import com.tj.boilerplate.coreapi.support.pagination.Offset
import com.tj.boilerplate.coreapi.support.pagination.OffsetPaginationRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class OffsetPaginationArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(Offset::class.java) &&
            parameter.parameterType == OffsetPaginationRequest::class.java

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val page = webRequest.getParameter(OffsetPaginationRequest::page.name)?.toIntOrNull()
        val size = webRequest.getParameter(OffsetPaginationRequest::size.name)?.toIntOrNull()
        val sorts = webRequest.getParameterValues(OffsetPaginationRequest::sorts.name)?.toList() ?: emptyList()

        return OffsetPaginationRequest(
            page = page,
            size = size,
            sorts = sorts,
        )
    }
}
