/*
 * package com.myorg.controller;
 * 
 * import javax.validation.Valid;
 * 
 * import org.springframework.http.MediaType; import
 * org.springframework.http.ResponseEntity; import
 * org.springframework.validation.Errors; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.RequestBody; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RestController;
 * 
 * import com.myorg.base.objects.Header; import com.myorg.base.objects.Response;
 * import com.myorg.base.objects.ResponseCode; import
 * com.myorg.base.util.ResponseUtil; import
 * com.myorg.constants.ResponseCodeConfig; import
 * com.myorg.objects.v1.EmployeeProfileRequest; import
 * com.myorg.objects.v1.EmployeeProfileResponse; import
 * com.myorg.objects.v1.EmployeeProfileStatus;
 * 
 * import lombok.extern.slf4j.Slf4j;
 * 
 * @RestController
 * 
 * @RequestMapping(value={"/api"})
 * 
 * @Slf4j public class EmployeeController {
 * 
 * @GetMapping(value = "/findUser", produces = MediaType.APPLICATION_JSON_VALUE)
 * public ResponseEntity<EmployeeProfileResponse>
 * getEmployeeProfile(@Valid @RequestBody EmployeeProfileRequest request, Errors
 * error) {
 * 
 * 
 * log.info("Fetching For User -> {} ", request.getUserId());
 * 
 * Header header= request.getHeader(); EmployeeProfileResponse response=new
 * EmployeeProfileResponse();
 * 
 * String transactionID=header.getTransactionId();
 * log.debug("transactionID  is -> {}  ", transactionID);
 * 
 * 
 * response.setEmpId("1234"); response.setEmailId("xyz@domain.com");
 * response.setFullName("Prabhu M"); response.setMobileNumber("989898989898");
 * 
 * 
 * ResponseCode responseCode=ResponseCodeConfig.FETCH_USER_SUCCESS;
 * 
 * return ResponseUtil.responseStatusOK(response, responseCode,header);
 * 
 * }
 * 
 * }
 */