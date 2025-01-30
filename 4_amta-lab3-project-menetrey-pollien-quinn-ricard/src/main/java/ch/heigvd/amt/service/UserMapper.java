package ch.heigvd.amt.service;

import ch.heigvd.amt.beans.UserDTO;
import ch.heigvd.amt.entity.User;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Used to represent the corresponding user in different situations
 */
@ApplicationScoped
public class UserMapper {

    public UserDTO map(Object[] source) {
        User user = (User) source[0];
        int placedBetCount = (int) source[1];

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setBalance(user.getBalance());
        userDTO.setCreatedBets(placedBetCount);
        return userDTO;
    }
}
