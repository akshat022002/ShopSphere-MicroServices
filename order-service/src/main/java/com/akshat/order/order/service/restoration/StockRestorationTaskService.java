package com.akshat.order.order.service.restoration;

import com.akshat.order.common.exception.ResourceNotFoundException;
import com.akshat.order.order.entity.StockRestorationTask;
import com.akshat.order.order.enums.RestorationStatus;
import com.akshat.order.order.repository.StockRestorationTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockRestorationTaskService {

    private final StockRestorationTaskRepository taskRepository;

    public void markCompleted(Long taskId){

        StockRestorationTask task = taskRepository.findById(taskId)
                .orElseThrow(()-> new ResourceNotFoundException("Restoration task not found"));

        task.setStatus(RestorationStatus.COMPLETED);
        task.setLastError(null);

        taskRepository.flush();

    }

    public void recordFailure(Long taskId,String error){

        StockRestorationTask task = taskRepository.findById(taskId)
                .orElseThrow(()-> new ResourceNotFoundException("Restoration task not found"));

        task.setAttemptCount(task.getAttemptCount()+1);
        task.setLastError(
                error==null
                ?"Unknown restoration error"
                :error.substring(0,Math.min(error.length(),1000))
        );

        taskRepository.flush();
    }

}
