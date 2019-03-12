package com.createdpro.util;

import org.springframework.util.StringUtils;

import com.createdpro.config.SensitiveHouse;

public class FormatText {

	public static String format(String text) {
		if(StringUtils.isEmpty(text)) {
			return null;
		}
		text = text.replace("&", "&amp;");
		text = text.replace("<", "&lt;");
		text = text.replace(">", "&gt;");
		text = text.replace(" ", "&nbsp;");
		text = text.replace("\"", "&quot;");

		text = text.replace("\n", "<br />");
		text = text.replace("\r\n", "<br />");
		
		for (int i = 0; i < SensitiveHouse.WORDS.length; i++) {
			String word = SensitiveHouse.WORDS[i];
			text = text.replace(word, "*");
		}
		
		return text;
	}
	
}
