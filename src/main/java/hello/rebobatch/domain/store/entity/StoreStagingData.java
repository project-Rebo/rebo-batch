package hello.rebobatch.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stg_store")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreStagingData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "csv_id")
    private String csvId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "road_address")
    private String roadAddress;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "category_large")
    private String categoryLarge;

    @Column(name = "category_middle")
    private String categoryMiddle;

    @Column(name = "category_small")
    private String categorySmall;
}