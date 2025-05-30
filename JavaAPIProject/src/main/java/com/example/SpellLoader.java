package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SpellLoader 
{
    private String loadClass;
    private int level;
    private int statModifier;
    private ArrayList<String> spells;
    public SpellLoader()
    {
        spells = new ArrayList<>(); // ← initialize it here
        try (BufferedReader reader = new BufferedReader(new FileReader("output.txt"))) 
        {
            String line;
            // I learned how to use .split and trim from https://www.w3schools.com/java/ref_string_split.asp 
            if ((line = reader.readLine()) != null && line.startsWith("class")) 
            {
                loadClass = line.split(":")[1].trim();
            }
            if ((line = reader.readLine()) != null && line.startsWith("level")) 
            {
                level = Integer.parseInt(line.split(":")[1].trim());
            }
            if ((line = reader.readLine()) != null && line.startsWith("stat modifier")) 
            {
                statModifier = Integer.parseInt(line.split(":")[1].trim());
            }
            while ((line = reader.readLine()) != null) 
            {
                spells.add(line.trim());
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    public String getLoadClass(){return loadClass;}
    public int getLevel(){return level;}
    public int getModifier(){return statModifier;}
    public ArrayList<String> getSpells(){return spells;}
}
