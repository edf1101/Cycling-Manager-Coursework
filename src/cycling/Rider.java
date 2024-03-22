package cycling;

import java.util.ArrayList;

/**
 * Class holding riders' details
 *
 * @author 730002704
 * @author 730003140
 * @version 1.0
 */
public class Rider extends Entity {
    private final ArrayList<Integer> stagesRegistered = new ArrayList<>();

    private final Team myTeam;
    private final String name;
    private final int yearOfBirth;

    /**
     * Constructor for the Rider class.
     *
     * @param name        Name of the rider
     * @param yearOfBirth Year of birth of the rider
     * @param team        The team the rider belongs to
     * @throws IllegalArgumentException If the name is empty or null, or the year of
     *                                  birth
     *                                  is less than 1900
     */
    public Rider(String name, int yearOfBirth, Team team)
            throws IllegalArgumentException {
        super(); // Call the entity constructor

        // Check the arguments are legal
        if (name == null || name.isEmpty() || yearOfBirth < 1900) {
            freeId(); // as super() has been called, we need to free the ID
            throw new IllegalArgumentException("The name mustn't be empty or null, and the year must be >= 1900");
        }

        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.myTeam = team;
    }

    /**
     * Get the details of the rider in a string form.
     *
     * @return A string describing the rider
     */
    public String getDetails() {
        // Get the name of the team
        String teamName = myTeam.getName();

        return "Rider: " + name + " Year of Birth:" + yearOfBirth + " Team " + teamName;
    }

    /**
     * Register the rider for a stage.
     *
     * @param stageId The stage to say I have registered for
     */
    public void registerForStage(int stageId) {
        stagesRegistered.add(stageId);
    }

    /**
     * Getter for registered stages.
     *
     * @return The stages the rider is registered for
     */
    public ArrayList<Integer> getRegisteredStages() {
        return stagesRegistered;
    }

    /**
     * Get the details of the rider in a string form.
     *
     * @return A string describing the rider
     */
    @Override
    public String toString() {
        return getDetails();
    }

    @Override
    public void remove() {
        int idsBefore = usedIds.size(); // Get the number of usedIds before the checkpoint is removed
        freeId(); // Remove the checkpoint from the usedIds list
        assert idsBefore == usedIds.size() - 1 : "Number of IDs incorrect after removal";
    }

    /**
     * Getter for the team the rider is in.
     *
     * @return The team the rider is in
     */
    public Team getMyTeam() {
        return myTeam;
    }
}
