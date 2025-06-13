package com.example;

//Imports
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Iterator;

public class SpellBook
{
    //Instance variables for player class, level, and their spells.
    private String playerClass;
    private int playerLevel;
    private ArrayList<String> spells;

    //Constructor that sets the instance variables to the parameters.
    public SpellBook(String playerClass, int playerLevel)
    {
        this.playerClass = playerClass;
        this.playerLevel = playerLevel;
    }

    //Getter methods
    public String getPlayerClass(){return playerClass;}
    public int getPlayerLevel(){return playerLevel;}
    public ArrayList<String> getPlayerStatModifier(){return spells;}

    //Setter methods
    public void levelUp(){playerLevel ++;}
    public void setLevel(int newLevel){playerLevel = newLevel;}

    public static String getData(String endpoint) throws Exception
    {

        /*endpoint is a url (string) that you get from an API website*/
        URL url = new URL(endpoint);
        /*connect to the URL*/
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        /*creates a GET request to the API.. Asking the server to retrieve information for our program*/
        connection.setRequestMethod("GET");
        /* When you read data from the server, it wil be in bytes, the InputStreamReader will convert it to text.
        The BufferedReader wraps the text in a buffer so we can read it line by line*/
        BufferedReader buff = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;//variable to store text, line by line
        /*A string builder is similar to a string object but faster for larger strings,
        you can concatenate to it and build a larger string. Loop through the buffer
        (read line by line). Add it to the stringbuilder */
        StringBuilder content = new StringBuilder();
        while ((inputLine = buff.readLine()) != null) {
            content.append(inputLine);
        }
        buff.close(); //close the bufferreader
        connection.disconnect(); //disconnect from server
        Thread.sleep(15);
        return content.toString(); //return the content as a string
    }
    // Modified version of the top answer of https://stackoverflow.com/questions/68401511/how-to-check-if-a-value-exists-in-a-json-array-for-a-particular-key
    // Checks if a value exists in a json array for a particular key.
    public boolean hasValue(JSONArray json, String key, String value)
    {
        for(int i = 0; i < json.length(); i++)
        {
            JSONObject c = (JSONObject) json.get(i);
            String s = c.getString(key);
            if(s.equals(value))
            {
                return true;
            }
        }
        return false;
    }

    // In Dungeons & Dragons, spell levels range from Cantrip, the lowest, to 9, the highest.
    // The maximum level spell you can use is determined by your level in a class.
    // For example, max-level wizards can cast spells all the way from Cantrip to 9, and a level 10 wizard can cast from Cantrip to 5.
    // Returns the maximum level spells that a player can use.
    // Here is a page displaying the spell levels of "full casters" : https://roll20.net/compendium/dnd5e/Wizard#content
    // As you can see, for example, at level 4 a wizard would be able to cast level 2 and level 1 spells.
    // And then here is an example for "half casters" : https://roll20.net/compendium/dnd5e/Paladin#content
    // As you can see, a level 4 paladin would be able to cast only level 1 spells.
    public int getSpellLevel()
    {
        //Checks to see if the player is one of the half-caster classes, paladin or ranger.
        //If so, simply codes an equivalent of the table in this page https://roll20.net/compendium/dnd5e/Paladin#content
        //where it returns a spell level based on what level the player is.
        if (playerClass.equals("ranger") || playerClass.equals("paladin"))
        {
            if (playerLevel == 1){return 0;}
            else if (playerLevel <= 4){return 1;}
            else if (playerLevel <= 8){return 2;}
            else if (playerLevel <= 12){return 3;}
            else if (playerLevel <= 16){return 4;}
            else {return 5;}
        }

        //Checks to see if the player is one of the full-caster classes, so every caster that isn't paladin or ranger
        //If so, simply codes an equivalent of the table in this page https://roll20.net/compendium/dnd5e/Wizard#content
        //where it returns a spell level based on what level the player is.
        else
        {
            if (playerLevel <= 2){return 1;}
            else if (playerLevel <= 4){return 2;}
            else if (playerLevel <= 6){return 3;}
            else if (playerLevel <= 8){return 4;}
            else if (playerLevel <= 10){return 5;}
            else if (playerLevel <= 12){return 6;}
            else if (playerLevel <= 14){return 7;}
            else if (playerLevel <= 16){return 8;}
            else {return 9;}
        }
    }

