package com.example.taskmanager.controller;

import com.example.taskmanager.model.entities.Task;
import jakarta.mail.Quota;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

    public abstract class Util {
    @SneakyThrows
        public static Resource getResaurce(File csvFile){
            Path path = Paths.get(csvFile.getAbsolutePath());
            return new ByteArrayResource(Files.readAllBytes(path));
        }
    }
