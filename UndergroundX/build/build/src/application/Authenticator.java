package application;

public class Authenticator { 

	public static Profile validate(String userId, String password) {
		ProfileStorage ps = ProfilesManager.profileStorageList.get(userId);
		if (ps==null || !ps.getProfile().getPassword().equals(password)) return null;
		else return ps.getProfile();
	}

	public static boolean validateNewLogin(String login) {
		return ProfilesManager.profileStorageList.get(login)==null;
	}
	
}
