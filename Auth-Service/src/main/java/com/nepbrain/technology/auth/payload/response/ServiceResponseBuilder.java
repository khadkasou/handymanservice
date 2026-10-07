package com.nepbrain.technology.auth.payload.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nepbrain.technology.auth.util.ExceptionUtil;
import com.nepbrain.technology.auth.util.MessageBundle;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class ServiceResponseBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ServiceResponseBuilder.class);

    private ServiceResponseBuilder() {
    }

    public static ServiceResponse buildSuccess(String message) {
        ServiceResponse response = new ServiceResponse();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    public static ServiceResponse buildSuccess(String message, Object data) {
        ServiceResponse response = buildSuccess(message);
        response.setData(data);
        return response;
    }

    public static ServiceResponse buildError(String code) {
        ServiceResponse response = new ServiceResponse();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(MessageBundle.getMessage(code));
        return response;
    }

    public static ServiceResponse buildException(Exception e, HttpStatus httpStatus) {
        ServiceResponse response = new ServiceResponse();
        response.setSuccess(false);
        response.setCode(String.valueOf(httpStatus.value()));
        response.setMessage(e.getMessage());
        response.setDebugMessage(ExceptionUtil.getStackTraceString(e));
        return response;
    }

    public static ServiceResponse buildException(String code, String message) {
        ServiceResponse response = new ServiceResponse();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        return response;
    }


    public static void sendError(HttpStatus status, String code) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletResponse response = null;
        if (requestAttributes != null) {
            response = requestAttributes.getResponse();
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_EMPTY);
        try {
            if (response != null) {
                response.setContentType(APPLICATION_JSON_VALUE);
                response.setStatus(status.value());
                mapper.writeValue(response.getWriter(), ServiceResponseBuilder.buildException(code, MessageBundle.getMessage(code)));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
