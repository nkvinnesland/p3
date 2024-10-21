package com.example.application.views.myview;

import ai.peoplecode.OpenAIConversation;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;

public class GoogleCloudStorageHelper {

    private final Storage storage;
    private final String bucketName;
    private final OpenAIConversation openAIConversation; // Ensure this is initialized

    public GoogleCloudStorageHelper(String openAIKey) {
        // Replace with your actual bucket name
        this.bucketName = "doc-project3";
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.openAIConversation = new OpenAIConversation(openAIKey, "gpt-4"); // Initialize with your OpenAI API key
    }

    // List Blobs in a specific folder
    public List<Blob> listBlobsInFolder(String folderPrefix) {
        List<Blob> blobs = new ArrayList<>();
        Iterable<Blob> allBlobs = storage.list(bucketName, Storage.BlobListOption.prefix(folderPrefix)).iterateAll();
        for (Blob blob : allBlobs) {
            blobs.add(blob);
        }
        return blobs;
    }

    public Blob getBlobForTopic(String folderPrefix, String fileName) {
        Iterable<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(folderPrefix)).iterateAll();

        System.out.println("Looking for file: " + fileName + " in folder: " + folderPrefix);
        for (Blob blob : blobs) {
            System.out.println("Found blob: " + blob.getName()); // Log each blob found

            // Extract just the file name part from the full blob name
            String blobFileName = blob.getName().substring(blob.getName().lastIndexOf('/') + 1);

            if (blobFileName.equals(fileName)) {
                System.out.println("Matched blob: " + blob.getName());
                return blob;
            }
        }
        System.out.println("File not found.");
        return null; // Return null if no blob is found
    }
    // Asynchronously process PDF and call OpenAI
    public void processPdfAsync(Blob blob, Runnable onComplete) {
        // Show progress or notification
        Notification.show("Processing PDF...");

        CompletableFuture.runAsync(() -> {
            // Your PDF processing and OpenAI call here
            String result = extractTextAndSendToOpenAI(blob);

            // Once processing is done, update the UI (in the UI thread)
            UI.getCurrent().access(() -> {
                // Update the UI with the result
                Notification.show("PDF Processing Complete");
                onComplete.run();  // Call the completion callback
            });
        });
    }

    // Extract text and send it to OpenAI
    private String extractTextAndSendToOpenAI(Blob blob) {
        StringBuilder extractedText = new StringBuilder();
        try {
            // Get the PDF content as a byte array from the blob
            byte[] pdfContent = blob.getContent();

            // Load the PDF document from the byte[] in PDFBox 3.0.3
            PDDocument document = Loader.loadPDF(pdfContent);

            // Extract text using PDFTextStripper
            PDFTextStripper pdfStripper = new PDFTextStripper();
            extractedText.append(pdfStripper.getText(document));

            // Close the document
            document.close();

            // Now call OpenAI API with the extracted text
            String openAIResponse = openAIConversation.askQuestion("Explain the document", extractedText.toString());

            return openAIResponse;

        } catch (IOException e) {
            System.out.println("Error extracting text from PDF: " + e.getMessage());
            return "Error processing document";
        }
    }

    // Extract text from PDF Blob using Apache PDFBox
    public String extractTextFromPdf(Blob blob) {
        StringBuilder extractedText = new StringBuilder();
        try {
            byte[] pdfContent = blob.getContent(); // Get the PDF content as byte array
            PDDocument document = Loader.loadPDF(pdfContent); // Load the PDF

            PDFTextStripper pdfStripper = new PDFTextStripper(); // Extract text from PDF
            extractedText.append(pdfStripper.getText(document));

            document.close(); // Close the document after extraction
        } catch (IOException e) {
            System.out.println("Error extracting text from PDF: " + e.getMessage());
        }
        return extractedText.toString(); // Return extracted text
    }

    // Split document content into chunks (based on character count)
    public List<String> splitContentIntoChunks(String content, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int contentLength = content.length();
        for (int i = 0; i < contentLength; i += chunkSize) {
            chunks.add(content.substring(i, Math.min(contentLength, i + chunkSize)));
        }
        return chunks;
    }
}