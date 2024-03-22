package cycling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class holding teams, their name, description and riders within.
 *
 * @author 730003140, 730002704
 * @version 1.0
 */
public class Team extends Entity {
    private final String name; // Name of the team
    private final String description; // Description of the team
    private final HashMap<Integer, Rider> myRiders = new HashMap<>(); // Holds all the riders in this team

    /**
     * Constructor for the Team class.
     *
     * @param name        Name of the team
     * @param description Description of the team
     * @throws InvalidNameException if the name is invalid (too long/short or
     *                              contains whitespace)
     */
    public Team(String name, String description) throws InvalidNameException {
        super(); // Call the entity constructor

        // Check for invalid (rule breaking) name
        if (name == null || name.length() > 30 || name.isEmpty() || name.contains(" ")) {
            freeId(); // as super() has been called, we need to free the ID
            throw new InvalidNameException(" name broke naming rules. Length must be 0<length<=30, and no whitespace");
        }

        // Set up this new instance with the essential details
        this.name = name;
        this.description = description;
    }

    /**
     * Getter for the riders' Ids that belong to this team
     *
     * @return a hashmap of ints for the rider Ids in the team
     */
    public HashMap<Integer, Rider> getRiders() {
        return myRiders;
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
     * Gets details about the team.
     * Format: "Name: [name] Description: [description]"
     *
     * @return A short description of the team
     */
    public String getDetails() {
        return String.format("Name: %s  Description: %s", name, description);
    }

    /**
     * Gets a short description of the team.
     *
     * @return A string description of this object
     */
    @Override
    public String toString() {
        return "Team Class " + getDetails();
    }

    @Override
    public void remove() {
        freeId(); // Remove the team from the usedIds list

        // Needs to be a while loop to stop concurrent modification exception
        while (!new ArrayList<Integer>(myRiders.keySet()).isEmpty()) {
            try {
                deleteRider(new ArrayList<Integer>(myRiders.keySet()).get(0));
            } catch (IDNotRecognisedException e) {
                assert false : "Rider ID not found in team";
            }
        }
    }

    /**
     * Add a rider to the team.
     *
     * @param rider The rider object to add
     */
    public void addRider(Rider rider) {
        int ridersBefore = myRiders.size();
        myRiders.put(rider.getId(), rider);
        // assert rider added
        assert myRiders.size() == ridersBefore + 1 : "Rider not added to team";
    }

    /**
     * Remove a rider from the team.
     *
     * @param riderId The Id of the rider to remove
     * @throws IDNotRecognisedException If the rider Id is not in the team
     */
    public void deleteRider(int riderId) throws IDNotRecognisedException {
        if (!myRiders.containsKey(riderId)) {
            throw new IDNotRecognisedException("Rider Id " + riderId + " not found in team ");
        }
        int ridersBefore = myRiders.size();
        myRiders.get(riderId).remove(); // Remove the rider through its own method
        myRiders.remove((Integer) riderId); // Remove it from our list of riders
        // assert rider removed
        assert myRiders.size() == ridersBefore - 1 : "Rider not removed from team";
    }
}
