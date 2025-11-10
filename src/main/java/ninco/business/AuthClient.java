package ninco.business;

import ninco.business.dto.EmployeeDTO;

/*
 * AuthClient is a singleton class that manages the authentication state of the application.
 * It holds information about the currently logged-in user and provides methods to access and modify this information.
 */
public class AuthClient {
  private EmployeeDTO currentUser;
  private static AuthClient instance;

  private AuthClient() {
    this.currentUser = null;
  }

  public static AuthClient getInstance() {
    if (instance == null) {
      instance = new AuthClient();
    }

    return instance;
  }

  public EmployeeDTO getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(EmployeeDTO currentUser) {
    this.currentUser = currentUser;
  }
}
