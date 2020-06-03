package com.myorg.base.util;

import org.springframework.http.ResponseEntity;

import com.myorg.base.objects.Header;
import com.myorg.base.objects.Response;
import com.myorg.base.objects.ResponseCode;
import com.myorg.base.objects.Status;

public interface ResponseUtil {

	@SuppressWarnings("unchecked")
	static  <T,K> ResponseEntity<T> responseStatusOK( Response<K> response,ResponseCode responseCode, Header header) {
		
		
		Status status=(Status) response.getStatus();
		
		status.setTransactionId(header.getTransactionId());
		status.setStatusCode(responseCode.getCode());
		status.setStatusDescription(responseCode.getDescrption());
		
		return (ResponseEntity<T>) ResponseEntity.ok().body(response);
	}

}
