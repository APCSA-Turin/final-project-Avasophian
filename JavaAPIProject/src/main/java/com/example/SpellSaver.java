package com.example;

//Imports
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class SpellSaver 
{
    //Saves the data given by the parameters into output.txt. Here is an example format of what would be saved:
    //class:cleric
    //level:20
    //stat modifier:5
    //gate
    //astral-projection
    //mass-heal
    //true-resurrection
    public static void saveData(JSONArray spellList, String theClass, int theLevel, int statMod) throws IOException
    {
        //Writes into a new file, called output.txt.
        try (FileWriter writer = new FileWriter("output.txt"))
        {
            //Writes the class, level, and stat modifier using the given parameter.
            writer.write("class:" + theClass + "\n");
            writer.write("level:" + theLevel + "\n");
            writer.write("stat modifier:" + statMod + "\n");

            //Iterates through the given spellList, and writes each spell's index.
            for (int i = 0; i < spellList.length(); i++)
            {
                JSONObject item = (JSONObject)spellList.get(i);
                String index = item.getString("index");
                writer.write(index + "\n");
            }
        }
        //Catches exceptions.
        catch (IOException e)
        {
            e.printStackTrace();
        }   
    }
}
