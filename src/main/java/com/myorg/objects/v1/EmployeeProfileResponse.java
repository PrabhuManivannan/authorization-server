package com.myorg.objects.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.myorg.base.objects.Response;

import lombok.Data;

@Data
public class EmployeeProfileResponse implements Response<EmployeeProfileStatus>{
	
	@JsonProperty(value = "status")
	private EmployeeProfileStatus status = new EmployeeProfileStatus();

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String empId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String fullName;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String emailId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String mobileNumber;

	

}
