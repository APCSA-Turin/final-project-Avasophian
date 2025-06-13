package com.example;
//Imports
import org.json.JSONArray;
import org.json.JSONObject;

public class SpellSearch
{
    //Searches through a JSONArray and returns the index of the String find.
    //If find is not in the Array, returns -1.
    //Functionally identical to searchIndex(), instead it searches for a name (like "Mage Hand") instead of an index (like "mage-hand").
    public static int searchName (JSONArray spells, String find) throws Exception
    {
        //Keeps track of what index the find String was found at.
        int foundIndex = -1;

        //Iterates through the given spells array until the find String is found.
        for(int j=0;j<spells.length();j++)
        {
            JSONObject item = (JSONObject)spells.get(j);

            //Sets found index to the index of the String if it is found in the list, then terminates the loop.
            if (item.getString("name").toLowerCase().equals(find))
            {
                foundIndex = j;
                break;
            }
        }
        //Returns the found index.
        return foundIndex;
    }

    //Searches through a JSONArray and returns the index of the String find.
    //If find is not in the Array, returns -1.
    //Functionally identical to searchName(), instead it searches for a index (like "mage-hand") instead of an name (like "Mage Hand").
    public static int searchIndex (JSONArray spells, String find) throws Exception
    {
        //Keeps track of what index the find String was found at.
        int foundIndex = -1;

        //Iterates through the given spells array until the find String is found.
        for(int j=0;j<spells.length();j++)
        {
            JSONObject item = (JSONObject)spells.get(j);
            //Sets found index to the index of the String if it is found in the list, then terminates the loop.
            if (item.getString("index").toLowerCase().equals(find))
            {
                foundIndex = j;
                break;
            }
        }
        //Returns the found index.
        return foundIndex;
    }

    //Searches for valid spells and puts them into a new JSONArray, then returns said JSONArray.
    public static JSONArray search2 (JSONArray spells, String find) throws Exception
    {
        JSONArray validSpells = new JSONArray();
        for(int j=0;j<spells.length();j++)
        {
            JSONObject item = (JSONObject)spells.get(j);
            String temp= item.getString("url");
            String url = "https://www.dnd5eapi.co";
            url+=temp;
            String urlString = SpellBook.getData(url);
            JSONObject spell = new JSONObject(urlString);
            if (spell.getString("index").indexOf(find) > -1)
            {
                validSpells.put(spell);
            }
        }
        return validSpells;
    }
}
