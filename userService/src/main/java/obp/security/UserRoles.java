package obp.security;

import java.util.ArrayList;
import java.util.List;

public enum UserRoles {
	ROLE_RO,
	ROLE_RWO,
	ROLE_RWCD,
	ROLE_ADMIN;
	
	public List<UserRoles> getAllRoles() {
		List<UserRoles> allRoles = new ArrayList<>();
		switch(this) {
		case ROLE_RO:
			allRoles.add(ROLE_RO);
			return allRoles;
		case ROLE_RWO:
			allRoles.add(ROLE_RO);
			allRoles.add(ROLE_RWO);
			return allRoles;
		case ROLE_RWCD:
			allRoles.add(ROLE_RO);
			allRoles.add(ROLE_RWO);
			allRoles.add(ROLE_RWCD);
			return allRoles;
		case ROLE_ADMIN:
			allRoles.add(ROLE_RO);
			allRoles.add(ROLE_RWO);
			allRoles.add(ROLE_RWCD);
			allRoles.add(ROLE_ADMIN);
			return allRoles;
		default:
			return allRoles;
		}
	}
}
