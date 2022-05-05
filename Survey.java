import java.util.HashMap;
import java.util.HashSet;

public class Survey
{
    private HashSet<String> blacklist;

    private int typeCounter;
    private int foodCounter;

    private static int numTypes = 1;
    private static String[] typeNames = {"italian"};

    private static HashMap<String, Double> types;

    private static String[][] mainFoods = {
            {"pasta", "lasagna"}
    };

    public Survey()
    {


        blacklist = new HashSet<>(numTypes);
        types = new HashMap<String, Double>();

        typeCounter = -1;
        foodCounter = -1;
    }

    public String getTypeQuestion()
    {
        foodCounter = -1;
        ++typeCounter;
        if(typeCounter >= numTypes)
        {
            return "Finished";
        }
        return "How do you feel about " + typeNames[typeCounter] + " food?";
    }

    public void sendTypeResponse(int response)
    {
        types.put(typeNames[typeCounter], (response == 0)? 0: (response == 1)? .5: (response == 2)? 1 : -1);
    }

    public String getFoodQuestion()
    {
        ++foodCounter;
        if(foodCounter >= mainFoods[typeCounter].length)
        {
            return "Finished";
        }
        return "Are you ok with eating " + mainFoods[typeCounter][foodCounter] + "?";
    }

    public void sendFoodResponse(boolean response)
    {
        if(!response)
        {
            blacklist.add(mainFoods[typeCounter][foodCounter]);
        }
    }
}