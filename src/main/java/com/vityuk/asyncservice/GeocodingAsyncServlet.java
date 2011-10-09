package com.vityuk.asyncservice;

import javax.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/geocode-async", asyncSupported = true)
public class GeocodingAsyncServlet {

}
