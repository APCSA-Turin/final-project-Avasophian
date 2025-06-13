package com.example;
//Imports
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SpellLoader
{
    //Instance variables to keep track of the loaded values.
    private String loadClass;
    private int level;
    private int statModifier;
    private ArrayList<String> spells;

    //The constructor loads the file and sets the instance variables to the loaded values.
    public SpellLoader()
    {
        //Initializes spells.
        spells = new ArrayList<>();

        //Iterates through each line in the output.txt file.
        //Here is an example of how the save file would be written, so, something it'd be iterating through.
        //class:cleric
        //level:20
        //stat modifier:5
        //gate
        //astral-projection
        //mass-heal
        //true-resurrection
        //As you can see, first the class level and stat modifier are printed, then the spells.
        try (BufferedReader reader = new BufferedReader(new FileReader("output.txt")))
        {
            //This String represents each line of the file.
            String line;

            //I learned how to use .split and trim from https://www.w3schools.com/java/ref_string_split.asp
            //Basically, split simply divides the string into two substrings using a "Regex" (the separator)
            //Then, using indices, you can access the substring. So line.split(:)[1] is accessing the second substring.
            //So if for example the code was called on class:wizard, line.split(:)[1] would equal "wizard"
            //I was experiencing issues so I had to also add trim, which just trims spaces from the start and beginning of the text.
            //Also, .contains is used to check if class, level, or stat modifier are in the line for... self-explanatory reasons.
            if ((line = reader.readLine()) != null && line.contains("class"))
            {
                //Saves the loaded class to the loadClass variable.
                loadClass = line.split(":")[1].trim();
            }
            if ((line = reader.readLine()) != null && line.contains("level"))
            {
                //Saves the loaded level to the level variable after converting it to an integer.
                level = Integer.parseInt(line.split(":")[1].trim());
            }
            if ((line = reader.readLine()) != null && line.contains("stat modifier"))
            {
                //Saves the loaded stat modifier to the stat modifier variable after converting it to an integer.
                statModifier = Integer.parseInt(line.split(":")[1].trim());
            }
            //Afterward, iterates through the rest of the lines to access the spells.
            while ((line = reader.readLine()) != null)
            {
                //Adds each line to the spells list.
                spells.add(line.trim());
            }
        }
        //Catches an exception.
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //Getter methods for the instance variables.
    public String getLoadClass(){return loadClass;}
    public int getLevel(){return level;}
    public int getModifier(){return statModifier;}
    public ArrayList<String> getSpells(){return spells;}
}
