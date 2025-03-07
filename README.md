# Chat Application

Video demo: https://www.youtube.com/watch?v=eph4UfaUiuc

## Description

This project is a Java-based application that demonstrates a client-server communication system with encryption and decryption functionalities. The project uses JavaFX for the user interface and integrates with Firebase for database operations.

## Key Components

Main Classes
App: The main JavaFX application class that sets up the user interface.
Client: Handles client-side operations, including connecting to the server and sending/receiving messages.
Server: Sets up the server to listen for client connections.
ThreadServerController: Manages individual client connections and handles message encryption/decryption.
Database: Integrates with Firebase to store and retrieve data.

## Configuration

pom.xml: Maven configuration file for managing dependencies and build settings.
launch.json: VS Code launch configurations for running the application.

## Features

Client-Server Communication: The client can connect to the server and exchange messages.
Encryption/Decryption: Messages can be encrypted using DES or Caesar cipher methods.
JavaFX UI: A graphical user interface for the client application.
Firebase Integration: Stores and retrieves data from Firebase.

## Dependencies

JavaFX
Firebase Admin SDK
Maven
