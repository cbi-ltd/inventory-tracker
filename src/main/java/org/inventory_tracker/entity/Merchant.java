package org.inventory_tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "merchant",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "cams_merchant_id")
    }
)
public class Merchant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cams_merchant_id", nullable = false, unique = true)
    private String camsMerchantId;

    private String merchantName;

    private String merchantEmail;

    private String merchantRole;

    private String institutionId;

    @OneToMany(mappedBy = "merchant")
    private List<Station> stations = new ArrayList<>();
}
