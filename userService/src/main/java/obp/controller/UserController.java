package obp.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import obp.security.AuthenticationRequest;
import obp.security.SerializableUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import obp.common.JsonRestResponse;
import obp.dao.AuthenticationResponse;
import obp.dao.UserUpdate;
import obp.services.UserService;
import obp.services.exceptions.KongApiException;
import obp.services.exceptions.RoleNotFoundException;
import obp.services.exceptions.UserExistsException;
import obp.services.exceptions.UserNotFoundException;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController  {
	
	private UserService userService;
	private Collection<String> options;
	private RequestMappingInfoHandlerMapping _mapping;

	@Autowired
	public UserController(UserService userService, RequestMappingInfoHandlerMapping mapping) {
		this.userService = userService;
		this._mapping=_mapping;
		Set<String> set = new HashSet<>();
		_mapping = mapping;
		_mapping.getHandlerMethods()
				.keySet()
				.forEach(key -> set.addAll(key.getPatternsCondition().getPatterns()
						.stream()
						.filter(pattern -> pattern.split("/").length == 3)
						.map(s -> s.substring(1))
						.collect(Collectors.toList())));
		set.remove("error");
		options = set;
	}


	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public JsonRestResponse authenticate(@RequestBody AuthenticationRequest request, Principal principal) {
		JsonRestResponse restResponse = JsonRestResponse.getInstance();
		try {
			AuthenticationResponse response = userService.authenticate(request.getUsername(), request.getPassword());
			restResponse.getData().add(response);
			restResponse.setCode(HttpStatus.OK);
		} catch (UserNotFoundException e) {
			restResponse.setCode(HttpStatus.UNAUTHORIZED);
			restResponse.setMessage(e.getMessage());
		}
	    return restResponse;
	}

	@PreAuthorize("#oauth2.hasScope('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public JsonRestResponse create(@RequestBody AuthenticationRequest request) {
		JsonRestResponse restResponse = JsonRestResponse.getInstance();
		try {
			SerializableUser user = userService.create(request.getUsername(), request.getPassword());
			if (user != null) {
				restResponse.setCode(HttpStatus.CREATED);
				restResponse.setMessage("Created");
			} else {
				restResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
				restResponse.setMessage("Error");
			}
		} catch (UserExistsException uee) {
			uee.printStackTrace();
			restResponse.setCode(HttpStatus.BAD_REQUEST);
			restResponse.setMessage("User already exists");
		} catch (KongApiException kae) {
			kae.printStackTrace();
			restResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
			restResponse.setMessage("Kong api error");		
		}
		return restResponse;
	}
  
	@PreAuthorize("#oauth2.hasScope('ROLE_ADMIN')")
	@RequestMapping(value = "/{username}", method = RequestMethod.PUT)
	public JsonRestResponse update(@PathVariable String username, @RequestBody UserUpdate userUpdate) {
		JsonRestResponse restResponse = JsonRestResponse.getInstance();
		try {
			SerializableUser user = userService.update(username, userUpdate.getRole());
			if (user != null) {
				restResponse.setCode(HttpStatus.OK);
				restResponse.setMessage("Updated user role");
			}
		} catch (UserNotFoundException | RoleNotFoundException unfe) {
			restResponse.setCode(HttpStatus.NOT_FOUND);
			restResponse.setMessage((unfe.getMessage() != null) ? unfe.getMessage() : "Not Found");
		}
		return restResponse;
	}

	@PreAuthorize("#oauth2.hasScope('ROLE_ADMIN')")
	@RequestMapping(value = "/{username}", method = RequestMethod.GET)
	public JsonRestResponse getUser(@PathVariable String username) {
		JsonRestResponse restResponse = JsonRestResponse.getInstance();
		try {
			SerializableUser user = userService.getUserByName(username);
			if (user != null) {
				restResponse.setCode(HttpStatus.OK);
				restResponse.setData(user);
			}
		} catch (UserNotFoundException  unfe) {
			restResponse.setCode(HttpStatus.NOT_FOUND);
			restResponse.setMessage((unfe.getMessage() != null) ? unfe.getMessage() : "Not Found");
		}
		return restResponse;
	}

	@PreAuthorize("#oauth2.hasScope('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.GET)
	public JsonRestResponse getAllUsers() {
		JsonRestResponse restResponse = JsonRestResponse.getInstance();
		try {
			List<SerializableUser> users = userService.getAllUsers();
			if (users != null && !users.isEmpty()) {
				restResponse.setCode(HttpStatus.OK);
				restResponse.setData(users);
			}
		} catch (UserNotFoundException  unfe) {
			restResponse.setCode(HttpStatus.NOT_FOUND);
			restResponse.setMessage((unfe.getMessage() != null) ? unfe.getMessage() : "Not Found");
		}
		return restResponse;
	}

	@PreAuthorize("#oauth2.hasScope('ROLE_ADMIN')")
	@RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
	public JsonRestResponse deleteUser(@PathVariable String username) {
		JsonRestResponse restResponse = JsonRestResponse.getInstance();
		try {
			userService.delete(username);
			restResponse.setCode(HttpStatus.OK);
			restResponse.setMessage("User deleted successfully");

		} catch (UserNotFoundException unfe) {
			restResponse.setCode(HttpStatus.NOT_FOUND);
			restResponse.setMessage((unfe.getMessage() != null) ? unfe.getMessage() : "Not Found");
		}
		return restResponse;
	}

	@PreAuthorize("#oauth2.hasScope('ROLE_ADMIN')")
	@RequestMapping(method = RequestMethod.OPTIONS)
	public Collection<String> getOptions() {
		return options;
	}


}
