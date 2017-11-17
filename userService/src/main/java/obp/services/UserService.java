package obp.services;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import obp.dao.KongConsumerResponse;
import obp.dao.AuthenticationResponse;
import obp.security.SerializableUser;
import obp.repo.UserRepository;
import obp.security.UserRoles;
import obp.services.exceptions.KongApiException;
import obp.services.exceptions.RoleNotFoundException;
import obp.services.exceptions.UserExistsException;
import obp.services.exceptions.UserNotFoundException;

// TODO: move this back to individual Kong consumers
// see: https://atlasrv02.saicwebhost.net/display/OBP/Kong+JWT+Issues
@Service
public class UserService {

	private final AuthenticationManager authenticationManager;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	private final KongJwtRegisterService kongJwtRegisterService;
	private final JwtService jwtService;

	@Autowired
	UserService(AuthenticationManager authenticationManager, 
			UserRepository userRepository,
			PasswordEncoder passwordEncoder, 
			KongJwtRegisterService kongJwtRegisterService, 
			JwtService jwtService) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.kongJwtRegisterService = kongJwtRegisterService;
		this.jwtService = jwtService;
	}

	public AuthenticationResponse authenticate(String username, String password) throws UserNotFoundException {

		AuthenticationResponse holder = null;
		try {
			final Authentication auth = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			holder = jwtService.retrieveAccessToken(auth);
		} catch (Exception e) {
			throw new UserNotFoundException(e.getMessage(), e);
		}
		return holder;
	}
	
	public SerializableUser create(String username, String rawPassword) throws UserExistsException, KongApiException {
		SerializableUser existingUser = userRepository.findByUsername(username);
		if (existingUser != null) {
			throw new UserExistsException();
		}

		SerializableUser user = createUser(username, rawPassword);
		userRepository.save(user);

		try {
			KongConsumerResponse consumerResponse = kongJwtRegisterService.createKongConsumer(user);
			SerializableUser kongMetadataUser = kongJwtRegisterService.createKongJwtCredential(user, consumerResponse);
			userRepository.save(kongMetadataUser);
		} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
			// e.printStackTrace();
			throw new KongApiException();
		}
		return user;
	}

	private SerializableUser createUser(String username, String rawPassword) {
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
  	grantedAuthorities.add(new SimpleGrantedAuthority(UserRoles.ROLE_RO.toString()));
  	String password = passwordEncoder.encode(rawPassword);
  	SerializableUser user = new SerializableUser(username, password, grantedAuthorities);
		return user;
	}
	
	public SerializableUser update(String username, String role) throws UserNotFoundException, RoleNotFoundException {
		SerializableUser existingUser = userRepository.findByUsername(username);
		if (existingUser == null) {
			throw new UserNotFoundException("User not found");
		}
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		UserRoles userRoleInput = UserRoles.valueOf(role.toUpperCase());

		if (userRoleInput == null) {
			throw new RoleNotFoundException("User role not found");
		}

		for (UserRoles userRole : userRoleInput.getAllRoles()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(userRole.toString()));
		}

		SerializableUser updatedUser = new SerializableUser(username, existingUser.getPassword(), grantedAuthorities);
		updatedUser.setId(existingUser.getId());
		updatedUser.setClient_secret(existingUser.getClient_secret());
		userRepository.save(updatedUser);

		return existingUser;
	}
	public void delete(String username) throws UserNotFoundException {
		SerializableUser existingUser = userRepository.findByUsername(username);
		if (existingUser == null) {
			throw new UserNotFoundException("User not found");
		}

		userRepository.delete(existingUser);
	}
	public SerializableUser getUserByName(String username) throws UserNotFoundException {
		SerializableUser existingUser = userRepository.findByUsername(username);
		if (existingUser == null) {
			throw new UserNotFoundException("User not found");
		}
		return existingUser;
	}

	public SerializableUser getUserById(String id) throws UserNotFoundException, RoleNotFoundException {
		SerializableUser existingUser = userRepository.findOne(id);
		if (existingUser == null) {
			throw new UserNotFoundException("User not found");
		}
		return existingUser;
	}

	public List<SerializableUser> getAllUsers() throws UserNotFoundException {
		List<SerializableUser> existingUsers = (List<SerializableUser>) userRepository.findAll();
		if (existingUsers == null || existingUsers.isEmpty()) {
			throw new UserNotFoundException("User not found");
		}
		return existingUsers;
	}
}
