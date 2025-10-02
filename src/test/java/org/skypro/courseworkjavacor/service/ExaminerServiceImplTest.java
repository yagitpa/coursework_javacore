package org.skypro.courseworkjavacor.service;

import org.skypro.courseworkjavacor.model.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты ExaminerServiceImpl")
class ExaminerServiceImplTest {

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private ExaminerServiceImpl examinerService;

    @Test
    @DisplayName("Возврат уникальных случайных вопросов")
    void whenGetQuestions_thenReturnRequestedAmountOfUniqueQuestions() {
        // Arrange
        Set<Question> mockQuestions = Set.of(
                new Question("Q1", "A1"),
                new Question("Q2", "A2"),
                new Question("Q3", "A3")
        );

        when(questionService.getAll()).thenReturn(mockQuestions);
        when(questionService.getRandomQuestion())
                .thenReturn(new Question("Q1", "A1"))
                .thenReturn(new Question("Q1", "A1"))
                .thenReturn(new Question("Q2", "A2"))
                .thenReturn(new Question("Q3", "A3"));

        // Act
        var result = examinerService.getQuestions(3);

        // Assert
        assertThat(result)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(mockQuestions)
                .doesNotHaveDuplicates();

        // Verify
        verify(questionService, times(1)).getAll();
        verify(questionService, times(4)).getRandomQuestion();
        verifyNoMoreInteractions(questionService);
    }

    @Test
    @DisplayName("Проверка граничного случая, когда запрос равен доступному количеству вопросов")
    void getQuestions_WhenAmountEqualsAvailable_ThenReturnAllQuestions() {
        // Arrange
        Set<Question> mockQuestions = Set.of(
                new Question("Q1", "A1"),
                new Question("Q2", "A2")
        );

        when(questionService.getAll()).thenReturn(mockQuestions);
        when(questionService.getRandomQuestion())
                .thenReturn(new Question("Q1", "A1"))
                .thenReturn(new Question("Q2", "A2"));

        // Act
        var result = examinerService.getQuestions(2);

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(mockQuestions);

        // Verify
        verify(questionService).getAll();
        verify(questionService, times(2)).getRandomQuestion();
        verifyNoMoreInteractions(questionService);
    }

    @Test
    @DisplayName("Проверка граничного случая, когда запрошено минимальное количество вопросов")
    void getQuestions_WhenAskSingleQuestion_ThenReturnOneQuestion() {
        // Arrange
        Set<Question> mockQuestions = Set.of(new Question("Q1", "A1"));

        when(questionService.getAll()).thenReturn(mockQuestions);
        when(questionService.getRandomQuestion()).thenReturn(new Question("Q1", "A1"));

        // Act
        var result = examinerService.getQuestions(1);

        // Assert
        assertThat(result)
                .hasSize(1)
                .containsExactly(new Question("Q1", "A1"));

        // Verify
        verify(questionService).getAll();
        verify(questionService, times(1)).getRandomQuestion();
    }

    @Test
    @DisplayName("Проверка граничного случая, когда в системе нет вопросов")
    void getQuestions_WhenAskEmptyCollection_ThenThrowException() {
        // Arrange
        when(questionService.getAll()).thenReturn(Set.of());

        // Act & Assert
        assertThatThrownBy(() -> examinerService.getQuestions(1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Запрошенное количество вопросов (1) превышает доступное (0)");

        // Verify
        verify(questionService).getAll();
        verify(questionService, never()).getRandomQuestion();
    }

    @Test
    @DisplayName("Выброс исключения если запрошено больше вопросов чем есть в системе")
    void getQuestions_WhenAmountExceedsAvailable_ThenThrowResponseStatusException() {
        // Arrange
        Set<Question> mockQuestions = Set.of(
                new Question("Q1", "A1"),
                new Question("Q2", "A2")
        );

        when(questionService.getAll()).thenReturn(mockQuestions);

        // Act & Assert
        assertThatThrownBy(() -> examinerService.getQuestions(5))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Запрошенное количество вопросов (5) превышает доступное (2)")
                .extracting("status")
                .isEqualTo(org.springframework.http.HttpStatus.BAD_REQUEST);

        // Verify
        verify(questionService).getAll();
        verify(questionService, never()).getRandomQuestion();
    }

    @Test
    @DisplayName("Выброс исключения если запрошено отрицательное количество вопросов")
    void getQuestions_WhenAmountIsNegative_ThenThrowException() {

        // Act & Assert
        assertThatThrownBy(() -> examinerService.getQuestions(-1))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Количество вопросов не может быть отрицательным: -1")
                .extracting("status")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(questionService, never()).getAll();
        verify(questionService, never()).getRandomQuestion();
    }

    @Test
    @DisplayName("Возврат пустой коллекции если запрошено 0 вопросов")
    void getQuestions_WhenAmountIsZero_ThenReturnEmptyCollection() {
        // Act
        var result = examinerService.getQuestions(0);

        // Assert
        assertThat(result)
                .isEmpty();

        // Verify
        verify(questionService, never()).getRandomQuestion();
    }


}
