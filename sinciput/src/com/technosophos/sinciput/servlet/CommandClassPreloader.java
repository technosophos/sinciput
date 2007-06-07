package com.technosophos.sinciput.servlet;

import java.util.Map;
import java.util.HashMap;

/**
 * This class provides tools for preloading command classes.
 * @author mbutcher
 *
 */
public class CommandClassPreloader {
	
	
	public static Map<String, Class<?>> preloadClasses(Map<String, String> classes) 
			throws ClassNotFoundException {
		HashMap<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		
		for(String k: classes.keySet()) {
			classMap.put(k, loadClass(classes.get(k)));
		}
		return classMap;
	}
	
	public static Class<?> loadClass( String className ) throws ClassNotFoundException {
		
		Class<?> c = Class.forName(className);
		return c;
	}
}
