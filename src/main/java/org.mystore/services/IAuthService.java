package org.mystore.services;

import org.antlr.v4.runtime.misc.Pair;
import org.mystore.dtos.UserDTO;
import org.mystore.models.User;

public interface IAuthService {

    User signup(String name,String email,String password, String phoneNumber);

    Pair<User, String> login(String email, String password);

    String validateToken(String token);

    UserDTO getUserFromToken(String token);
}
