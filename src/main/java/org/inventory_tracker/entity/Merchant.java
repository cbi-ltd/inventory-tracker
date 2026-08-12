package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "camsMerchantId")
    }
)
public class Merchant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String camsMerchantId;

    @Column()
    private String merchantName;

    @Column()
    private String merchantEmail;

    @Column(nullable = false)
    private String merchantRole;

    @Column(nullable = false)
    private String institutionId;
}
