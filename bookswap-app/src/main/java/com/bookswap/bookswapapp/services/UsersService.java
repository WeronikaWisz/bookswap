package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.manageusers.ChangePassword;
import com.bookswap.bookswapapp.dtos.manageusers.UpdateUserData;
import com.bookswap.bookswapapp.exception.ApiBadRequestException;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.UserRepository;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UsersService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    PasswordEncoder encoder;

    @Autowired
    public UsersService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User getUserProfileData(){
        String username = getCurrentUserUsername();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
    }

    @Transactional
    public boolean updateUserProfileData(UpdateUserData updateUserData){
        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
        boolean dataChanged = false;
        if(updateUserData.getEmail() != null && !Objects.equals(updateUserData.getEmail(), "")
                && !updateUserData.getEmail().equals(user.getEmail())){
            if (userRepository.existsByEmail(updateUserData.getEmail())) {
                throw new ApiBadRequestException("exception.emailUsed");
            }
            user.setEmail(updateUserData.getEmail());
            dataChanged = true;
        }
        if(updateUserData.getName() != null && !Objects.equals(updateUserData.getName(), "")
                && !updateUserData.getName().equals(user.getUsername())){
            user.setName(updateUserData.getName());
            dataChanged = true;
        }
        if(updateUserData.getSurname() != null && !Objects.equals(updateUserData.getSurname(), "")
                && !updateUserData.getSurname().equals(user.getSurname())){
            user.setSurname(updateUserData.getSurname());
            dataChanged = true;
        }
        if(updateUserData.getPhone() != null && !Objects.equals(updateUserData.getPhone(), "")){

            String phoneNumber = updateUserData.getPhone();
            phoneNumber = phoneNumber.replaceAll("\\s+", "");
            if(!phoneNumber.startsWith("+48")){
                phoneNumber = "+48" + phoneNumber;
            }
            if(!updateUserData.getPhone().equals(user.getPhone())) {
                user.setPhone(phoneNumber);
                dataChanged = true;
            }
        } else if (Objects.equals(updateUserData.getPhone(), "")){
            user.setPhone("");
            dataChanged = true;
        }
        if(updateUserData.getPostalCode() != null && !Objects.equals(updateUserData.getPostalCode(), "")
                && !updateUserData.getPostalCode().equals(user.getPostalCode())){
            user.setPostalCode(updateUserData.getPostalCode());
            dataChanged = true;
        }
        if(updateUserData.getPost() != null && !Objects.equals(updateUserData.getPost(), "")
                && !updateUserData.getPost().equals(user.getPost())){
            user.setPost(updateUserData.getPost());
            dataChanged = true;
        }
        if(updateUserData.getCity() != null && !Objects.equals(updateUserData.getCity(), "")
                && !updateUserData.getCity().equals(user.getCity())){
            user.setCity(updateUserData.getCity());
            dataChanged = true;
        }
        if(updateUserData.getStreet() != null && !Objects.equals(updateUserData.getStreet(), "")
                && !updateUserData.getStreet().equals(user.getStreet())){
            user.setStreet(updateUserData.getStreet());
            dataChanged = true;
        }
        if(updateUserData.getBuildingNumber() != null && !Objects.equals(updateUserData.getBuildingNumber(), "")
                && !updateUserData.getBuildingNumber().equals(user.getBuildingNumber())){
            user.setBuildingNumber(updateUserData.getBuildingNumber());
            dataChanged = true;
        }
        if(updateUserData.getDoorNumber() != null && !updateUserData.getDoorNumber().equals(user.getDoorNumber())){
            user.setDoorNumber(updateUserData.getDoorNumber());
            dataChanged = true;
        }
        if(dataChanged) {
            user.setUpdateDate(LocalDateTime.now());
        }
        return dataChanged;
    }

    @Transactional
    public void changePassword(ChangePassword changePassword){
        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot found user"));
        if(encoder.matches(changePassword.getOldPassword(), user.getPassword())){
            if(encoder.matches(changePassword.getNewPassword(), user.getPassword())){
                throw new ApiBadRequestException("exception.newPasswordSameAsOld");
            } else {
                user.setPassword(encoder.encode(changePassword.getNewPassword()));
            }
        } else {
            throw new ApiBadRequestException("exception.wrongOldPassword");
        }
    }

    private String getCurrentUserUsername(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }

}
