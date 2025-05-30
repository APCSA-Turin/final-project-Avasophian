package com.example;
import org.json.JSONArray;
import org.json.JSONObject;

public class SpellSearch
{
    public static int search (JSONArray spells, String find) throws Exception
    {
        int foundIndex = -1;
            
        for(int j=0;j<spells.length();j++)
        {
            JSONObject item = (JSONObject)spells.get(j);
            if (item.getString("name").toLowerCase().equals(find))
            {
                foundIndex = j;
                break;
            }         
        }
        return foundIndex;
    }

    public static int searchIndex (JSONArray spells, String find) throws Exception
    {
        int foundIndex = -1;
            
        for(int j=0;j<spells.length();j++)
        {
            JSONObject item = (JSONObject)spells.get(j);
            if (item.getString("index").toLowerCase().equals(find))
            {
                foundIndex = j;
                break;
            }         
        }
        return foundIndex;
    }

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
