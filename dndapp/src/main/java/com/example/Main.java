package com.example;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage)
     {
       
        for (String fontName : Font.getFontNames()) {
            System.out.println(fontName);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


}