    // In Dungeons & Dragons, the maximum amount of spells a caster can know depends on their class.
    // Wizards, Druids and Clerics can prepare their level + their stat modifier.
    // Paladins, being half casters, can prepare their level divided by two + their stat modifier
    public int spellsKnownCalculator(int statModifier)
    {
        int spellsKnown = 0;
        if (playerClass.toLowerCase().equals("wizard") || playerClass.toLowerCase().equals("druid") || playerClass.toLowerCase().equals("cleric"))
        {
            spellsKnown = statModifier + playerLevel;
        }
        //Uses https://www.dndbeyond.com/classes/4-paladin to determine how many spells a paladin can learn.
        else if (playerClass.toLowerCase().equals("paladin"))
        {
            spellsKnown = statModifier + playerLevel/2;
        }
        //Uses https://www.dndbeyond.com/classes/6-sorcerer to determine how many spells a sorcerer can learn.
        else if (playerClass.toLowerCase().equals("sorcerer"))
        {
            if (playerLevel <= 11){spellsKnown = playerLevel + 1;}
            else if (playerLevel == 12){spellsKnown = 12;}
            else if (playerLevel == 13){spellsKnown = 13;}
            else if (playerLevel <= 15){spellsKnown = playerLevel - 1;}
            else if (playerLevel == 15){spellsKnown = 15;}
            else{spellsKnown = 15;}
        }
        //Uses https://www.dndbeyond.com/classes/1-bard to determine how many spells a bard can learn.
        else if (playerClass.toLowerCase().equals("bard"))
        {
            if (playerLevel <= 9){spellsKnown = playerLevel + 3;}
            else if (playerLevel == 10){spellsKnown = 14;}
            else if (playerLevel <= 12){spellsKnown = 15;}
            else if (playerLevel == 13){spellsKnown = 16;}
            else if (playerLevel == 14){spellsKnown = 18;}
            else if (playerLevel <= 16){spellsKnown = 19;}
            else if (playerLevel == 17){spellsKnown = 20;}
            else{spellsKnown = 22;}
        }
        //Uses https://www.dndbeyond.com/classes/5-ranger to determine how spells a ranger can learn.
        else if (playerClass.toLowerCase().equals("ranger"))
        {
            if (playerLevel == 1){spellsKnown = 0;}
            else if (playerLevel <= 3){spellsKnown = playerLevel;}
            else if (playerLevel == 4){spellsKnown = 3;}
            else if (playerLevel <= 6){spellsKnown = 4;}
            else if (playerLevel <= 8){spellsKnown = 5;}
            else if (playerLevel <= 10){spellsKnown = 6;}
            else if (playerLevel <= 12){spellsKnown = 7;}
            else if (playerLevel <= 14){spellsKnown = 8;}
            else if (playerLevel <= 16){spellsKnown = 9;}
            else if (playerLevel <= 18){spellsKnown = 10;}
            else{spellsKnown = 11;}
        }
        //Uses
        else if (playerClass.toLowerCase().equals("warlock"))
        {
            if (playerLevel <= 9){spellsKnown = playerLevel + 1;}
            else if (playerLevel <= 11){spellsKnown = playerLevel;}
            else if (playerLevel <= 13){spellsKnown = playerLevel - 1;}
            else if (playerLevel == 14){spellsKnown = 12;}
            else if (playerLevel <= 16){spellsKnown = 13;}
            else if (playerLevel <= 18){spellsKnown = 14;}
            else{spellsKnown = 15;}
        }
        // Returns the amount of spells the player can know/prepare/
        return spellsKnown;
    }


