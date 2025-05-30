package com.example;

public class SpellUtility 
{
    //This calculates the average damage (or healing) of a spell.
    //Spell damage can be displayed in many different forms. 
    //The most basic is a single die, so like 1d6. If it is, this method just calculates the average using the max and minimum damage divided by 2.
    // But some spells will have multiple die rolls, like 4d6 + 2d8
    //Or a dice roll and the player's stat modifier, like 1d3 + MOD. Or even a dice roll and a number, like 1d4 + 1. 
    //If so, this method uses recursion to calculate the averages of each component and sums them up.
    //This method can calculate the average damage no matter which of these forms it is in.
    public static int getAverage(String diceRoll, int statModifier)
    {
        int avg = 0;
        //This if statement checks to see if the diceRoll string has a space, as in "1d8" vs "1d8 + 1". 1d8 
        //If the dice roll string contains a space, it means it's either multiple die rolls, a dice roll and the stat modifier, or a dice roll in the number.
        //This means that simply calculating the average once won't work. 
        if (!(diceRoll.contains(" ")))
        {
            //Checks to see if diceRoll contains a d. If not, then it's just a number string like 1 or a string to indicate stat modifier like MOD.
            if(diceRoll.contains("d"))
            {
                //Multiplier refers to the "2" part of "2d8." Essentially how many times the dice is being rolled.
                String multiplier = "";

                //Multiplier refers to the "d8" part of "d8." The type of dice that's being rolled.
                String dice = "";
                
                //This keeps track of the index, so a new substring can be created later with the number of the dice.
                int idx = 0;
                
                //Iterates through the diceRoll string.
                //This loop is intended to split the multiplier part of the string from the dice part.
                for(int i = 0; i < diceRoll.length() - 1; i++)
                {
                    //The loop iterates until it encounters a d and adds each character in diceRoll until then to the multiplier string.
                    if (!(diceRoll.substring(i, i+1).equals("d")))
                    {
                        multiplier += diceRoll.substring(i, i+1);
                    }    
                    
                    //When a d is found, sets index to the index of the d and terminates the loop.
                    else
                    {
                        idx = i;
                        break;
                    }
                }

                //This sets the dice number using substrings and the index calculated above. For example, if diceRoll was 1d10, this would set dice to 10.
                //This calculation is performed with index + 1 because it is accessing the diceRoll string beginning from the number after d.
                dice += diceRoll.substring(idx + 1);

                //This converts multiplier and dice to integers so calculations can be performed.
                Integer newMultiplier = Integer.parseInt(multiplier);
                Integer newDice = Integer.parseInt(dice);

                //This calculates the max possible damage by multiplying the multiplier and the type of dice.
                //So, the max damage of a 4d8 die is 32, and the max damage of a 6d10 die is 60.
                int maxDmg = newMultiplier * newDice;

                //The average damage is the maximum damage plus the minimum damage divided by two.
                //The minimum damage in this case is multiplier because each dice rolled has a value of at least one.
                //So if 10d8's were rolled and all were one, the minimum, the value would be ten. 
                avg = (maxDmg + newMultiplier)/2;     

                //Returns the calculated average.
                return avg;   
            }
            
            //If there is no d, it means that diceRoll is either a integer or "MOD." 

            //Some spells have damage displayed as, for example, "1d5 + MOD".
            //Mod stands for the spellcasting stat modifier of the player, so for example +3.
            //This returns the stat modifier of the player in place of MOD as of course, that would be the average damage.
            else if (diceRoll.equals("MOD"))
            {
                return statModifier;
            }

            //Some spells have damage displayed as, for example, "1d5 + 1".
            //This would just return the Integer as the average damage of 1 is just... 1.
            else 
            {
                Integer num = Integer.parseInt(diceRoll);
                return num;
            }
        }

        //If a spell's damage has 2 rolls or 2 components in the damage, the string diceRoll is split into 2 strings.
        //Then, the method getAverage is called recursively to add the averages of the 2 new strings together.
        else
        {
            //Initializes the first and second string, as well as the index of the second string.
            String firstNum = "";
            String secondNum = "";
            int idx = 0;

            //Iterates through the string diceRoll. 
            for(int i = 0; i < diceRoll.length() - 1; i++)
            {
                //The loop iterates until it encounters a space and adds each character in diceRoll until then to the firstNum string.
                if (!(diceRoll.substring(i, i+1).equals(" ")))
                {
                    firstNum += diceRoll.substring(i, i+1);
                }           
                //When a space is found, sets index to the index of the space and terminates the loop.
                else
                {
                    idx = i;
                    break;
                }
            }
            //Sets the second roll to the substring at index + 3. This is because the " + " in between 2 dice rolls is 3 indices.
            //So by adding 3, the substring begins at the second dice roll. 
            //If diceRoll was "1d8 + 2d8", secondNum would be 2d8 for example.
            secondNum = diceRoll.substring(idx + 3);

            //Calls getAverage recursively, to add the averages of the 2 "components" of the roll together.
            return getAverage(firstNum, statModifier) + getAverage(secondNum, statModifier);
        }
    }

    //Converts the range to an integer to make sorting by range possible.
    //Currently doesn't work when it comes to the 4 spells that have a range of 1 mile. I am working on a fix, but this works for a majority of spells
    public static int rangeInt(String range)
    {
        //Self is the lowest range, so it sets the value to -1 so this will always be on the bottom when it comes to sorting by range.
        if (range.equals("Self"))
        {
            return -1;
        }

        //Touch is the second lowest, so the value is set to 0.
        else if (range.equals("Touch"))
        {
            return 0;
        }
        
        //Unlimited is, of course, the widest range, so I set it to a value larger than any spells with a range would have.
        else if (range.equals("Unlimited"))
        {
            return 1000;
        }
        
        String newNum = "";

        //If the range is an actual number, like 120 feet, this for loop simply sets newNum a string of the number.
        //So in the 120 feet example, newNum would be set to "120".
        for (int i = 0;  i < range.length() - 1; i++)
        {
            //If the substring is not a space (the space between, say, "120" and "feet" in "120 feet") adds the character to newNum. 
            if (!(range.substring(i, i + 1).equals(" ")))
            {
                newNum += range.substring(i, i + 1);
            }
            //Terminates the loop otherwise.
            else
            {
                break;
            }
        }
        //Converts newNum to an integer and returns that integer.
        Integer numAsInt = Integer.parseInt(newNum);
        return numAsInt;
    }
    
    //Removes the word MOD from a string.
    //This enables the conversion of something like "1d20 + MOD" to be something like "1d20 + 3" instead.
    public static String removeMOD(String original)
    {
        String newString = "";
        //Iterates through the original string.
        //This loop is intended to add the values of the string before MOD onto newString.
        for (int i = 0; i < original.length(); i++)
        {
            //If the character is not equal to M, adds the character to newString.
            if (!(original.substring(i, i + 1).equals("M")))
            {
                newString += original.substring(i, i + 1);
            }

            //Terminates the loop if the character is equal to M.
            else
            {
                break;
            }
        }
        return newString;
    }

    //Returns the String with the letter capitalized.
    public static String capitalizeString(String str)
    {
        //The place I use this function in was giving me errors so I had to include this.
        if (str == null || str.isEmpty()) 
        {
            return str;
        }
        //Returns the string with the first letter set to uppercase and the rest set to lowercase.
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    } 
}
