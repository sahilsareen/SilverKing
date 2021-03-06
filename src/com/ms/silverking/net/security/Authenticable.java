package com.ms.silverking.net.security;

import java.util.Optional;

public interface Authenticable {
  /**
   * Add authentication result into current Authenticable object
   *
   * @param result authentication result generated by Authenticator
   */
  public void setAuthenticationResult(AuthResult result);

  /**
   * Get authentication result for current Authenticable object
   *
   * @return a <b>present authentication result</b> if current object has been authenticated; <b>Optional.empty</b>
   * if haven't been authenticated
   */
  Optional<AuthResult> getAuthenticationResult();

  public default boolean hasBeenAuthenticated() {
    return getAuthenticationResult().isPresent();
  }
}