    //Prints the information of a spell nicely.
    public void displaySpellInfo(JSONObject item, int statModifier) throws Exception
    {
        //Accesses each spell using the url of the object.
        //item.getString("url") would return something like /api/2014/spells/acid-arrow.
        String temp= item.getString("url");
        String url = "https://www.dnd5eapi.co";

        //url and temp are combined to form a full address, something like https://www.dnd5eapi.co/api/2014/spells/acid-arrow.
        //This enables the data of that spell to be accessed using getData.
        url+=temp;

        //Uses getData and creates a new JSONObject to access the information of that spell.
        String urlString = getData(url);
        JSONObject spell = new JSONObject(urlString);

        //Sets various features of the spell to different variables.
        JSONArray classesThatlearn = spell.getJSONArray("classes");
        JSONArray spellDesc = spell.getJSONArray("desc");
        int spellLevel = spell.getInt("level");
        String spellName = spell.getString("name");
        //Iterates through classes that learn to convert the JSONArray into a neat format.
        //So for example, if the classesThatLearn array was ['wizard', 'sorcerer', 'bard', 'warlock']
        //classes would equal "wizard, sorcerer, bard, warlock"
        String classes = "";
        for (int c = 0; c < classesThatlearn.length(); c++)
        {
            classes += classesThatlearn.getJSONObject(c).getString("name") + ", ";
        }
        //Removes the excess comma at the end of the list.
        classes = classes.substring(0, classes.length() - 2);

        //Prints the name of the spell.
        System.out.println("Name: " + spellName);

        //Prints the level of the spell.
        //In the JSON file, cantrips are just displayed as level 0 spells, so this displays 0 instead of a cantrip if it is a cantrip.
        if (spell.getInt("level") == 0)
        {
            System.out.println("Level: Cantrip");
        }
        else
        {
            System.out.println("Level: " + spellLevel);
        }

        //Prints the classes that can learn the spell.
        System.out.println("Classes: " + classes);

        //Prints the range of the spell.
        System.out.println("Range: " + spell.getString("range"));

        //If the spell has concentration, prints the duration and that it has concentration.
        if (spell.getBoolean("concentration"))
        {
            System.out.println("Duration: Concentration, " + spell.getString("duration"));
        }

        //Otherwise, just prints the duration.
        else
        {
            System.out.println("Duration: " + spell.getString("duration"));
        }

        //Prints the time it takes to cast the spell.
        System.out.println("Casting time: " + spell.getString("casting_time"));

        //If the description only has one String, just prints that.
        if (spellDesc.length() == 1)
        {
            System.out.println("Description: " + spellDesc.get(0));
        }
        //Iterates through the description array if there's more than one String and prints them all.
        else
        {
            System.out.print("Description: ");
            for (int y = 0; y < spellDesc.length(); y++)
            {
                System.out.print(spellDesc.get(y) + " ");
                System.out.println();
            }
        }

        //Checks if the spell is a damaging spell by checking to see if it has the key damage.
        if (spell.has("damage"))
        {
            //Accesses the JSONObject damage.
            JSONObject damage = spell.getJSONObject("damage");

            //If a damage type is present in the object damage, prints the damage type.
            if (damage.has("damage_type"))
            {
                JSONObject damage_type = damage.getJSONObject("damage_type");
                System.out.println("Damage type: " + damage_type.getString("name"));
            }

            //If the damage is displayed with the key damage_at_slot_level (so it scales with the spell slot used), iterates through the keys using an Iterator<> to display all the damages.
            if (damage.has("damage_at_slot_level"))
            {
                System.out.println("Damage per slot level: ");
                JSONObject spellDamages = damage.getJSONObject("damage_at_slot_level");

                //This Iterator<String> is just a list of all the keys in spellDamages. This is used so the damage at each level can be accessed.
                Iterator<String> keys = spellDamages.keys();

                //Checks to see if there's a next value in the key list.
                while (keys.hasNext())
                {
                    //If so, accesses the following key.
                    String k = keys.next();

                    //Gets spellDamages at that key so that the damage of the spell and the average can be printed nicely.
                    String spellDmg = spellDamages.getString(k);

                    //If the spell contains a stat modifier, removes it from the spell and replaces it with the modifier.
                    if (spellDmg.contains("MOD"))
                    {
                        spellDmg = SpellUtility.removeMOD(spellDmg) + statModifier;
                    }
                    System.out.println(k + ": " + spellDmg + " (Average: " + SpellUtility.getAverage(spellDmg, statModifier) + ")");
                }
            }

            //If the damage is displayed with the key damage_at_character_level (so it scales with the level of the character), iterates through the keys using an Iterator<> to display all the damages.
            else if (damage.has("damage_at_character_level"))
            {
                System.out.println("Damage per character level: ");
                JSONObject spellDamages = damage.getJSONObject("damage_at_character_level");

                //This Iterator<String> is just a list of all the keys in spellDamages. This is used so the damage at each level can be accessed.
                Iterator<String> keys = spellDamages.keys();

                //Checks to see if there's a next value in the key list.
                while (keys.hasNext())
                {
                    //If so, accesses the following key
                    String k = keys.next();

                    //Gets spellDamages at that key so that the damage of the spell and the average can be printed nicely.
                    String spellDmg = spellDamages.getString(k);

                    //If the spell contains a stat modifier, removes it from the spell and replaces it with the modifier.
                    if (spellDmg.contains("MOD"))
                    {
                        spellDmg = SpellUtility.removeMOD(spellDmg) + statModifier;
                    }
                    System.out.println(k + ": " + spellDmg + " (Average: " + SpellUtility.getAverage(spellDmg, statModifier) + ")");
                }
            }
        }

        //If it's a healing spell, iterates through the keys using an Iterator<> to display all the damages.
        if (spell.has("heal_at_slot_level"))
        {
            System.out.println("Healing per spell slot level: ");
            JSONObject spellHealing = spell.getJSONObject("heal_at_slot_level");
            //This Iterator<String> is just a list of all the keys in spellDamages. This is used so the damage at each level can be accessed.
            Iterator<String> keys = spellHealing.keys();

            //Checks to see if there's a next value in the key list.
            while (keys.hasNext())
            {
                //If so, accesses the following key
                String k = keys.next();

                //Gets spellHeal at that key so that the damage of the spell and the average can be printed nicely.
                String spellHeal = spellHealing.getString(k);
                //If the spell contains a stat modifier, removes it from the spell and replaces it with the modifier.
                if (spellHeal.contains("MOD"))
                {
                    spellHeal = SpellUtility.removeMOD(spellHeal) + statModifier;
                }
                System.out.println(k + ": " + spellHeal + " (Average: " + SpellUtility.getAverage(spellHeal, statModifier) + ")");
            }
        }
        System.out.println();
    }

