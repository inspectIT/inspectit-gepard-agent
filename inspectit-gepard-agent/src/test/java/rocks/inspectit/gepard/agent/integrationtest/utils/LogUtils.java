/* (C) 2024 */
package rocks.inspectit.gepard.agent.integrationtest.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogUtils {

  /**
   * Checks, if the logs contain "HELLO GEPARD" and "BYE GEPARD" for a specific number of times
   *
   * @param logs the logs
   * @param times the amount of times "HELLO GEPARD" and "BYE GEPARD" should be present in the logs
   */
  public static void assertLogs(String logs, int times) {
    boolean loggedHelloGepardTwice = containsTimes(logs, "HELLO GEPARD", times);
    boolean loggedByeGepardTwice = containsTimes(logs, "BYE GEPARD", times);

    assertTrue(loggedHelloGepardTwice);
    assertTrue(loggedByeGepardTwice);
  }

  /**
   * Checks, if a specific message can be found for a specific amount of times inside the provided
   * logs.
   *
   * @return true, if the message appears the expected amount of times in the logs
   */
  public static boolean containsTimes(String logs, String message, int times) {
    int count = countTimes(logs, message);
    return count == times;
  }

  /**
   * Counts how many times a specific message can be found inside the provided logs
   *
   * @param logs the logs
   * @param message the message to look for
   * @return the amount of times the message appears in the logs
   */
  public static int countTimes(String logs, String message) {
    int count = 0;
    int index = 0;
    while (index != -1) {
      index = logs.indexOf(message, index);
      if (index != -1) {
        count++;
        index += message.length();
      }
    }
    return count;
  }
}
