package cycling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class holding riders' details
 *
 * @author 730002704 & 730003140
 * @version 1.0
 */
public class Rider implements java.io.Serializable {

    // Holds reference to all the riders created by their Id
    //static private final HashMap<Integer, Rider> riders = new HashMap<Integer, Rider>();
    static private final ArrayList<Integer> idsUsed = new ArrayList<>();

    private final int myId;
    private final Team myTeam;
    private final String name;
    private final int yearOfBirth;

    /**
     * Constructor for the Rider class.
     *
     * @param name        Name of the rider
     * @param yearOfBirth Year of birth of the rider
     * @param team        The team the rider belongs to
     * @throws IllegalArgumentException If the name is empty or null, or the year of birth is < 1900
     */
    public Rider(String name, int yearOfBirth, Team team)
            throws IllegalArgumentException{
        // Check the arguments are legal
        if (name == null || name.isEmpty() || yearOfBirth < 1900) {
            throw new IllegalArgumentException("The name mustn't be empty or null, and the year must be >= 1900");
        }

        this.myId = UniqueIdGenerator.calculateUniqueId(idsUsed);
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.myTeam = team;
        idsUsed.add(this.myId); // Add this rider to the list of riders

        // Add the rider to the team
        //Team.getTeamById(team).addRider(this.myId);
    }

    ///**
    // * Pushes a rider into the system.
    // *
    // * @param id the ID of the rider to add
    // * @param rider the rider object to add
    // */
    //public static void pushRider(int id, Rider rider) {
    //    riders.put(id, rider);
    //}

    /**
     * Getter for the Id attribute on the rider class
     *
     * @return this instance of a rider's Id
     */
    public int getId() {
        return myId;
    }

    ///**
    // * Gets the rider specified by the Id
    // *
    // * @param riderId The rider to find
    // * @return The rider's object
    // * @throws IDNotRecognisedException If the rider is not in the system
    // */
    //public static Rider getRiderById(int riderId) throws IDNotRecognisedException {
    //    if (!riders.containsKey(riderId)) {
    //        throw new IDNotRecognisedException("Rider " + riderId + " is not part of the system");
    //    }
    //    return riders.get(riderId);
    //}

    /**
     * Get the details of the rider in a string form
     *
     * @return A string describing the rider
     */
    public String getDetails() {
        // Get the name of the team
        String teamName = myTeam.getName();

        return "Rider: " + name + " Year of Birth:" + yearOfBirth + " Team " + teamName;
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
     * @throws IDNotRecognisedException If the rider is not in the system
     */
    public void remove() throws IDNotRecognisedException {
        idsUsed.remove(Integer.valueOf(this.myId));
    }

    /**
     * Getter for the team the rider is in
     *
     * @return The team the rider is in
     */
    public Team getMyTeam() {
        return myTeam;
    }
}
