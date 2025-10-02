package org.skypro.courseworkjavacor.service;

import org.skypro.courseworkjavacor.exceptions.QuestionAlreadyExistsException;
import org.skypro.courseworkjavacor.exceptions.QuestionNotFoundException;
import org.skypro.courseworkjavacor.model.Question;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class JavaQuestionService implements QuestionService {
    private final Set<Question> questionSet = new HashSet<>();
    private final Random random = new Random();

    @Override
    public Question add(String question, String answer) {
        return add(new Question(question, answer));
    }

    @Override
    public Question add(Question question) {
        if (!questionSet.add(question)) {
            throw new QuestionAlreadyExistsException("Вопрос уже добавлен в список: " + question);
        }
        return question;
    }

    @Override
    public Question remove(Question question) {
        if (!questionSet.remove(question)) {
            throw new QuestionNotFoundException("Вопрос не найден: " + question);
        }
        return question;
    }

    @Override
    public Collection<Question> getAll() {
        return Set.copyOf(questionSet);
    }

    @Override
    public Question getRandomQuestion() {
        if (questionSet.isEmpty()) {
            throw new QuestionNotFoundException("Не найдено подходящих вопросов");
        }
        int randomIndex = random.nextInt(questionSet.size());
        return questionSet.stream()
                .skip(randomIndex)
                .findFirst()
                .orElseThrow(() -> new QuestionNotFoundException("Не найдено случайных вопросов"));
    }
}
