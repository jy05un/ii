package com.ii.object.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

	USER ("ROLE_USER"),
	ADMIN ("ROLE_ADMIN");
	
	private final String roleString;
	
	public static String findRoleByName(String role) {
		return Role.valueOf(role).getRoleString(); // USER를 ROLE_USER로 변환하여 반환
	}
	
	public static String addRole(Role baseRole, Role newRole) {
		String priorRoleString = baseRole.getRoleString() + ", " + newRole.getRoleString();
		return priorRoleString;
	}
	
}
