package com.example.taskmanager;

import com.example.taskmanager.controller.Constant;
import com.example.taskmanager.model.DTOs.*;
import com.example.taskmanager.model.entities.User;
import com.example.taskmanager.model.exceptions.BadRequestException;
import com.example.taskmanager.model.exceptions.NotFoundException;
import com.example.taskmanager.model.exceptions.UnauthorizedException;
import com.example.taskmanager.model.repositories.UserRepository;
import com.example.taskmanager.service.EmailSenderService;
import com.example.taskmanager.service.UserService;
import jakarta.persistence.Column;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private UserService userService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSuccessfulLogin1() {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(Constant.EMAIL);
        loginDTO.setPassword(Constant.PASS);

        User user = new User();
        user.setEmail(Constant.EMAIL);
        user.setPassword(Constant.PASS);

        UserWithoutPasswordDTO expected=new UserWithoutPasswordDTO(1,Constant.FIRST_NAME,Constant.LAST_NAME,Constant.EMAIL,Constant.ROLE_NAME);

        when(userRepository.existsByEmail(loginDTO.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(userService.login(loginDTO)).thenReturn(expected);

        UserWithoutPasswordDTO result = userService.login(loginDTO);

        assertNotNull(result);
        assertEquals(expected, result);
    }
    @Test
    public void testWrongEmail() {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(Constant.EMAIL);
        loginDTO.setPassword(Constant.PASS);

        when(userRepository.existsByEmail(loginDTO.getEmail())).thenReturn(false);
        assertThrows(UnauthorizedException.class, () -> userService.login(loginDTO));
    }

    @Test
    public void testWrongPassword() {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(Constant.EMAIL);
        loginDTO.setPassword("3456Sbb#");

        User user = new User();
        user.setEmail(Constant.EMAIL);
        user.setPassword(Constant.PASS);

        when(userRepository.existsByEmail(loginDTO.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);
        assertThrows(UnauthorizedException.class, () -> userService.login(loginDTO));
    }

    @Test
    public void testLoginUserNotEnabled() {

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail(Constant.EMAIL);
        loginDTO.setPassword(Constant.PASS);

        User user = new User();
        user.setEmail(Constant.EMAIL);
        user.setPassword(Constant.PASS);
        user.setEnable(false);

        when(userRepository.existsByEmail(loginDTO.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        assertThrows(UnauthorizedException.class, () -> userService.login(loginDTO));
    }

    @Test
    void registerSuccessfulRegistrationReturnsUserWithoutPasswordDTO() {

        UserRegisterDTO registerData = new UserRegisterDTO();
        registerData.setEmail(Constant.EMAIL);
        registerData.setPassword(Constant.PASS);
        registerData.setConfirmPassword(Constant.PASS);
        registerData.setFirstName(Constant.FIRST_NAME);
        registerData.setLastName(Constant.LAST_NAME);
        registerData.setRoleName(Constant.ROLE_NAME);

        User u = new User();
        u.setEmail(registerData.getEmail());
        u.setPassword(registerData.getPassword());
        u.setFirstName(registerData.getFirstName());
        u.setLastName(registerData.getLastName());

        UserWithoutPasswordDTO expectedUser=new UserWithoutPasswordDTO();
        expectedUser.setEmail("test@example.com");
        expectedUser.setFirstName(registerData.getFirstName());
        expectedUser.setLastName(registerData.getLastName());

        when(userRepository.existsByEmail(registerData.getEmail())).thenReturn(false);
        when(mapper.map(registerData, User.class)).thenReturn(u);
        when(passwordEncoder.encode(registerData.getPassword())).thenReturn(Constant.PASS);
        when(userRepository.save(any(User.class))).thenReturn(u);
        when(mapper.map(u, UserWithoutPasswordDTO.class)).thenReturn(expectedUser);

        UserWithoutPasswordDTO result = userService.register(registerData);

        assertNotNull(result);
        assertEquals(expectedUser, result);
    }

    @Test
    void registerPasswordMismatchThrowsBadRequestException() {

        UserRegisterDTO registerData = new UserRegisterDTO();
        registerData.setPassword(Constant.PASS);
        registerData.setConfirmPassword("mismatchedPassword");

        assertThrows(BadRequestException.class, () -> userService.register(registerData));
    }
    @Test
    void registerEmailAlreadyExistsThrowsBadRequestException() {

        UserRegisterDTO registerData = new UserRegisterDTO();
        registerData.setEmail(Constant.EMAIL);
        registerData.setPassword(Constant.PASS);
        registerData.setConfirmPassword(Constant.PASS);

        when(userRepository.existsByEmail(registerData.getEmail())).thenReturn(true);
        assertThrows(BadRequestException.class, () -> userService.register(registerData));
    }

    @Test
    void changePasswordValidDataPasswordChangedSuccessfully() {

        long userId = 1;
        ChangePassDTO changePassData = new ChangePassDTO();
        changePassData.setNewPassword("newPassword123");

        User u = new User();
        u.setId(userId);

        UserWithoutPasswordDTO expectedUser=new UserWithoutPasswordDTO();
        expectedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(u));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedPassword");
        when(userRepository.save(u)).thenReturn(u);
        when(mapper.map(u,UserWithoutPasswordDTO.class)).thenReturn(expectedUser);
        UserWithoutPasswordDTO result = userService.changePassword(changePassData, userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("encodedPassword", u.getPassword());
    }

    @Test
    void changePasswordInvalidUserIdThrowsException() {

        long userId = 1;
        String newPassword = "newPassword123";

        ChangePassDTO changePassData = new ChangePassDTO();
        changePassData.setNewPassword(newPassword);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.changePassword(changePassData, userId));
    }

    @Test
    void editProfileValidDataProfileEditedSuccessfully() {

        long userId = 1;
        String newEmail = "newemail@example.com";

        EditProfilDTO editProfilDTO = new EditProfilDTO();
        editProfilDTO.setEmail(newEmail);
        editProfilDTO.setFirstName(Constant.FIRST_NAME);
        editProfilDTO.setLastName(Constant.LAST_NAME);

        User u = new User();
        u.setId(userId);
        u.setEmail(Constant.EMAIL);

        UserWithoutPasswordDTO expected = new UserWithoutPasswordDTO();

        when(userRepository.existsByEmail(newEmail)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(u));
        when(mapper.map(editProfilDTO, UserWithoutPasswordDTO.class)).thenReturn(expected);

        UserWithoutPasswordDTO result = userService.editProfile(editProfilDTO, userId);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    void editProfileEmailAlreadyExistsThrowsException2() {
        long userId = 1L;
        EditProfilDTO editProfileDTO = new EditProfilDTO();
        editProfileDTO.setEmail(Constant.EMAIL);
        editProfileDTO.setFirstName(Constant.FIRST_NAME);
        editProfileDTO.setLastName(Constant.LAST_NAME);

        User existingUser = new User();
        existingUser.setId(2L);

        when(userRepository.existsByEmail(editProfileDTO.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(editProfileDTO.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(UnauthorizedException.class, () -> userService.editProfile(editProfileDTO, userId));
    }
    @Test
    void deleteValidPasswordUserDeletedSuccessfully() {

    long userId = 1;
    String password = Constant.PASS;

    UserPasswordDTO userPasswordDTO = new UserPasswordDTO();
    userPasswordDTO.setPassword(password);

    User u = new User();
    u.setId(userId);
    u.setPassword(passwordEncoder.encode(password));

    when(userRepository.findById(userId)).thenReturn(Optional.of(u));
    when(passwordEncoder.matches(password, u.getPassword())).thenReturn(true);

    assertDoesNotThrow(() -> userService.delete(userPasswordDTO, userId));
}
    @Test
    void deleteInvalidPasswordThrowsException() {

        long userId = 1;
        String password = Constant.PASS;

        UserPasswordDTO userPasswordDTO = new UserPasswordDTO();
        userPasswordDTO.setPassword(password);

        User u = new User();
        u.setId(userId);
        u.setPassword(passwordEncoder.encode("wrongpassword"));

        when(userRepository.findById(userId)).thenReturn(Optional.of(u));
        when(passwordEncoder.matches(password, u.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> userService.delete(userPasswordDTO, userId));
    }

    @Test
    void deleteUserNotFoundThrowsException() {

        long userId = 1;
        String password = Constant.PASS;

        UserPasswordDTO userPasswordDTO = new UserPasswordDTO();
        userPasswordDTO.setPassword(password);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.delete(userPasswordDTO, userId));
    }
    @Test
    void editProfileEmailAlreadyExistsThrowsException1() {
        long userId = 1L;
        EditProfilDTO editProfileDTO = new EditProfilDTO();
        editProfileDTO.setEmail(Constant.EMAIL);
        editProfileDTO.setFirstName(Constant.FIRST_NAME);
        editProfileDTO.setLastName(Constant.LAST_NAME);

        User existingUser = new User();
        existingUser.setId(2L);
        when(userRepository.existsByEmail(editProfileDTO.getEmail())).thenReturn(true);
        when(userRepository.findByEmail(editProfileDTO.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(UnauthorizedException.class, () -> userService.editProfile(editProfileDTO, userId));
    }

    @Test
    void sendConfirmationEmailShouldSendEmailWithCorrectContent() {

        User user = new User();
        user.setEmail("test@example.com");
        user.setConfirmatronToken("token");

        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setTo(user.getEmail());
        expectedMessage.setSubject("Confirm your email");
        expectedMessage.setText("To confirm your email, please click the link below:\n\n" +
                "http://localhost:8090/confirm?token=" + user.getConfirmatronToken());

        JavaMailSender mailSender = mock(JavaMailSender.class);

        EmailSenderService emailSenderService = new EmailSenderService();
        emailSenderService.setMailSender(mailSender);

        emailSenderService.sendEmail(user.getEmail(),"Confirm your email","To confirm your email, please click the link below:\n\n" +
                "http://localhost:8090/confirm?token=" + user.getConfirmatronToken());

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage sentMessage = captor.getValue();

        assertEquals(expectedMessage.getTo()[0], sentMessage.getTo()[0]);
        assertEquals(expectedMessage.getSubject(), sentMessage.getSubject());
        assertEquals(expectedMessage.getText(), sentMessage.getText());
    }

    @Test
    void confirmEmailValidTokenReturnsTrue() {

        String token = "validToken";
        User user = new User();
        user.setConfirmatronToken(token);
        user.setEnable(false);

        when(userRepository.findAllByConfirmatronToken(token)).thenReturn(Optional.of(user));

        EmailSenderService emailSenderService = new EmailSenderService();
        boolean result =userService.confirmEmail(token);

        assertTrue(result);
        assertNull(user.getConfirmatronToken());
        assertTrue(user.isEnable());
        Mockito.verify(userRepository).save(user);
    }
    @Test
    void confirmEmailInvalidTokenThrowsNotFoundException() {

        String token = "invalidToken";
        when(userRepository.findAllByConfirmatronToken(token)).thenReturn(Optional.empty());

        EmailSenderService emailSenderService = new EmailSenderService();

        assertThrows(NotFoundException.class, () -> userService.confirmEmail(token));
    }
}



