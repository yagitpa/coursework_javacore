package org.skypro.courseworkjavacor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.courseworkjavacor.exceptions.QuestionAlreadyExistsException;
import org.skypro.courseworkjavacor.exceptions.QuestionNotFoundException;
import org.skypro.courseworkjavacor.model.Question;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты JavaQuestionService")
class JavaQuestionServiceTest {

    private JavaQuestionService javaQuestionService;

    @BeforeEach
    void setJavaQuestionService() {
        javaQuestionService = new JavaQuestionService();
    }

    @Test
    @DisplayName("Проверка механизма добавления вопроса")
    void whenAskAddQuestion_ThenAddQuestion() {
        // ACt
        Question question = javaQuestionService.add("Что такое Java?", "Язык программирования");

        // Assert
        assertThat(question)
                .isNotNull()
                .extracting(Question::getQuestion, Question::getAnswer)
                .containsExactly("Что такое Java?", "Язык программирования");

        assertThat(javaQuestionService.getAll())
                .hasSize(1)
                .containsExactly(question);
    }

    @Test
    @DisplayName("Проверка механизма добавления вопроса через готовый объект Question")
    void whenAskAddQuestionWithObject_ThenAddQuestion() {
        // Arrange
        Question question = new Question("Что такое ООП?", "Объектно-ориентированное программирование");
        Question addedQuestion = javaQuestionService.add(question);

        // Assert
        assertThat(addedQuestion)
                .isEqualTo(question)
                .isSameAs(question);

        assertThat(javaQuestionService.getAll())
                .hasSize(1)
                .containsOnly(question);
    }

    @Test
    @DisplayName("Тест выброса исключения при добавлении дублирующего вопроса")
    void whenAddDuplicateQuestion_ThenThrowQuestionAlreadyExistsException() {
        // Arrange
        Question question = new Question("Что такое Java?", "Язык программирования");
        javaQuestionService.add(question);

        // Act & Assert
        assertThatThrownBy(() -> javaQuestionService.add(question))
                .isInstanceOf(QuestionAlreadyExistsException.class)
                .hasMessage("Вопрос уже добавлен в список: " + question);
    }

    @Test
    @DisplayName("Удаление вопроса из коллекции")
    void whenAskRemoveExistingQuestion_ThenRemoveQuestion() {
        // Arrange
        Question question = javaQuestionService.add("Что такое Java?", "Язык программирования");

        // Act
        Question removedQuestion = javaQuestionService.remove(question);

        // Assert
        assertThat(removedQuestion)
                .isEqualTo(question);

        assertThat(javaQuestionService.getAll())
                .isEmpty();
    }

    @Test
    @DisplayName("Удаление несуществующего вопроса приводит к выбросу исключения")
    void whenAskRemoveNonExistentQuestion_ThenThrowQuestionNotFoundException() {
        // Arrange
        Question question = new Question("Q1", "A1");

        // Act & Assert
        assertThatThrownBy(() -> javaQuestionService.remove(question))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessage("Вопрос не найден: " + question);
    }

    @Test
    @DisplayName("Проверка вывода всех вопросов")
    void whenAskGetAllQuestions_ThenReturnAllQuestions() {
        // Arrange
        Question question1 = javaQuestionService.add("Q1", "A1");
        Question question2 = javaQuestionService.add("Q2", "A2");

        // Act
        Collection<Question> allQuestions = javaQuestionService.getAll();

        // Assert
        assertThat(allQuestions)
                .hasSize(2)
                .containsExactlyInAnyOrder(question1, question2)
                .doesNotContainNull()
                .isUnmodifiable();

        assertThatThrownBy(() -> allQuestions.clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Случайный вопрос должен быть из коллекции и не быть null")
    void whenAskGetRandomQuestion_ThenReturnRandomQuestion() {
        // Arrange
        javaQuestionService.add("Q1", "A1");
        javaQuestionService.add("Q2", "A2");
        javaQuestionService.add("Q3", "A3");

        Collection<Question> allQuestions = javaQuestionService.getAll();

        // Act
        Question randomQuestion = javaQuestionService.getRandomQuestion();

        // Assert
        assertThat(randomQuestion)
                .isNotNull()
                .isIn(allQuestions);

        assertThat(allQuestions)
                .hasSize(3)
                .contains(randomQuestion);
    }

    @Test
    @DisplayName("Возврат защищенной копии списка вопросов")
    void whenAskGetAllQuestions_ThenReturnDefensiveCopy() {
        // Arrange
        Question question = javaQuestionService.add("Q1", "A1");
        Collection<Question> firstCall = javaQuestionService.getAll();
        Collection<Question> secondCall = javaQuestionService.getAll();

        // Assert
        assertThat(firstCall)
                .isNotSameAs(secondCall)
                .containsExactly(question);
    }

    @Test
    @DisplayName("Проверка корректности equals() и hashCode()")
    void questionEquality_ShouldMaintainContract() {
        // Arrange
        Question question1 = new Question("Что такое Java?", "Язык программирования");
        Question question2 = new Question("Что такое Java?", "Язык программирования");
        Question question3 = new Question("Что такое Python?", "Язык программирования");
        Question question4 = new Question("Что такое Java?", "Высокоуровневый язык программирования");

        // Assert
        assertThat(question1)
                .isEqualTo(question1)
                .isEqualTo(question2)
                .hasSameHashCodeAs(question2)
                .isNotEqualTo(question3)
                .isNotEqualTo(question4)
                .isNotEqualTo(null)
                .isNotEqualTo("Что такое ООП?");

        assertThat(question2).isEqualTo(question1);

        Set<Question> questionSet = new HashSet<>();
        questionSet.add(question1);
        questionSet.add(question2);

        assertThat(questionSet).hasSize(1);
        assertThat(questionSet).containsOnly(question1);
    }
}
