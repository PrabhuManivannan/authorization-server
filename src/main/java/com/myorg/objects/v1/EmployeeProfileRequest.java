package com.myorg.objects.v1;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class EmployeeProfileRequest {
	@NotNull(message = "header is empty")
	@JsonInclude(JsonInclude.Include.ALWAYS)
	@Valid
	private CommonHeader header;

	@NotBlank(message = "userId is empty")
	@JsonInclude(JsonInclude.Include.ALWAYS)
	private String userId;

}
