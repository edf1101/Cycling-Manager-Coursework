package cycling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class holding riders' details
 *
 * @author Kit Matthewson & Ed Fillingham
 * @version 1.0
 */
public class Rider {

    // Holds reference to all the riders created by their ID
    static private HashMap<Integer,Rider> riders = new HashMap<Integer,Rider>();

    private int myID;
    private int team;
    private String name;
    private int yearOfBirth;

    /**
     * Constructor for the Rider class.
     *
     * @param name Name of the rider
     * @param yearOfBirth Year of birth of the rider
     * @param team The team the rider belongs to
     */
    public Rider(String name, int yearOfBirth, int team) {
        this.myID = UniqueIDGenerator.calculateUniqueID(riders);
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.team = team;
        riders.put(this.myID, this); // add to static hashmap of riders in system

        // Add the rider to the team
        Team.getTeamById(team).addRider(this.myID);
    }

    /**
     * Getter for the ID attribute on the rider class
     *
     * @return this instance of a rider's ID
     */
    public int getID() {
        return myID;
    }

    /**
     * Gets the rider specified by the ID
     *
     * @param riderID The rider to find
     * @return The rider's object
     */
    public static Rider getRiderById(int riderID) {
        return riders.get(riderID);
    }

    /**
     * Get the details of the rider in a string form
     *
     * @return A string describing the rider
     */
    public String getDetails() {
        // Get the name of the team
        String teamName = Team.getTeamById(team).getName();

        return "Rider: " + name + " Year of Birth:" + yearOfBirth + " Team "+ teamName;
    }

    /**
     * Get the details of the rider in a string form
     *
     * @return A string describing the rider
     */
    @Override
    public String toString() {
        return getDetails();
    }

    /**
     * Removes the rider from the system
     */
    public void remove() {
        // TODO actually do something when we remove a rider
        //  ie cascade down removing riders scores and shifting points
        //  Then remove it from static riders hashmap

        // then remove it from my list of all riders
        riders.remove(this.myID);
    }

    /**
     * Getter for all the rider IDs in the system.
     *
     * @return An array of all the rider IDs
     */
    public static ArrayList<Integer> getRiderIds() {
        return new ArrayList<Integer>(riders.keySet());
    }
}
