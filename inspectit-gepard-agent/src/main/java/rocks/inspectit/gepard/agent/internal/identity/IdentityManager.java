/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.identity;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.identity.model.IdentityInfo;

public class IdentityManager {

  private static final Logger log = LoggerFactory.getLogger(IdentityManager.class);

  private final IdentityInfo identityInfo;

  private IdentityManager() {
    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
    long pid = runtime.getPid();
    String localHostName = getLocalHostname();
    this.identityInfo = new IdentityInfo(pid, localHostName, hash(pid + localHostName));
  }

  public static IdentityManager getInstance() {
    return new IdentityManager();
  }

  public IdentityInfo getIdentityInfo() {
    return this.identityInfo;
  }

  /**
   * Determines the current hostname.
   *
   * @return the hostname or if the operation is not allowed by the security check, the textual
   *     representation of the IP address. The default value is "0.0.0.0".
   */
  private String getLocalHostname() {
    String localHostName = "0.0.0.0";
    try {
      localHostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      log.info("Could not determine hostname", e);
    }
    return localHostName;
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
