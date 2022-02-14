package com.task3.try1.service;

import com.task3.try1.model.Role;
import com.task3.try1.model.User;
import com.task3.try1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.sql.Timestamp;

@Service
public class UserService implements UserDetailsService {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public boolean saveNewUser(User user) {
        User userFromDB = userRepository.findUserByUsername(user.getUsername());

        if (userFromDB != null || userRepository.findUserByEmail(user.getEmail())!=null) {
            return false;
        }

        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public boolean unblockUser (User user, boolean nonlock) {
        User userFromDB = userRepository.findUserByUsername(user.getUsername());

        if (userFromDB != null) {
            user.setNonlock(nonlock);
            userRepository.save(user);
            return true;
        }
        return false;
    }


    public Boolean checkUser(HttpServletRequest request){
        User user =  userRepository.findUserByUsername(request.getRemoteUser());
        if (user == null || !user.isAccountNonLocked())
        {
            HttpSession session = request.getSession();
            session.invalidate();
            return false;
        }
        return true;
    }


}
