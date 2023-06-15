package com.example.taskmanager.controller;

import com.example.taskmanager.model.exceptions.BadRequestException;
import lombok.SneakyThrows;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

    public abstract class Util {
        private enum PRIORITY {HIGH, MEDIUM, LOW}

        public static final String LOGGED = "LOGGED";
        public static final String LOGGED_ID = "LOGGED_ID";
        public static final String IN_PROGRESS = "IN_PROGRESS";
        public static final String FINISHED = "FINISHED";
        public static final String ToDo = "ToDo";
        public static final String EMAIL = "test@example.com";
        public static final String PASS = "123456Sbb#";
        public static final String FIRST_NAME = "Ivo";
        public static final String LAST_NAME = "Ivanov";
        public static final String ROLE_NAME = "USER";

        @SneakyThrows
        public static Resource getResource(File csvFile) {
            Path path = Paths.get(csvFile.getAbsolutePath());
            return new ByteArrayResource(Files.readAllBytes(path));
        }

        public static boolean checkPriority(String priority) {
            for (int i = 0; i < PRIORITY.values().length; i++) {
                if (priority.equalsIgnoreCase(PRIORITY.values()[i].toString())) {
                    return true;
                }
            }
            throw new BadRequestException("Invalid priority type");
        }
    }