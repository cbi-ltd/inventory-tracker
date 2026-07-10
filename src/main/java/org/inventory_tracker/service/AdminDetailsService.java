// package org.inventory_tracker.service;

// import lombok.RequiredArgsConstructor;
// import org.inventory_tracker.repository.AdminRepository;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// @Service
// @RequiredArgsConstructor
// public class AdminDetailsService implements UserDetailsService {

//     private final AdminRepository adminRepository;

//     @Override
//     public UserDetails loadUserByUsername(String username)
//             throws UsernameNotFoundException {

//         return adminRepository.findByEmail(username)
//                 .orElseThrow(() ->
//                         new UsernameNotFoundException(
//                                 "Admin not found"
//                         )
//                 );
//     }
// }
