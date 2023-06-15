package com.example.taskmanager.service;

import com.example.taskmanager.model.DTOs.*;
import com.example.taskmanager.model.entities.User;
import com.example.taskmanager.model.exceptions.BadRequestException;
import com.example.taskmanager.model.exceptions.NotFoundException;
import com.example.taskmanager.model.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService extends AbstractService {

@Autowired
JavaMailSender mailSender;

    public UserWithoutPasswordDTO login(final LoginDTO loginDTO) {
        if (!userRepository.existsByEmail(loginDTO.getEmail())) {
            throw new UnauthorizedException("Wrong credentials");
        }
        final User u = ifPresent(userRepository.findByEmail(loginDTO.getEmail()));
        if (! u.isEnable()) {
            throw new UnauthorizedException("Wrong credentials");
        }
        if(passwordEncoder.matches(loginDTO.getPassword(), u.getPassword())){
            System.out.println();
            return mapper.map(u, UserWithoutPasswordDTO.class);
        }
        else {
            throw new UnauthorizedException("Wrong credentials");
        }
    }

    public UserWithoutPasswordDTO register(final UserRegisterDTO registerData) {
        if (!registerData.getPassword().equals((registerData).getConfirmPassword())) {
            throw new BadRequestException("Password mismatched");
        }
        if (userRepository.existsByEmail(registerData.getEmail())) {
            throw new BadRequestException("Email already exist");
        }
        final User u = mapper.map(registerData, User.class);
        u.setDateTimeRegistration(LocalDateTime.now());
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        u.setConfirmatronToken(generateConfirmationToken());

        userRepository.save(u);
        sendConfirmationEmail(u);
        return mapper.map(u, UserWithoutPasswordDTO.class);
    }

    public UserWithoutPasswordDTO changePassword(final ChangePassDTO changePassData, final long id) {
        final User u=ifPresent(userRepository.findById(id));
        u.setPassword(passwordEncoder.encode(changePassData.getNewPassword()));
        userRepository.save(u);

        return mapper.map(u, UserWithoutPasswordDTO.class);
    }

    public UserWithoutPasswordDTO editProfile(final EditProfilDTO editProfilDTO, final long id) {
        if (userRepository.existsByEmail(editProfilDTO.getEmail()) &&
                userRepository.findByEmail(editProfilDTO.getEmail()).get().getId()!=id) {
            throw new UnauthorizedException("Email already exist");
        }
        final User u=ifPresent(userRepository.findById(id));
        u.setEmail(editProfilDTO.getEmail());
        u.setFirstName(editProfilDTO.getFirstName());
        u.setLastName(editProfilDTO.getLastName());
        userRepository.save(u);
        return mapper.map(u,UserWithoutPasswordDTO.class);
    }
    @Transactional
    public void delete(final UserPasswordDTO userPasswordDTO,final long id) {
        final User u=ifPresent(userRepository.findById(id));
        if(!passwordEncoder.matches(userPasswordDTO.getPassword(), u.getPassword())) {
            throw new UnauthorizedException("Not authorized");
        }
        userRepository.delete(u);
    }

    private String generateConfirmationToken(){
        return UUID.randomUUID().toString();
    }
    private void sendConfirmationEmail(final User user){
        SimpleMailMessage message =new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Confirm your email");
        message.setText("To confirm your email, please click the link below:\n\n" +
                "http://localhost:8090/confirm?token=" + user.getConfirmatronToken());
        new Thread(()->  mailSender.send(message)).start();
    }
    public boolean confirmEmail(final String token){
        final User user=userRepository.findAllByConfirmatronToken(token).orElseThrow(()->new NotFoundException("Token not found"));
        user.setConfirmatronToken(null);
        user.setEnable(true);
        userRepository.save(user);
        return true;
    }
    @Scheduled(fixedRate = 1000*60*5)
    public void deleteUnverifiedUsers() {

        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(5);
        List<User> unverifiedUsers = userRepository.findAllByEnableFalseAAndDateTimeRegistration(cutoffTime);
        userRepository.deleteAll(unverifiedUsers);
    }

}

