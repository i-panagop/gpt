package iop.chat.model;

public record GptConversationResponseChoice(
        int index,
        GptMessage message,
        String finish_reason
) {
}