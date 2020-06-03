package com.myorg.base.objects;

public interface Header {

	public String getTransactionId();

	public void setTransactionId(String transactionId);

	public String getChannel();

	public void setChannel(String channel);

	public String getCallingAPI();

	public void setCallingAPI(String callingAPI);

}
