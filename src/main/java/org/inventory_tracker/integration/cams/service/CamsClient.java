// package org.inventory_tracker.integration.cams.service;

// import org.inventory_tracker.integration.cams.dto.PendingTransferRequest;
// import org.springframework.stereotype.Service;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.web.client.RestTemplate;
// import java.math.*;
// import org.springframework.http.*;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class CamsClient {

//     private final RestTemplate restTemplate;
//     private final CamsConfiguration camsConfiguration;

//     public void registerPendingTransfer(
//             String virtualAccountNumber,
//             String saleNumber,
//             BigDecimal amount,
//             String tid,
//             String deviceSerial) {

//         PendingTransferRequest request = PendingTransferRequest.builder()
//                 .virtualAccountNumber(virtualAccountNumber)
//                 .saleNumber(saleNumber)
//                 .amount(amount)
//                 .tid(tid)
//                 .deviceSerial(deviceSerial)
//                 .build();

//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);

//         HttpEntity<PendingTransferRequest> entity = new HttpEntity<>(request, headers);

//         String url = camsConfiguration.getBaseUrl() + "/api/v1/pending-transfer";
//         restTemplate.postForEntity(url, entity, Void.class);

//         log.info("Pending transfer registered. saleNumber={}", saleNumber);
//     }


//     public void registerPendingTransfer(String virtualAccountNumber, String saleNumber, BigDecimal amount, String deviceSerial) {

//         PendingTransferRequest request = PendingTransferRequest.builder()
//                 .virtualAccountNumber(virtualAccountNumber)
//                 .saleNumber(saleNumber)
//                 .amount(amount)
//                 .deviceSerial(deviceSerial)
//                 .build();

//         HttpHeaders headers = new HttpHeaders();
//         headers.setContentType(MediaType.APPLICATION_JSON);

//         HttpEntity<PendingTransferRequest> entity = new HttpEntity<>(request, headers);

//         String url = camsConfiguration.getBaseUrl() + "/api/v1/pending-transfer";
//         restTemplate.postForEntity(url, entity, Void.class);

//         log.info("Pending transfer registered. saleNumber={}", saleNumber);
//     }
// }
