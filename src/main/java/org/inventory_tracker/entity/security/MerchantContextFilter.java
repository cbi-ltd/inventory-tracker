// package org.inventory_tracker.entity.security;

// import lombok.RequiredArgsConstructor;
// import org.inventory_tracker.entity.Merchant;
// import org.inventory_tracker.repository.MerchantRepository;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
// import org.inventory_tracker.exception.ResourceNotFoundException;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import java.io.IOException;



// @Component
// @RequiredArgsConstructor
// public class MerchantContextFilter extends OncePerRequestFilter {
//     private final MerchantRepository merchantRepository;

//     @Override
//     protected void doFilterInternal(
//             HttpServletRequest request,
//             HttpServletResponse response,
//             FilterChain filterChain)
//             throws ServletException, IOException {

//         try {
//             String merchantId = request.getHeader("X-Merchant-Id");

//             if (merchantId != null) {
//                 Merchant merchant = merchantRepository.findByCamsMerchantId(merchantId)
//                         .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

//                 MerchantPrincipal principal = new MerchantPrincipal(merchant.getCamsMerchantId(), merchant.getMerchantName(),
//                                                                     merchant.getMerchantEmail(), merchant.getInstitutionId());

//                 MerchantContext.setCurrentMerchant(principal);
//             }
//             filterChain.doFilter(request, response);
//         } 
//         finally {
//             MerchantContext.clear();
//         }
//     }
// }
