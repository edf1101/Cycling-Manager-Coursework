package cycling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to generate unique IDs for the system.
 *
 * @author 730003140 & 730002704
 * @version 1.0
 */
public class UniqueIdGenerator<T> {

    public static <T> int calculateUniqueId(HashMap<Integer, T> genericHashMap) {

        // Go through all the teams and store their IDs in an array
        // also store the maximum ID
        ArrayList<Integer> genericIds = new ArrayList<Integer>();
        int maxId = -1;

        for (Integer currentId : genericHashMap.keySet()) {
            genericIds.add(currentId);
            if (currentId > maxId) {
                maxId = currentId;
            }
        }

        // If there are gaps in the IDs ie ids = [0, 1, 3, 4] then return the first gap
        // (in this case 2)
        for (int i = 0; i < maxId; i++) {
            if (!genericIds.contains(i)) {
                return i;
            }
        }
        // If there are no gaps then return the next number in the sequence
        return maxId + 1;
    }

}