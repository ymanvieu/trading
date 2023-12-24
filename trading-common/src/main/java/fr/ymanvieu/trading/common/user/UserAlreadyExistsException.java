package fr.ymanvieu.trading.common.user;

import fr.ymanvieu.trading.common.exception.BusinessException;

public class UserAlreadyExistsException extends BusinessException {

	public UserAlreadyExistsException(String username) {
		super("user.error.username-already-exists", username);
	}
}
