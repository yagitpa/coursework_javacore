package org.skypro.courseworkjavacor.service;

import org.skypro.courseworkjavacor.model.Question;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class ExaminerServiceImpl implements ExaminerService {
    private final QuestionService questionService;

    public ExaminerServiceImpl(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public Collection<Question> getQuestions(int amount) {
        if (amount < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Количество вопросов не может быть отрицательным: " + amount
            );
        }
        if (amount == 0) {
            return Set.of();
        }

        Collection<Question> allQuestions = questionService.getAll();
        if (amount > allQuestions.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Запрошенное количество вопросов (" + amount + ") превышает доступное (" + allQuestions.size() + ")"
            );
        }

        Set<Question> randomQuestions = new HashSet<>();
        while (randomQuestions.size() < amount) {
            Question randomQuestion = questionService.getRandomQuestion();
            randomQuestions.add((randomQuestion));
        }
        return randomQuestions;
    }
}
