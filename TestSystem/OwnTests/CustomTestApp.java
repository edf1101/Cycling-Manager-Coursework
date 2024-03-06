package OwnTests;

import cycling.*;

/**
 * Main class to run all tests
 */
public class CustomTestApp {

    /**
     * Main method to run all tests
     *
     * @param args not used
     * @throws InvalidCheckpointTimesException
     * @throws DuplicatedResultException
     * @throws InvalidStageTypeException
     * @throws InvalidStageStateException
     * @throws InvalidLocationException
     * @throws InvalidLengthException
     * @throws InvalidNameException
     * @throws IllegalNameException
     */
    public static void main(String[] args)
            throws IDNotRecognisedException, NameNotRecognisedException, IllegalNameException, InvalidNameException,
            InvalidLengthException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException,
            DuplicatedResultException, InvalidCheckpointTimesException {
        System.out.println("The system compiled and started the execution...");

        // run each sets of tests twice to check they don't affect each other

        System.out.println("Starting team impl tests...");
        PortalTeamsTests.teamImplTests();
        PortalTeamsTests.teamImplTests();
        System.out.println("Passed.\n");

        System.out.println("Starting rider impl tests...");
        PortalRidersTests.riderImplTests();
        PortalRidersTests.riderImplTests();
        System.out.println("Passed.\n");

        System.out.println("Starting race impl tests...");
        PortalRacesTests.raceImplTests();
        PortalRacesTests.raceImplTests();
        System.out.println("Passed.\n");

        System.out.println("Starting stage impl tests...");
        PortalStagesTests.stageImplTests();
        PortalStagesTests.stageImplTests();
        System.out.println("Passed.\n");

        System.out.println("Starting checkpoint tests...");
        PortalCheckpointsTests.checkpointImplTests();
        PortalCheckpointsTests.checkpointImplTests();
        System.out.println("Passed.\n");

        System.out.println("Starting scoring tests...");
        PortalScoringTests.scoringImplTests();
        PortalScoringTests.scoringImplTests();
        System.out.println("Passed.\n");

        System.out.println("Starting misc tests...");
        PortalMiscTests.miscImplTests();
        PortalMiscTests.miscImplTests();
        System.out.println("Passed.\n");

        System.out.println("Starting serialisation tests...");
        PortalSerialisationTests.serialisationImplTests();
        PortalSerialisationTests.serialisationImplTests();
        System.out.println("Passed.\n");

        System.out.println("All tests passed.");
    }

}
