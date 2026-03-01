package com.academic.sastracare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StudentAudioService {

    private final StudentQueryService studentQueryService;
    private final HuggingFaceTtsService ttsService;
    private final RedisTemplate<String, byte[]> redisTemplate;
    private final MessageSource messageSource;

    private static final long AUDIO_CACHE_TTL = 3600; // 1 hour

    private String buildKey(String type,
                            Long studentId,
                            String extra,
                            String lang) {
        return "audio:" + type + ":" + studentId + ":" + extra + ":" + lang;
    }

    private byte[] getOrGenerate(String key,
                                 String speech,
                                 String lang) {

        byte[] cached = redisTemplate.opsForValue().get(key);

        if (cached != null) {
            return cached;
        }

        byte[] audio = ttsService.convertTextToSpeech(speech, lang);

        redisTemplate.opsForValue().set(
                key,
                audio,
                AUDIO_CACHE_TTL,
                TimeUnit.SECONDS
        );

        return audio;
    }

    private Locale resolveLocale(String lang) {
        return Locale.forLanguageTag(lang);
    }

    // =========================
    // ATTENDANCE
    // =========================
    public byte[] getAttendanceAudio(Long studentId,
                                     Integer semesterNumber,
                                     String lang) {

        lang = normalizeLang(lang);

        var response =
                studentQueryService.getAttendance(studentId, semesterNumber, lang);

        String speech =
                messageSource.getMessage(
                        "attendance",
                        new Object[]{
                                semesterNumber,
                                Math.round(response.getPercentage())
                        },
                        resolveLocale(lang)
                );

        return getOrGenerate(
                buildKey("attendance", studentId,
                        semesterNumber.toString(), lang),
                speech,
                lang
        );
    }

    // =========================
    // FEES
    // =========================
    public byte[] getFeesAudio(Long studentId,
                               Integer semesterNumber,
                               String lang) {

        lang = normalizeLang(lang);

        var response =
                studentQueryService.getFees(studentId, semesterNumber, lang);

        String speech =
                messageSource.getMessage(
                        "fees",
                        new Object[]{
                                semesterNumber,
                                Math.round(response.getTotalAmount()),
                                Math.round(response.getPaidAmount())
                        },
                        resolveLocale(lang)
                );

        return getOrGenerate(
                buildKey("fees", studentId,
                        semesterNumber.toString(), lang),
                speech,
                lang
        );
    }

    // =========================
    // SGPA
    // =========================
    public byte[] getSgpaAudio(Long studentId,
                               Integer semesterNumber,
                               String lang) {

        lang = normalizeLang(lang);

        var response =
                studentQueryService.getSgpa(studentId, semesterNumber, lang);

        String speech =
                messageSource.getMessage(
                        "sgpa",
                        new Object[]{
                                semesterNumber,
                                response.getSgpa()
                        },
                        resolveLocale(lang)
                );

        return getOrGenerate(
                buildKey("sgpa", studentId,
                        semesterNumber.toString(), lang),
                speech,
                lang
        );
    }

    // =========================
    // CGPA (no semester)
    // =========================
    public byte[] getCgpaAudio(Long studentId,
                               String lang) {

        lang = normalizeLang(lang);

        var response =
                studentQueryService.getCgpa(studentId, lang);

        String speech =
                messageSource.getMessage(
                        "cgpa",
                        new Object[]{
                                response.getCgpa()
                        },
                        resolveLocale(lang)
                );

        return getOrGenerate(
                buildKey("cgpa", studentId,
                        "overall", lang),
                speech,
                lang
        );
    }

    // =========================
    // RESULT
    // =========================
    public byte[] getResultAudio(Long studentId,
                                 Integer semesterNumber,
                                 String lang) {

        lang = normalizeLang(lang);

        var response =
                studentQueryService.getResult(studentId, semesterNumber, lang);

        String resultStatus =
                response.getArrears() != null && response.getArrears() > 0
                        ? "arrears"
                        : "pass";

        String speech =
                messageSource.getMessage(
                        "result",
                        new Object[]{ semesterNumber, resultStatus },
                        resolveLocale(lang)
                );

        return getOrGenerate(
                buildKey("result", studentId,
                        semesterNumber.toString(), lang),
                speech,
                lang
        );
    }

    private String normalizeLang(String lang) {
        if (lang != null && lang.contains(",")) {
            lang = lang.split(",")[0];
        }
        if (lang != null && lang.contains("-")) {
            lang = lang.split("-")[0];
        }
        return (lang == null || lang.isBlank()) ? "en" : lang;
    }
}