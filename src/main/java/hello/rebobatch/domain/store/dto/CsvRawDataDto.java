package hello.rebobatch.domain.store.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CsvRawDataDto {
    private String csvId;
    private String storeName;
    private String branchName;
    private String categoryLargeCode;
    private String categoryLarge;
    private String categoryMiddleCode;
    private String categoryMiddle;
    private String categorySmallCode;
    private String categorySmall;
    private String industryCode;
    private String industryName;
    private String sidoCode;
    private String sidoName;
    private String sigunguCode;
    private String sigunguName;
    private String adminDongCode;
    private String adminDongName;
    private String legalDongCode;
    private String legalDongName;
    private String lotNumberCode;
    private String lotDivisionCode;
    private String lotDivisionName;
    private String lotMainNumber;
    private String lotSubNumber;
    private String lotAddress;
    private String roadNameCode;
    private String roadName;
    private String buildingMainNumber;
    private String buildingSubNumber;
    private String buildingMgmtNumber;
    private String buildingName;
    private String roadAddress;
    private String oldZipCode;
    private String newZipCode;
    private String dongInfo;
    private String floorInfo;
    private String unitInfo;
    private String longitude;
    private String latitude;
}