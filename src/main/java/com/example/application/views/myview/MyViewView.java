package com.example.application.views.myview;
import io.github.cdimascio.dotenv.Dotenv;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ai.peoplecode.OpenAIConversation;
import com.google.cloud.storage.Blob;

import java.util.List;

@PageTitle("NP-AI: Objective AI Summaries")
@Menu(icon = "line-awesome/svg/pencil-ruler-solid.svg", order = 0)
@Route("")
public class MyViewView extends Composite<VerticalLayout> {

    private OpenAIConversation conversation;
    private GoogleCloudStorageHelper storageHelper;
    private Paragraph textLarge;
    private TextField textField;

    public MyViewView() {
        Dotenv dotenv = Dotenv.configure()
                .directory("/Users/nathankvinnesland/IdeaProjects/proj3")
                .load();        String apiKey = dotenv.get("API_KEY");
        // Initialize the assistant and Google Cloud helper
        conversation = new OpenAIConversation(apiKey, "gpt-4o");
        storageHelper = new GoogleCloudStorageHelper(apiKey);

        // Title and Description
        H1 h1 = new H1("Welcome to NP-AI");
        h1.getStyle().set("font-size", "var(--lumo-font-size-xxl)").set("color", "var(--lumo-primary-color)");
        h1.getStyle().set("text-align", "center");

        H4 h4 = new H4("Please select a topic below to get an objective AI summary, or ask any custom question.");
        h4.getStyle().set("text-align", "center");

        // Initialize Buttons with Icons and Styles
        Button buttonPrimary = createStyledButton("Climate Change", VaadinIcon.GLOBE.create(), "ClimateChange/", "United-in-Science-2024_en.pdf", "Explain climate change.");
        Button buttonPrimary2 = createStyledButton("Israeli/Palestine Conflict", VaadinIcon.FLAG_O.create(), "IPConflict/", "1435.pdf", "Explain the Israel-Palestine conflict.");
        Button buttonPrimary3 = createStyledButton("Moon Landing", VaadinIcon.ROCKET.create(), "MoonLanding/", "apollo-11-mission-report.pdf", "Is the moon landing real?");

        // Custom Question Section
        textLarge = new Paragraph("Standing by...");
        textLarge.setWidthFull();
        textLarge.getStyle().set("font-size", "var(--lumo-font-size-xl)").set("padding", "1rem");

        textField = new TextField("Ask a Question");
        textField.setWidthFull();
        Button buttonPrimary4 = new Button("Ask", VaadinIcon.QUESTION_CIRCLE.create());
        buttonPrimary4.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary4.getStyle().set("min-width", "150px");
        buttonPrimary4.addClickListener(event -> handleCustomQuestion());

        // Layout for buttons
        HorizontalLayout buttonLayout = new HorizontalLayout(buttonPrimary, buttonPrimary2, buttonPrimary3);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.setSpacing(true);
        buttonLayout.getStyle().set("padding", "1rem");

        // Custom Question Layout
        HorizontalLayout questionLayout = new HorizontalLayout(textField, buttonPrimary4);
        questionLayout.setWidthFull();
        questionLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Add all components to the layout
        getContent().setWidth("100%");
        getContent().setPadding(false);
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().add(h1, h4, buttonLayout, textLarge, questionLayout);
    }

    // Helper method to create styled buttons with icons
    private Button createStyledButton(String text, Icon icon, String folderPrefix, String fileName, String question) {
        Button button = new Button(text, icon);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        button.getStyle().set("min-width", "250px");
        button.addClickListener(event -> handleButtonClick(folderPrefix, fileName, question));
        return button;
    }

    // Modified handleButtonClick method to use folderPrefix, fileName, and question
    private void handleButtonClick(String folderPrefix, String fileName, String question) {
        try {
            // Retrieve the specific document content from Google Cloud Storage
            Blob blob = storageHelper.getBlobForTopic(folderPrefix, fileName);

            if (blob == null) {
                textLarge.setText("Document not found.");
                return;
            }

            String documentContent;

            // Check if the file is a PDF
            if (blob.getName().endsWith(".pdf")) {
                documentContent = storageHelper.extractTextFromPdf(blob);
            } else {
                documentContent = new String(blob.getContent());
            }

            if (documentContent.isEmpty()) {
                textLarge.setText("Failed to extract content from the document.");
                return;
            }

            StringBuilder combinedSummaries = new StringBuilder();
            List<String> chunks = storageHelper.splitContentIntoChunks(documentContent, 2048);

            for (String chunk : chunks) {
                String prompt = "Please summarize the following document:\n\n" + chunk;
                try {
                    String summary = conversation.askQuestion(question, prompt);
                    combinedSummaries.append("Summary of ").append(blob.getName()).append(" (Chunk):\n").append(summary).append("\n\n");
                } catch (Exception e) {
                    textLarge.setText("Error summarizing the document.");
                    return;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            textLarge.setText(combinedSummaries.toString());

        } catch (Exception e) {
            textLarge.setText("An error occurred while processing the document.");
        }
    }

    // Method to handle custom question from the user
    private void handleCustomQuestion() {
        String userQuestion = textField.getValue();
        String response = conversation.askQuestion(
                "You are a non-partisan news assistant. Provide neutral summaries of news articles and fact-based explanations of current events.",
                userQuestion);
        textLarge.setText(response);
    }
}