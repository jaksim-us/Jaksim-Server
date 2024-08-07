package org.sopt.jaksim.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.jaksim.global.exception.NotFoundException;
import org.sopt.jaksim.global.message.ErrorMessage;
import org.sopt.jaksim.task.domain.Task;
import org.sopt.jaksim.task.domain.TaskTimer;
import org.sopt.jaksim.task.domain.Todo;
import org.sopt.jaksim.task.domain.TodoTask;
import org.sopt.jaksim.task.repository.TaskTimerRepository;
import org.sopt.jaksim.task.repository.TodoRepository;
import org.sopt.jaksim.task.repository.TodoTaskRepository;
import org.sopt.jaksim.user.facade.UserFacade;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoTaskService {
    private final TaskTimerRepository taskTimerRepository;
    private final TodoTaskRepository todoTaskRepository;
    private final TaskTimerService taskTimerService;
    private final UserFacade userFacade;
    public void excute(Todo todo, List<Long> newTaskIdList) {
        // 새로운 태스크들을 TodoTask로 생성
        List<TodoTask> newTodoTask = new ArrayList<>();
        for (Long taskId : newTaskIdList) {
            newTodoTask.add(TodoTask.create(taskId, todo.getId(), newTaskIdList.indexOf(taskId) + 1));
        }
        // 해당 targetDate에 생성된 유저의 TodoTask를 조회
        List<TodoTask> oldTodoTask = todoTaskRepository.findByTodoId(todo.getId());
        syncTodoTasks(oldTodoTask, newTodoTask, todo.getTargetDate());
    }

    public void syncTodoTasks(List<TodoTask> previous, List<TodoTask> current, LocalDate targetDate) {
        Map<Long, TodoTask> oldTaskMap = previous.stream()
                .collect(Collectors.toMap(TodoTask::getTaskId, task -> task));
        Map<Long, TodoTask> newTaskMap = current.stream()
                .collect(Collectors.toMap(TodoTask::getTaskId, task -> task));
        // 사용자가 뺀 태스크
        for (TodoTask oldTask : previous) {
            if (!newTaskMap.containsKey(oldTask.getTaskId())) {
                todoTaskRepository.delete(oldTask);
                oldTaskMap.remove(oldTask.getTaskId());
            }
        }

        // 사용자가 새롭게 추가한 태스크
        for (TodoTask newTask : current) {
            if (!oldTaskMap.containsKey(newTask.getTaskId())) {
                oldTaskMap.put(newTask.getTaskId(), newTask);
                if (!taskTimerService.isExistByTaskIdAndTargetDate(newTask.getTaskId(), targetDate)) {
                    taskTimerRepository.save(TaskTimer.create(3L, newTask.getTaskId()));
                }
            }
        }

        // 태스크 순서 변경된 것 반영
        for (TodoTask newTask : current) {
            if (oldTaskMap.containsKey(newTask.getTaskId())) {
                oldTaskMap.get(newTask.getTaskId()).setTaskOrder(newTask.getTaskOrder());
            }
        }

        todoTaskRepository.saveAll(oldTaskMap.values());
    }

    public List<TodoTask> getTodoTaskByTodoId(Long todoId) {
        return todoTaskRepository.findByTodoId(todoId);
    }

}
