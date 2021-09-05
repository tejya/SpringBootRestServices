package com.example.SpringBootCrudRestServices.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

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
	public User getUserById(@PathVariable(value = "id") long id) throws ResourceNotFoundException {
		User userobj = null;
		// return userRepository.findById(id).orElseThrow(() -> new
		// ResourceNotFoundException("User does not found" + id));

		Optional<User> existingUser = userRepository.findById(id);
		if (existingUser.isPresent())
			userobj = existingUser.get();
		else
			throw new ResourceNotFoundException("User is not present in database");

		return userobj;
	}

	@PostMapping("/submitUser")
	public User createUser(@RequestBody User user) {
		return userRepository.save(user);
	}

	@PutMapping(path = "/updateUser/{id}", produces = "application/json", consumes = "application/json")
	public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable(name = "id") long id) throws Exception {
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
			else
				throw new ResourceNotFoundException("User is not present in database");

		} catch (Exception e) {
			// return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
			
			throw new Exception("User is not found in database");

		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable(name = "id") long id) {
		try {
			userRepository.deleteById(id);
			HashSet<String> s = new HashSet<>();
			s.add(null);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			throw new ResourceNotFoundException("User is not present to delete the record");
		}
	}

		
	@ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<Object> handleUserNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        CustomErrorType custError=new CustomErrorType("Record is not present");
        return new ResponseEntity(custError, HttpStatus.NOT_FOUND);
    }

	//The endpoints are used from postman
	//http://localhost:9000/api/users/2
}
