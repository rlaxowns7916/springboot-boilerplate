package com.tj.boilerplate.coreapi.confirguration.web

import com.tj.boilerplate.coreapi.support.pagination.Cursor
import com.tj.boilerplate.coreapi.support.pagination.CursorPaginationRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class CursorPaginationArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Cursor::class.java) &&
            parameter.parameterType == CursorPaginationRequest::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val limit = webRequest.getParameter(CursorPaginationRequest::limit.name)?.toIntOrNull()
        val nextCursor = webRequest.getParameter(CursorPaginationRequest::cursor.name)

        return CursorPaginationRequest(
            limit = limit,
            cursor = nextCursor,
        )
    }
}
