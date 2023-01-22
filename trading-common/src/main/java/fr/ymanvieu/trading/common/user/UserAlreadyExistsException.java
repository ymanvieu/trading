package fr.ymanvieu.trading.common.user;

public class UserAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 3392756365434206128L;
	
	private final String login;

	public UserAlreadyExistsException(String login) {
		super(login);
		this.login = login;
	}
	
	public String getLogin() {
		return login;
	}
}
