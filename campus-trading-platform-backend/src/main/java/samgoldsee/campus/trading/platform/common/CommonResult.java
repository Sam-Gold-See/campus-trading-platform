package samgoldsee.campus.trading.platform.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 通用返回结果
 *
 * @author HuangChunXin
 * @date 2026/5/22 09:58
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResult<T> {

	public static final int SUCCESS = 200;

	public static final String SUCCESS_MSG = "success";

	public static final int ERROR = 500;

	public static final String ERROR_MSG = "fail";

	/**
	 * 响应代码
	 */
	private Integer code;

	/**
	 * 响应数据
	 */
	private T data;

	/**
	 * 响应消息
	 */
	private String msg;

	public CommonResult() {
	}

	public CommonResult(Integer code, T data, String msg) {
		this.code = code;
		this.data = data;
		this.msg = msg;
	}

	public static <T> CommonResult<T> ok() {
		return new CommonResult<>(SUCCESS, null, SUCCESS_MSG);
	}

	public static <T> CommonResult<T> ok(T data) {
		return new CommonResult<>(SUCCESS, data, SUCCESS_MSG);
	}

	public static <T> CommonResult<T> ok(T data, String msg) {
		return new CommonResult<>(SUCCESS, data, msg);
	}

	public static <T> CommonResult<T> fail() {
		return new CommonResult<>(ERROR, null, ERROR_MSG);
	}

	public static <T> CommonResult<T> fail(String msg) {
		return new CommonResult<>(ERROR, null, msg);
	}

	public static <T> CommonResult<T> fail(Integer code, String msg) {
		return new CommonResult<>(code, null, msg);
	}

	public static <T> CommonResult<T> build(Integer code, T data, String msg) {
		return new CommonResult<>(code, data, msg);
	}
}
