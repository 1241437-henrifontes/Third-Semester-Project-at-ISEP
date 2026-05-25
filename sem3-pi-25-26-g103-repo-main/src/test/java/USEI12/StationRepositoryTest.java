package USEI12;

import Model.Graph.Node;
import Repositories.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StationRepository class.
 *
 * <p>This test suite validates singleton behavior, station and line
 * record processing, input validation, and repository state updates.</p>
 */
class StationRepositoryTest {

    private StationRepository repo;

    /**
     * Initializes the repository and clears its internal state
     * before each test.
     *
     * @throws Exception if reflection access fails
     */
    @BeforeEach
    void setUp() throws Exception {
        repo = StationRepository.getInstance();
        Method clear = StationRepository.class.getDeclaredMethod("clear");
        clear.setAccessible(true);
        clear.invoke(repo);
    }

    /**
     * Tests singleton behavior and basic getters.
     */
    @Test
    void singletonAndGetters() {
        StationRepository r2 = StationRepository.getInstance();
        assertSame(repo, r2);
        assertNotNull(repo.getStations());
        assertNotNull(repo.getEdges());
    }

    /**
     * Tests station record processing with valid and invalid inputs.
     *
     * @throws Exception if reflection invocation fails
     */
    @Test
    void processStationRecordValidAndInvalid() throws Exception {
        Method processStation = StationRepository.class.getDeclaredMethod("processStationRecord", String[].class);
        processStation.setAccessible(true);

        InvocationTargetException ex = assertThrows(InvocationTargetException.class,
                () -> processStation.invoke(repo, (Object) new String[]{"only", "2"}));
        assertTrue(ex.getCause().getMessage().contains("expected 6"));

        assertThrows(InvocationTargetException.class,
                () -> processStation.invoke(repo, (Object) new String[]{"", "Name", "0", "0", "0", "0"}));

        assertThrows(InvocationTargetException.class,
                () -> processStation.invoke(repo, (Object) new String[]{"ID", "", "0", "0", "0", "0"}));

        assertThrows(InvocationTargetException.class,
                () -> processStation.invoke(repo, (Object) new String[]{"ID", "Name", "200", "0", "0", "0"}));

        assertThrows(InvocationTargetException.class,
                () -> processStation.invoke(repo, (Object) new String[]{"ID", "Name", "0", "200", "0", "0"}));

        assertThrows(InvocationTargetException.class,
                () -> processStation.invoke(repo, (Object) new String[]{"ID", "Name", "0", "0", "-1", "0"}));

        assertThrows(InvocationTargetException.class,
                () -> processStation.invoke(repo, (Object) new String[]{"ID", "Name", "0", "0", "0", "-1"}));

        Object node = processStation.invoke(repo, (Object) new String[]{"S1", "Station1", "10", "20", "100", "200"});
        assertInstanceOf(Node.class, node);
        List<Node> list = repo.getStations();
        assertEquals(1, list.size());
        assertEquals("S1", list.get(0).getNode_id());
    }

    /**
     * Tests line record processing with valid and invalid inputs.
     *
     * @throws Exception if reflection invocation fails
     */
    @Test
    void processLineRecordValidAndInvalid() throws Exception {
        Method processStation = StationRepository.class.getDeclaredMethod("processStationRecord", String[].class);
        processStation.setAccessible(true);
        processStation.invoke(repo, (Object) new String[]{"S1", "A", "0", "0", "0", "0"});
        processStation.invoke(repo, (Object) new String[]{"S2", "B", "0", "0", "1", "1"});

        Method processLine = StationRepository.class.getDeclaredMethod("processLineRecord", String[].class);
        processLine.setAccessible(true);

        InvocationTargetException ex2 = assertThrows(InvocationTargetException.class,
                () -> processLine.invoke(repo, (Object) new String[]{"S1", "S2", "3"}));
        assertTrue(ex2.getCause().getMessage().contains("expected 5"));

        assertThrows(InvocationTargetException.class,
                () -> processLine.invoke(repo, (Object) new String[]{"", "S2", "1", "1", "1"}));

        assertThrows(InvocationTargetException.class,
                () -> processLine.invoke(repo, (Object) new String[]{"S9", "S2", "1", "1", "1"}));

        assertThrows(InvocationTargetException.class,
                () -> processLine.invoke(repo, (Object) new String[]{"S1", "", "1", "1", "1"}));

        assertThrows(InvocationTargetException.class,
                () -> processLine.invoke(repo, (Object) new String[]{"S1", "S9", "1", "1", "1"}));

        assertThrows(InvocationTargetException.class,
                () -> processLine.invoke(repo, (Object) new String[]{"S1", "S2", "0", "1", "1"}));

        assertThrows(InvocationTargetException.class,
                () -> processLine.invoke(repo, (Object) new String[]{"S1", "S2", "1", "0", "1"}));

        assertThrows(InvocationTargetException.class,
                () -> processLine.invoke(repo, (Object) new String[]{"S1", "S2", "1", "1", "0"}));

        Object edge = processLine.invoke(repo, (Object) new String[]{"S1", "S2", "3.5", "7", "2"});
        assertNotNull(edge);
        assertEquals(1, repo.getEdges().size());
        assertEquals("A", repo.getEdges().get(0).getVOrig().getName());
        assertEquals("B", repo.getEdges().get(0).getVDest().getName());
    }
}
