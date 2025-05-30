package com.example;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

public class SpellSaver 
{
    public static void saveData(JSONArray spellList, String theClass, int theLevel, int statMod) throws IOException
    {
        try (FileWriter writer = new FileWriter("output.txt"))
        {
            writer.write("class:" + theClass + "\n");
            writer.write("level:" + theLevel + "\n");
            writer.write("stat modifier:" + statMod + "\n");
            for (int i = 0; i < spellList.length(); i++)
            {
                JSONObject item = (JSONObject)spellList.get(i);
                String index = item.getString("index");
                writer.write(index + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }   
    }
}
