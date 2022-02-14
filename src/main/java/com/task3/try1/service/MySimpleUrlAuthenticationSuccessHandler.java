package com.task3.try1.service;

import com.task3.try1.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;

public class MySimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    UserService userService;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private String targetUrl = "/userlist";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = userService.userRepository.findUserByUsername(authentication.getName());
            if(user!=null) {
                user.setLastLoginDate(new Timestamp(System.currentTimeMillis()));
                userService.userRepository.save(user);
                redirectStrategy.sendRedirect(request, response, targetUrl);
            }
        }
    }
}