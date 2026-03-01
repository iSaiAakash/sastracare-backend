package com.academic.sastracare.controller;

import com.academic.sastracare.dto.SemesterResponse;
import com.academic.sastracare.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SemesterController {

    private final SemesterService semesterService;

    @GetMapping("/semester-summary")
    public SemesterResponse getSummary(
            @RequestHeader(value = "Accept-Language", defaultValue = "en")
            String language) {

        return semesterService.getSemesterSummary(language);
    }

    @GetMapping(
            value = "/semester-summary/audio",
            produces = "audio/mpeg"
    )
    public byte[] getSummaryAudio(
            @RequestHeader(value = "Accept-Language", defaultValue = "en")
            String language) {

        return semesterService.getSemesterSummaryAudio(language);
    }
}