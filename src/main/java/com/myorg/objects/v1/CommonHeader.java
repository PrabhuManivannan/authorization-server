package com.myorg.objects.v1;


import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myorg.base.objects.Header;

import lombok.Data;

@Data
public class CommonHeader implements Header{

	@NotBlank(message = "transaction id is empty")
	private String transactionId;

	@NotBlank(message = "channel is empty")
	@JsonInclude(JsonInclude.Include.ALWAYS)
	private String channel;

	@NotBlank(message = "callingAPI is empty")
	@JsonInclude(JsonInclude.Include.ALWAYS)
	private String callingAPI;

}
