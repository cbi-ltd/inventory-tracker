// package org.inventory_tracker.controller;

// @RestController
// @RequestMapping("/api/v1/offline-payment-reconciliations")
// @RequiredArgsConstructor
// public class OfflinePaymentReconciliationController {

//     private final OfflinePaymentReconciliationService reconciliationService;

//     @PostMapping
//     public ResponseEntity<
//             ApiSuccessResponse<OfflinePaymentReconciliationResponse>>
//             create(@Valid @RequestBody CreateReconciliationRequest request) {

//         OfflinePaymentReconciliationResponse response =
//                 reconciliationService.create(request);

//         return ResponseEntity.status(HttpStatus.CREATED)
//                 .body(new ApiSuccessResponse<>(
//                         LocalDateTime.now(),
//                         HttpStatus.CREATED.value(),
//                         "Reconciliation recorded successfully",
//                         response));
//     }

//     @PostMapping("/{id}/confirm")
//     public ResponseEntity<
//             ApiSuccessResponse<OfflinePaymentReconciliationResponse>>
//             confirm(@PathVariable Long id) {

//         OfflinePaymentReconciliationResponse response =
//                 reconciliationService.confirm(id);

//         return ResponseEntity.ok(new ApiSuccessResponse<>(
//                 LocalDateTime.now(),
//                 HttpStatus.OK.value(),
//                 "Reconciliation confirmed successfully",
//                 response));
//     }
// }
