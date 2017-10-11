package de.beckers.members;

import lombok.Data;

public class UserSession {
	@Data
	private static class UserData {
		private String userId;
		
		private String userName;
	}
	
	private static final ThreadLocal<UserData> sessions = new ThreadLocal<UserData>() {
		protected UserData initialValue() {
			return new UserData();
		};
	};
	
	public static void setUser(String id, String userName) {
		UserData session = sessions.get();
		session.setUserId(id);
		session.setUserName(userName);
	}
	
	public static String getUserId() {
		return sessions.get().getUserId();
	}
}
