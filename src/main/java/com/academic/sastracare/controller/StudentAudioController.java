package com.academic.sastracare.controller;

import com.academic.sastracare.dto.AudioRequest;
import com.academic.sastracare.service.StudentAudioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audio")
@RequiredArgsConstructor
public class StudentAudioController {

    private final StudentAudioService audioService;

    private ResponseEntity<byte[]> buildAudioResponse(byte[] audio) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .body(audio);
    }

    // =========================
    // ATTENDANCE
    // =========================
    @PostMapping("/attendance")
    public ResponseEntity<byte[]> attendanceAudio(
            @RequestParam("semester") Integer semester,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language,
            @Valid @RequestBody AudioRequest request) {

        return buildAudioResponse(
                audioService.getAttendanceAudio(
                        request.getStudentId(),
                        semester,
                        language
                )
        );
    }

    // =========================
    // FEES
    // =========================
    @PostMapping("/fees")
    public ResponseEntity<byte[]> feesAudio(
            @RequestParam("semester") Integer semester,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language,
            @Valid @RequestBody AudioRequest request) {

        return buildAudioResponse(
                audioService.getFeesAudio(
                        request.getStudentId(),
                        semester,
                        language
                )
        );
    }

    // =========================
    // SGPA
    // =========================
    @PostMapping("/sgpa")
    public ResponseEntity<byte[]> sgpaAudio(
            @RequestParam("semester") Integer semester,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language,
            @Valid @RequestBody AudioRequest request) {

        return buildAudioResponse(
                audioService.getSgpaAudio(
                        request.getStudentId(),
                        semester,
                        language
                )
        );
    }

    // =========================
    // CGPA (no semester)
    // =========================
    @PostMapping("/cgpa")
    public ResponseEntity<byte[]> cgpaAudio(
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language,
            @Valid @RequestBody AudioRequest request) {

        return buildAudioResponse(
                audioService.getCgpaAudio(
                        request.getStudentId(),
                        language
                )
        );
    }

    // =========================
    // RESULT
    // =========================
    @PostMapping("/result")
    public ResponseEntity<byte[]> resultAudio(
            @RequestParam("semester") Integer semester,
            @RequestHeader(value = "Accept-Language", defaultValue = "en") String language,
            @Valid @RequestBody AudioRequest request) {

        return buildAudioResponse(
                audioService.getResultAudio(
                        request.getStudentId(),
                        semester,
                        language
                )
        );
    }
}