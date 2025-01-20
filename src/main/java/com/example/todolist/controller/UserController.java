package com.example.todolist.controller;

import com.example.todolist.entity.User;
import com.example.todolist.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "users/list";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        model.addAttribute("newUser", new User());
        return "users/add";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("newUser") User user, Model model) {
        if (userService.isUsernameTaken(user.getUsername())) {
            model.addAttribute("error", "Имя пользователя уже занято");
            return "users/add";
        }
        if (userService.isEmailTaken(user.getEmail())) {
            model.addAttribute("error", "Email уже занят");
            return "users/add";
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("default_password");
        }

        userService.saveUser(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        model.addAttribute("user", user);
        return "users/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") User user, Model model) {
        User existingUser = userService.getUserById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }

        userService.updateUser(id, existingUser);
        return "redirect:/users";
    }


    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
