package com.project.coursework2;

import javafx.application.Application;

/**
 * Entry point for the CRBAS application.
 * This class exists so the fat-jar manifest can point to a non-JavaFX class,
 * which avoids module-path issues when launching outside of an IDE.
 *
 * @author Group 2
 * @version 1.0
 */
public class Launcher {

    /**
     * Starts the application.
     * Sets the macOS accessibility workaround before launching JavaFX to prevent
     * a known StackOverflowError in {@code MacAccessible.getUnignoredChildren}
     * that occurs when DatePicker controls are used in full-screen mode.
     *
     * @param args command-line arguments passed through to JavaFX
     */
    public static void main(String[] args) {
        System.setProperty("glass.accessible.force", "false");
        Application.launch(HelloApplication.class, args);
    }
}
