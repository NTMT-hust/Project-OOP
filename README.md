ViSoBERT Sentiment Analysis Project
This project implements a Vietnamese Sentiment Analysis tool using the ViSoBERT pre-trained model. The system is designed using Object-Oriented Programming (OOP) principles in Java, ensuring modularity, encapsulation, and easy maintenance.

📋 Table of Contents
Prerequisites

Installation & Setup

Folder Structure

Configuration

Usage

Prerequisites
Ensure you have the following installed on your machine:

Java Development Kit (JDK): Version 11 or higher.

IDE: Visual Studio Code (recommended) or IntelliJ IDEA.

Dependencies: Any required .jar libraries (e.g., JSON parsers, ML bindings) should be placed in the lib folder.

Installation & Setup
Crucial Step: You must manually download and configure the model files before running the application.

1. Download the Model
Download the pre-trained ViSoBERT model from the following link: Download ViSoBERT Model

2. Extract Files
Navigate to your project directory: Project-OOP/src/resources/.

Create a folder named visobert.

Extract the contents of the downloaded .zip file into this folder.

⚠️ Important: Do not drag the whole zip file or a parent folder in. The file path must look exactly like this: Project-OOP/src/resources/visobert/config.json

Folder Structure
The project follows a standard Java workspace structure optimized for VS Code:

Plaintext

Project-OOP/
├── .vscode/
│   └── settings.json        # VS Code project configuration
├── bin/                     # Compiled Output (generated automatically)
├── lib/                     # External Dependencies (.jar files)
├── src/                     # Source Code
│   ├── resources/           # Assets and Models
│   │   └── visobert/        # [MODEL FILES GO HERE]
│   │       ├── config.json
│   │       ├── pytorch_model.bin
│   │       └── vocab.txt
│   └── Main.java            # Entry point
└── README.md
src: Contains all source code and resource assets.

lib: Contains external .jar dependencies.

bin: Destination for compiled .class files.

Configuration
This project uses VS Code settings to manage the classpath and output directories.

If you wish to customize where the compiled files or libraries are located, modify the .vscode/settings.json file:

JSON

{
    "java.project.sourcePaths": ["src"],
    "java.project.outputPath": "bin",
    "java.project.referencedLibraries": [
        "lib/**/*.jar"
    ]
}
OOP Design Principles
This project applies the following OOP concepts:

Encapsulation: The internal logic of loading the heavy ViSoBERT model is hidden within a dedicated Model class.

Single Responsibility: File I/O, JSON parsing, and Sentiment Prediction are handled by separate methods or classes.

Abstraction: The Main class interacts with a simplified interface to analyze text, without needing to understand the underlying complex matrix operations.

Would you like me to generate a .gitignore file to ensure you don't accidentally upload the heavy model files (bin/resources) to GitHub?
