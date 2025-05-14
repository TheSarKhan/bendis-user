package com.sarkhan.backend.payment.controller;


import com.sarkhan.backend.dto.order.OrderRequest;
import com.sarkhan.backend.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/invoice")
    public ResponseEntity<String> createInvoice(@RequestBody OrderRequest orderRequest, @RequestHeader("Authorization") String token) {
        token = token.substring(7);
        String response = paymentService.createInvoice(orderRequest, token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoice/{uuid}")
    public ResponseEntity<String> getInvoice(@PathVariable String uuid) {
        String response = paymentService.getInvoice(uuid);
        return ResponseEntity.ok(response);
    }
}
