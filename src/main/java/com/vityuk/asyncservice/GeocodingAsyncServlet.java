package com.vityuk.asyncservice;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.HttpResponseBodyPart;
import com.ning.http.client.HttpResponseHeaders;
import com.ning.http.client.HttpResponseStatus;

@WebServlet(urlPatterns = "/geocode-async", asyncSupported = true)
public class GeocodingAsyncServlet extends HttpServlet {
	private static final long serialVersionUID = 7874040623030716005L;

	private static Logger LOG = LoggerFactory.getLogger(GeocodingAsyncServlet.class);

	private static final int RESPONSE_TIMEOUT_MS = 10 * 1000;

	private AsyncHttpClient client;

	@Override
	public void init() throws ServletException {
		AsyncHttpClientConfig config = new AsyncHttpClientConfig.Builder()
				.setConnectionTimeoutInMs(RESPONSE_TIMEOUT_MS)
				.build();

		client = new AsyncHttpClient(config);
	}

	@Override
	public void destroy() {
		client.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final long begin = System.currentTimeMillis();

		final String address = req.getParameter("address");
		final AsyncContext asyncContext = req.startAsync();
		try {
			final ServletOutputStream os = asyncContext.getResponse().getOutputStream();

			client.prepareGet("http://maps.googleapis.com/maps/api/geocode/json?sensor=false")
					.addQueryParameter("address", address)
					.execute(new AsyncHandler<String>() {
						@Override
						public STATE onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
							// Status handling should be here
							return STATE.CONTINUE;
						}

						@Override
						public STATE onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
							bodyPart.writeTo(os);
							return STATE.CONTINUE;
						}

						@Override
						public STATE onHeadersReceived(HttpResponseHeaders headers) throws Exception {
							return STATE.CONTINUE;
						}

						@Override
						public void onThrowable(Throwable t) {
							LOG.error("Error while calling service", t);
						}

						@Override
						public String onCompleted() throws Exception {
							os.flush();
							asyncContext.complete();

							long end = System.currentTimeMillis();
							LOG.info("Completed serving request in {} milliseconds", (end - begin));
							return "DONE"; // not needed for us
						}
					});

		} catch (IOException e) {
			asyncContext.complete(); // Release context on exception
			LOG.info("Error while processing request", e);
			throw new RuntimeException(e);
		}
	}
}
