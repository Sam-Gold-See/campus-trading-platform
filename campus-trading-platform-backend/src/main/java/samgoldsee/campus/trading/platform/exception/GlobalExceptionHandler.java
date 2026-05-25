package samgoldsee.campus.trading.platform.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import samgoldsee.campus.trading.platform.common.CommonResult;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 捕获所有抛出的异常，返回统一格式的错误响应
 *
 * @author HuangChunXin
 * @date 2026/5/22 11:06
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 处理业务异常
	 */
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.OK)
	public CommonResult<?> handleBusinessException(BusinessException e) {
		log.error("Business exception: {}", e.getMessage(), e);
		return CommonResult.fail(e.getCode(), e.getMessage());
	}

	/**
	 * 处理参数校验异常
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.OK)
	public CommonResult<?> handleValidationException(MethodArgumentNotValidException e) {
		log.error("Validation exception: {}", e.getMessage());
		String errorMsg = e.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(", "));
		return CommonResult.fail(errorMsg);
	}

	/**
	 * 处理参数异常
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.OK)
	public CommonResult<?> handleIllegalArgumentException(IllegalArgumentException e) {
		log.error("Illegal argument exception: {}", e.getMessage(), e);
		return CommonResult.fail(e.getMessage());
	}

	/**
	 * 处理参数绑定异常
	 */
	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.OK)
	public CommonResult<?> handleBindException(BindException e) {
		log.error("Bind exception: {}", e.getMessage());
		String errorMsg = e.getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(", "));
		return CommonResult.fail(errorMsg);
	}

	/**
	 * 处理所有其他异常
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.OK)
	public CommonResult<?> handleException(Exception e) {
		log.error("Unexpected exception: {}", e.getMessage(), e);

		// 获取根本原因
		Throwable cause = e;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}

		return CommonResult.fail(cause.getMessage() != null ? cause.getMessage() : "系统错误");
	}
}
