package com.codit.be_boda.chat.service.answer;

import com.codit.be_boda.analysis.dto.CoverageLlmResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * coverage_item.detail(JSON)을 CoverageLlmResponse로 변환한다.
 *
 * <p>치료 유형별 AnswerGenerator 6곳에 복사돼 있던 parseCoverageDetail을 하나로 합쳤다.
 * 복사본 중 진단·통원 2곳에만 "null" 문자열 방어가 있었고 나머지 4곳에는 빠져 있었는데,
 * CoverageItemQueryRepository가 DB의 NULL을 "null" 문자열로 넘기기 때문에
 * 4곳에서는 detail이 비어 있으면 NullPointerException이 날 수 있었다. 방어가 있는 버전으로 통일한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CoverageDetailParser {

    private final ObjectMapper objectMapper;

    public CoverageLlmResponse parse(String detail) {
        if (detail == null || detail.isBlank() || "null".equalsIgnoreCase(detail)) {
            return empty();
        }

        try {
            CoverageLlmResponse parsed = objectMapper.readValue(detail, CoverageLlmResponse.class);
            return parsed == null ? empty() : parsed;
        } catch (Exception e) {
            log.warn("coverage_item detail 파싱 실패. detail={}", detail, e);
            return empty();
        }
    }

    private CoverageLlmResponse empty() {
        return new CoverageLlmResponse(false, List.of(), null);
    }
}
