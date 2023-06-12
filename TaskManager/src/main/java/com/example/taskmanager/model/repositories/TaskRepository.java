package com.example.taskmanager.model.repositories;

import com.example.taskmanager.model.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Integer> {
Optional<Task>findById(long id);
@Query(value = "SELECT t.* FROM tasks AS t WHERE (t.status=:statusToDo OR t.status=:statusInProgress) AND t.user_id=:userId",nativeQuery = true)
List<Task> findByStatus( String statusToDo, String statusInProgress,  long userId);

}
