package org.mystore.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.misc.Pair;
import org.mystore.dtos.LoginRequestDTO;
import org.mystore.dtos.SignupRequestDTO;
import org.mystore.dtos.UserDTO;
import org.mystore.dtos.ValidateTokenRequestDTO;
import org.mystore.mapper.UserMapper;
import org.mystore.models.User;
import org.mystore.services.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private IAuthService authService;
    //signup
    //login
    // validatetoken

    //logout
//    @PostMapping("/logout")
//    @PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
//    public ResponseEntity<Void> logout(HttpServletResponse response) {
//        Cookie cookie = new Cookie("token", null);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(false); // true if HTTPS
//        cookie.setPath("/");
//        cookie.setMaxAge(0); // delete cookie immediately
//        response.addCookie(cookie);
//
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
    public void logout(HttpServletResponse response) throws IOException {
        // Delete the token cookie
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true if HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(0); // delete immediately
        response.addCookie(cookie);

        // Redirect to login page
        response.sendRedirect("/login"); // change /login to your login page URL
    }



    //forgetpassword

    @PostMapping("/signup")
    public UserDTO signup (@RequestBody SignupRequestDTO signupRequestDTO){
        System.out.println("signup");
        User user= authService.signup(signupRequestDTO.getName(),
                signupRequestDTO.getEmail(),
                signupRequestDTO.getPassword(),
                signupRequestDTO.getPhoneNumber());

        return UserMapper.toDTO(user);
    }



//    @PostMapping("/login")
//    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
//        Pair<User,String> reponse=authService.login(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());
//        User user=reponse.a;
//        String token=reponse.b;
//        MultiValueMap<String,String> header=new LinkedMultiValueMap<>();
//        header.add(HttpHeaders.SET_COOKIE,token);
//
//
//        return new ResponseEntity<>(UserMapper.toDTO(user),header, HttpStatus.OK);
//    }
@PostMapping("/login")
public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequestDTO, HttpServletResponse response) {
    Pair<User, String> reponse = authService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
    User user = reponse.a;
    String token = reponse.b;

    Cookie cookie = new Cookie("token", token);
    cookie.setHttpOnly(true);   // prevent JavaScript access
    cookie.setSecure(false);    // true if HTTPS
    cookie.setPath("/");        // available to entire app
    response.addCookie(cookie);

    return ResponseEntity.ok(UserMapper.toDTO(user));
}

//scaller
//    @PostMapping("/validateToken")
//    public ResponseEntity<Void> validateToken(@RequestBody ValidateTokenRequestDTO validateTokenRequestDTO)
//    {
//        boolean result=authService.validateToken(validateTokenRequestDTO.getToken(),validateTokenRequestDTO.getUserId());
//        if(!result){
//           throw new RuntimeException("Invalid token");
//        }
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    //scaller

    @PostMapping("/validateToken")

    public ResponseEntity<String> validateToken(@CookieValue("token") String token) {


        String result = authService.validateToken(token); // extract userId from token

        if (result == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
//        UserDTO user = authService.getUserFromToken(token);

        return ResponseEntity.ok(result);
    }

}
