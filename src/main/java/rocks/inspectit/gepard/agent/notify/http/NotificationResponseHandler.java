package rocks.inspectit.gepard.agent.notify.http;

import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

public class NotificationResponseHandler implements HttpClientResponseHandler<Boolean> {

  @Override
  public Boolean handleResponse(ClassicHttpResponse response) throws HttpResponseException {
    int statusCode = response.getCode();

    if (statusCode == 200) {
      // process response
      return true;
    } else {
      throw new HttpResponseException(statusCode, "Server returned an unexpected response status");
    }
  }
}
