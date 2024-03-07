package cycling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class holding teams, their name, description and riders within.
 *
 * @author 730003140, 730002704
 * @version 1.0
 */
public class Team implements java.io.Serializable {
    // TODO does removing a rider remove its results?

    // Holds reference to all the teams created
    static private final ArrayList<Integer> idsUsed = new ArrayList<>();

    private final int myId; // Unique Id of the team
    private final String name; // Name of the team
    private final String description; // Description of the team
    private final HashMap<Integer,Rider> myRiders = new HashMap<>(); // Holds all the riders in this team

    /**
     * Constructor for the Team class.
     *
     * @param name        Name of the team
     * @param description Description of the team
     * @throws InvalidNameException if the name is invalid (too long/short or contains whitespace)
     */
    public Team(String name, String description) throws InvalidNameException {

        // Check for invalid (rule breaking) name
        if (name == null || name.length() > 30 || name.isEmpty() || name.contains(" ")) {
            throw new InvalidNameException(" name broke naming rules. Length must be 0<length<=30, and no whitespace");
        }

        // Set up this new instance with the essential details
        this.myId = UniqueIdGenerator.calculateUniqueId(idsUsed);
        this.name = name;
        this.description = description;
        idsUsed.add(this.myId); // Add this team to the list of teams
        //teams.put(this.myId, this); // Add this team to the list of teams
    }

    /**
     * Pushes a team into the system.
     *
     * @param id the ID of the team to add
     * @param team the team object to add
     */
    //public static void pushTeam(int id, Team team) {
    //    teams.put(id, team);
    //}

    /**
     * Getter for the riders' Ids that belong to this team
     *
     * @return an array of ints for the rider Ids in the team
     */
    public HashMap<Integer,Rider> getRiders() {
        return myRiders;
    }

    /**
     * Getter for the Id attribute on the team class
     *
     * @return this instance of a team's Id
     */
    public int getId() {
        return myId;
    }

    /**
     * Getter for the name attribute on the team class
     *
     * @return this instance of a team's name
     */
    public String getName() {
        return name;
    }

    /**
     * Function to Get a team reference by its Id.
     *
     * @param id the Id of the team to try find
     * @return the team with the given Id if it exists
     */

    /**
     * Gets details about the team
     * Not required by spec but Adding it in case / extension
     *
     * @return A short description of the team
     */
    public String getDetails() {
        return String.format("Name: %s  Description: %s", name, description);
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


    /**
     * Remove the team from the system
     */
    public void remove() {
        idsUsed.remove(Integer.valueOf(this.myId)); // Remove this team from the list of teams

        // Go through all riders in the team and remove them
        while (!new ArrayList<Integer>(myRiders.keySet()).isEmpty()) { // needs to be while loop to stop concurrent modification exception
            try {
                removeRider(new ArrayList<Integer>(myRiders.keySet()).getFirst());
            } catch (IDNotRecognisedException e) {
                assert false : "Rider Id not found in team, this should not happen here";
            }
        }
    }

    /**
     * Add a rider to the team
     *
     * @param rider the rider object to add
     */
    public void addRider(Rider rider) {
        myRiders.put(rider.getId(),rider);
    }

    /**
     * Remove a rider from the team
     *
     * @param riderId the Id of the rider to remove
     * @throws IDNotRecognisedException if the rider Id is not in the team
     */
    public void removeRider(int riderId) throws IDNotRecognisedException {
        if (!myRiders.containsKey(riderId)) {
            throw new IDNotRecognisedException("Rider Id " + riderId + " not found in team ");
        }
        myRiders.get(riderId).remove(); // Remove the rider through its own method
        myRiders.remove(Integer.valueOf(riderId)); // remove it from our list of riders
    }

}
