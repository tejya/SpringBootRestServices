package com.example.SpringBootCrudRestServices.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SpringBootCrudRestServices.entity.User;
import com.example.SpringBootCrudRestServices.exception.ResourceNotFoundException;
import com.example.SpringBootCrudRestServices.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@GetMapping
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@GetMapping("/{id}")
	public User getUserById(@PathVariable(value = "id") long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not found" + id));
	}

	@PostMapping("/submitUser")
	public User createUser(@RequestBody User user) {
		return userRepository.save(user);
	}

	@PutMapping(path="/updateUser/{id}",produces="application/json",consumes = "application/json")  
	public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable(name = "id") long id) {
		// User existingUser= userRepository.findById(id).orElseThrow(() -> new
		// ResourceNotFoundException("User does not found" + id));
		try {
			Optional<User> existingUser = userRepository.findById(id);
			User obj = null;
			System.out.println(existingUser.isPresent());
			if (existingUser.isPresent()) {
				obj = existingUser.get();
				obj.setFirstName(user.getFirstName());
				obj.setLastname(user.getLastname());
				obj.setEmail(user.getEmail());
				userRepository.save(obj);
				return new ResponseEntity<User>(obj, HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity(new CustomErrorType("User does not found"),HttpStatus.NOT_FOUND);
		
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable(name = "id") long id) {
		try {
		userRepository.deleteById(id);
		return ResponseEntity.ok().build();
		}catch (Exception e) {
			return new ResponseEntity(new CustomErrorType("User is not present"),HttpStatus.NOT_FOUND);
		}
	}

}
