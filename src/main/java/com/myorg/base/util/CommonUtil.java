/*******************************************************************************
 * Copyright (C) 2018, Lixo Technologies Pvt Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement:
 *    This product includes software developed by the Lixo Technologies Pvt Ltd
 *
 * 4. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package com.myorg.base.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myorg.base.objects.CommonResponse;
import com.myorg.base.objects.CommonStatus;
import com.myorg.base.objects.ResponseCode;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Lixo Technologies
 * @version 1.0
 * @since 2020-01-01
 */
@Slf4j
public class CommonUtil {

	public static final ObjectMapper MAPPER = new ObjectMapper();
	private static final String JSON_HEADER = "header";
	private static final String STATUS = "status";

	private CommonUtil() {
		// empty constructor for singleton class
	}

	public static void writeResponse(HttpServletResponse httpResponse, int status, String response) throws IOException {
		httpResponse.setStatus(status);
		httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		httpResponse.getWriter().write(response);
		httpResponse.flushBuffer();
	}

	public static String toJsonString(Object object) {
		String value = null;
		try {
			value = MAPPER.writeValueAsString(object);
		} catch (Exception exception) {
			log.error("error parsing request as json", exception);
		}
		return value;
	}

	public static void writeResponse(HttpServletResponse response, String statusCode, String statusDeserption,
			String transactionId, int httpStatus) throws IOException {
		CommonStatus status = new CommonStatus();
		status.setStatusCode(statusCode);
		status.setStatusDescription(statusDeserption);
		status.setTransactionId(transactionId);
		CommonResponse commonResponse = new CommonResponse();
		commonResponse.setStatus(status);
		String json = MAPPER.writeValueAsString(commonResponse);
		writeResponse(response, httpStatus, json);
	}

	public static void writeResponse(HttpServletResponse response, ResponseCode statusCode, String transactionId,
			int httpStatus) throws IOException {
		CommonStatus status = new CommonStatus();
		status.setStatusCode(statusCode.getCode());
		status.setStatusDescription(statusCode.getDescrption());
		status.setTransactionId(transactionId);
		CommonResponse commonResponse = new CommonResponse();
		commonResponse.setStatus(status);
		String json = MAPPER.writeValueAsString(commonResponse);
		writeResponse(response, httpStatus, json);
	}

	private static String getValueFromBody(String body, String parentNode, String childNode) throws IOException {

		JsonNode jsonNode = MAPPER.readTree(body);

		String value = "";
		if (jsonNode.hasNonNull(parentNode)) {
			log.debug("{} -> value not null getting it from object ", parentNode);
			JsonNode status = jsonNode.get(parentNode);
			if (status.hasNonNull(childNode)) {
				log.debug("{} -> value  not null getting it from object ", childNode);
				value = status.get(childNode).asText();
			}
		}
		return value;
	}

	public static String getHeaderValueFromRequestBody(String body, String childNode) throws IOException {
		return getValueFromBody(body, JSON_HEADER, childNode);
	}

	public static String getStatusValueFromResponseBody(String body, String childNode) throws IOException {
		return getValueFromBody(body, STATUS, childNode);
	}

}
