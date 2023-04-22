package iop.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import iop.chat.model.GptConversationRequest;
import iop.chat.model.GptConversationResponse;
import iop.chat.model.GptMessage;
import iop.chat.model.GptRequest;
import iop.chat.model.GptResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class Gpt {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException, InterruptedException {
        String prompt;
        if (args.length > 0 && "start".equals(args[0])) {
            List<GptMessage> messageHistory = new ArrayList<>();
            messageHistory.add(new GptMessage("system", "You are a helpful assistant."));
            System.out.println("Starting application...");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                try {
                    String input = scanner.nextLine();
                    if (input.equals("stop")) {
                        // Stop the application
                        System.out.println("Stopping application...");
                        System.exit(0);
                    }
                    messageHistory.add(new GptMessage("user", input));

                    GptConversationRequest chatGptRequest = new GptConversationRequest("gpt-3.5-turbo", messageHistory,
                        1, 500);
                    String requestBody = mapper.writeValueAsString(chatGptRequest);

                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer <api-key>")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                    var client = HttpClient.newHttpClient();
                    var response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        GptConversationResponse chatGptResponse = mapper.readValue(response.body(),
                            GptConversationResponse.class);
                        GptMessage answer = chatGptResponse.choices()[chatGptResponse.choices().length - 1].message();
                        if (Objects.nonNull(answer) && Objects.nonNull(answer.content())) {
                            messageHistory.add(new GptMessage(answer.role(), answer.content()));
                            System.out.println(answer.content().replace("\n", "").trim());
                        }
                    } else {
                        messageHistory.remove(messageHistory.size() - 1);
                        System.out.println(response.statusCode());
                        System.out.println(response.body());
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    System.out.println("Stopping application...");
                    System.exit(0);
                }
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter a string to search for: ");
            prompt = scanner.nextLine();

            GptRequest chatGptRequest = new GptRequest("text-davinci-003", prompt, 1, 500);
            String input = mapper.writeValueAsString(chatGptRequest);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer <api-key>")
                .POST(HttpRequest.BodyPublishers.ofString(input))
                .build();

            var client = HttpClient.newHttpClient();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                GptResponse chatGptResponse = mapper.readValue(response.body(), GptResponse.class);
                String answer = chatGptResponse.choices()[chatGptResponse.choices().length - 1].text();
                if (!answer.isEmpty()) {
                    System.out.println(answer.replace("\n", "").trim());
                }
            } else {
                System.out.println(response.statusCode());
                System.out.println(response.body());
            }
        }

    }
}