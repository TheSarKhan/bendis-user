package com.sarkhan.backend.payment.service.impl;

import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.jwt.JwtService;
import com.sarkhan.backend.model.user.User;
import com.sarkhan.backend.payment.config.PayriffConfig;
import com.sarkhan.backend.payment.dto.response.PayriffInvoiceResponse;
import com.sarkhan.backend.payment.model.Invoice;
import com.sarkhan.backend.payment.service.PaymentService;
import com.sarkhan.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PayriffConfig payriffConfig;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${payriff.api.key}")
    private String apiKey;
    @Value("${payriff.merchant.id}")
    private String merchantId;

    @Override
    public String createInvoice(OrderRequest orderRequest, String token) {
        String email = jwtService.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        String url = "https://api.payriff.com/api/v2/invoices";

        Invoice.Body body = new Invoice.Body();
        body.setAmount(orderRequest.getTotalPrice());
        body.setCurrencyType("AZN");
        body.setDescription("Ödəniş");
        body.setFullName(user.getNameAndSurname());
        body.setEmail(email);
        body.setPhoneNumber(user.getCountryCode() + user.getPhoneNumber());
        body.setApproveURL(payriffConfig.getApproveUrl());
        body.setCancelURL(payriffConfig.getCancelUrl());
        body.setDeclineURL(payriffConfig.getDeclineUrl());
        body.setCustomMessage("Xahiş edirik 24 saat içində ödəyin");
        body.setExpireDate(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_DATE_TIME));
        body.setInstallmentPeriod(0);
        body.setInstallmentProductType("BIRKART");
        body.setLanguageType("AZ");
        body.setSendSms(false);
        body.setSendWhatsapp(true);
        body.setSendEmail(false);
        body.setAmountDynamic(false);
        body.setDirectPay(true);
        body.setMetadata(Map.of(
                "Order İD", String.valueOf(orderRequest.getOrderId()),
                "Total Price", String.valueOf(orderRequest.getTotalPrice())
        ));

        Invoice request = new Invoice();
        request.setMerchant(merchantId);
        request.setBody(body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<Invoice> entity = new HttpEntity<>(request, headers);

        ResponseEntity<PayriffInvoiceResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                PayriffInvoiceResponse.class
        );

        return response.getBody().getPayload().getPaymentUrl(); // <<< Kullanıcıyı buraya yönlendir!
    }



    @Override
    public String getInvoice(String uuid) {
        String url = "https://api.payriff.com/api/v2/get-invoice";
        //    d3f48371896f46e9a8a341ac3ea44726
        // İstek gövdesini oluşturuyoruz
        Map<String, Object> body = new HashMap<>();
        body.put("uuid", uuid);

        Map<String, Object> request = new HashMap<>();
        request.put("merchant", merchantId);
        request.put("body", body);

        // Header ayarları
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);  // Eğer "Bearer " gerekiyorsa: "Bearer " + apiKey

        // HTTP isteği oluşturuluyor
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // API'ye istek gönderiliyor
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        return response.getBody();
    }
}
