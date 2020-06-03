package com.myorg.base.objects;

public interface Response<T> {
	public T getStatus();
	public void setStatus(T status);
}
