package hello.rebobatch.domain.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "store")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 225)
    private String csvId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String categoryLarge;
    @Column(length = 50)
    private String categoryMiddle;
    @Column(length = 50)
    private String categorySmall;

    @Column(length = 255)
    private String roadAddress;

    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public Store(Long id,String csvId, String name, String categoryLarge, String categoryMiddle,
                 String categorySmall, String roadAddress, BigDecimal latitude,
                 BigDecimal longitude, LocalDateTime createdAt, LocalDateTime updatedAt,
                 Boolean isDeleted) {
        this.id = id;
        this.csvId = csvId;
        this.name = name;
        this.categoryLarge = categoryLarge;
        this.categoryMiddle = categoryMiddle;
        this.categorySmall = categorySmall; // 오타 수정
        this.roadAddress = roadAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDeleted = (isDeleted != null) ? isDeleted : false;
    }
}
