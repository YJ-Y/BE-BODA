package com.codit.be_boda.chat.service.answer;

import com.codit.be_boda.chat.dto.request.ChatMessageRequest;
import com.codit.be_boda.chat.type.TreatmentType;

/**
 * 치료 유형별 답변 생성기(전략).
 *
 * <p>각 구현체가 자신이 담당하는 치료 유형을 {@link #treatmentType()}으로 밝히면,
 * {@link TreatmentAnswerGenerators}가 "치료 유형 → 생성기" 표를 만들어 ChatAnswerService에 제공한다.
 * 새 치료 유형을 추가할 때 ChatAnswerService를 고치지 않고 구현체 하나만 추가하면 된다.
 */
public interface TreatmentAnswerGenerator {

    TreatmentType treatmentType();

    // CHIP_CLAIM 문자열 응답
    String generateClaimAnswer(Long analysisId, ChatMessageRequest request);

    // CHIP_AMOUNT 문자열 응답
    String generateAmountAnswer(Long analysisId, ChatMessageRequest request);

    // CHIP_CLAIM 문자열 + 카드 응답
    ClaimAnswerResult generateStructuredClaimAnswer(Long analysisId, ChatMessageRequest request);

    // CHIP_AMOUNT 문자열 + 카드 응답
    AmountAnswerResult generateStructuredAmountAnswer(Long analysisId, ChatMessageRequest request);
}
