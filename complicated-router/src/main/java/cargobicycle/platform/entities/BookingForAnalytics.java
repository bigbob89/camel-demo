package cargobicycle.platform.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingForAnalytics {
    private String customerName;
    private String providerName;
    private String bicycleDescription;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private OffsetDateTime fromDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private OffsetDateTime toDate;
}
