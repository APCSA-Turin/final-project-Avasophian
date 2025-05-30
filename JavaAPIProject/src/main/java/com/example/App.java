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
public class App 
{
    public static void main(String[] args) throws Exception 
    {
        
        Scanner scan = new Scanner(System.in);
        String endpoint = "https://www.dnd5eapi.co/api/2014/spells";
        String jsonString = SpellBook.getData(endpoint);
        JSONObject json = new JSONObject(jsonString);
        JSONArray results = json.getJSONArray("results");
        File file = new File("output.txt");
        String playerClass = "";
        int playerLevel = 0;
        int statModifier = 0;
        boolean load = false;
        ArrayList<String> loadedSpells = new ArrayList<String>();
        ArrayList<String> playerSpellBook = new ArrayList<String>();


        if (file.exists() && file.length() > 0) 
        {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Saved data found. Do you want to load it? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("yes")) 
            {
                SpellLoader s = new SpellLoader();
                load = true;
                playerClass = s.getLoadClass();
                playerLevel = s.getLevel();
                statModifier = s.getModifier();
                loadedSpells = s.getSpells();
            } 
            else
            {
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
        SpellBook spellBook = new SpellBook(playerClass, playerLevel, playerSpellBook);
        JSONArray playerSpells = new JSONArray();
        JSONArray spellsCanLearn = new JSONArray();
        int spellsCanKnow = spellBook.spellsKnownCalculator(statModifier);
        Integer[] allowed = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        ArrayList<Integer> allowedLevels = new ArrayList<Integer>(Arrays.asList(allowed));
        String[] allowed2 = {"Acid", "Bludgeoning", "Cold", "Fire", "Force", "Lightning", "Necrotic", "Piercing", "Poison", "Psychic", "Radiant", "Slashing", "Thunder"};
        ArrayList<String> allowedDmgTypes = new ArrayList<String>(Arrays.asList(allowed2));
        ArrayList<String> fixedAllowedDmgTypes = new ArrayList<String>(Arrays.asList(allowed2));
            
        boolean filterByDamageType = false;
        boolean filterByDamage = false;
        boolean filterByNonDamaging = false;

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
            if (spellBook.hasValue(classesThatlearn, "index", playerClass) && (spellBook.getSpellLevel()) >= spellLevel)
            {
                spellsCanLearn.put(spell);
            }
        }

        if (load)
        {
            for (String str : loadedSpells)
            {
                int found = SpellSearch.searchIndex(spellsCanLearn, str);
                playerSpells.put(spellsCanLearn.get(found));
            }
        }



        while (true)
        {
            System.out.println("1. See available spells\n2. Add to your spellbook\n3. View your spellbook\n4. Sort available spells\n5. Filter available spells\n6. Search for a spell\n7. Go to your stats\n8. Quit");
            int playerChoice = scan.nextInt();
            scan.nextLine();
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
                    if (allowedLevels.contains(spellLevel) && (!filterByDamage || spell.has("damage")) && (!filterByNonDamaging || !spell.has("damage")) && (!filterByDamageType || (spell.has("damage") && spell.getJSONObject("damage").has("damage_type") && allowedDmgTypes.contains(spell.getJSONObject("damage").getJSONObject("damage_type").getString("name")))))                   
                    {
                        spellBook.displaySpellInfo(item, statModifier);
                    }
                }
            }
            else if (playerChoice == 2)
            {
                System.out.println("State the name of the spell you want to add to your known spells");
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
                System.out.println();
                System.out.println("Spells Known: " + playerSpells.length() + "/" + spellsCanKnow);
                System.out.println("Your Spells: ");
                System.out.println();
                for(int j=0;j<playerSpells.length();j++)
                {
                    JSONObject item = (JSONObject)playerSpells.get(j);
                    spellBook.displaySpellInfo(item, statModifier);
                }
            }
            else if (playerChoice == 4)
            {
                System.out.println("1. Sort by level, highest to lowest\n2. Sort by range, highest to lowest");
                int whichSort = scan.nextInt();
                if (whichSort == 1)
                {
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
                        Object temp = spellsCanLearn.get(i);
                        spellsCanLearn.put(i, spellsCanLearn.get(minIndex));
                        spellsCanLearn.put(minIndex, temp);           
                    } 
                }
                else if (whichSort == 2)
                {
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
                        Object temp = spellsCanLearn.get(i);
                        spellsCanLearn.put(i, spellsCanLearn.get(minIndex));
                        spellsCanLearn.put(minIndex, temp);           
                    }  
                }
        }
        else if (playerChoice == 5)
        {
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
                        if (choiceToRemove >= 0 && choiceToRemove <= 9)
                        {   
                            if (allowedLevels.contains(choiceToRemove))
                            {
                                allowedLevels.remove((Object)choiceToRemove);
                            }
                            else
                            {
                                allowedLevels.add(choiceToRemove);
                            }
                        }
                        else if (choiceToRemove == -1)
                        {
                            break;
                        }
                    }                
                }
                else if (choice == 2)
                {
                    while (true)
                    {
                        
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
                else if (choice == 3)
                {
                    while (true)
                    {
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
                        playerDmgTypeChoice = SpellUtility.capitalizeString(playerDmgTypeChoice);
                        
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
                    filterByDamage = false;
                    filterByNonDamaging = false;
                    ArrayList<Integer> newAllowedLvls = new ArrayList<Integer>(Arrays.asList(allowed));
                    allowedLevels = newAllowedLvls;
                }
                else if (choice == 5)
                {
                    break;
                }
            }
        }
        else if (playerChoice == 6)
        {
            while (true)
            {
                System.out.println("1. Search available spells\n2. Search all spells \n3. Quit ");
                int c = scan.nextInt();
                scan.nextLine();
                if (c == 1)
                {
                    int count = 0;
                    System.out.println("Search bar: ");
                    String findSpell = scan.nextLine().toLowerCase();
                    System.out.println();
                    JSONArray searchSpells = SpellSearch.search2(spellsCanLearn, findSpell);
                    for(int j=0;j<searchSpells.length();j++)
                    {
                        JSONObject item = (JSONObject)searchSpells.get(j);
                        {
                            System.out.println((j+1) + ". " + item.getString("name"));
                            count ++;
                        }
                    }
                    if (count > 0)
                    {
                        System.out.println("Which of these spells did you want to view? (Enter the name)");
                        findSpell = scan.nextLine().toLowerCase();
                        System.out.println();
                        int foundIndex = SpellSearch.search(spellsCanLearn, findSpell);
                        if (foundIndex != -1)
                        {
                            spellBook.displaySpellInfo((JSONObject)spellsCanLearn.get(foundIndex), statModifier);
                        }
                        else
                        {
                            System.out.println("Spell not found...");
                        }
                    }
                }
                else if (c == 2)
                {
                    System.out.println("Enter spell: ");
                    String findSpell = scan.nextLine().toLowerCase();
                    System.out.println();
                    int foundIndex = SpellSearch.search(results, findSpell);
                    if (foundIndex != -1)
                    {
                        spellBook.displaySpellInfo((JSONObject)results.get(foundIndex), statModifier);
                    }
                    else
                    {
                        System.out.println("Spell not found...");
                    }
                }
                else if (c == 3)
                {
                    break;
                }
            }
        }

        else if (playerChoice == 7)
        {
            System.out.println("Class: " + spellBook.getPlayerClass());
            System.out.println("Level: " + spellBook.getPlayerLevel());
            System.out.println("Stat Modifier: " + statModifier);
            System.out.println("Would you like to edit anything? (yes/no)");
            String lvlChoice = scan.nextLine().toLowerCase();
            if (lvlChoice.equals("yes"))
            {
                System.out.println("1. Level up\n2. Change stat modifier");
                lvlChoice = scan.nextLine();
                if (lvlChoice.equals("1") && spellBook.getPlayerLevel() < 20)
                {
                    spellBook.levelUp();
                }
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
        else if (playerChoice == 8)
        {
            break;
        }
        SpellSaver.saveData(playerSpells, playerClass, playerLevel, statModifier);
        {}
    }
    }
}

