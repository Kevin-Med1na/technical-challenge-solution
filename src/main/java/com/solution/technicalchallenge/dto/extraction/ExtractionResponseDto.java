package com.solution.technicalchallenge.dto.extraction;

import com.solution.technicalchallenge.enums.JobStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtractionResponseDto {

    private UUID id;
    private JobStatus status;
    private int total;
    private int processed;
    private int successful;
    private int failed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
