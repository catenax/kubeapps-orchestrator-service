package com.autosetup.utility;

public class Validator {

	private static final String specialCharacters = "[" + "-/@#!*$%^&.'_+={}()" + "]+";

	public static boolean isOnlySpecialCaharcter(String input)
	{
		return input.matches(specialCharacters);
	}
}
