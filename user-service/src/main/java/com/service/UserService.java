package com.service;

import com.model.User;
import com.model.UserPrincipal;
import com.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

	private UserRepository userRepository;
	private ApplicationContext context;
	private JwtService jwtService;
	
	// BCrypt
	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	
	public UserService(UserRepository userRepository, ApplicationContext context, JwtService jwtService) {
        this.userRepository = userRepository;
        this.context = context;
        this.jwtService=jwtService;
    }

	public User createUser(User user) {
		// Ensure the username and email are unique (you can add further validation here
		// if needed)
		if (userRepository.findByUsername(user.getUsername()) != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
		}
		if (userRepository.findByEmail(user.getEmail()) != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
		}
		return userRepository.save(user);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}

	public User updateUser(Long id, User userDetails) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		existingUser.setUsername(userDetails.getUsername());
		existingUser.setEmail(userDetails.getEmail());
		
		if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
	        existingUser.setPassword(encoder.encode(userDetails.getPassword())); // Hash the new password
	    }

		return userRepository.save(existingUser);
	}

	public void deleteUser(Long id) {
		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		userRepository.delete(existingUser);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userRepository.findByUsername(username);
		
		if(user==null) {
			System.out.println("User does not exist");
			throw new UsernameNotFoundException("User does not exist");
		}
		return new UserPrincipal(user);
	}
	
	// Register user
	public User registerUser(User user) {
		//This line encodes the password using BCrypt
		user.setPassword(encoder.encode(user.getPassword()));
		return userRepository.save(user);
	}

	public String verifyLogin(User user) {
		AuthenticationManager authManager = context.getBean(AuthenticationManager.class);
		Authentication authentication = authManager.authenticate(
				new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
		);
		if(authentication.isAuthenticated())
			return jwtService.generateJwtToken(user.getUsername());
		
		return "Failure";
	}
}
