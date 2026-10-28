package com.codit.be_boda.chat.service;

import com.codit.be_boda.chat.entity.ChatMessage;
import com.codit.be_boda.chat.entity.ChatMessageSource;
import com.codit.be_boda.chat.entity.ChatSession;
import com.codit.be_boda.chat.repository.ChatMessageRepository;
import com.codit.be_boda.chat.repository.ChatMessageSourceRepository;
import com.codit.be_boda.chat.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 채팅 메시지 저장 전담. 트랜잭션은 이 클래스의 메서드 단위로 건다.
// (ChatService 안에서 직접 부르면 프록시를 안 거쳐서 @Transactional이 적용되지 않기 때문)
@Component
@RequiredArgsConstructor
public class ChatMessageWriter {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageSourceRepository chatMessageSourceRepository;

    // 첫 번째 저장: (첫 질문이면) 시스템 프롬프트 + 사용자 메시지
    @Transactional
    public ChatMessage saveUserMessage(ChatSession chatSession, String systemPrompt, ChatMessage userMessage) {
        if (systemPrompt != null) {
            chatSession.saveSystemPrompt(systemPrompt);
            chatSessionRepository.save(chatSession);
        }
        return chatMessageRepository.save(userMessage);
    }

    // 두 번째 저장: AI 답변 + 답변 근거
    @Transactional
    public ChatMessage saveAiMessage(ChatMessage aiMessage, List<AnswerSource> sources) {
        ChatMessage savedAiMessage = chatMessageRepository.save(aiMessage);
        saveMessageSources(savedAiMessage.getMessageId(), sources);
        return savedAiMessage;
    }

    // ChatService에 있던 메서드를 그대로 옮겨옴
    private void saveMessageSources(Long messageId, List<AnswerSource> sources) {
        if (sources == null || sources.isEmpty()) {
            return;
        }

        List<ChatMessageSource> messageSources = sources.stream()
                .filter(source -> source.chunkId() != null)
                .map(source -> new ChatMessageSource(
                        messageId,
                        source.chunkId(),
                        source.citedText(),
                        source.relevanceScore()
                ))
                .toList();

        if (messageSources.isEmpty()) {
            return;
        }

        chatMessageSourceRepository.saveAll(messageSources);
    }
}