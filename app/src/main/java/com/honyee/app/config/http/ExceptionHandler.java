package com.honyee.app.config.http;

import com.honyee.app.exp.CommonException;
import com.honyee.app.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

@Slf4j
@Component
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = CommonException.class)
    @ResponseBody
    public MyResponse<?> handler(CommonException e) {
        String logContext = LogUtil.filterStackToString(e);
        log.info(logContext);
        return new MyResponse<>(e.getCode(), e.getCommonMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public MyResponse<?> handler(MethodArgumentNotValidException e) {
        String logContext = LogUtil.filterStackToString(e);
        log.info(logContext);

        String includeFields = e.getBindingResult().getAllErrors().stream()
                .map(arg -> {
                    String argObjectName = ((FieldError) arg).getField();
                    String defaultMessage = arg.getDefaultMessage();
                    return argObjectName + ":" + defaultMessage;
                })
                .collect(Collectors.joining("、"));
        return new MyResponse<>(MyResponseCodeEnums.COMMON_EXCEPTION.getCode(), includeFields);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    @ResponseBody
    public MyResponse<?> handler(Exception e) {
        String logContext = LogUtil.filterStackToString(e);
        log.error(logContext);
        return new MyResponse<>(MyResponseCodeEnums.COMMON_EXCEPTION.getCode(), e.getMessage());
    }

}
