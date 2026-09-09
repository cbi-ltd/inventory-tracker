// package org.inventory_tracker.security;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;
// import org.inventory_tracker.service.AdminDetailsService;
// import org.inventory_tracker.service.JwtService;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;
// import java.io.IOException;


// @Component
// @RequiredArgsConstructor
// public class JwtAuthenticationFilter extends OncePerRequestFilter {

//     private final JwtService jwtService;
//     private final AdminDetailsService adminDetailsService;

//     @Override
//     protected void doFilterInternal(
//             HttpServletRequest request,
//             HttpServletResponse response,
//             FilterChain filterChain
//     ) throws ServletException, IOException {

//         final String authHeader = request.getHeader("Authorization");
//         final String jwt;
//         final String username;

//         if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//             filterChain.doFilter(request, response);
//             return;
//         }

//         jwt = authHeader.substring(7);
//         username = jwtService.extractUsername(jwt);

//         if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//             UserDetails userDetails = adminDetailsService.loadUserByUsername(username);

//             if (jwtService.isTokenValid(jwt, userDetails)) {

//                 UsernamePasswordAuthenticationToken authToken =
//                         new UsernamePasswordAuthenticationToken(
//                                 userDetails,
//                                 null,
//                                 userDetails.getAuthorities()
//                         );

//                 authToken.setDetails(
//                         new WebAuthenticationDetailsSource()
//                                 .buildDetails(request)
//                 );

//                 SecurityContextHolder.getContext().setAuthentication(authToken);
//             }
//         }

//         filterChain.doFilter(request, response);
//     }
// }







// @Service
// public class JwtService {

//     @Value("${jwt.secret}")
//     private String secretKey;

//     public String generateToken(UserDetails userDetails) {
//         return Jwts.builder()
//                 .subject(userDetails.getUsername())
//                 .issuedAt(new Date())
//                 .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
//                 .signWith(getSigningKey())
//                 .compact();
//     }

//     public String extractUsername(String token) {
//         return extractAllClaims(token).getSubject();
//     }

//     public boolean isTokenValid(String token, UserDetails userDetails) {
//         String username = extractUsername(token);

//         return username.equals(userDetails.getUsername())
//                 && !isTokenExpired(token);
//     }

//     private boolean isTokenExpired(String token) {
//         return extractAllClaims(token)
//                 .getExpiration()
//                 .before(new Date());
//     }

//     private Claims extractAllClaims(String token) {
//         return Jwts.parser()
//                 .verifyWith(getSigningKey())
//                 .build()
//                 .parseSignedClaims(token)
//                 .getPayload();
//     }

//     private SecretKey getSigningKey() {
//         return Keys.hmacShaKeyFor(
//                 Decoders.BASE64.decode(secretKey)
//         );
//     }
// }





// @Service
// @RequiredArgsConstructor
// public class AdminDetailsService implements UserDetailsService {

//     private final AdminRepository adminRepository;

//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//         Admin admin = adminRepository.findByUsername(username)
//                 .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));

//         return org.springframework.security.core.userdetails.User
//                 .withUsername(admin.getUsername())
//                 .password(admin.getPassword())
//                 .authorities(admin.getRole().name()) || 
//                 .roles(admin.getRoles().stream().map(Enum::name).toArray(String[]::new))
//                 .build();
//     }
// }



// AuthenticationManager is needed when you're implementing username/password login yourself.
// @RestController
// @RequiredArgsConstructor
// public class AuthenticationController {

//     private final AuthenticationManager authenticationManager;
//     private final JwtService jwtService;

//     @PostMapping("/auth/login")
//     public AuthenticationResponse login(@RequestBody LoginRequest request) {

//         Authentication authentication =
//                 authenticationManager.authenticate(
//                         new UsernamePasswordAuthenticationToken(
//                                 request.getUsername(),
//                                 request.getPassword()
//                         )
//                 );

//         String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());

//         return new AuthenticationResponse(token);
//     }
// }


//If a user can have multiple roles (in the user entity):
// @ElementCollection(fetch = FetchType.EAGER)
// @Enumerated(EnumType.STRING)
// private Set<Role> roles = new HashSet<>();