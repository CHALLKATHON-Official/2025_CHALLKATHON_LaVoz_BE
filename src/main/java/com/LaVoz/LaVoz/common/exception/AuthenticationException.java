package com.LaVoz.LaVoz.common.exception;

import com.LaVoz.LaVoz.web.apiResponse.ApiResponse;
import com.LaVoz.LaVoz.web.apiResponse.error.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.rmi.ServerException;

@RequiredArgsConstructor
@Getter
@Component
public class  AuthenticationException implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private String code;
    private String message;

    public void sendErrorResponse(HttpServletResponse response, ErrorStatus errorStatus) throws IOException, ServerException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        code = errorStatus.getCode();
        message = errorStatus.getMessage();

        ApiResponse<Void> errorResponse = ApiResponse.onFailure(code, message, null);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized

        // BaseResponse 형식으로 응답 생성
        ApiResponse<String> baseResponse = ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED.getCode(), ErrorStatus._UNAUTHORIZED.getMessage(), null);

        // JSON 응답 작성
        String jsonResponse = objectMapper.writeValueAsString(baseResponse);
        response.getWriter().write(jsonResponse);
    }
}