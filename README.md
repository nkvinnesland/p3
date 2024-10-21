Nathan Kvinnesland, Kristina Lim

This project is part of the CS 514 Bridge Software Development class at the University of San Francisco (USF). It is a Gen-AI-powered tool that provides users with objective, AI-generated summaries of key topics. The tool leverages both Google Cloud Storage and OpenAI’s GPT-4 to retrieve and process documents, summarizing them for the user. It serves as an interactive tool for understanding complex issues like climate change, the Israel-Palestine conflict, and the moon landing controversy.

Project Overview

This project demonstrates the integration of Google Cloud Storage with Retrieval-Augmented Generation (RAG) technology to enable real-time document retrieval, analysis, and summarization. The RAG implementation represents the extra credit portion of the project, enhancing the assistant’s ability to provide fact-based, neutral summaries by directly referencing documents retrieved from storage. Users can select from predefined topics (such as Climate Change, the Israel-Palestine Conflict, and the Moon Landing), and the application retrieves relevant documents stored in Google Cloud Storage. The content is processed, with PDF text extraction where necessary, and then summarized using OpenAI’s GPT-4 model.

The RAG approach ensures that the responses are grounded in specific, up-to-date information, improving the AI’s ability to provide factual, unbiased summaries. In addition to predefined topics, users can also ask custom questions, with the assistant providing responses based on its existing knowledge base and any relevant documents available.

**FEATURES**

1. Predefined Topic Selection

User Perspective: Users can choose from topics like “Climate Change,” “Israeli/Palestine Conflict,” and “Moon Landing Faked?” Upon selection, the app retrieves relevant documents from cloud storage, processes them, and generates a summary.
Coding Challenges: The primary challenges were integrating Google Cloud Storage to list and retrieve specific documents, and using Apache PDFBox to extract text from PDF files for further processing by OpenAI’s GPT-4. Handling large file sizes and breaking content into manageable chunks for OpenAI’s token limit was also a significant challenge.

2. Custom Question Submission

User Perspective: Users can input their own questions into the application to receive AI-generated responses, allowing for more flexibility beyond the pre-defined topics.
Coding Challenges: Handling free-form user input required developing a dynamic interaction with OpenAI’s API. This involved careful prompt design to ensure neutral, fact-based responses and accounting for rate-limiting when sending multiple API requests.

3. Document Summarization via OpenAI

User Perspective: For each topic, the app retrieves the relevant document, processes it, and splits it into chunks for OpenAI to summarize. Users receive a complete, summarized version of the content, with each chunk processed separately and then combined.
Coding Challenges: One of the main difficulties was managing API token limits, which required splitting large documents into smaller pieces for summarization. Managing delays between API calls to prevent rate-limiting issues was also crucial. Additionally, ensuring accurate content extraction from PDFs was key to generating accurate summaries.

4. Responsive UI with Vaadin

User Perspective: The UI allows users to interact smoothly with the app. Buttons are clearly labeled, and the app responds to user input by fetching and displaying summaries in a visually appealing way.
Coding Challenges: Using Vaadin, a Java-based front-end framework, we had to create a responsive, user-friendly layout. We implemented several customizations to enhance the user experience, including styling buttons, adding icons, and ensuring the layout adapts to different screen sizes.

**IMPORTANT NOTES**

Due to github not allowing the push of my .env file and my google cloud storage credentials you will not be able to run this by opening it in IntelliJ. I have spoken with the professor and he said not to worry as we will present it in class and during the code review.
