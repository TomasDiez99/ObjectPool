import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class PoolTest {
    private ObjectPool<TestCase> pool;

    private static final int lowerLimit = 5;
    private static final int upperLimit = 10;

    @BeforeEach
    public void poolSetup() {
        pool = new Pool<>(TestCase::new, e -> e.setAlive(true), e -> e.setAlive(false), lowerLimit, upperLimit);
    }

    @Test
    public void poolCreation() {
        //Then
        assertNotNull(pool);
    }

    @Test
    public void getItemEmptyPool() {
        //When
        TestCase item = pool.getItem();

        //Then
        assertNotNull(item);
        assertTrue(item.isAlive());
    }

    @Test
    public void getAndDisposeItem() {
        //Given
        TestCase item = pool.getItem();

        //When
        pool.disposeItem(item);

        //Then
        assertFalse(item.isAlive());
    }

    @Test
    public void multipleGet() {
        //Given
        int iterations = 5000;
        TestCase[] testCases = new TestCase[iterations];

        //When
        for (int i = 0; i < iterations; ++i) {
            testCases[i] = pool.getItem();
        }

        //Then
        for (int i = 0; i < iterations; ++i) {
            assertNotNull(testCases[i]);
            assertTrue(testCases[i].isAlive());
        }
    }

    @Test
    public void multipleDisposes() {
        //Given
        int iterations = 5000;
        TestCase[] testCases = new TestCase[iterations];
        for (int i = 0; i < iterations; ++i) {
            testCases[i] = pool.getItem();
        }

        //When
        for (int i = 0; i < iterations; ++i) {
            pool.disposeItem(testCases[i]);
        }

        //Then
        for (int i = 0; i < iterations; ++i) {
            assertNotNull(testCases[i]);
            assertFalse(testCases[i].isAlive());
        }
    }

    @Test
    public void instancesCountAfterMultipleDisposes() {
        //Given
        int instancesBefore = TestCase.Instances;
        int iterations = 5000;
        HashSet<TestCase> hashSet = new HashSet<>();
        for (int i = 0; i < iterations; ++i) {
            TestCase item = pool.getItem();
            hashSet.add(item);
            pool.disposeItem(item);
        }
        int instancesAfter = TestCase.Instances;
        int createdInstances = instancesAfter - instancesBefore;
        //Then
        assertTrue(createdInstances <= upperLimit + 1);
        assertTrue(createdInstances >= lowerLimit - 1);
        assertNotEquals(hashSet.size(), iterations);
        assertTrue(hashSet.size() <= upperLimit + 1);

    }

    @Test
    public void instancesCountAfterMultipleGetWithoutDisposes() {
        //Given
        int instancesBefore = TestCase.Instances;
        int iterations = 5000;
        HashSet<TestCase> hashSet = new HashSet<>();
        for (int i = 0; i < iterations; ++i) {
            TestCase item = pool.getItem();
            hashSet.add(item);
        }
        int instancesAfter = TestCase.Instances;
        int createdInstances = instancesAfter - instancesBefore;
        //Then
        assertTrue(createdInstances >= lowerLimit - 1);
        assertFalse(createdInstances <= upperLimit + 1);
        assertEquals(hashSet.size(), iterations);
        assertFalse(hashSet.size() <= upperLimit + 1);
    }

}
