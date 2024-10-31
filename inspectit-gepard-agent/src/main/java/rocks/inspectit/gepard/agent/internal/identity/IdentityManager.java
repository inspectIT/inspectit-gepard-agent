/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import rocks.inspectit.gepard.agent.internal.identity.model.IdentityInfo;

/** Responsible for generating the agentId. */
public class IdentityManager {

  private static IdentityManager instance;

  private final IdentityInfo identityInfo;

  private IdentityManager() {
    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    String vmId = runtime.getName();
    this.identityInfo = new IdentityInfo(vmId, hash(vmId));
  }

  public static IdentityManager getInstance() {
    if (Objects.isNull(instance)) instance = new IdentityManager();
    return instance;
  }

  public IdentityInfo getIdentityInfo() {
    return this.identityInfo;
  }

  /**
   * Hashes the given input with SHA3-256.
   *
   * @param input the <code>String</code> to be hashed.
   * @return the SHA3-256 hashed <code>String</code>
   */
  private static String hash(String input) {
    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("SHA3-256");
    } catch (NoSuchAlgorithmException e) {
      throw new UnsupportedOperationException("SHA3-256 not supported", e);
    }
    byte[] bytes = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
    StringBuilder hexString = new StringBuilder(2 * bytes.length);
    for (byte b : bytes) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }
}
