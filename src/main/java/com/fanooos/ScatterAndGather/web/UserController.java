package com.fanooos.ScatterAndGather.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanooos.ScatterAndGather.model.User;
import com.fanooos.ScatterAndGather.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/users")
	public List<User> getBestOffer() {
		return userService.getUsers();
	}

}
