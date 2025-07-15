package com.sarkhan.backend.controller;

import com.sarkhan.backend.service.PaymentHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/paymentHistory")
@RequiredArgsConstructor
public class PaymentHistoryController {
    private final PaymentHistoryService paymentHistoryService;

    @PostMapping("/approve")
    public ResponseEntity<String> handleApprove(@RequestParam("orderId") Long orderId,
                                                @RequestParam("card") String cardLast4Digits) {

        return ResponseEntity.ok(paymentHistoryService.handleApprove(orderId,cardLast4Digits));
    }
}
