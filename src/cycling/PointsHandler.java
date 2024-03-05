package cycling;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.Function;
/**
 * This handles the sorting and ranking of points/times to riderIds using Generic
 * types for modularity.
 *
 * @param <T> The type of object being sorted.
 *          LocalTime for times, Integer/int for points.
 * @author 730003140 & 730002704
 * @version 1.0
 */
public class PointsHandler<T extends Comparable<T>> {
// There was a lot of code in Race class that could be shortened with a generic type.
// This class implements that idea
    private int[] riderRanks; // The riderIds in order of their points/times
    private T[] riderScores; // The points/times of the riders
    private final boolean reversed; // whether the order is reversed or not (false for times, true for points)
    private final Function<Integer,T> getScore; // The method to get the score from the rider
    private final ArrayList<Integer> stageIds; // The stages to get the riders from
    /**
     * Constructor for the PointsHandler class
     * @param reversed Whether the order is reversed or not (false for times, true for points)
     * @param scoringFunction The method to get the score from the rider
     * @param stageIds The stages to get the riders from
     */
    public PointsHandler(Function<Integer,T>  scoringFunction,boolean reversed, ArrayList<Integer> stageIds) {
        this.reversed = reversed;
        this.getScore = scoringFunction;
        this.stageIds = stageIds;

        sortRiders();
    }

    /**
     * Sorts the riders by their points/times putting their ranked ids and times into the instance arrays.
     */
    private void sortRiders() {
        HashMap<T, Integer> riderTimes= new HashMap<T,Integer>();
        // may well be a better way to do this idk
        // Add all riders and their GC times to the hashmap
        for(int stageId : stageIds){
            Stage stage = null;
            try {
                stage = Stage.getStageById(stageId);
            } catch (IDNotRecognisedException e) {
                assert false : "Stage ID not recognised - shouldn't happen";
            }
            for(int riderId : stage.getRegisteredRiders()){
                T score = null;
                try {
                    score = getScore.apply(riderId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if(!riderTimes.containsKey(score)){


                    riderTimes.put(score,riderId);
                }
            }

        }

        // Sort the list according to whether it's reversed or not
        ArrayList<T> scoresList = new ArrayList<>(riderTimes.keySet());
        if (reversed)
            scoresList.sort(Comparator.reverseOrder());
        else
            scoresList.sort(Comparator.naturalOrder());

        // Convert ArrayList to array
        riderScores = scoresList.toArray((T[]) new Comparable[scoresList.size()]);

        // create a list to store int values of the riderIds in order
        int[] riderIds = new int[scoresList.size()];
        for(int i = 0; i < scoresList.size(); i++){
            riderIds[i] = riderTimes.get(scoresList.get(i));
        }
        riderRanks = riderIds;
    }

    /**
     * Getter for the riderRanks array
     *
     * @return the ids of riders in order of rank
     */
    public int[] getRiderRanks() {
        return riderRanks;
    }

    /**
     * Getter for the riderScores array
     *
     * @return the scores of the riders as LocalTime format
     */
    public LocalTime[] getRiderTimes() {
        LocalTime[] riderTimes = new LocalTime[riderScores.length];
        for(int i = 0; i < riderScores.length; i++){
            riderTimes[i] = (LocalTime) riderScores[i];
        }
        return riderTimes;
    }
}
