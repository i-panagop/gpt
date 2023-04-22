package iop.chat.model;

public record GptRequest(String model, String prompt, int temperature, int max_tokens) {
}