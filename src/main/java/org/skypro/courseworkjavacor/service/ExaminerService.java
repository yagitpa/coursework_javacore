package org.skypro.courseworkjavacor.service;

import org.skypro.courseworkjavacor.model.Question;

import java.util.Collection;

public interface ExaminerService {
    Collection<Question> getQuestions(int amount);
}
