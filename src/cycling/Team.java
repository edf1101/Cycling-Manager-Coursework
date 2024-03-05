package cycling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class holding teams, their name, description and riders within.
 *
 * @author 730003140, 730002704
 * @version 1.0
 *
 */
public class Team {

    // Holds reference to all the teams created
    static private final HashMap<Integer, Team> teams = new HashMap<Integer, Team>();

    private final int myId; // Unique Id of the team
    private final String name; // Name of the team
    private final String description; // Description of the team
    private final ArrayList<Integer> riderIds = new ArrayList<Integer>(); // Holds all the rider Ids belonging to this
                                                                          // team

    /**
     * Constructor for the Team class.
     *
     * @param name        Name of the team
     * @param description Description of the team
     */
    public Team(String name, String description) throws InvalidNameException {

        // Check for invalid (rule breaking) name
        if (name == null || name.length() > 30 || name.isEmpty() || name.contains(" ")) {
            throw new InvalidNameException(" name broke naming rules. Length must be 0<length<=30, and no whitespace");
        }

        // Set up this new instance with the essential details
        this.myId = UniqueIdGenerator.calculateUniqueId(teams);
        this.name = name;
        this.description = description;

        teams.put(this.myId, this); // Add this team to the list of teams
    }

    /**
     * Getter for the riders' Ids that belong to this team
     *
     * @return an array of ints for the rider Ids in the team
     */
    public int[] getRiders() {
        return riderIds.stream().mapToInt(Integer::intValue).toArray();
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
    public static Team getTeamById(int id) {
        return teams.get(id);
    }

    /**
     * Gets details about the team
     * Not required by spec but Adding it in case / extension
     *
     * @return A short
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
        // Go through all riders in the team and remove them
        while (!riderIds.isEmpty()) { // needs to be while loop to stop concurrent modification exception
            try {
                Rider.getRiderById(riderIds.getFirst()).remove();
            } catch (IDNotRecognisedException e) {
                throw new RuntimeException(e);
            }
        }
        teams.remove(myId);
    }

    /**
     * Add a rider to the team
     *
     * @param riderId the Id of the rider to add
     */
    public void addRider(int riderId) {
        riderIds.add(riderId);
    }

    /**
     * Remove a rider from the team
     *
     * @param riderId the Id of the rider to remove
     * @throws IDNotRecognisedException if the rider Id is not in the team
     */
    public void removeRider(int riderId) throws IDNotRecognisedException {
        if (!riderIds.contains(riderId)) {
            throw new IDNotRecognisedException("Rider Id " + riderId + " not found in team ");
        }
        riderIds.remove(Integer.valueOf(riderId));
    }

}
