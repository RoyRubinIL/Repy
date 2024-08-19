package com.example.repy.Network;

import android.content.Context;

import com.example.repy.APIRequests.ChatGptRequest;
import com.example.repy.Interfaces.ApiService;
import com.example.repy.Models.Appeal;
import com.example.repy.APIResponses.ChatGptResponse;
import com.example.repy.Utilities.DataManager;
import com.example.repy.Utilities.StorageManager;
import com.example.repy.Utilities.UserManager;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatGPTService {
    private final ApiService apiService;
    private final UserManager userManager;
    private final DataManager dataManager;
    private final StorageManager storageManager;
    private final Context context;

    public ChatGPTService(String apiKey, Context context) {
        Retrofit retrofit = ApiClient.getOpenAIClient(apiKey);
        apiService = retrofit.create(ApiService.class);
        userManager = new UserManager();
        dataManager = DataManager.getInstance();
        storageManager = StorageManager.getInstance(context);
        this.context = context;
    }

    public void generateAppealDocument(Appeal appeal, String ticketUid, OnDocumentGeneratedListener listener) {
        createPromptFromAppeal(appeal, userManager.getCurrentUserUid(), ticketUid, prompt -> {
            ChatGptRequest.Message userMessage = new ChatGptRequest.Message("user", prompt);
            ChatGptRequest.Message systemMessage = new ChatGptRequest.Message("system", "You are a helpful assistant.");
            ChatGptRequest request = new ChatGptRequest("gpt-4o-mini", Arrays.asList(systemMessage, userMessage));

            Call<ChatGptResponse> call = apiService.getChatCompletion(request);
            enqueueWithRetry(call, appeal, 3, listener); // Pass the appeal object along with retries
        });
    }

    private void enqueueWithRetry(Call<ChatGptResponse> call, Appeal appeal, int retries, OnDocumentGeneratedListener listener) {
        call.enqueue(new Callback<ChatGptResponse>() {
            @Override
            public void onResponse(Call<ChatGptResponse> call, Response<ChatGptResponse> response) {
                if (response.isSuccessful()) {
                    ChatGptResponse chatGptResponse = response.body();
                    if (chatGptResponse != null && !chatGptResponse.getChoices().isEmpty()) {
                        String content = chatGptResponse.getChoices().get(0).getMessage().getContent();

                        // Generate PDF from the content
                        String pdfPath = generatePDF(content, appeal.getUid());

                        // Upload the PDF to Firebase Storage
                        storageManager.uploadPdfToFirebase(userManager.getCurrentUserUid(),pdfPath, appeal, listener);
                    } else {
                        listener.onFailure(new Exception("Empty response from ChatGPT."));
                    }
                } else if (response.code() == 429 && retries > 0) {
                    // Handle rate limiting error with retry
                    try {
                        Thread.sleep(2000); // Wait for 2 seconds before retrying
                        enqueueWithRetry(call.clone(), appeal, retries - 1, listener); // Retry
                    } catch (InterruptedException e) {
                        listener.onFailure(e);
                    }
                } else {
                    listener.onFailure(new Exception("API Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ChatGptResponse> call, Throwable t) {
                if (retries > 0) {
                    enqueueWithRetry(call.clone(), appeal, retries - 1, listener); // Retry on failure
                } else {
                    listener.onFailure(t);
                }
            }
        });
    }

    private void createPromptFromAppeal(Appeal appeal, String userUid, String ticketUid, OnPromptGeneratedListener listener) {
        dataManager.getUserData(userUid, user -> {
            if (user == null) {
                listener.onPromptGenerated("Failed to fetch user details for the appeal.");
                return;
            }

            dataManager.getTicketByTicketUid(userUid, ticketUid, ticket -> {
                final StringBuilder prompt = new StringBuilder();

                if (ticket != null) {
                    prompt.append("Generate a formal appeal document based on the following details:\n");

                    if (appeal.getDate() != null) {
                        prompt.append("Date: ").append(appeal.getDate().toString()).append("\n");
                    }

                    if (appeal.getReason() != null && !appeal.getReason().isEmpty()) {
                        prompt.append("Reason: ").append(appeal.getReason()).append("\n");
                    }

                    if (ticket != null && !ticket.toString().isEmpty()) {
                        prompt.append("Ticket Details:\n").append(ticket.toString()).append("\n");
                    }

                    if (user != null && !user.toString().isEmpty()) {
                        prompt.append("Details of the appellant: ").append(user.toString()).append("\n");
                    }

                    prompt.append("Format the document appropriately. If there is a missing value, ignore(do not write [missing value])");
                } else {
                    prompt.append("Failed to fetch ticket details for the appeal.");
                }

                listener.onPromptGenerated(prompt.toString());
            });
        });
    }

    private String generatePDF(String content, String appealUid) {
        // Use the internal storage directory
        String pdfPath = context.getFilesDir() + "/appeal_" + appealUid + ".pdf";

        try (PdfWriter writer = new PdfWriter(pdfPath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            document.add(new Paragraph(content));
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle the error appropriately
        }

        return pdfPath;
    }

    public interface OnDocumentGeneratedListener {
        void onSuccess(String pdfUrl);
        void onFailure(Throwable t);
    }

    public interface OnPromptGeneratedListener {
        void onPromptGenerated(String prompt);
    }
}
