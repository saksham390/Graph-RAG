package com.example.graphrag.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {

    private final ObjectProvider<ChatLanguageModel> chatModel;

    public AnswerService(ObjectProvider<ChatLanguageModel> chatModel) {
        this.chatModel = chatModel;
    }

    public String answer(String question, List<String> documentContext, List<String> graphContext) {
        ChatLanguageModel model = chatModel.getIfAvailable();
        String context = "DOCUMENT CONTEXT:\n" + String.join("\n", documentContext) +
                "\nGRAPH CONTEXT:\n" + String.join("\n", graphContext);
        if (model != null) {
            return model.generate("Answer the question using only the context below. If the context is " +
                    "insufficient, say so. Question: " + question + "\n\n" + context);
        }
        if (!graphContext.isEmpty()) {
            return graphContext.get(0);
        }
        if (!documentContext.isEmpty()) {
            return documentContext.get(0);
        }
        return "No relevant information was found.";
    }
}