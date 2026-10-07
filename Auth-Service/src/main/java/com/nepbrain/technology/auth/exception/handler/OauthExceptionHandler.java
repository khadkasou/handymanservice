package com.nepbrain.technology.auth.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.auth.enums.ErrorCodeEnum;
import com.nepbrain.technology.auth.payload.response.ServiceResponse;
import com.nepbrain.technology.auth.payload.response.ServiceResponseBuilder;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class OauthExceptionHandler implements AuthenticationFailureHandler {

  private final ObjectMapper mapper;
  @Override
  public void onAuthenticationFailure(
          @Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull AuthenticationException exception)
      throws IOException {
    ServiceResponse serviceResponse;
    if (exception instanceof OAuth2AuthenticationException oauth2AuthenticationException
        && oauth2AuthenticationException.getError().getErrorCode().equals("access_denied")) {
      serviceResponse =
              ServiceResponseBuilder.buildError(ErrorCodeEnum.IAM007.getCode());
    } else {
      serviceResponse = ServiceResponseBuilder.buildException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), exception.getMessage());
    }
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.getWriter().write(mapper.writeValueAsString(serviceResponse));
  }
}
