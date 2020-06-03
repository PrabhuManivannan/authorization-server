package com.myorg.base.objects;

public interface Status {
	public String getTransactionId();

	public void setTransactionId(String transactionId);

	public String getStatusCode();

	public void setStatusCode(String statusCode);

	public String getStatusDescription();

	public void setStatusDescription(String statusDescription);

}