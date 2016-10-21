package cn.standardai.api.core.test;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import cn.standardai.api.core.util.CryptUtil;
import cn.standardai.api.core.util.DateUtil;

public class TestMain {


	public static void main(String[] args) throws UnsupportedEncodingException {

		System.out.println(new String(Base64.decodeBase64("x16YjoF1LNE=".getBytes("UTF-8")), "UTF-8"));
		System.out.println(new String(Base64.decodeBase64("0+m8Ku71ny4=".getBytes("UTF-8")), "UTF-8"));

		// TODO Auto-generated method stub
		//Date date = DateUtil.parse("2015-12-12 11:11:12", DateUtil.YYYY_MM_DD_HH_MM_SS);


		return;
	}

}
