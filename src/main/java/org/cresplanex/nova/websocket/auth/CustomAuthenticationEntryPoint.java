package org.cresplanex.nova.websocket.auth;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.cresplanex.nova.websocket.constants.ServerErrorCode;
//import org.cresplanex.nova.websocket.dto.ErrorAttributeDto;
//import org.cresplanex.nova.websocket.dto.ErrorResponseDto;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
//            throws IOException, ServletException {
//        log.info("Authentication failed: {}", authException.getMessage());
//
//        Map<String, Object> errorAttributes = new HashMap<>();
//        errorAttributes.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
//
//        ErrorAttributeDto errorAttributeDTO = new ErrorAttributeDto(
//                request.getRequestURI(),
//                errorAttributes
//        );
//
//        ErrorResponseDto errorResponseDTO = ErrorResponseDto.create(
//                ServerErrorCode.WS_AUTHENTICATION_FAILED,
//                "Authentication failed",
//                errorAttributeDTO
//        );
//        String jsonResponse = objectMapper.writeValueAsString(errorResponseDTO);
//
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.setContentType("application/json;charset=UTF-8");
//        response.getWriter().write(jsonResponse);
//    }
//}

public class CustomAuthenticationEntryPoint {
}
