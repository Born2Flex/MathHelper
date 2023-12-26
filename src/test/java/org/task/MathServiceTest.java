package org.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.task.calculations.Evaluator;
import org.task.calculations.Parser;
import org.task.dto.EquationDto;
import org.task.exceptions.EquationException;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MathServiceTest {
    private Parser parser;
    private Evaluator evaluator;
    private MathRepository repository;
    @BeforeEach
    void prepare() {
        parser = mock(Parser.class);
        evaluator = mock(Evaluator.class);
        repository = mock(MathRepository.class);
    }

    @Test
    void saveEquationTest_CorrectEquation() {
        MathService service = new MathService(parser, evaluator, repository);
        when(parser.validateParentheses(anyString())).thenReturn(true, true);
        assertDoesNotThrow(() -> {
            service.saveEquation("2 * x + 5 = 17");
        });
        verify(repository, times(1)).saveEquation(any(), any(), any());
        verify(parser, times(2)).validateParentheses(anyString());
        verify(parser, times(2)).parseExpr(anyString());
    }

    @Test
    void saveEquationTest_EquationAlreadyExist() {
        MathService mathService = new MathService(parser, evaluator, repository);
        when(parser.validateParentheses(anyString())).thenReturn(true);
        when(repository.equationAlreadyExists(anyString())).thenReturn(true);
        EquationException exception = assertThrows(EquationException.class, () -> mathService.saveEquation("2*x+5=17"));
        verify(repository).equationAlreadyExists(anyString());
        verify(repository, never()).saveEquation(anyString(), anyString(), anyString());
        assertEquals("Equation already exists", exception.getMessage());
    }

    @Test
    void testSaveEquation_InvalidEquation() {
        MathService mathService = new MathService(parser, evaluator, repository);
        when(parser.validateParentheses(anyString())).thenReturn(false);
        EquationException exception = assertThrows(EquationException.class, () -> mathService.saveEquation("invalid equation"));
        verify(parser, never()).validateParentheses(anyString());
        verify(repository, never()).equationAlreadyExists(anyString());
        verify(repository, never()).saveEquation(anyString(), anyString(), anyString());
        assertEquals("Incorrect equation!", exception.getMessage());
    }

    @Test
    void testSaveEquation_InvalidParentheses() {
        MathService mathService = new MathService(parser, evaluator, repository);
        when(parser.validateParentheses(anyString())).thenReturn(false);
        EquationException exception = assertThrows(EquationException.class, () -> mathService.saveEquation("2*(x)+5+х)+5=10"));
        verify(parser).validateParentheses(anyString());
        verify(repository).equationAlreadyExists(anyString());
        verify(repository, never()).saveEquation(anyString(), anyString(), anyString());
        assertEquals("Incorrect parentheses!", exception.getMessage());
    }


    @Test
    void testSaveRoot_ValidRoot() {
        int equationId = 1;
        double root = 2.0;
        MathService mathService = new MathService(parser, evaluator, repository);
        when(repository.findEquationById(equationId)).thenReturn(new EquationDto());
        when(repository.rootAlreadyExists(anyInt(), anyDouble())).thenReturn(false);
        boolean result = mathService.saveRoot(equationId, root);
        verify(repository).findEquationById(equationId);
        verify(evaluator).validateRootForEquation(any(), any(), anyDouble());
        verify(repository, never()).saveRoot(equationId, root);
        assertFalse(result);
    }

    @Test
    void testSaveRoot_InvalidRoot() {
        MathService mathService = new MathService(parser, evaluator, repository);
        when(repository.findEquationById(anyInt())).thenReturn(new EquationDto());
        when(repository.rootAlreadyExists(anyInt(), anyDouble())).thenReturn(false);
        when(evaluator.validateRootForEquation(anyString(), anyString(), anyDouble())).thenReturn(false);
        int equationId = 1;
        double root = 2.0;
        boolean result = mathService.saveRoot(equationId, root);
        verify(repository).findEquationById(equationId);
        verify(evaluator).validateRootForEquation(any(), any(), anyDouble());
        verify(repository, never()).rootAlreadyExists(equationId, root);
        verify(repository, never()).saveRoot(equationId, root);
        assertFalse(result);
    }

    @Test
    void testSaveRoot_RootAlreadyExists() {
        MathService mathService = new MathService(parser, evaluator, repository);
        when(repository.findEquationById(anyInt())).thenReturn(new EquationDto());
        when(repository.rootAlreadyExists(anyInt(), anyDouble())).thenReturn(true);
        when(evaluator.validateRootForEquation(any(), any(), anyDouble())).thenReturn(true);
        int equationId = 1;
        double root = 2.0;
        boolean result = mathService.saveRoot(equationId, root);
        verify(repository).findEquationById(equationId);
        verify(evaluator).validateRootForEquation(any(), any(), anyDouble());
        verify(repository).rootAlreadyExists(equationId, root);
        verify(repository, never()).saveRoot(equationId, root);
        assertFalse(result);
    }

    @Test
    void testSaveRoot_SuccessfulSaveInDB() {
        MathService mathService = new MathService(parser, evaluator, repository);
        when(repository.findEquationById(anyInt())).thenReturn(new EquationDto());
        when(repository.rootAlreadyExists(anyInt(), anyDouble())).thenReturn(false);
        when(evaluator.validateRootForEquation(any(), any(), anyDouble())).thenReturn(true);
        int equationId = 1;
        double root = 2.0;
        boolean result = mathService.saveRoot(equationId, root);
        verify(repository).findEquationById(equationId);
        verify(evaluator).validateRootForEquation(any(), any(), anyDouble());
        verify(repository).rootAlreadyExists(equationId, root);
        verify(repository).saveRoot(equationId, root);
        assertTrue(result);
    }

    @Test
    void testFindEquationsByRoot() {
        MathService mathService = new MathService(parser, evaluator, repository);
        double root = 2.0;
        List<String> equations = new ArrayList<>();
        equations.add("2*x+5=17");
        equations.add("-1.3*5/x=1.2");
        when(repository.findEquationsByRoot(root)).thenReturn(equations);
        List<String> result = mathService.findEquationsByRoot(root);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("2*x+5=17"));
        assertTrue(result.contains("-1.3*5/x=1.2"));
    }

    @Test
    void testFindEquationsWithSingleRoot() {
        MathService mathService = new MathService(parser, evaluator, repository);
        List<String> equations = new ArrayList<>();
        equations.add("2*x+5=17");
        when(repository.findEquationsWithSingleRoot()).thenReturn(equations);
        List<String> result = mathService.findEquationsWithSingleRoot();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains("2*x+5=17"));
    }

    @Test
    void testFindAllEquations() {
        MathService mathService = new MathService(parser, evaluator, repository);
        List<EquationDto> equationDtos = new ArrayList<>();
        EquationDto dto = new EquationDto();
        dto.setEquationId(1);
        dto.setLeftPart("2*x");
        dto.setRightPart("5");
        dto.setEquation("2*x+5=17");
        equationDtos.add(dto);
        when(repository.findAllEquations()).thenReturn(equationDtos);
        List<EquationDto> result = mathService.findAllEquations();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getEquationId());
        assertEquals("2*x", result.get(0).getLeftPart());
        assertEquals("5", result.get(0).getRightPart());
        assertEquals("2*x+5=17", result.get(0).getEquation());
    }
}