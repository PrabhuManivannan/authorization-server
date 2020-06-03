/**
 * <h1>Lixo Technologies Pvt Ltd.</h1>
 */
package com.myorg.constant;

import com.myorg.base.objects.ResponseCode;

/**
 *
 * @author Lixo Technologies
 * @version 1.0
 * @since 2020-01-01
 */
public enum ResponseCodeDescription implements ResponseCode {

	TOKEN_EXPIRED("400", "token expired"), TOKEN_INVALID("400", "token invalid"), TOKEN_RETRIEVED("1001", "success"),
	UNAUTHORIZED("401", "Unauthorized"), FORBIDDEN("403", "Access denied"), VALIDATION_ERROR("400", "Validation Error"),
	INTERNAL_SERVER_ERROR("500", "Unable to process your request. Please contact service provider with transaction id");

	private final String code;

	private final String descrption;

	ResponseCodeDescription(String code, String descrption) {
		this.code = code;
		this.descrption = descrption;
	}

	public String getCode() {
		return code;
	}

	public String getDescrption() {
		return descrption;
	}

}
