package com.banda.publicsite.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Section 8 "Structured course" scenario: dates/price/instrument/minimum-age are distinct,
 * validated fields — never folded into free text. */
public record CreateCourseAnnouncementRequest(
        @NotBlank @Size(max = 255) String title,
        @Size(max = 255) String description,
        @NotNull LocalDate startDate,
        LocalDate endDate,
        @NotNull @DecimalMin(value = "0.0") BigDecimal price,
        @NotBlank @Size(max = 255) String instrument,
        @Min(0) int minimumAge
) {

    @AssertTrue(message = "endDate must not be before startDate")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
