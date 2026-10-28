package com.codit.be_boda.chat.service.answer;

import com.codit.be_boda.chat.type.TreatmentType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 스프링에 등록된 모든 {@link TreatmentAnswerGenerator}를 모아 "치료 유형 → 생성기" 표로 관리한다.
 *
 * <p>애플리케이션 시작 시점에 표를 검증한다.
 * 같은 치료 유형을 담당하는 생성기가 둘이거나, 담당 생성기가 없는 치료 유형이 있으면 바로 실패시켜
 * 런타임에 특정 치료 유형 질문에서만 에러가 나는 상황을 막는다.
 */
@Component
public class TreatmentAnswerGenerators {

    private final Map<TreatmentType, TreatmentAnswerGenerator> generators;

    public TreatmentAnswerGenerators(List<TreatmentAnswerGenerator> generatorList) {
        Map<TreatmentType, TreatmentAnswerGenerator> map = new EnumMap<>(TreatmentType.class);

        for (TreatmentAnswerGenerator generator : generatorList) {
            TreatmentAnswerGenerator duplicated = map.put(generator.treatmentType(), generator);
            if (duplicated != null) {
                throw new IllegalStateException(
                        "치료 유형 " + generator.treatmentType() + "을(를) 담당하는 생성기가 둘 이상입니다: "
                                + duplicated.getClass().getSimpleName() + ", "
                                + generator.getClass().getSimpleName()
                );
            }
        }

        List<TreatmentType> missing = Arrays.stream(TreatmentType.values())
                .filter(type -> !map.containsKey(type))
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalStateException("담당 생성기가 없는 치료 유형이 있습니다: " + missing);
        }

        this.generators = Collections.unmodifiableMap(map);
    }

    public TreatmentAnswerGenerator get(TreatmentType treatmentType) {
        return generators.get(treatmentType);
    }
}
