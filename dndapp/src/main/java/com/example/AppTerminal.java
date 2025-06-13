package com.example;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Hello world!
 *
 */
public class AppTerminal
{
    public static void main(String[] args) throws Exception
    {
        //Initializing and declaring variables:
        Scanner scan = new Scanner(System.in);
        //Accesses the endpoint of the API.
        String endpoint = "https://www.dnd5eapi.co/api/2014/spells";
        String jsonString = SpellBook.getData(endpoint);
        JSONObject json = new JSONObject(jsonString);

        //Initializes a JSONArray that contains a list of all the spells.
        //Each spell in results has a name, index, level, and URL with more detailed information.
        JSONArray results = json.getJSONArray("results");
        //Initializes new JSONArrays to keep track of the spells and the spells the player can learn. These will be declared later
        JSONArray playerSpells = new JSONArray();
        JSONArray spellsCanLearn = new JSONArray();
        //Initializes a string to represent the class of the player.
        String playerClass = "";
        //Integers to represent the level and stat modifier of the player.
        int playerLevel = 0;
        int statModifier = 0;
        //Creates a new file with a pathname of output.txt.
        File file = new File("output.txt");
        //These lists will eventually be used for filtering.
        //Allowed is for the possible spell levels 0-9, and allowed2 is for the damage types
        Integer[] allowed = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        String[] allowed2 = {"Acid", "Bludgeoning", "Cold", "Fire", "Force", "Lightning", "Necrotic", "Piercing", "Poison", "Psychic", "Radiant", "Slashing", "Thunder"};
        //Allowed levels and allowedDmgTypes changes, fixedAllowedDmgTypes does not. I used an arraylist despite it not changing so i could use .contains().
        ArrayList<Integer> allowedLevels = new ArrayList<Integer>(Arrays.asList(allowed));
        ArrayList<String> allowedDmgTypes = new ArrayList<String>(Arrays.asList(allowed2));
        ArrayList<String> fixedAllowedDmgTypes = new ArrayList<String>(Arrays.asList(allowed2));
        ArrayList<String> loadedSpells = new ArrayList<String>();

        //Boolean to keep track of if the player has loaded a file.
        boolean load = false;
        //Booleans to keep track of if filtering is enabled.
        boolean filterByDamageType = false;
        boolean filterByDamage = false;
        boolean filterByNonDamaging = false;

        //Checks to see if there is a save file that exists and has a length above 0 (so, there's something in the file).
        if (file.exists() && file.length() > 0)
        {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Saved data found. Do you want to load it? (yes/no): ");
            String response = scanner.nextLine().toLowerCase();

            //If the player types yes, initializes a new SpellLoader which will load the file.
            //The reason I made SpellLoader's constructor load the file is so I could access the instance variables through getter methods.
            if (response.equals("yes"))
            {
                SpellLoader s = new SpellLoader();
                //Sets load to true to indicate that a file has been loaded in the program.
                load = true;

                //Sets the data of the player to the variables loaded by the SpellLoader constructor
                playerClass = s.getLoadClass();
                playerLevel = s.getLevel();
                statModifier = s.getModifier();
                loadedSpells = s.getSpells();
            }
            else
            {
                //Sets the data of the player to data that they inputted into the program.
                System.out.println("What class are you?: ");
                playerClass = scan.nextLine().toLowerCase();
                System.out.println("What level are you?: ");
                playerLevel = scan.nextInt();
                scan.nextLine();
                System.out.println("What is your spellcasting stat modifier (so 1, 2, 3, etc)? (For example, Wizards use INT as their spellcasting stat): ");
                statModifier = scan.nextInt();
                scan.nextLine();
            }
        }

        //Initializes a new spellbook with the inputted class and level.
        SpellBook spellBook = new SpellBook(playerClass, playerLevel);
        //Calculates how many spells the player can know based on their class and stat modifier.
        int spellsCanKnow = spellBook.spellsKnownCalculator(statModifier);

        //Iterates through each JSONObject in results and adds the spells to the spellsCanLearn JSONArray.
        for(int i=0;i<results.length();i++)
        {
            JSONObject item = (JSONObject) results.get(i);
            String temp= item.getString("url");
            String url = "https://www.dnd5eapi.co";
            url+=temp;
            String urlString = SpellBook.getData(url);
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

        //Checks to see if something was loaded. If so, iterates through loadedSpells and
        //I had to put this down here because I needed to initialize spellsCanLearn before doing this code segment.
        //Technically I could've done it with results instead but as results has 319 objects that wouyld have taken ridiculously long.
        //So, the load variable just makes sure that loadedSpells, you know, has things in it and exists.
        if (load)
        {
            //Searches through the spells the player can learn using the indices from loaded spells and adds them to the list.
            for (String str : loadedSpells)
            {
                int found = SpellSearch.searchIndex(spellsCanLearn, str);
                playerSpells.put(spellsCanLearn.get(found));
            }
        }
        //Begins the main program, which is a while loop.
        while (true)
        {
            //Players input different numbers depending on what they want to do.
            //1 is to see available spells so the spells that you can learn
            //2 is to add said spells to your spellbook
            //3 is to view your spells
            //4 opens a sorting menu
            //5 opens a filtering menu
            //6 allows you to search
            //7 allows you to go to your stats
            //8 breaks the loop
            System.out.println("1. See available spells\n2. Add to your spellbook\n3. Remove from your spellbook\n4. View your spellbook\n5. Sort available spells\n6. Filter available spells\n7. Search for a spell\n8. Go to your stats\n9. Quit");

            //Keeps track of the choice of the player.
            int playerChoice = scan.nextInt();
            scan.nextLine();
            //Prints the available spells that the player can learn.
            if (playerChoice == 1)
            {
                for(int j=0;j<spellsCanLearn.length();j++)
                {
                    JSONObject item = (JSONObject)spellsCanLearn.get(j);
                    String temp= item.getString("url");
                    String url = "https://www.dnd5eapi.co";
                    url+=temp;
                    String urlString = SpellBook.getData(url);
                    JSONObject spell = new JSONObject(urlString);
                    int spellLevel = spell.getInt("level");
                    //Checks for certain filter toggles and adds filtering based on what the player selected in the filter menu.
                    if (allowedLevels.contains(spellLevel) && (!filterByDamage || spell.has("damage")) && (!filterByNonDamaging || !spell.has("damage")) && (!filterByDamageType || (spell.has("damage") && spell.getJSONObject("damage").has("damage_type") && allowedDmgTypes.contains(spell.getJSONObject("damage").getJSONObject("damage_type").getString("name")))))
                    {
                        //Calls for display Spell Info to print the spell nicely.
                        spellBook.displaySpellInfo(item, statModifier);
                    }
                }
            }
            //Adds spells to your spellbook.
            else if (playerChoice == 2)
            {
                System.out.println("State the name of the spell you want to add to your known spells");
                //Converts it to lowercase so case sensitivity is not considered.
                String spellName = scan.nextLine().toLowerCase();
                boolean found = false;
                int foundIndex = 0;
                for(int j=0;j<spellsCanLearn.length();j++)
                {
                    if (spellName.equals(((JSONObject) spellsCanLearn.get(j)).getString("name").toLowerCase()))
                    {
                        foundIndex = j;
                        found = true;
                        break;
                    }
                }
                if (found)
                {
                    //Checks to make sure the player can add any more spells to their spellbook by comparing their current to spellsCanKnow.
                    if (playerSpells.length() < spellsCanKnow)
                    {
                        System.out.println("Adding " + ((JSONObject)spellsCanLearn.get(foundIndex)).getString("name") + " to your spells...");
                        playerSpells.put(spellsCanLearn.get(foundIndex));
                    }
                    else
                    {
                        System.out.println("You can't learn any more spells at your current level.");
                    }
                }
                else
                {
                    System.out.println("Spell not found...");
                }
            }
            else if (playerChoice == 3)
            {
                System.out.println("Your current spells: ");
                for(int j=0;j<playerSpells.length();j++)
                {
                    JSONObject item = (JSONObject)playerSpells.get(j);
                    String temp= item.getString("url");
                    String url = "https://www.dnd5eapi.co";
                    url+=temp;
                    String urlString = SpellBook.getData(url);
                    JSONObject spell = new JSONObject(urlString);
                    System.out.println((j+1) + ": " + spell.getString("name"));
                }
                System.out.println("State the name of the spell you want to remove from your known spells");
                //Converts it to lowercase so case sensitivity is not considered.
                String spellName = scan.nextLine().toLowerCase();
                boolean found = false;
                int foundIndex = 0;
                for(int j=0;j<playerSpells.length();j++)
                {
                    if (spellName.equals(((JSONObject) playerSpells.get(j)).getString("name").toLowerCase()))
                    {
                        foundIndex = j;
                        found = true;
                        break;
                    }
                }
                if (found)
                {
                    playerSpells.remove(foundIndex);
                    System.out.println("Spell removed.");
                }
                else
                {
                    System.out.println("Spell not found...");
                }
            }
            else if (playerChoice == 4)
            {
                //Nicely displays the spells that they have in their spellbook and its info.
                System.out.println();
                System.out.println("Spells Known: " + playerSpells.length() + "/" + spellsCanKnow);
                System.out.println("Your Spells: ");
                System.out.println();
                for(int j=0;j<playerSpells.length();j++)
                {
                    JSONObject item = (JSONObject)playerSpells.get(j);
                    //Calls displaySpellInfo to print the information cleanly.
                    spellBook.displaySpellInfo(item, statModifier);
                }
            }
            else if (playerChoice == 5)
            {
                //Allows the player to sort by both level and range.
                System.out.println("1. Sort by level, highest to lowest\n2. Sort by range, highest to lowest");
                int whichSort = scan.nextInt();
                if (whichSort == 1)
                {
                    //Uses a selection sort algorithm to sort by level and alphabetical order.
                    for (int i = 0; i < spellsCanLearn.length() - 1; i++)
                    {
                        int minIndex = i;
                        for (int j = i + 1; j < spellsCanLearn.length(); j++)
                        {

                            JSONObject spell1 = (JSONObject)spellsCanLearn.get(j);
                            JSONObject spell2 = (JSONObject)spellsCanLearn.get(minIndex);

                            int num1 = spell1.getInt("level");
                            int num2 = spell2.getInt("level");

                            if (num1 > num2 ||  (num1 == num2 && spell1.getString("name").compareToIgnoreCase(spell2.getString("name")) < 0))
                            {
                                minIndex = j;
                            }
                        }
                        //Swaps the object at i and the object at minIndex.
                        Object temp = spellsCanLearn.get(i);
                        spellsCanLearn.put(i, spellsCanLearn.get(minIndex));
                        spellsCanLearn.put(minIndex, temp);
                    }
                }

                else if (whichSort == 2)
                {
                    //Uses a selection sort algorithm to sort by level and alphabetical order.
                    for (int i = 0; i < spellsCanLearn.length() - 1; i++)
                    {
                        int minIndex = i;
                        for (int j = i + 1; j < spellsCanLearn.length(); j++)
                        {

                            JSONObject spell1 = (JSONObject)spellsCanLearn.get(j);
                            JSONObject spell2 = (JSONObject)spellsCanLearn.get(minIndex);

                            int num1 = SpellUtility.rangeInt(spell1.getString("range"));
                            int num2 = SpellUtility.rangeInt(spell2.getString("range"));

                            if (num1 > num2 ||  (num1 == num2 && spell1.getString("name").compareToIgnoreCase(spell2.getString("name")) < 0))
                            {
                                minIndex = j;
                            }
                        }
                        //Swaps the object at i and the object at minIndex.
                        Object temp = spellsCanLearn.get(i);
                        spellsCanLearn.put(i, spellsCanLearn.get(minIndex));
                        spellsCanLearn.put(minIndex, temp);
                    }
                }
            }
            else if (playerChoice == 6)
            {
                //Allows the player to filter their available spells.
                while (true)
                {
                    System.out.println("Filter toggles:\n1. Filter by level\n2. Filter by damaging vs non-damaging spells\n3. Filter by damage type\n4. Remove filters\n5. Quit");
                    int choice = scan.nextInt();
                    if (choice == 1)
                    {
                        while (true)
                        {
                            System.out.println("Levels to display (All are toggled on by default). Checkmark means you have them toggled on, X means they're toggled off.");
                            System.out.println("Type the number to disable/enable it, for cantrip type 0, to quit type -1");
                            //Displays the current allowed levels for the spells by iterating through allowedLevels.
                            for (int i = 0; i <= 9; i++)
                            {
                                if (allowedLevels.contains(i))
                                {
                                    if (i == 0)
                                    {
                                        System.out.println("Cantrip: ✅");
                                    }
                                    else
                                    {
                                        System.out.println(i + ": ✅");
                                    }
                                }
                                else
                                {
                                    if (i == 0)
                                    {
                                        System.out.println("Cantrip: ❌");
                                    }
                                    else
                                    {
                                        System.out.println(i + ": ❌");
                                    }
                                }
                            }

                            Integer choiceToRemove = scan.nextInt();
                            //Removes choiceToRemove from allowedLevels if it is between 0-9.
                            //choiceToRemove is casted as an Object to avoid it being interpreted as an index.
                            if (choiceToRemove >= 0 && choiceToRemove <= 9)
                            {
                                if (allowedLevels.contains(choiceToRemove))
                                {
                                    //Toggles off if the level is toggled on.
                                    allowedLevels.remove((Object)choiceToRemove);
                                }
                                else
                                {
                                    //Toggles on if the level is toggled off.
                                    allowedLevels.add(choiceToRemove);
                                }
                            }
                            else if (choiceToRemove == -1)
                            {
                                break;
                            }
                        }
                    }
                    //Allows players to filter damaging and non damaging spells.
                    else if (choice == 2)
                    {
                        while (true)
                        {
                            //Displays which spell types are and aren't allowed.
                            if (filterByDamage)
                            {
                                System.out.println("Damaging spells: ✅");
                                System.out.println("Non-damaging spells: ❌");
                            }
                            else if (filterByNonDamaging)
                            {
                                System.out.println("Damaging spells: ❌");
                                System.out.println("Non-damaging spells: ✅");
                            }
                            else
                            {
                                System.out.println("Damaging spells: ✅");
                                System.out.println("Non-damaging spells: ✅");
                            }

                            //Gives the player a choice of what type of spells they want to display and sets the filter variables depending on their choice.
                            System.out.println("If you want to only display damaging spells, enter 1\n If you want to only display non-damaging spells, enter 2\nIf you want to display all types of spells, enter 3\nIf you want to exit enter -1");
                            int playerDmgChoice = scan.nextInt();
                            if (playerDmgChoice == 1)
                            {
                                filterByDamage = true;
                                filterByNonDamaging = false;
                            }
                            else if (playerDmgChoice == 2)
                            {
                                filterByDamage = false;
                                filterByNonDamaging = true;
                            }
                            else if (playerDmgChoice == 3)
                            {
                                filterByDamage = false;
                                filterByNonDamaging = false;
                            }
                            else if (playerDmgChoice == -1)
                            {
                                break;
                            }
                        }
                    }

                    //Filters by damage type.
                    else if (choice == 3)
                    {
                        scan.nextLine();
                        while (true)
                        {
                            //Checks if a damage type is filtered. If no filters are applied, so all allowed damage types are in allowedDmgTypes, then sets filterByDamageType to false.
                            //If an allowed damage type is missing from allowedDmgType that means a filter has been applied, which means filterByDamageType is true.
                            if (allowedDmgTypes.containsAll(fixedAllowedDmgTypes))
                            {
                                filterByDamageType = false;
                            }
                            else
                            {
                                filterByDamageType = true;
                            }

                            //Iterates through to print which damage types have been toggled on and off using .contains().
                            for (int i = 0; i < fixedAllowedDmgTypes.size(); i++)
                            {
                                if (allowedDmgTypes.contains(fixedAllowedDmgTypes.get(i)))
                                {
                                    System.out.println(fixedAllowedDmgTypes.get(i) + ": ✅");
                                }
                                else
                                {
                                    System.out.println(fixedAllowedDmgTypes.get(i) + ": ❌");
                                }
                            }
                            System.out.println("Enter the damage type to disable/enable it, to quit type -1");
                            String playerDmgTypeChoice = scan.nextLine();
                            //Capitalizes the string, in case the player enters a lowercase spell name.
                            //All spell damage types are capitalized after all.
                            playerDmgTypeChoice = SpellUtility.capitalizeString(playerDmgTypeChoice);

                            //Removes the choice from allowedDmgTypes if it's in fixedAllowedDmgTypes and is also in allowedDmgTypes.
                            //If it's in fixedAllowedDmgTypes but not in allowedDmgTypes, adds it back.
                            if (fixedAllowedDmgTypes.contains(playerDmgTypeChoice))
                            {
                                if (allowedDmgTypes.contains(playerDmgTypeChoice))
                                {
                                    allowedDmgTypes.remove(playerDmgTypeChoice);
                                }
                                else
                                {
                                    allowedDmgTypes.add(playerDmgTypeChoice);
                                }
                            }
                            else if (playerDmgTypeChoice.equals("-1"))
                            {
                                break;
                            }
                        }
                    }
                    else if (choice == 4)
                    {
                        //Clears all filters by setting them back to default.
                        filterByDamage = false;
                        filterByNonDamaging = false;
                        filterByDamageType = false;
                        ArrayList<Integer> newAllowedLvls = new ArrayList<Integer>(Arrays.asList(allowed));
                        allowedLevels = newAllowedLvls;
                        ArrayList<String> newAllowedDmgTypes = new ArrayList<String>(Arrays.asList(allowed2));
                        allowedDmgTypes = newAllowedDmgTypes;

                    }
                    else if (choice == 5)
                    {
                        break;
                    }
                }
            }
            else if (playerChoice == 7)
            {
                while (true)
                {
                    //Allows you to search for a spell , either your available spells or all spells.
                    System.out.println("1. Search available spells\n2. Search all spells \n3. Quit ");
                    int c = scan.nextInt();
                    scan.nextLine();

                    if (c == 1)
                    {
                        //The variable that tracks to see if any spells were added to searchSpells.
                        int count = 0;
                        System.out.println("Search bar: ");
                        //Converts findSpell to a index so it can be searched for.
                        String findSpell = scan.nextLine();
                        findSpell = SpellUtility.convertToIndex(findSpell);
                        System.out.println();
                        //Searches through the spells and adds them to searchSpells.
                        JSONArray searchSpells = SpellSearch.search2(spellsCanLearn, findSpell);
                        for(int j=0;j<searchSpells.length();j++)
                        {
                            //Iterates through searchSpells and prints nicely.
                            JSONObject item = (JSONObject)searchSpells.get(j);
                            {
                                System.out.println((j+1) + ". " + item.getString("name"));
                                count ++;
                            }
                        }
                        if (count > 0)
                        {
                            while (true)
                            //Allows the player to select which spell they want to view in detail.
                            {
                                System.out.println("Which of these spells did you want to view? (Enter the name) or -1 to quit.");
                                findSpell = scan.nextLine().toLowerCase();
                                if (!(findSpell.equals("-1")))
                                {
                                    System.out.println();
                                    //Displays the info of the selected spell.
                                    int foundIndex = SpellSearch.searchName(spellsCanLearn, findSpell);

                                    //Checks to see if the index isn't -1 (so checks to see if it's found)
                                    if (foundIndex != -1)
                                    {
                                        spellBook.displaySpellInfo((JSONObject)spellsCanLearn.get(foundIndex), statModifier);
                                    }
                                    else
                                    {
                                        System.out.println("Spell not found...");
                                    }
                                }
                                else
                                {
                                    break;
                                }
                            }
                        }
                    }
                    //Does the same thing as above, but with results instead of spellsCanLearn.
                    else if (c == 2)
                    {
                        //The variable that tracks to see if any spells were added to searchSpells.
                        int count = 0;
                        System.out.println("Search bar: ");
                        //Converts findSpell to a index so it can be searched for.
                        String findSpell = scan.nextLine();
                        findSpell = SpellUtility.convertToIndex(findSpell);
                        System.out.println();
                        //Searches through the spells and adds them to searchSpells.
                        JSONArray searchSpells = SpellSearch.search2(results, findSpell);
                        for(int j=0;j<searchSpells.length();j++)
                        {
                            //Iterates through searchSpells and prints nicely.
                            JSONObject item = (JSONObject)searchSpells.get(j);
                            {
                                System.out.println((j+1) + ". " + item.getString("name"));
                                count ++;
                            }
                        }
                        if (count > 0)
                        {
                            while (true)
                            //Allows the player to select which spell they want to view in detail.
                            {
                                System.out.println("Which of these spells did you want to view? (Enter the name) or -1 to quit.");
                                findSpell = scan.nextLine().toLowerCase();
                                if (!(findSpell.equals("-1")))
                                {
                                    System.out.println();
                                    //Displays the info of the selected spell.
                                    int foundIndex = SpellSearch.searchName(results, findSpell);

                                    //Checks to see if the index isn't -1 (so checks to see if it's found)
                                    if (foundIndex != -1)
                                    {
                                        spellBook.displaySpellInfo((JSONObject)results.get(foundIndex), statModifier);
                                    }
                                    else
                                    {
                                        System.out.println("Spell not found...");
                                    }
                                }
                                else
                                {
                                    break;
                                }
                            }
                        }
                    }
                    else if (c == 3)
                    {
                        break;
                    }
                }
            }

            else if (playerChoice == 8)
            {
                //Prints the information of the player.
                System.out.println("Class: " + spellBook.getPlayerClass());
                System.out.println("Level: " + spellBook.getPlayerLevel());
                System.out.println("Stat Modifier: " + statModifier);

                //Allows the player to level up or change their stat modifier. The max level is of course 20.
                System.out.println("Would you like to edit anything? (yes/no)");
                String lvlChoice = scan.nextLine().toLowerCase();
                if (lvlChoice.equals("yes"))
                {
                    System.out.println("1. Level up\n2. Change stat modifier");
                    lvlChoice = scan.nextLine();
                    //Allows the player to level up if it wouldn't make them above level 20.
                    if (lvlChoice.equals("1") && spellBook.getPlayerLevel() < 20)
                    {
                        spellBook.levelUp();
                    }
                    //Changes the player's stat modifier.
                    else if (lvlChoice.equals("2"))
                    {
                        System.out.println("What would you like to change your stat modifier to? (From 0-5)");
                        int newStatModifier = scan.nextInt();
                        if (newStatModifier >= 0 && newStatModifier <= 5)
                        {
                            statModifier = newStatModifier;
                        }
                        else
                        {
                            System.out.println("That's not a valid stat modifier.");
                        }
                    }
                }
                System.out.println();


            }
            else if (playerChoice == 9)
            {
                break;
            }
            SpellSaver.saveData(playerSpells, playerClass, playerLevel, statModifier);
        }
    }
}

