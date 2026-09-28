package com.example.graphrag.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

public interface ToolCallingService {

    String answer(String question);

    @Service
    class DefaultToolCallingService implements ToolCallingService {
        private final ObjectProvider<ChatLanguageModel> chatModel;
        private final GraphRagTools tools;

        public DefaultToolCallingService(ObjectProvider<ChatLanguageModel> chatModel, GraphRagTools tools) {
            this.chatModel = chatModel;
            this.tools = tools;
        }

        @Override
        public String answer(String question) {
            ChatLanguageModel model = chatModel.getIfAvailable();
            if (model == null) {
                return "Tool calling requires OPENAI_API_KEY. Use /api/rag/query or /api/graphrag/query without it.";
            }
            ToolAssistant assistant = AiServices.builder(ToolAssistant.class)
                    .chatLanguageModel(model)
                    .tools(tools)
                    .build();
            return assistant.answer(question);
        }
    }

    interface ToolAssistant {
        String answer(String question);
    }
}