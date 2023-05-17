package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.model.User;
import com.example.demo.model.repository.UserRepository;
import com.example.demo.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    UserRepository userRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final PasswordEncoder passwordEncoder;
    public List<User> listUsers(){
        return userRepo.findAll();
    }

    public User createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public User getUserById(String id){
        Optional<User> u =  userRepo.findById(id);
        if (u.isPresent()) return u.get();
        throw new NotFoundException();
    }

    public User getUserByEmail(String email){
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        User user = mongoTemplate.findOne(query, User.class);
        return user;
    }

    public User updateUser(User user,String userId){
        User u = getUserById(userId);
        Utils.copyNonEmptyProperties(user,u,new String[]{"id"});
        return userRepo.save(u);

    }

    public boolean deleteUser(String usrId){
        User u = getUserById(usrId);
        userRepo.delete(u);
        return true;
    }
}
