package com.task3.try1.controller;

import com.task3.try1.model.User;
import com.task3.try1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @GetMapping({"/","/edit"})
    public String home( Model model) {
        return "redirect:/userlist";
    }

    @PreAuthorize("@userService.checkUser(#request)")
    @GetMapping("/userlist")
    public String userlist(HttpServletRequest request, Model model) {
        model.addAttribute("users", userService.allUsers());
        return "userlist";
    }

    @PreAuthorize("@userService.checkUser(#request)")
    @PostMapping(value = "/edit", params = "delete")
    public String delete(HttpServletRequest request, Model model, @RequestParam(value = "ids" , required = false) long[] ids) {
        if (ids != null) {
            for (long id : ids) {
                userService.deleteUser(id);
            }
        }
        return "redirect:userlist";
    }

    @PreAuthorize("@userService.checkUser(#request)")
    @PostMapping(value = "/edit", params = "block")
    public String block(HttpServletRequest request, Model model, @RequestParam(value = "ids" , required = false) long[] ids) {
        if(ids!=null){
            for (long id : ids)
            {
                User user = userService.findUserById(id);
                userService.unblockUser(user,false);
            }
        }

        return "redirect:userlist";
    }

    @PreAuthorize("@userService.checkUser(#request)")
    @PostMapping(value = "/edit", params = "unblock")
    public String unblock(HttpServletRequest request, Model model, @RequestParam(value = "ids" , required = false) long[] ids) {
        if(ids!=null) {
            for (long id : ids) {
                User user = userService.findUserById(id);
                userService.unblockUser(user, true);
            }
        }
        return "redirect:userlist";
    }

}

