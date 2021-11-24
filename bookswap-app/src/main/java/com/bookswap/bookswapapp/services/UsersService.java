package com.bookswap.bookswapapp.services;

import com.bookswap.bookswapapp.dtos.manageusers.UpdateUserData;
import com.bookswap.bookswapapp.models.User;
import com.bookswap.bookswapapp.repositories.UserRepository;
import com.bookswap.bookswapapp.security.userdetails.UserDetailsI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UsersService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserProfileData(){
        String username = getCurrentUserUsername();
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot find user"));
    }

    @Transactional
    public boolean updateUserProfileData(UpdateUserData updateUserData){
        String username = getCurrentUserUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Cannot find user"));
        logger.info("found user");
        boolean dataChanged = false;
        if(updateUserData.getEmail() != null && !Objects.equals(updateUserData.getEmail(), "")
                && !updateUserData.getEmail().equals(user.getEmail())){
            if (userRepository.existsByEmail(updateUserData.getEmail())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Email address is already taken");
            }
            user.setEmail(updateUserData.getEmail());
            dataChanged = true;
            logger.info("change email");
        }
        logger.info("past email");
        if(updateUserData.getName() != null && !Objects.equals(updateUserData.getName(), "")
                && !updateUserData.getName().equals(user.getUsername())){
            user.setName(updateUserData.getName());
            dataChanged = true;
            logger.info("past name");
        }
        logger.info("past name");
        if(updateUserData.getSurname() != null && !Objects.equals(updateUserData.getSurname(), "")
                && !updateUserData.getSurname().equals(user.getSurname())){
            user.setSurname(updateUserData.getSurname());
            dataChanged = true;
            logger.info("change surname");
        }
        logger.info("past surname");
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
            logger.info("change phone");
        } else if (Objects.equals(updateUserData.getPhone(), "")){
            user.setPhone("");
            dataChanged = true;
            logger.info("change phone 2");
        }
        logger.info("past phone");
        if(updateUserData.getPostalCode() != null && !Objects.equals(updateUserData.getPostalCode(), "")
                && !updateUserData.getPostalCode().equals(user.getPostalCode())){
            user.setPostalCode(updateUserData.getPostalCode());
            dataChanged = true;
            logger.info("postal code");
        }
        logger.info("past postal code");
        if(updateUserData.getPost() != null && !Objects.equals(updateUserData.getPost(), "")
                && !updateUserData.getPost().equals(user.getPost())){
            user.setPost(updateUserData.getPost());
            dataChanged = true;
            logger.info("post");
        }
        logger.info("past post");
        if(updateUserData.getCity() != null && !Objects.equals(updateUserData.getCity(), "")
                && !updateUserData.getCity().equals(user.getCity())){
            user.setCity(updateUserData.getCity());
            dataChanged = true;
            logger.info("city");
        }
        logger.info("past city");
        if(updateUserData.getStreet() != null && !Objects.equals(updateUserData.getStreet(), "")
                && !updateUserData.getStreet().equals(user.getStreet())){
            user.setStreet(updateUserData.getStreet());
            dataChanged = true;
            logger.info("street");
        }
        logger.info("past street");
        if(updateUserData.getBuildingNumber() != null && !Objects.equals(updateUserData.getBuildingNumber(), "")
                && !updateUserData.getBuildingNumber().equals(user.getBuildingNumber())){
            user.setBuildingNumber(updateUserData.getBuildingNumber());
            dataChanged = true;
            logger.info("building");
        }
        logger.info("past building");
        if(updateUserData.getDoorNumber() != null && !updateUserData.getDoorNumber().equals(user.getDoorNumber())){
            user.setDoorNumber(updateUserData.getDoorNumber());
            dataChanged = true;
            logger.info("door");
        }
        logger.info("past door");
        if(dataChanged) {
            user.setUpdateDate(LocalDateTime.now());
        }
        logger.info("update");
        return dataChanged;
    }

    private String getCurrentUserUsername(){
        UserDetailsI userDetails = (UserDetailsI) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        return userDetails.getUsername();
    }

}
