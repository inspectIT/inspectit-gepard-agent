package rocks.inspectit.gepard.agent.internal.identity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocks.inspectit.gepard.agent.internal.identity.type.IdGenerationType;
import rocks.inspectit.gepard.agent.internal.properties.PropertiesResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class IdentityManager {
    private static final Logger log = LoggerFactory.getLogger(IdentityManager.class);

    private String agentId;

    private IdentityManager() {}

    public static IdentityManager create() {
        return new IdentityManager();
    }

    public void setIdentity() {
        IdGenerationType idGenerationType = PropertiesResolver.getIdGenerationType();
        try {
            switch (idGenerationType) {
                case FQDN -> agentId = hash(InetAddress.getLocalHost().getCanonicalHostName());
                case IP_ADDRESS -> agentId = hash(InetAddress.getLocalHost().getHostAddress());
                case SERVICE_PROCESS_NAME -> ProcessHandle.current().info().commandLine().ifPresent(commandLine -> agentId = commandLine.trim());
            }
        } catch (UnknownHostException e) {
            log.info("Could not determine hostname", e);
        }

        System.out.println("AGENT ID: " + agentId);
    }

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
