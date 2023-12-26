package org.task;

import org.junit.jupiter.api.*;
import org.task.db.DBInitializer;
import org.task.dto.EquationDto;
import org.task.exceptions.InvalidPropertiesException;
import org.task.properties.PropertiesLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class MathRepositoryTest {
    private static Properties properties;
    private MathRepository mathRepository;
    @BeforeAll
    static void setup() throws SQLException {
        properties = new PropertiesLoader().loadProperties();
        new DBInitializer(properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password"))
                .initialize();
        Connection connection = DriverManager.getConnection(properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password"));
        connection.createStatement().executeUpdate("ALTER TABLE equations SET REFERENTIAL_INTEGRITY FALSE");
    }

    @BeforeEach
    void prepare() {
        mathRepository = new MathRepository(properties.getProperty("url"),
                properties.getProperty("username"),
                properties.getProperty("password"));
        try {
            Connection connection = DriverManager.getConnection(properties.getProperty("url"),
                    properties.getProperty("username"),
                    properties.getProperty("password"));

            connection.createStatement().execute("TRUNCATE TABLE roots RESTART IDENTITY");
            connection.createStatement().execute("TRUNCATE TABLE equations RESTART IDENTITY");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void repositoryCreationTest() {
        assertDoesNotThrow(() -> {
            new MathRepository(properties.getProperty("url"),
                            properties.getProperty("username"),
                            properties.getProperty("password"));
        });
    }

    @Test
    void repositoryCreationTest_IncorrectProperties() {
        assertThrows(InvalidPropertiesException.class, () -> {
            new MathRepository("jdbc:h2:mem:testdb;MODE=asdasdPostgreSQL",
                    properties.getProperty("username"),
                    properties.getProperty("password"));
        });
    }

    @Test
    void saveEquationTest() {
        mathRepository.saveEquation("5*x", "5", "5*x=5");
        EquationDto equationDto = mathRepository.findEquationById(1);
        assertNotNull(equationDto);
        assertEquals(1, equationDto.getEquationId());
        assertEquals("5*x", equationDto.getLeftPart());
        assertEquals("5", equationDto.getRightPart());
        assertEquals("5*x=5", equationDto.getEquation());
    }

    @Test
    void findEquationByIdTest_EquationDontExists() {
        EquationDto equationDto = mathRepository.findEquationById(999);
        assertNull(equationDto);
    }

    @Test
    void saveEquationAndFindAllTest() {
        mathRepository.saveEquation("3*x", "7", "3*x=7");
        mathRepository.saveEquation("4*x", "8", "4*x=8");
        List<EquationDto> equationList = mathRepository.findAllEquations();
        assertNotNull(equationList);
        assertEquals(2, equationList.size());
        EquationDto firstEquation = equationList.get(0);
        assertEquals("3*x", firstEquation.getLeftPart());
        assertEquals("7", firstEquation.getRightPart());
        assertEquals("3*x=7", firstEquation.getEquation());
        EquationDto secondEquation = equationList.get(1);
        assertEquals("4*x", secondEquation.getLeftPart());
        assertEquals("8", secondEquation.getRightPart());
        assertEquals("4*x=8", secondEquation.getEquation());
    }

    @Test
    void saveRootTest() {
        mathRepository.saveEquation("6*x", "5", "6*x=5");
        EquationDto equationDto = mathRepository.findEquationById(1);
        assertNotNull(equationDto);
        mathRepository.saveRoot(equationDto.getEquationId(), 2.0);
        List<String> equationsWithRoot = mathRepository.findEquationsByRoot(2.0);
        assertNotNull(equationsWithRoot);
        assertEquals(1, equationsWithRoot.size());
        assertEquals("6*x=5", equationsWithRoot.get(0));
    }

    @Test
    void saveRootTest_DuplicateRoot() {
        mathRepository.saveEquation("4*x", "7", "4*x=7");
        EquationDto equationDto = mathRepository.findEquationById(1);
        assertNotNull(equationDto);
        mathRepository.saveRoot(equationDto.getEquationId(), 2.0);
        List<String> equationsWithRoot = mathRepository.findEquationsByRoot(2.0);
        assertNotNull(equationsWithRoot);
        assertEquals(1, equationsWithRoot.size());
        assertEquals("4*x=7", equationsWithRoot.get(0));
    }

    @Test
    void findEquationsWithSingleRootTest() {
        mathRepository.saveEquation("2*x", "5", "2*x+5");
        mathRepository.saveEquation("3*x", "7", "3*x+7");
        mathRepository.saveEquation("4*x", "8", "4*x+8");
        mathRepository.saveRoot(1, 2.0);
        mathRepository.saveRoot(2, 3.0);
        mathRepository.saveRoot(3, 4.0);
        List<String> equationsWithSingleRoot = mathRepository.findEquationsWithSingleRoot();
        assertNotNull(equationsWithSingleRoot);
        assertEquals(3, equationsWithSingleRoot.size());
        assertEquals("3*x+7", equationsWithSingleRoot.get(1));
    }

    @Test
    void findEquationsWithSingleRootTest_NoEquations() {
        List<String> equationsWithSingleRoot = mathRepository.findEquationsWithSingleRoot();
        assertNotNull(equationsWithSingleRoot);
        assertTrue(equationsWithSingleRoot.isEmpty());
    }

    @Test
    void findEquationsWithSingleRootTest_MultipleRoots() {
        mathRepository.saveEquation("5*x", "10", "5*x+10");
        mathRepository.saveRoot(4, 2.0);
        mathRepository.saveRoot(4, 3.0);
        List<String> equationsWithSingleRoot = mathRepository.findEquationsWithSingleRoot();
        assertNotNull(equationsWithSingleRoot);
        assertTrue(equationsWithSingleRoot.isEmpty());
    }

    @Test
    void equationAlreadyExistsTest() {
        mathRepository.saveEquation("2*x", "5", "2*x+5");
        boolean exists = mathRepository.equationAlreadyExists("2*x+5");
        assertTrue(exists);
    }

    @Test
    void equationAlreadyExistsTest_NotExists() {
        mathRepository.saveEquation("2*x", "5", "2*x+5");
        boolean exists = mathRepository.equationAlreadyExists("3*x+7");
        assertFalse(exists);
    }

    @Test
    void testRootAlreadyExists() {
        mathRepository.saveEquation("2*x", "5", "2*x+5");
        mathRepository.saveRoot(1, 2.0);
        boolean exists = mathRepository.rootAlreadyExists(1, 2.0);
        assertTrue(exists);
    }

    @Test
    void testRootAlreadyExists_NotExists() {
        boolean exists = mathRepository.rootAlreadyExists(1, 3.0);
        assertFalse(exists);
    }
}