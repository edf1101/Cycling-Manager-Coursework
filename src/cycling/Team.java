package cycling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class holding teams, their name, description and riders within.
 *
 * @author Ed Fillingham, Kit Matthewson
 * @version 1.0
 *
 */
public class Team {

    // Holds reference to all the teams created
    static private HashMap<Integer,Team> teams = new HashMap<Integer,Team>();

    private int myID; // Unique ID of the team
    private String name; // Name of the team
    private String description; // Description of the team
    private ArrayList<Integer> riderIds = new ArrayList<Integer>(); // Holds all the rider IDs belonging to this team

    /**
     * Constructor for the Team class.
     *
     * @param name Name of the team
     * @param description Description of the team
     */
    public Team(String name, String description) {

        // Set up this new instance with the essential details
        this.myID = UniqueIDGenerator.calculateUniqueID(teams);
        this.name = name;
        this.description = description;

        teams.put(this.myID,this); // Add this team to the list of teams
    }

    /**
     * Getter for the riders' IDs that belong to this team
     *
     * @return an array of ints for the rider IDs in the team
     */
    public int[] getRiders(){
        return riderIds.stream().mapToInt(Integer::intValue).toArray();
    }


    /**
     * Getter for the ID attribute on the team class
     *
     * @return this instance of a team's ID
     */
    public int getId(){
        return myID;
    }

    /**
     * Getter for the name attribute on the team class
     *
     * @return this instance of a team's name
     */
    public String getName(){
        return name;
    }


    /**
     * Function to Get a team reference by its ID.
     * @param id the ID of the team to try find
     * @return the team with the given ID if it exists
     */
    public static Team getTeamById(int id) {
        return teams.get(id);
    }

    /**
     * Gets details about the team
     * Not required by spec but Adding it in case / extension
     * @return A short
     */
    public String getDetails(){
        return String.format("Name: %s  Description: %s",name,description);
    }


    /**
     * Nice toString method for description of a team object
     *
     * @return A string description of this object
     */
    @Override
    public String toString() {
        return getDetails();
    }

    public void remove(){
        // TODO actually do something when we remove a team
        //  ie cascade down removing riders and shifting points
        //  Then remove it from static teams hashmap
    }

    public void addRider(int riderId){
        riderIds.add(riderId);
    }

}
