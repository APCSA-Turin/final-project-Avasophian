package com.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

//Overall the GUI has a bit more minimal commenting except in cases where the intent of something isn't clear.
/**
 * IF YOU WANT TO SEE A MORE DETAILED EXPLANATION OF THE LOGIC PLEASE LOOK AT THE TERMINAL VERSION!! 
 * It's far more clearly commented whereas the GUI isn't because I just finished it yesterday and did  not have the time to.
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
public class AppGUI extends Application 
{ 
    private String playerClass;
    private int playerLevel;
    private int statModifier;
    private JSONArray spellsCanLearn = new JSONArray();
    private JSONArray playerSpells = new JSONArray();
    private int spellsCanKnow;

    //These lists will eventually be used for filtering.
    //Allowed is for the possible spell levels 0-9, and allowed2 is for the damage types
    private Integer[] allowed = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private String[] allowed2 = {"Acid", "Bludgeoning", "Cold", "Fire", "Force", "Lightning", "Necrotic", "Piercing", "Poison", "Psychic", "Radiant", "Slashing", "Thunder"};
    //Allowed levels and allowedDmgTypes changes, fixedAllowedDmgTypes does not. I used an arraylist despite it not changing so i could use .contains().
    private ArrayList<Integer> allowedLevels = new ArrayList<Integer>(Arrays.asList(allowed));
    private ArrayList<String> allowedDmgTypes = new ArrayList<String>(Arrays.asList(allowed2));
    private ArrayList<String> fixedAllowedDmgTypes = new ArrayList<String>(Arrays.asList(allowed2));
    private ArrayList<String> loadedSpells = new ArrayList<String>();

    //Boolean to keep track of if the player has loaded a file.
    //Booleans to keep track of if filtering is enabled.
    private boolean filterByDamageType = false;
    private boolean filterByDamage = false;
    private boolean filterByNonDamaging = false;

    JSONArray results = new JSONArray();
    public void start(Stage primaryStage) throws Exception 
    {
        //Accesses the endpoint of the API.
        String endpoint = "https://www.dnd5eapi.co/api/2014/spells";
        String jsonString = SpellBook.getData(endpoint);
        JSONObject json = new JSONObject(jsonString);
        //Initializes a JSONArray that contains a list of all the spells.
        //Each spell in results has a name, index, level, and URL with more detailed information.
        results = json.getJSONArray("results");
        //Initializes new JSONArrays to keep track of the spells and the spells the player can learn. These will be declared later
        //Creates a new file with a pathname of output.txt.
        File file = new File("output.txt");

        if (file.exists() && file.length() > 0)
        {
            //Creates a new VBox to prompt the player to load.  https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/VBox.html
            VBox load = new VBox(20);
            //sets the padding (meaning the space between the elements of the vbox)
            load.setPadding(new Insets(30));
            //Centers the vbox
            load.setAlignment(Pos.CENTER);

            //Prompts player to load data
            Text loadText = new Text("Saved data found.\nDo you want to load your previous spellbook data?");
            //Sets font to georgia
            loadText.setFont(new Font("Georgia", 25));
            //Centers the text
            loadText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            

            //Creates a new button, which is something the player can click on to do an action https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Button.html
            Button yesButton = new Button("Yes");
            yesButton.setFont(new Font("Georgia", 20));

            //setonAction basically uses the event handler class to perform an action when the button is clicked.
            //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/ButtonBase.html
            yesButton.setOnAction(new EventHandler<ActionEvent>() 
            {
                //handle is the EventHandler class's method that runs the code when the button is clicked.
                //https://docs.oracle.com/javase/8/javafx/api/javafx/event/EventHandler.html 
                public void handle(ActionEvent event) 
                {
                    //Creates a new SpellLoader
                    SpellLoader s = new SpellLoader();

                    //Sets the data of the player to the variables loaded by the SpellLoader constructor
                    playerClass = s.getLoadClass();
                    playerLevel = s.getLevel();
                    statModifier = s.getModifier();
                    loadedSpells = s.getSpells();

                    //Creates a new spellBook with playerClass and playerLevel.
                    SpellBook spellBook = new SpellBook(playerClass, playerLevel);
                    spellsCanKnow = spellBook.spellsKnownCalculator(statModifier);

                    for(int i = 0; i < results.length(); i++)
                    {
                        JSONObject item = results.getJSONObject(i);
                        String url = "https://www.dnd5eapi.co" + item.getString("url");
                        try 
                        {
                            String urlString = SpellBook.getData(url);
                            JSONObject spell = new JSONObject(urlString);
                            JSONArray classesThatLearn = spell.getJSONArray("classes");
                            int spellLevel = spell.getInt("level");

                            //Checks to see if the spell is able to be learned by a player with the specified class and level.
                            //If so, adds it to spellsCanLearn.
                            if (spellBook.hasValue(classesThatLearn, "index", playerClass) && (spellBook.getSpellLevel()) >= spellLevel)
                            {
                                spellsCanLearn.put(spell);
                            }
                        } 
                        catch (Exception e) 
                        {
                            e.printStackTrace();
                        }
                    }

                    
                    for (String str : loadedSpells)
                    {
                        int found = -1;
                        try 
                        {
                            found = SpellSearch.searchIndex(spellsCanLearn, str);
                        } 
                        catch (Exception e) 
                        {
                            e.printStackTrace();
                        }
                        playerSpells.put(spellsCanLearn.get(found));
                    }

                    showMainMenu(primaryStage, spellsCanLearn);
                }
            });

            //Button for "no"
            Button noButton = new Button("No");
            noButton.setFont(new Font("Georgia", 20));
            //Goes to character setup if the user clicks no, to make a new character instead of load.
            noButton.setOnAction(new EventHandler<ActionEvent>() 
            {
                public void handle(ActionEvent event) 
                {
                    charSetup(primaryStage, results);
                }
            });
            //Adds CSS to the buttons.
            yesButton.getStyleClass().add("brown-button");
            noButton.getStyleClass().add("brown-button");

            //Creates a new Hbox for the yes and no buttons.
            HBox yesAndNoButtons = new HBox(20, yesButton, noButton);
            yesAndNoButtons.setAlignment(Pos.CENTER);
            //Adds the text and the buttons to the load vbox.
            load.getChildren().addAll(loadText, yesAndNoButtons);

            //Sets the scene and adds css functionality.
            Scene loadScene = new Scene(load, 600, 400);
            loadScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            //Loads the scene with primaryStage and sets it to fullscreen.
            primaryStage.setScene(loadScene);
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.show();
        }
    }

    private void charSetup(Stage primaryStage, JSONArray results)
    {
        //Creates a text for class choice and gives it a font.
        Text classText = new Text("Choose Class:");
        classText.setFont(new Font("Georgia", 35));

        //creates a FlowPane to permit wrapping if for some reason it's not in fullscreen.
        //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/FlowPane.html
        FlowPane classFlowpane = new FlowPane();
        //the vertical and horizontal spacing.
        classFlowpane.setHgap(10);
        classFlowpane.setVgap(20);
        classFlowpane.setPadding(new Insets(10));
        //Sets the amount of length it can go to before wrapping.
        classFlowpane.setPrefWrapLength(500);
        //Centers it
        classFlowpane.setAlignment(Pos.CENTER);

        //The list of magical classes in D&D. 
        //Wizard , cleric, sorcerer, bard, druid and warlock are full-casters, while ranger and paladin are half-casters
        String[] classesList = {"wizard", "cleric", "sorcerer", "bard", "druid", "warlock", "ranger", "paladin"};

        //Iterates through classeslist using a nested for loop
        for (String c : classesList)
        {
            //accesses image from resources/images/ using getResourceAsStream.
            //https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html
            //each image in images is titled according to class so this is easily accessed.
            //Changes said image to an ImageView because that's how JavaFX displays images
            //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/image/ImageView.html
            ImageView imgView = new ImageView(new Image(getClass().getResourceAsStream("/images/" + c + ".jpg"), 140, 140, true, true));
            //This button just displays the image clickably with no text
            //This just makes it so the image can have round borders
            Rectangle round = new Rectangle(140, 140);
            round.setArcWidth(30);
            round.setArcHeight(30);
            imgView.setClip(round);
            Button classButton = new Button("", imgView);
            classButton.getStyleClass().add(c);
            classButton.setOnAction(new EventHandler<ActionEvent>() 
            {
                public void handle(ActionEvent event) 
                {
                    playerClass = c;
                }
            });

            //Creates a new label that's a capitalized version of the class name to put under the image.
            Label label = new Label(SpellUtility.capitalizeString(c));
            label.setFont(new Font("Georgia", 25));
            //Makes a new VBOX (so puts the classbutton under the label) with spacing 5
            VBox card = new VBox(5, classButton, label);
            //Centers it
            card.setAlignment(Pos.CENTER);
            card.getStyleClass().add("card");
            card.getStyleClass().add(c);
            //Adds it to the whole flowpane
            classFlowpane.getChildren().add(card);
        }

        //Makes a new label for the level
        Text lvlLabel = new Text("Level:");
        lvlLabel.setFont(new Font("Georgia", 20));
        //Makes a dropdown menu using a ComboBox with each level so the player can select the level they want
        //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/ComboBox.html
        ComboBox<Integer> levelBox = new ComboBox<>();
        for (int i = 1; i <= 20; i++) 
        {
            //Adds each number from 1 to 20 to the levelBox
            levelBox.getItems().add(i);
        }
        //Uses EventHandlers to set the playerLevel to the selected value in the levelBox 
        levelBox.setOnAction(new EventHandler<ActionEvent>() 
        {
            public void handle(ActionEvent event) 
            {
                playerLevel = levelBox.getValue();
            }
        });
        //Makes a new label for the stat
        Text statLabel = new Text("Spellcasting Stat Modifier:");
        statLabel.setFont(new Font("Georgia", 20));
        //Makes a dropdown menu using a ComboBox with each stat modifier so the player can select the level they want
        //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/ComboBox.html
        ComboBox<Integer> stats = new ComboBox<>();
        for (int i = 0; i <= 5; i++) 
        {
            //Adds each stat modifier from 0 to 5 to the levelBox
            stats.getItems().add(i);
        }

        //Uses EventHandlers to set the statModifier to the selected value in the stats combobox 
        stats.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent event) 
            {
                statModifier = stats.getValue();
            }
        });
        //Adds a button for when the player wishes to confirm their choice
        Button confirmButton = new Button("Confirm");
        confirmButton.setFont(new Font("Georgia", 30));
        confirmButton.getStyleClass().add("confirm");
        //Sets the margins to add more spacing in between confirmButton and everything else
        //30 which is the first value in setMargin adds more spacing on the top
        VBox.setMargin(confirmButton, new Insets(30, 0, 0, 0));
        //Uses eventHandlers for what happens when confirm is clicked
        confirmButton.setOnAction(new EventHandler<ActionEvent>() 
        {
            public void handle(ActionEvent event) 
            {
                try 
                {
                    //Saves the new data of the player's class, spells, etc using spellSaver, 
                    SpellSaver.saveData(playerSpells, playerClass, playerLevel, statModifier);
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                if (playerClass != null && playerLevel > 0)
                {
                    //Initializes a new spellbook with the inputted class and level.
                    SpellBook spellBook = new SpellBook(playerClass, playerLevel);
                    //Calculates how many spells the player can know based on their class and stat modifier.
                    spellsCanKnow = spellBook.spellsKnownCalculator(statModifier);

                    //Iterates through each JSONObject in results and adds the spells to the spellsCanLearn JSONArray.
                    for(int i=0;i<results.length();i++)
                    {
                        JSONObject item = (JSONObject) results.get(i);
                        String temp= item.getString("url");
                        String url = "https://www.dnd5eapi.co";
                        url+=temp;
                        String urlString = "";
                        try 
                        {
                            urlString = SpellBook.getData(url);
                        } catch (Exception e) 
                        {
                            e.printStackTrace();
                        }
                        JSONObject spell = new JSONObject(urlString);
                        JSONArray classesThatlearn = spell.getJSONArray("classes");
                        int spellLevel = spell.getInt("level");
                        //Checks to see if the spell is able to be learned by a player with the specified class and level.
                        //If so, adds it to spellsCanLearn.
                        if (spellBook.hasValue(classesThatlearn, "index", playerClass) && (spellBook.getSpellLevel()) >= spellLevel)
                        {
                            spellsCanLearn.put(spell);
                        }
                    }
                    //Transitions to the main menu
                    showMainMenu(primaryStage, spellsCanLearn);
                }
            }
        });
        //Creates a new VBOX with all the elements  above
        VBox wholeScreen = new VBox(15, classText, classFlowpane, lvlLabel, levelBox, statLabel, stats, confirmButton);
        wholeScreen.setPadding(new Insets(20));
        wholeScreen.setAlignment(Pos.CENTER);

        //C.enters it when the window is fuillscreen
        StackPane centeredWholeScreen = new StackPane(wholeScreen);
        centeredWholeScreen.setPrefSize(600, 500);

        //Puts everything into a scrollPane incase it is not fullscreened for some reason.
        ScrollPane wholeScreeninScrollPane = new ScrollPane(centeredWholeScreen);
        wholeScreeninScrollPane.setFitToWidth(true);
        wholeScreeninScrollPane.setFitToHeight(true);
        wholeScreeninScrollPane.setPannable(true);


        //Sets the title, the scene, fullscreens it, and shows it.
        Scene scene = new Scene(wholeScreeninScrollPane, 600, 500);
        
        //Adds css file
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setTitle("Character Setup");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();
        
        
    }   
    public static void main(String[] args) 
    {
        launch(args);
    }

    private void showMainMenu(Stage stage, JSONArray spellsCanLearn) 
    {
        Text menuTitle = new Text("Main Menu");
        menuTitle.getStyleClass().add("main-menu-title");

        menuTitle.setFont(new Font("Georgia", 16));
        String[] menuOptions = { "See available spells", "Manage your spellbook", "Go to your stats", "Quit"};
        GridPane menuGrid = new GridPane();
        menuGrid.setHgap(40);
        menuGrid.setVgap(20);
        menuGrid.setPadding(new Insets(20));
        menuGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < menuOptions.length; i++) 
        {
            int row = i / 2;
            int col = i % 2;

            String labelText = menuOptions[i];
            Image img = new Image(getClass().getResourceAsStream("/images/option" + (i+1) + ".png"), 200, 200, true, true);
            ImageView imgView = new ImageView(img);

            Button optionButton = new Button("", imgView);
            optionButton.setPrefHeight(230);
            optionButton.setPrefWidth(230);
            optionButton.setOnAction(new EventHandler<ActionEvent>() 
            {
                public void handle(ActionEvent event) 
                {
                    if (labelText.equals("See available spells")) 
                    {
                        showAvailableSpells(stage, statModifier, new SpellBook(playerClass, playerLevel));
                    }
                    else if (labelText.equals("Manage your spellbook")) 
                    {
                        showSpellBook(stage, statModifier, new SpellBook(playerClass, playerLevel));
                    }    
                    else if (labelText.equals("Go to your stats")) 
                    {
                        showPlayerStats(stage, statModifier, new SpellBook(playerClass, playerLevel));
                    }                          
                    else if (labelText.equals("Quit"))
                    {
                        try 
                        {
                            SpellSaver.saveData(playerSpells, playerClass, playerLevel, statModifier);
                        } 
                        catch (IOException e) 
                        {
                            e.printStackTrace();
                        }
                        stage.setFullScreen(false);
                        Platform.exit();
                        System.exit(0);
                    }
                }
            });
            Label label = new Label(labelText);
            label.setWrapText(true);
            label.setMaxWidth(230);
            label.setAlignment(Pos.CENTER);
            
            VBox optionBox = new VBox(5, optionButton, label);
            optionBox.setAlignment(Pos.CENTER);
            optionBox.getStyleClass().add("menu-option");

            Rectangle round = new Rectangle();
            round.widthProperty().bind(optionBox.widthProperty());
            round.heightProperty().bind(optionBox.heightProperty());
            round.setArcWidth(40); 
            round.setArcHeight(40);
            optionBox.setClip(round);

            menuGrid.add(optionBox, col, row);
        }

        VBox menuLayout = new VBox(20, menuTitle, menuGrid);
        menuLayout.setPadding(new Insets(20));
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.getStyleClass().add("main-menu");
        menuLayout.setStyle("-fx-background-color: #baabb8;");

        Scene scene = new Scene(menuLayout, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setMaximized(true);
    }       

    private void showAvailableSpells(Stage stage, int statModifier, SpellBook spellBook) 
    {

        Font spellFont = new Font("Georgia", 16);
        Font spellFontSmall = new Font("Georgia", 14);

        BorderPane wholeLayout = new BorderPane();
        wholeLayout.setPadding(new Insets(20));
        wholeLayout.setStyle("-fx-background-color: #baabb8;");

        HBox layout2 = new HBox(20);
        layout2.setPadding(new Insets(20));

        Text spellCountText = new Text("Spells: " + playerSpells.length() + " / " + spellsCanKnow);
        spellCountText.setFont(spellFont);

        HBox top = new HBox(spellCountText);
        top.setAlignment(Pos.TOP_RIGHT);
        top.setPadding(new Insets(0, 10, 0, 0));

        VBox topContainer = new VBox(10, top);
        topContainer.setPadding(new Insets(0, 10, 10, 10));


        VBox spellListVBox = new VBox(10);
        spellListVBox.setPadding(new Insets(10));
        ScrollPane spellScrollPane = new ScrollPane(spellListVBox);
        spellScrollPane.setFitToWidth(true);
        spellScrollPane.setPrefWidth(350);
        spellScrollPane.getStyleClass().add("spell-scroll");

        Rectangle clip = new Rectangle();
        clip.setArcWidth(30); 
        clip.setArcHeight(30);
        clip.widthProperty().bind(spellScrollPane.widthProperty());
        clip.heightProperty().bind(spellScrollPane.heightProperty());
        spellScrollPane.setClip(clip);

        spellScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        VBox.setVgrow(spellScrollPane, Priority.ALWAYS);

        TextFlow spellInfoText = new TextFlow();
        spellInfoText.setPrefWidth(900);
        spellInfoText.setLineSpacing(5);


        Button addButton = new Button("Add");
        addButton.setFont(new Font("Georgia", 16));
        addButton.setDisable(true); 
        addButton.getStyleClass().add("spell-button");

        VBox.setMargin(addButton, new Insets(20, 0, 0, 0));
        
        //for some reason my code wasnt working if I tried to do it like normal. 
        //So i did some digging and found out you had to make a single length array for some reason??
        //I don't fully get it I'm gonna be honest with you but it works.
        JSONObject[] selectedSpell2 = new JSONObject[1]; 


        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(10));


        TextField searchField = new TextField();
        searchField.setPromptText("Search spells...");
        searchField.setPrefWidth(220);
        searchField.setFont(spellFont);
        searchField.getStyleClass().add("text-field");


        HBox.setHgrow(searchField, Priority.ALWAYS);

        Button searchButton = new Button("Search");
        searchButton.setMinWidth(90);
        searchButton.setFont(spellFont);
        searchButton.getStyleClass().add("search-button");

        searchButton.setOnAction(new EventHandler<ActionEvent>() 
        {
            public void handle(ActionEvent event) 
            {
                String searchString = SpellUtility.convertToIndex(searchField.getText().toLowerCase().trim());
                JSONArray searchResults = new JSONArray();

                try 
                {
                    searchResults = SpellSearch.search2(spellsCanLearn, searchString);
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }

                spellListVBox.getChildren().clear();

                if (searchResults.length() == 0) 
                {
                    Label noResults = new Label("No spells found.");
                    noResults.setFont(spellFont);
                    spellListVBox.getChildren().add(noResults);
                    return;
                }

                for (int i = 0; i < searchResults.length(); i++) 
                {
                    JSONObject spellObj = searchResults.getJSONObject(i);
                    Button spellButton = createSpellButton(spellObj, spellFont, selectedSpell2, spellInfoText, statModifier, addButton, playerSpells, spellsCanKnow, spellBook, true);
                    spellListVBox.getChildren().add(spellButton);
                }
            }
        });
        searchBox.getChildren().addAll(searchField, searchButton);
        VBox spellListSection = new VBox(10, searchBox, spellScrollPane);
        addButton.setOnAction(new EventHandler<ActionEvent>() 
        {
            public void handle(ActionEvent event) 
            {
                JSONObject selectedSpell = selectedSpell2[0];
                if (selectedSpell != null) 
                {
                    String spellName = selectedSpell.getString("name").toLowerCase();
                    boolean alreadyKnown = false;
                    for (int i = 0; i < playerSpells.length(); i++) 
                    {
                        if (playerSpells.getJSONObject(i).getString("name").toLowerCase().equals(spellName)) 
                        {
                            alreadyKnown = true;
                            break;
                        }
                    }
                    if (alreadyKnown) 
                    {
                        addButton.setDisable(true);
                    } 
                    else if (playerSpells.length() < spellsCanKnow) 
                    {
                        playerSpells.put(selectedSpell);
                        addButton.setDisable(true);
                        spellCountText.setText("Spells: " + playerSpells.length() + " / " + spellsCanKnow);
                    }
                    else 
                    {
                        System.out.println("You can't learn any more spells at your current level.");
                    }
                }
            }
        });

        VBox spellInfoBox = new VBox(10, spellInfoText, addButton);
        spellInfoBox.setPadding(new Insets(10));
        spellInfoBox.setPrefWidth(900);

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(250);

        Label sortLabel = new Label("Sort Options:");
        sortLabel.setFont(spellFontSmall);

        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("None", "Level (high to low)");
        sortComboBox.setPromptText("Select sort option");
        sortComboBox.setPrefWidth(200);
        sortComboBox.getStyleClass().add("combo-box");
        sortComboBox.setOnAction(new EventHandler<ActionEvent>() 
        {
            public void handle(ActionEvent event) 
            {
                String choice = sortComboBox.getValue();

                if (choice == null || choice.equals("None")) 
                {
                    for (int i = 0; i < spellsCanLearn.length() - 1; i++) 
                    {
                        int minIndex = i;
                        for (int j = i + 1; j < spellsCanLearn.length(); j++) 
                        {
                            JSONObject spell1 = spellsCanLearn.getJSONObject(j);
                            JSONObject spell2 = spellsCanLearn.getJSONObject(minIndex);

                            String name1 = spell1.getString("name").toLowerCase();
                            String name2 = spell2.getString("name").toLowerCase();

                            if (name1.compareTo(name2) < 0) 
                            {
                                minIndex = j;
                            }
                        }
                        Object temp = spellsCanLearn.get(i);
                        spellsCanLearn.put(i, spellsCanLearn.get(minIndex));
                        spellsCanLearn.put(minIndex, temp);
                    }
                }

                if (choice.equals("Level (high to low)")) 
                {
                    for (int i = 0; i < spellsCanLearn.length() - 1; i++) 
                    {
                        int minIndex = i;
                        for (int j = i + 1; j < spellsCanLearn.length(); j++) 
                        {
                            JSONObject spell1 = spellsCanLearn.getJSONObject(j);
                            JSONObject spell2 = spellsCanLearn.getJSONObject(minIndex);

                            int num1 = spell1.getInt("level");
                            int num2 = spell2.getInt("level");

                            if (num1 > num2 || (num1 == num2 && spell1.getString("name").compareToIgnoreCase(spell2.getString("name")) < 0)) 
                            {
                                minIndex = j;
                            }
                        }
                        Object temp = spellsCanLearn.get(i);
                        spellsCanLearn.put(i, spellsCanLearn.get(minIndex));
                        spellsCanLearn.put(minIndex, temp);
                    }
                } 
                updateFilteredSpellList(spellsCanLearn, spellListVBox, spellFont, selectedSpell2, spellInfoText, statModifier, addButton, spellBook, true);
            }
        });

        VBox sortingVBox = new VBox(5, sortLabel, sortComboBox);
        Label levelLabel = new Label("Filter by Level:");
        levelLabel.setFont(spellFont);

        VBox levelFilters = new VBox(3);
        String[] levels = { "Cantrip", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
        for (String lvl : levels) 
        {
            int lvlInt;
            if (lvl.equals("Cantrip")) 
            {
                lvlInt = 0;
            } 
            else 
            {
                lvlInt = Integer.parseInt(lvl);
            }

            CheckBox levelBox = new CheckBox(lvl);
            levelBox.setFont(spellFont);
            levelBox.setSelected(true); 
            levelBox.setOnAction(new EventHandler<ActionEvent>() 
            {
                public void handle(ActionEvent event) 
                {
                    if (levelBox.isSelected()) 
                    {
                        if (!allowedLevels.contains(lvlInt)) 
                        {
                            allowedLevels.add(lvlInt);
                        }
                    } 
                    else 
                    {
                        allowedLevels.remove((Object) lvlInt);
                    }

                    updateFilteredSpellList(spellsCanLearn, spellListVBox, spellFont, selectedSpell2, spellInfoText, statModifier, addButton, spellBook, true);
                }
            });
            levelFilters.getChildren().add(levelBox);
        }

        TitledPane levelSection = new TitledPane("Filter by Level", levelFilters);
        levelSection.setExpanded(false);

        Label damageLabel = new Label("Filter by Damage Type:");
        damageLabel.setFont(spellFont);

        VBox damageTypeFilters = new VBox(3);

        for (String type : fixedAllowedDmgTypes) 
        {
            CheckBox damageTypeBox = new CheckBox(type);
            damageTypeBox.setFont(spellFont);
            damageTypeBox.setSelected(true);

            damageTypeBox.setOnAction(new EventHandler<ActionEvent>() 
            {
                public void handle(ActionEvent event) 
                {
                    if (damageTypeBox.isSelected()) 
                    {
                        if (!allowedDmgTypes.contains(type)) allowedDmgTypes.add(type);
                    } 
                    else 
                    {
                        allowedDmgTypes.remove(type);
                    }

                    filterByDamageType = !allowedDmgTypes.containsAll(fixedAllowedDmgTypes);
                    updateFilteredSpellList(spellsCanLearn, spellListVBox, spellFont, selectedSpell2, spellInfoText, statModifier, addButton, spellBook, true);

                }
            });
            damageTypeFilters.getChildren().add(damageTypeBox);
        }

        TitledPane damageTypeSection = new TitledPane("Filter by Damage Type", damageTypeFilters);
        damageTypeSection.setExpanded(false);

        Label effectLabel = new Label("Damage Filter:");
        effectLabel.setFont(spellFontSmall);

        ComboBox<String> dmgComboBox = new ComboBox<>();
        dmgComboBox.getItems().addAll("No filter", "Damaging", "Non-Damaging");
        dmgComboBox.setPromptText("Select damage toggle");
        dmgComboBox.setPrefWidth(200);
        spellInfoBox.getStyleClass().add("combo-box");
        dmgComboBox.setOnAction(new EventHandler<ActionEvent>() 
        {
            public void handle(ActionEvent event) 
            {
                String selected = dmgComboBox.getValue();
                if (selected == null || selected.equals("No filter")) 
                {
                    filterByDamage = false;
                    filterByNonDamaging = false;
                } 
                else if (selected.equals("Damaging")) 
                {
                    filterByDamage = true;
                    filterByNonDamaging = false;
                } 
                else if (selected.equals("Non-Damaging")) 
                {
                    filterByDamage = false;
                    filterByNonDamaging = true;
                }
                updateFilteredSpellList(spellsCanLearn, spellListVBox, spellFont, selectedSpell2, spellInfoText, statModifier, addButton, spellBook, true);
            }
        });

        VBox effectTypeSection = new VBox(5, effectLabel, dmgComboBox);
        sidebar.getChildren().addAll(sortingVBox,levelSection,damageTypeSection,effectTypeSection);
        sidebar.getStyleClass().add("sidebar");

        ScrollPane sideBarScrollPane = new ScrollPane(sidebar);
        sideBarScrollPane.setFitToWidth(true);
        sideBarScrollPane.setPrefWidth(300);
        VBox.setVgrow(sideBarScrollPane, Priority.ALWAYS);

        layout2.getChildren().addAll(sidebar, spellListSection, spellInfoBox);

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(10));
        bottom.setAlignment(Pos.CENTER_RIGHT);

        Button back = new Button("Back");
        back.setFont(new Font("Georgia", 20));
        back.getStyleClass().add("spell-button");
        back.setOnAction(new EventHandler<ActionEvent>() 
        {
            public void handle(ActionEvent event) 
            {
                try 
                {
                    SpellSaver.saveData(playerSpells, playerClass, playerLevel, statModifier);
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                allowedLevels = new ArrayList<>(Arrays.asList(allowed));
                allowedDmgTypes = new ArrayList<>(Arrays.asList(allowed2));
                filterByDamage = false;
                filterByNonDamaging = false;
                filterByDamageType = false;

                showMainMenu(stage, spellsCanLearn);
            }
        });

        bottom.getChildren().add(back);
        wholeLayout.setTop(top);
        wholeLayout.setCenter(layout2);
        wholeLayout.setBottom(bottom);

        for (int i = 0; i < spellsCanLearn.length(); i++) 
        {
            JSONObject spellObj = spellsCanLearn.getJSONObject(i);
            Button spellButton = createSpellButton(spellObj, spellFont, selectedSpell2, spellInfoText, statModifier, addButton, playerSpells, spellsCanKnow, spellBook, true);
            spellListVBox.getChildren().add(spellButton);
        }

        Scene scene = new Scene(wholeLayout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
    }

    private void showSpellBook(Stage stage, int statModifier, SpellBook spellBook) 
    {
        BorderPane wholeLayout = new BorderPane();
        wholeLayout.setPadding(new Insets(20));
        wholeLayout.setStyle("-fx-background-color: #baabb8;");

        HBox layout2 = new HBox(20);
        layout2.setPadding(new Insets(20));

        Text spellCountText = new Text("Spells: " + playerSpells.length() + " / " + spellsCanKnow);
        spellCountText.setFont(new Font("Georgia", 16));

        HBox top = new HBox(spellCountText);
        top.setAlignment(Pos.TOP_RIGHT);
        top.setPadding(new Insets(0, 10, 0, 0));

        VBox topContainer = new VBox(10, top);
        topContainer.setPadding(new Insets(0, 10, 10, 10));


        VBox spellListVBox = new VBox(10);
        spellListVBox.setPadding(new Insets(10));

        ScrollPane spellScrollPane = new ScrollPane(spellListVBox);
        spellScrollPane.setFitToWidth(true);
        spellScrollPane.setPrefWidth(300);
        spellScrollPane.getStyleClass().add("spell-scroll");

        Rectangle clip = new Rectangle();
        clip.setArcWidth(30); 
        clip.setArcHeight(30);
        clip.widthProperty().bind(spellScrollPane.widthProperty());
        clip.heightProperty().bind(spellScrollPane.heightProperty());
        spellScrollPane.setClip(clip);
        spellScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        VBox.setVgrow(spellScrollPane, Priority.ALWAYS);

        TextFlow spellInfoText = new TextFlow();
        spellInfoText.setPrefWidth(1000);
        spellInfoText.setLineSpacing(5);


        Button removeButton = new Button("Remove");
        removeButton.setFont(new Font("Georgia", 16));
        removeButton.setDisable(true);
        removeButton.getStyleClass().add("add-button");
 

        VBox.setMargin(removeButton, new Insets(20, 0, 0, 0));

        //for some reason my code wasnt working if I tried to do it like normal. 
        //So i did some digging and found out you had to make a single length array for some reason??
        //I don't fully get it I'm gonna be honest with you but it works.
        JSONObject[] spell2 = new JSONObject[1]; 

        removeButton.setOnAction(new EventHandler<ActionEvent>() 
        {
            public void handle(ActionEvent event) 
            {
               JSONObject spell = spell2[0];
                if (spell != null) 
                {
                    String spellName = spell.getString("name").toLowerCase();
                    boolean found = false;
                    int foundIndex = 0;

                    for (int j = 0; j < playerSpells.length(); j++) 
                    {
                        if (spellName.equals(playerSpells.getJSONObject(j).getString("name").toLowerCase())) 
                        {
                            foundIndex = j;
                            found = true;
                            break;
                        }
                    }

                    if (found) 
                    {
                        playerSpells.remove(foundIndex);
                        removeButton.setDisable(true);

                        //once its removed you have to rebuild the vbox on the left because it doesnt automatically update
                        spellInfoText.getChildren().clear();
                        spell2[0] = null;
                        spellCountText.setText("Spells: " + playerSpells.length() + " / " + spellsCanKnow);
                        spellListVBox.getChildren().clear();
                        for (int i = 0; i < playerSpells.length(); i++) 
                        {
                            JSONObject spellObj = playerSpells.getJSONObject(i);
                            Button spellButton = createSpellButton(spellObj, new Font("Georgia", 16),spell2, spellInfoText, statModifier,removeButton, playerSpells, spellsCanKnow, spellBook,false);
                            spellListVBox.getChildren().add(spellButton);
                        }
                        
                    } 
                }
            }
        });

        VBox spellInfoBox = new VBox(10, spellInfoText, removeButton);
        spellInfoBox.setPadding(new Insets(10));
        spellInfoBox.setPrefWidth(1000);
        spellInfoBox.getStyleClass().add("spell-info");

        layout2.getChildren().addAll(spellScrollPane, spellInfoBox);

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(10));
        bottom.setAlignment(Pos.CENTER_RIGHT);

        Button back = new Button("Back");
        back.setFont(new Font("Georgia", 20));
        back.getStyleClass().add("spell-button");
        back.setOnAction(new EventHandler<ActionEvent>() 
        {
            public void handle(ActionEvent event) 
            {
                try 
                {
                    SpellSaver.saveData(playerSpells, playerClass, playerLevel, statModifier);
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                showMainMenu(stage, spellsCanLearn);
            }
        });

        bottom.getChildren().add(back);
        wholeLayout.setTop(topContainer);
        wholeLayout.setCenter(layout2);
        wholeLayout.setBottom(bottom);

        for (int i = 0; i < playerSpells.length(); i++) 
        {
            JSONObject spellJSon = playerSpells.getJSONObject(i);
            Button spellButton = createSpellButton(spellJSon, new Font("Georgia", 16),spell2, spellInfoText, statModifier,removeButton, playerSpells, spellsCanKnow, spellBook,false);
            spellListVBox.getChildren().add(spellButton);
        }

        Scene scene = new Scene(wholeLayout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
    }

    private void showPlayerStats(Stage stage, int statModifier, SpellBook spellBook)
    {
        Font labelFont = new Font("Georgia", 30);
        Font valueFont = new Font("Georgia", 40);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(30));

        HBox layout2 = new HBox(30);
        layout2.setAlignment(Pos.CENTER);

        String name = spellBook.getPlayerClass().toLowerCase();
        Image img = new Image(getClass().getResourceAsStream("/images/" + name + ".jpg"), 350, 350, true, true);
        ImageView imgView = new ImageView(img);
        Rectangle round = new Rectangle(350, 350);
        round.setArcWidth(20);
        round.setArcHeight(20);
        imgView.setClip(round);

        VBox playerVbox = new VBox(imgView);
        playerVbox.getStyleClass().add("menu-option");
        playerVbox.setPrefHeight(350);
        playerVbox.setPrefWidth(350);
        VBox.setVgrow(playerVbox, Priority.NEVER);
        playerVbox.setAlignment(Pos.CENTER);
        playerVbox.setMaxHeight(350);


        Text classText = new Text("Class: " + SpellUtility.capitalizeString(spellBook.getPlayerClass()));
        classText.setFont(valueFont);
        HBox classHBox = new HBox(classText);
        classHBox.setAlignment(Pos.CENTER_LEFT);
        classHBox.setPadding(new Insets(10));
        classHBox.getStyleClass().add("stat-label");


        Text txt = new Text("Level: " + spellBook.getPlayerLevel());
        txt.setFont(valueFont);
        HBox levelHBox = new HBox(txt);
        levelHBox.setAlignment(Pos.CENTER_LEFT);
        levelHBox.setPadding(new Insets(10));
        levelHBox.getStyleClass().add("stat-label");

        int[] statMod = {statModifier}; 
        Text statText = new Text("Stat Modifier: +" + statMod[0]);
        statText.setFont(valueFont);
        HBox statHBox = new HBox(statText);
        statHBox.setAlignment(Pos.CENTER_LEFT);
        statHBox.setPadding(new Insets(10));
        statHBox.getStyleClass().add("stat-label");

        VBox infoBox = new VBox(15);
        infoBox.getChildren().addAll(classHBox, levelHBox, statHBox);
        infoBox.setAlignment(Pos.CENTER);
    

        HBox hBox = new HBox(20);
        hBox.setAlignment(Pos.CENTER);

        Button lvlUp = new Button("Level Up");
        lvlUp.setFont(labelFont);
        lvlUp.getStyleClass().add("brown-button");
        lvlUp.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent event)
            {
                if (spellBook.getPlayerLevel() < 20)
                {
                    spellBook.levelUp();
                    playerLevel = spellBook.getPlayerLevel(); 
                    txt.setText("Level: " + spellBook.getPlayerLevel());
                    spellsCanKnow = spellBook.spellsKnownCalculator(statModifier);
                    spellsCanLearn = new JSONArray();
                    for (int i = 0; i < results.length(); i++) 
                    {
                        JSONObject item = results.getJSONObject(i);
                        String url = "https://www.dnd5eapi.co" + item.getString("url");
                        try 
                        {
                            String urlString = SpellBook.getData(url);
                            JSONObject spell = new JSONObject(urlString);
                            JSONArray classesThatLearn = spell.getJSONArray("classes");
                            int spellLevel = spell.getInt("level");
                            if (spellBook.hasValue(classesThatLearn, "index", playerClass) && spellBook.getSpellLevel() >= spellLevel) 
                            {
                                spellsCanLearn.put(spell);
                            }
                        } 
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        Button changeStatButton = new Button("Change Stat Modifier");
        changeStatButton.setFont(labelFont);
        changeStatButton.getStyleClass().add("brown-button");
        changeStatButton.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent event)
            {
                Stage statStage = new Stage();
                VBox inputLayout = new VBox(15);
                inputLayout.setPadding(new Insets(20));
                inputLayout.setAlignment(Pos.CENTER);

                Label prompt = new Label("Select new stat modifier:");
                ComboBox<Integer> modifierDropdown = new ComboBox<>();
                for (int i = 0; i <= 5; i++) 
                {
                    modifierDropdown.getItems().add(i);
                }

                Button submit = new Button("Submit");

                inputLayout.getChildren().addAll(prompt, modifierDropdown, submit);
                Scene inputScene = new Scene(inputLayout, 300, 160);
                statStage.setScene(inputScene);
                statStage.setTitle("Change Stat Modifier");
                statStage.show();

                submit.setOnAction(new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent e)
                    {
                        Integer selectedMod = modifierDropdown.getValue();      
                        statMod[0] = selectedMod;
                        statText.setText("Stat Modifier: +" + statMod[0]);
                        statStage.close();
                    }
                });
            }
        });

        Button backButton = new Button("Back");
        backButton.setFont(labelFont);
        backButton.getStyleClass().add("brown-button");
        backButton.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent event)
            {
                try 
                {
                    SpellSaver.saveData(playerSpells, playerClass, playerLevel, statModifier);
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                showMainMenu(stage, spellsCanLearn);
            }
        });
        hBox.getChildren().addAll(lvlUp, changeStatButton, backButton);
        layout2.getChildren().addAll(playerVbox, infoBox);
        HBox.setHgrow(playerVbox, Priority.NEVER);

        layout2.setStyle("-fx-background-color: #baabb8;");
        layout2.setAlignment(Pos.CENTER); 
        
        root.setCenter(layout2);
        root.setBottom(hBox);
        BorderPane.setMargin(hBox, new Insets(30, 0, 0, 0));

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setTitle("Player Stats");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }
    
    //I had this code snippet a lot of times in my code so I just made it a method, it builds a spell button based on the given info
    private Button createSpellButton(JSONObject spell, Font font, JSONObject[] selectedSpellHolder, TextFlow spellInfoText, int statModifier, Button actionButton, JSONArray playerSpells, int spellsCanKnow, SpellBook spellBook, boolean add) 
    {
        String spellName = spell.getString("name");
        Button spellButton = new Button(spellName);
        spellButton.getStyleClass().add("spell-button");
        spellButton.setMaxWidth(Double.MAX_VALUE);
        spellButton.setFont(font);
        spellButton.setOnAction(new EventHandler<ActionEvent>() 
        {
            public void handle(ActionEvent event) 
            {
                selectedSpellHolder[0] = spell;
                List<Text> spellInfo = new ArrayList<>();

                try 
                {
                    spellInfo = spellBook.displaySpellInfoReturnsStringBolded(spell, statModifier);
                } 
                catch (Exception e) 
                {
                    e.printStackTrace();
                }

                spellInfoText.getChildren().clear();
                spellInfoText.getChildren().addAll(spellInfo);

                String selectedName = spell.getString("name").toLowerCase();
                boolean alreadyKnown = false;

                for (int i = 0; i < playerSpells.length(); i++) 
                {
                    if (playerSpells.getJSONObject(i).getString("name").toLowerCase().equals(selectedName)) 
                    {
                        alreadyKnown = true;
                        break;
                    }
                }

                if (add) 
                {
                    if (alreadyKnown || playerSpells.length() >= spellsCanKnow) 
                    {
                        actionButton.setDisable(true);
                    } 
                    else 
                    {
                        actionButton.setDisable(false);
                    }
                } 
                else 
                {
                    actionButton.setDisable(false);
                }
            }
        });

        return spellButton;
    }

    //I had this code snippet a lot of times in my code so I just made it a method, it adds the spells that pass the filters to the UI
    private void updateFilteredSpellList(JSONArray spells, VBox spellListVBox, Font spellFont, JSONObject[] selectedSpell2, TextFlow spellInfoText, int statModifier, Button actionButton, SpellBook spellBook, boolean add) 
    {
        spellListVBox.getChildren().clear();

        for (int i = 0; i < spells.length(); i++) 
        {
            JSONObject item = spells.getJSONObject(i);
            String url = "https://www.dnd5eapi.co" + item.getString("url");

            JSONObject spell = null;
            try 
            {
                String urlString = SpellBook.getData(url);
                spell = new JSONObject(urlString);
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
                continue;
            }

            int spellLevel = spell.getInt("level");
            boolean passesLevel = allowedLevels.contains(spellLevel);
            boolean passesDamageFilter = !(filterByDamage && !spell.has("damage"));
            boolean passesNonDamageFilter = !(filterByNonDamaging && spell.has("damage"));
            boolean passesDamageTypeFilter = true;
            if (filterByDamageType) 
            {
                if (spell.has("damage") && spell.getJSONObject("damage").has("damage_type")) 
                {
                    String dmgType = spell.getJSONObject("damage").getJSONObject("damage_type").getString("name");
                    passesDamageTypeFilter = allowedDmgTypes.contains(dmgType);
                } 
                else 
                {
                    passesDamageTypeFilter = false;
                }
            }

            if (passesLevel && passesDamageFilter && passesNonDamageFilter && passesDamageTypeFilter) 
            {
                Button spellButton = createSpellButton(spell, spellFont, selectedSpell2, spellInfoText, statModifier, actionButton, playerSpells, spellsCanKnow, spellBook, add);
                spellListVBox.getChildren().add(spellButton);
            }
        }
        
    }

    
}
