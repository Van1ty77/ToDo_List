package com.example.todolist.controller;

import com.example.todolist.entity.User;
import com.example.todolist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("newUser", new User());
        return "add";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("newUser") User user, Model model) {
        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("error", "Имя пользователя уже занято");
            return "add";
        }
        if (userService.isEmailTaken(user.getEmail())) {
            model.addAttribute("error", "Email уже занят");
            return "add";
        }

        userService.saveUser(user);
        return "redirect:/user/list";
    }

    @GetMapping("/list")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user/listUsers";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") User user, Model model) {
        userService.updateUser(id, user);
        return "redirect:/user/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/user/list";
    }
}
