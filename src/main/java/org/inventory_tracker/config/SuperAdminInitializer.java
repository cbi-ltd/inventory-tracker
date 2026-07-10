// package org.inventory_tracker.config;


// import lombok.RequiredArgsConstructor;
// import org.inventory_tracker.entity.Admin;
// import org.inventory_tracker.enums.Role;
// import org.inventory_tracker.repository.AdminRepository;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Component;

// @Component
// @RequiredArgsConstructor
// public class SuperAdminInitializer implements CommandLineRunner {

//     private final AdminRepository adminRepository;
//     private final PasswordEncoder passwordEncoder;

//     @Override
//     public void run(String... args) {

//         boolean superAdminExists = adminRepository.existsByRole(Role.SUPER_ADMIN);

//         if (!superAdminExists) {
//             Admin admin = new Admin();

//             admin.setFullName("System Super Admin");
//             admin.setEmail("superadmin@inventory.com");
//             admin.setPassword(passwordEncoder.encode("SuperAdmin123"));
//             admin.setRole(Role.SUPER_ADMIN);
//             admin.setActive(true);

//             adminRepository.save(admin);
//             System.out.println("SUPER ADMIN CREATED");
//         }
//     }
// }