    public ArrayList<Text> displaySpellInfoReturnsStringBolded(JSONObject item, int statModifier) throws Exception 
    {

        ArrayList<Text> strings = new ArrayList<>();
        String temp = item.getString("url");
        String url = "https://www.dnd5eapi.co" + temp;
        String urlString = getData(url);
        JSONObject spell = new JSONObject(urlString);
        JSONArray classesThatlearn = spell.getJSONArray("classes");
        JSONArray spellDesc = spell.getJSONArray("desc");
        int spellLevel = spell.getInt("level");
        String spellName = spell.getString("name");

        String classes = "";
        for (int c = 0; c < classesThatlearn.length(); c++) 
        {
            classes += classesThatlearn.getJSONObject(c).getString("name") + ", ";
        }
        classes = classes.substring(0, classes.length() - 2);



        strings.add(SpellUtility.stylish("Name: ", true));
        strings.add(SpellUtility.stylish(spellName + "\n", false));

        strings.add(SpellUtility.stylish("Level: ", true));
        if (spellLevel == 0) 
        {
            strings.add(SpellUtility.stylish("Cantrip\n", false));
        } 
        else
        {
            strings.add(SpellUtility.stylish(spellLevel + "\n", false));
        }

        strings.add(SpellUtility.stylish("Classes: ", true));
        strings.add(SpellUtility.stylish(classes + "\n", false));

        strings.add(SpellUtility.stylish("Range: ", true));
        strings.add(SpellUtility.stylish(spell.getString("range") + "\n", false));

        strings.add(SpellUtility.stylish("Duration: ", true));
        if (spell.getBoolean("concentration")) 
        {
            strings.add(SpellUtility.stylish("Concentration, " + spell.getString("duration") + "\n", false));
        } 
        else 
        {
            strings.add(SpellUtility.stylish(spell.getString("duration") + "\n", false));
        }

        strings.add(SpellUtility.stylish("Casting time: ", true));
        strings.add(SpellUtility.stylish(spell.getString("casting_time") + "\n", false));

        strings.add(SpellUtility.stylish("Description: ", true));
        for (int i = 0; i < spellDesc.length(); i++) 
        {
            strings.add(SpellUtility.stylish(spellDesc.getString(i) + "\n", false));
        }

        if (spell.has("damage")) 
        {
            JSONObject damage = spell.getJSONObject("damage");

            if (damage.has("damage_type")) 
            {
                strings.add(SpellUtility.stylish("Damage type: ", true));
                strings.add(SpellUtility.stylish(damage.getJSONObject("damage_type").getString("name") + "\n", false));
            }

            if (damage.has("damage_at_slot_level")) 
            {
                strings.add(SpellUtility.stylish("Damage per slot level:\n", true));
                JSONObject spellDamages = damage.getJSONObject("damage_at_slot_level");
                Iterator<String> keys = spellDamages.keys();
                while (keys.hasNext()) 
                {
                    String k = keys.next();
                    String dmg = spellDamages.getString(k);
                    if (dmg.contains("MOD")) 
                    {
                        dmg = SpellUtility.removeMOD(dmg) + statModifier;
                    }
                    strings.add(SpellUtility.stylish(k + ": " + dmg + " (Average: " + SpellUtility.getAverage(dmg, statModifier) + ")\n", false));
                }
            } 
            else if (damage.has("damage_at_character_level")) 
            {
                strings.add(SpellUtility.stylish("Damage per character level:\n", true));
                JSONObject spellDamages = damage.getJSONObject("damage_at_character_level");
                Iterator<String> keys = spellDamages.keys();
                while (keys.hasNext()) 
                {
                    String k = keys.next();
                    String dmg = spellDamages.getString(k);
                    if (dmg.contains("MOD")) 
                    {
                        dmg = SpellUtility.removeMOD(dmg) + statModifier;
                    }
                    strings.add(SpellUtility.stylish(k + ": " + dmg + " (Average: " + SpellUtility.getAverage(dmg, statModifier) + ")\n", false));
                }
            }
        }

        if (spell.has("heal_at_slot_level")) 
        {
            strings.add(SpellUtility.stylish("Healing per spell slot level:\n", true));
            JSONObject heals = spell.getJSONObject("heal_at_slot_level");
            Iterator<String> keys = heals.keys();
            while (keys.hasNext())
            {
                String k = keys.next();
                String heal = heals.getString(k);
                if (heal.contains("MOD")) 
                {
                    heal = SpellUtility.removeMOD(heal) + statModifier;
                }
                strings.add(SpellUtility.stylish(k + ": " + heal + " (Average: " + SpellUtility.getAverage(heal, statModifier) + ")\n", false));
            }
        }
        strings.add(new Text("\n"));
        return strings;
    }
}

