# Chat Application

Video demo: https://www.youtube.com/watch?v=eph4UfaUiuc

## Description

This project is a Java-based application that demonstrates a client-server communication system with encryption and decryption functionalities. The project uses JavaFX for the user interface and integrates with Firebase for database operations.

## Key Components

Main Classes
App: The main JavaFX application class that sets up the user interface.<br>
Client: Handles client-side operations, including connecting to the server and sending/receiving messages.<br>
Server: Sets up the server to listen for client connections.<br>
ThreadServerController: Manages individual client connections and handles message encryption/decryption.<br>
Database: Integrates with Firebase to store and retrieve data.

## Configuration

pom.xml: Maven configuration file for managing dependencies and build settings.<br>
launch.json: VS Code launch configurations for running the application.<br>

## Features

Client-Server Communication: The client can connect to the server and exchange messages.<br>
Encryption/Decryption: Messages can be encrypted using DES or Caesar cipher methods.<br>
JavaFX UI: A graphical user interface for the client application.<br>
Firebase Integration: Stores and retrieves data from Firebase.<br>

## Dependencies

JavaFX<br>
Firebase Admin SDK<br>
Maven<br>
