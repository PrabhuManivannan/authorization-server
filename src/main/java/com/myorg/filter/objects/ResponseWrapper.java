package com.myorg.filter.objects;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ResponseWrapper<T> {

	private boolean success;
	private String message;
	private T data;

	@JsonProperty(value = "error_code")
	private String statusCode;

}
