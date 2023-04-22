package iop.chat.model;

import java.util.List;

public record GptConversationRequest(String model, List<GptMessage> messages, int temperature, int max_tokens) {
}