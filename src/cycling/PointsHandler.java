package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Function;

/**
 * This handles the sorting and ranking of points/times to riderIds using
 * generic types for modularity.
 *
 * @param <T> The type of object being sorted.
 *            LocalTime for times, Integer/int for points.
 * @author 730003140
 * @author 730002704
 * @version 1.0
 */
class PointsHandler<T extends Comparable<T>> {

    private int[] riderRanks; // The riderIds in order of their points/times
    private T[] riderScores; // The points/times of the riders
    private final boolean reversed; // Whether the order is reversed or not (false for times, true for points)
    private final Function<Integer, T> getScore; // The method to get the score from the rider
    private final ArrayList<Stage> stages; // The stages to get the riders from

    /**
     * Constructor for the PointsHandler class.
     *
     * @param reversed        Whether the order is reversed or not (false for times,
     *                        true for points)
     * @param scoringFunction The method to get the score from the rider
     * @param stages          The stages to get the riders from
     */
    protected PointsHandler(Function<Integer, T> scoringFunction, boolean reversed, ArrayList<Stage> stages) {
        this.reversed = reversed;
        this.getScore = scoringFunction;
        this.stages = stages;

        sortRiders();
    }

    /**
     * Sorts the riders by their points/times putting their ranked IDs and times
     * into the instance arrays.
     */
    private void sortRiders() {
        HashMap<T, Integer> riderTimes = new HashMap<T, Integer>();
        
        // Add all riders and their GC times to the hashmap
        for (Stage stage : stages) {
            for (int riderId : stage.getRegisteredRiders()) {
                T score = null;

                try {
                    score = getScore.apply(riderId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                if (!riderTimes.containsKey(score)) {
                    riderTimes.put(score, riderId);
                }
            }
        }

        // Sort the list according to whether it's reversed or not
        ArrayList<T> scoresList = new ArrayList<>(riderTimes.keySet());
        if (reversed) {
            scoresList.sort(Comparator.reverseOrder());
        } else {

            scoresList.sort(Comparator.naturalOrder());
        }

        // Convert ArrayList to array
        riderScores = scoresList.toArray(convertToArray(scoresList));

        // Create a list to store int values of the riderIds in order
        int[] riderIds = new int[scoresList.size()];
        for (int i = 0; i < scoresList.size(); i++) {
            riderIds[i] = riderTimes.get(scoresList.get(i));
        }
        riderRanks = riderIds;
    }

    /**
     * Converts an ArrayList of type T to an array of type T[].
     *
     * @param scoresList The ArrayList of type T to be converted
     * @return An array of type T[] containing the elements from the ArrayList
     */
    @SuppressWarnings("unchecked") // Suppresses the unchecked cast warning
    private T[] convertToArray(ArrayList<T> scoresList) {
        return (T[]) new Comparable[scoresList.size()];
    }

    /**
     * Getter for the riderRanks array.
     *
     * @return the ids of riders in order of rank
     */
    protected int[] getRiderRanks() {
        // assert all scoring metrics have same amount of riders registered
        assert riderRanks.length == riderScores.length :
                "scoring metrics have different amount of riders registered";

        return riderRanks;
    }

    /**
     * Getter for the riderScores array.
     *
     * @return the scores of the riders as LocalTime format
     */
    protected LocalTime[] getRiderTimes() {
        // assert all scoring metrics have same amount of riders registered
        assert riderRanks.length == riderScores.length:
                "scoring metrics have different amount of riders registered";

        LocalTime[] riderTimes = new LocalTime[riderScores.length];
        for (int i = 0; i < riderScores.length; i++) {
            riderTimes[i] = (LocalTime) riderScores[i];
        }
        assert riderTimes.length == riderRanks.length:
                "scoring metrics have different amount of riders registered";

        return riderTimes;
    }
}
