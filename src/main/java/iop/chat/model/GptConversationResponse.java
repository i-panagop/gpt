package iop.chat.model;

public record GptConversationResponse(
        String id,
        String object,
        int created,
        String model,
        GptConversationResponseChoice[] choices,
        GptResponseUsage usage) {
}