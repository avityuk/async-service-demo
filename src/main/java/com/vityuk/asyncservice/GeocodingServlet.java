package com.vityuk.asyncservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

@WebServlet(urlPatterns = "/geocode")
public class GeocodingServlet extends HttpServlet {
	private static final long serialVersionUID = 4265494199498461273L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String address = URLEncoder.encode(req.getParameter("address"), "UTF-8");

		URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?sensor=false&address=" + address);
		URLConnection urlConnection = url.openConnection();
		InputStream is = urlConnection.getInputStream();

		ServletOutputStream os = resp.getOutputStream();
		try {
			IOUtils.copy(is, os);
		} finally {
			IOUtils.closeQuietly(is);
		}
		os.flush();
	}
}
