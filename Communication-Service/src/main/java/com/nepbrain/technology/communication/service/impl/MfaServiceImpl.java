package com.nepbrain.technology.communication.service.impl;

import com.nepbrain.technology.communication.payload.request.MFARequest;
import com.nepbrain.technology.communication.payload.request.MFAVerifyRequest;
import com.nepbrain.technology.communication.payload.response.MfaResponse;
import com.nepbrain.technology.communication.service.CommunicationService;
import com.nepbrain.technology.communication.service.MfaService;
import com.nepbrain.technology.communication.util.GoogleAuthenticator;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.core.payload.response.GlobalResponseBuilder;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;


@Service
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MfaServiceImpl.class);

    private final HttpServletRequest request;
    private final CommunicationService communicationService;


    public static String generateOtp(String secretCode) {
        return GoogleAuthenticator.generateTotpCode(secretCode);
    }

    public String getUriForImage(String secret) {
        QrData data = new QrData.Builder()
                .label(request.getRemoteHost())
                .secret(secret)
                .issuer("swift")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = new byte[0];

        try {
            imageData = generator.generate(data);
        } catch (QrGenerationException e) {
            throw new GlobalException("QR001");
        }

        String mimeType = generator.getImageMimeType();

        return getDataUriForImage(imageData, mimeType);
    }

    public GlobalResponse verifyMFA(MFAVerifyRequest mfaVerifyRequest) {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        if (!verifier.isValidCode(mfaVerifyRequest.getSecret(), mfaVerifyRequest.getCode())) {
            throw new GlobalException("MFA001");
        }
        return GlobalResponseBuilder.buildSuccessResponse("MFA Code verified successfully");
    }


    @Override
    public GlobalResponse generateQrAndOtp(MFARequest mfaRequest) throws GlobalException {
        String qrCode = getUriForImage(mfaRequest.getSecret());
        String otp = generateOtp(mfaRequest.getSecret());
        MfaResponse mfaResponse = MfaResponse.builder()
                .code(otp)
                .qrImage(qrCode).build();
        return GlobalResponseBuilder.buildSuccessResponseWithData(null, "QR Code Generated Successfully", mfaResponse);
    }
}
