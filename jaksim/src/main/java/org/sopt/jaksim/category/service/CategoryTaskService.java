package org.sopt.jaksim.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.jaksim.category.domain.CategoryTask;
import org.sopt.jaksim.category.repository.CategoryTaskRepository;
import org.sopt.jaksim.global.exception.NotFoundException;
import org.sopt.jaksim.global.message.ErrorMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryTaskService {
    private final CategoryTaskRepository categoryTaskRepository;

    public List<Long> deleteAndGetTaskIdList(Long categoryId) {
        List<CategoryTask> categoryTaskList = categoryTaskRepository.findByCategoryId(categoryId);
        if (categoryTaskList.isEmpty()) {
            throw new NotFoundException(ErrorMessage.NOT_FOUND);
        }
        List<Long> taskIdList = categoryTaskList.stream()
                .map(CategoryTask::getTaskId).
                collect(Collectors.toList());
        categoryTaskRepository.deleteAll(categoryTaskList);
        return taskIdList;
    }
}
