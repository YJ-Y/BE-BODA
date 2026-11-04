package com.codit.be_boda.chat.service.answer;

import com.codit.be_boda.chat.service.AnswerSource;

import java.util.List;

// 답변 만들기 전에 약관에서 미리 확인해 둔 근거
// 청구/금액/서류 칩이 전부 이걸 보고 답변을 만든다
public record AnswerEvidence(
        List<AnswerSource> dentalExclusionSources
) {

    public AnswerEvidence {
        dentalExclusionSources = dentalExclusionSources == null ? List.of() : List.copyOf(dentalExclusionSources);
    }

    // 사랑니·매복치 발치 제외 조항이 약관에 있는지
    public boolean dentalExclusionDetected() {
        return !dentalExclusionSources.isEmpty();
    }
}
