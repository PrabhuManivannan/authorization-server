package com.myorg.objects.v1;

import com.myorg.base.objects.Status;

import lombok.Data;

@Data
public class EmployeeProfileStatus implements Status{
	private String statusCode;
	
	private String statusDescription;
	
	private String transactionId;
}
