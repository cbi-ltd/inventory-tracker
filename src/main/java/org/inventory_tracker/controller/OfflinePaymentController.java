// package org.inventory_tracker.controller;

// @RestController
// @RequestMapping("/api/v1/offline-payments")
// @RequiredArgsConstructor
// public class OfflinePaymentController {

//     private final OfflinePaymentService offlinePaymentService;

//     @GetMapping("/{id}")
//     public ResponseEntity<ApiSuccessResponse<OfflinePaymentResponse>>
//             getById(@PathVariable Long id) {

//         OfflinePaymentResponse response =
//                 offlinePaymentService.getById(id);

//         return ResponseEntity.ok(new ApiSuccessResponse<>(
//                 LocalDateTime.now(),
//                 HttpStatus.OK.value(),
//                 "Offline payment retrieved successfully",
//                 response));
//     }

//     @PostMapping("/{id}/confirm-receipt")
//     public ResponseEntity<ApiSuccessResponse<OfflinePaymentResponse>>
//             confirmReceipt(
//                     @PathVariable Long id,
//                     @Valid @RequestBody ConfirmOfflinePaymentRequest request) {

//         OfflinePaymentResponse response =
//                 offlinePaymentService.confirmReceipt(id, request);

//         return ResponseEntity.ok(new ApiSuccessResponse<>(
//                 LocalDateTime.now(),
//                 HttpStatus.OK.value(),
//                 "Offline payment receipt confirmed successfully",
//                 response));
//     }
// }
