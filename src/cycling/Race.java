package cycling;

import java.util.ArrayList;
import java.util.HashMap;

public class Race {

    private static final HashMap<Integer,Race> races = new HashMap<Integer,Race>();
    private int myID;
    private String name;
    private String description;
    private final ArrayList<Integer> stageIds = new ArrayList<Integer>(); // list of the stages belonging to this race

    /**
     * Constructor for the Race class
     *
     * @param name Name of the race
     * @param description Description of the race
     */
    public Race(String name, String description) throws InvalidNameException {
        // Check for invalid (rule breaking) name
        if (name == null || name.length()>30 || name.isEmpty() || name.contains(" ")){
            throw new InvalidNameException(" name broke naming rules. Length must be 0<length<=30, and no whitespace");
        }

        // Set up attributes for the object
        this.myID = UniqueIDGenerator.calculateUniqueID(races);
        this.name = name;
        this.description = description;

        // add the new object to the hashmap of all races
        races.put(this.myID,this);
    }


    /**
     * Getter for a race by its ID
     * @param id the ID to query
     * @return The race object reference
     */
    public static Race getRaceByID(int id){
        return races.get(id);
    }

    /**
     * Getter for the stage's name
     * @return The name of the stage instance
     */
    public String getName(){
        return name;
    }

    /**
     * Getter for the ID attribute on this Race instance
     * @return the ID
     */
    public int getId(){
        return myID;
    }

    /**
     * Gets a short text description of the race including:
     * race ID, name, description, the number of stages, and the total length
     * @return The formatted descriptor string
     */
    public String getDetails(){
        int numStages = stageIds.size();
        double length = 0.0;
        return String.format("Name: %s, Description: %s, Number of stages: %d, Total length: %.2f",
                name,description,numStages,length);
    }

    /**
     * Gets a short description of the race class
     *
     * @return The descriptor string
     */
    @Override
    public String toString(){
        return "Race Class " + getDetails();
    }

    /**
     * Removes this Race Instance
     */
    public void remove(){
        // TODO actually do something
        //  such as cascade down removing stages + checkpoints
        //  Then remove it from the hashmap of races

        races.remove(myID);
    }

    /**
     * Getter for the list of stage IDs that belong to this Race
     *
     * @return The stage IDs beloning to this race
     */
    public ArrayList<Integer> getStageIDs(){
        return stageIds;
    }

    /**
     * Gets the ID of a Race by its name
     *
     * @param name The name of the race to be removed.
     * @param portalsIDs the list of the raceIds that are contained by this portal instance, so we don't mix up this
     * with another portals race with a same name
     * @return The id of the race with the given name (in the context of the portal)
     * @throws NameNotRecognisedException When the name has not been found in the system
     */
    public static int getIDByName(String name, ArrayList<Integer> portalsIDs) throws NameNotRecognisedException {
        // Check the name exists in this system by iterating through
        // all raceIDs in system and checking name
        boolean foundName = false;
        int foundID = 0;
        for(int raceID : portalsIDs){
            if (Race.getRaceByID(raceID).getName().equals(name)){
                foundName = true;
                foundID = raceID;
                break;
            }
        }
        if (!foundName){
            throw new NameNotRecognisedException("The Race name: "+ name+ ", does not exist in this system");
        }
        return foundID;
    }

}
