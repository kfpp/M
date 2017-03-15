package com.qqonline.interfaces;

import java.io.InputStream;

import com.qqonline.domain.Weather;

public interface IXmlParse {

	public Weather parse(InputStream is) throws Exception;
}
