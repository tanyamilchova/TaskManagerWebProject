package com.example.taskmanager.service;

import com.example.taskmanager.model.entities.Task;
import com.example.taskmanager.model.entities.User;
import com.example.taskmanager.model.exceptions.NotFoundException;
import com.example.taskmanager.model.exceptions.UnauthorizedException;
import com.example.taskmanager.model.repositories.TaskRepository;
import com.example.taskmanager.model.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

@org.springframework.stereotype.Service
public abstract class Service {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    public ModelMapper mapper;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public <T> T ifPresent(Optional<T> opt){
        if(!opt.isPresent()){
            throw new NotFoundException("Resource not found");
        }
        return opt.get();
    }
    public User userById(final long id){
        Optional<User>opt=userRepository.findById(id);
        if(!opt.isPresent()){
            throw new NotFoundException("No user");
        }
        final User u=opt.get();
        return u;
    }

    public boolean ifOwner(final long userId,final Task task){
        final User u=userById(userId);
        if(task.getUser().getId()!=userId){
            throw new UnauthorizedException("Unauthorized role");
        }
        return true;
    }

}
