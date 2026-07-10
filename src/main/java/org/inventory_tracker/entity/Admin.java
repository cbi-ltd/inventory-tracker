// package org.inventory_tracker.entity;


// import jakarta.persistence.*;
// import lombok.Getter;
// import lombok.Setter;
// import org.inventory_tracker.enums.Role;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;

// import java.util.Collection;
// import java.util.List;

// @Getter
// @Setter
// @Entity
// public class Admin extends BaseEntity implements UserDetails {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(unique = true, nullable = false)
//     private String email;

//     private String password;

//     private String fullName;

//     @Enumerated(EnumType.STRING)
//     private Role role;

//     private Boolean active = true;

//     @ManyToOne
//     @JoinColumn(name = "company_id")
//     private Company company;

//     @ManyToOne
//     @JoinColumn(name = "station_id")
//     private Station station;

//     @Override
//     public Collection<? extends GrantedAuthority> getAuthorities() {

//         return List.of(
//                 new SimpleGrantedAuthority("ROLE_" + role.name())
//         );
//     }

//     @Override
//     public String getUsername() {
//         return email;
//     }

//     @Override
//     public boolean isAccountNonExpired() {
//         return true;
//     }

//     @Override
//     public boolean isAccountNonLocked() {
//         return true;
//     }

//     @Override
//     public boolean isCredentialsNonExpired() {
//         return true;
//     }

//     @Override
//     public boolean isEnabled() {
//         return active;
//     }
// }
