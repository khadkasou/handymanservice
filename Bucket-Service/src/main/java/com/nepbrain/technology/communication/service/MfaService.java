package com.nepbrain.technology.communication.service;

import com.nepbrain.technology.communication.payload.request.MFARequest;
import com.nepbrain.technology.communication.payload.request.MFAVerifyRequest;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;

public interface MfaService {
    GlobalResponse verifyMFA(MFAVerifyRequest mfaVerifyRequest);
    GlobalResponse generateQrAndOtp(MFARequest mfaRequest) throws GlobalException;


}
