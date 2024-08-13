package com.example.repy.Network;

import com.example.repy.Models.Appeal;
import com.example.repy.Models.User;
import com.example.repy.Network.ApiClient;
import com.example.repy.Network.ApiService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatGPTService {
    private ApiService apiService;

    public ChatGPTService(String apiKey) {
        Retrofit retrofit = ApiClient.getOpenAIClient(apiKey);
        apiService = retrofit.create(ApiService.class);
    }

    public void generateAppealDocument(Appeal appeal, OnDocumentGeneratedListener listener, User user) {
        // Craft the prompt based on the Appeal object
        String prompt = createPromptFromAppeal(appeal, user);

        // Build the request body
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        JsonArray messages = new JsonArray();
        messages.add(message);

        JsonObject body = new JsonObject();
        body.addProperty("model", "gpt-3.5-turbo"); // or "gpt-4" if available
        body.add("messages", messages);

        Call<JsonObject> call = apiService.getChatCompletion(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    String content = response.body()
                            .getAsJsonArray("choices")
                            .get(0).getAsJsonObject()
                            .getAsJsonObject("message")
                            .get("content").getAsString();

                    // Generate PDF from the content
                    String pdfPath = generatePDF(content, appeal.getId());

                    // Notify listener
                    listener.onSuccess(pdfPath);
                } else {
                    listener.onFailure(new Exception("API Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    private String createPromptFromAppeal(Appeal appeal, User user) {
        // Customize this method to create a prompt that instructs ChatGPT to generate the desired document
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a formal appeal document based on the following details:\n");
        prompt.append("Date: ").append(appeal.getDate().toString()).append("\n");
        prompt.append("Reason: ").append(appeal.getReason()).append("\n");
        prompt.append("Ticket Details:\n");
        //prompt.append(appeal.getTicket().toString()).append("\n");
        prompt.append("Details of the appellant: ").append(user.toString()).append("\n");
        prompt.append("Format the document appropriately.");

        return prompt.toString();
    }

    private String generatePDF(String content, String appealId) {
        // Use iText or any other PDF library to generate PDF
        // For demonstration, the path is hardcoded
        String pdfPath = "/path/to/generated/appeal_" + appealId + ".pdf";

        try (PdfWriter writer = new PdfWriter(pdfPath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph(content));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pdfPath;
    }

    public interface OnDocumentGeneratedListener {
        void onSuccess(String pdfPath);
        void onFailure(Throwable t);
    }
}

