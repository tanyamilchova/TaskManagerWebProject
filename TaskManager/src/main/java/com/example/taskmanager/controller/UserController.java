package com.example.taskmanager.controller;
import com.example.taskmanager.model.DTOs.*;
import com.example.taskmanager.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    public class UserController extends AbstractController{

    @Autowired
    private UserService userService;

    @PostMapping("/users/login")
    public UserWithoutPasswordDTO login(@Valid @RequestBody final LoginDTO loginDTO, final HttpSession session){
        UserWithoutPasswordDTO user=userService.login(loginDTO);
        session.setAttribute(Constant.LOGGED,true);
        session.setAttribute(Constant.LOGGED_ID,user.getId());
        return user;
    }
    @PostMapping("/users/register")
    public UserWithoutPasswordDTO register(@Valid @RequestBody final UserRegisterDTO registerData){
        return userService.register(registerData);
    }

    @PostMapping("/users/logout")
    public ResponseEntity<String> logout(final HttpSession session) {

        invalidateSession(session);
    return ResponseEntity.ok("Logged out successfully");
}
    @PutMapping("/users/changePass")
    public UserWithoutPasswordDTO changePassword(@Valid @RequestBody final ChangePassDTO changePassData, final HttpSession session){
        final long id=loggedId(session);
        return userService.changePassword(changePassData,id);
    }
    @PutMapping("/users/edit")
    public UserWithoutPasswordDTO editProfile(@Valid @RequestBody final EditProfilDTO editProfilDTO,final  HttpSession session){

        final long id=(int) session.getAttribute(Constant.LOGGED_ID);
        return userService.editProfile(editProfilDTO,id);
    }

    @DeleteMapping("/users/{id}/delete")
    public ResponseEntity<String> delete(@RequestBody final UserPasswordDTO userPasswordDTO, final HttpSession session){
        final long userId=loggedId(session);
        userService.delete(userPasswordDTO,userId);
        invalidateSession(session);
        return  ResponseEntity.ok("Account deleted");
}
    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam("token") final String token){
        if(userService.confirmEmail(token)){
            return "Email confirmed";
        }else {
            return "Invalid confirmation";
        }
    }

}
