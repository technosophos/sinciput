package com.technosophos.rhizome.web;

import java.util.Map;
import java.util.HashMap;

/**
 * This class provides tools for preloading command classes.
 * @author mbutcher
 *
 */
public class CommandClassPreloader {
	
	/**
	 * 
	 * @param classes
	 * @return
	 * @throws ClassNotFoundException
	 * @deprecated Use the other version of preloadClasses.
	 */
	public static Map<String, Class<?>> preloadClasses(Map<String, String> classes) 
			throws ClassNotFoundException {
		HashMap<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		
		for(String k: classes.keySet()) {
			classMap.put(k, loadClass(classes.get(k), k.getClass().getClassLoader()));
		}
		return classMap;
	}
	
	/**
	 * Given a list of classes and a classloader, this preloads classes.
	 * You want to specify the classloader so that you can manage your security contexts efficiently.
	 * @param classes Classes to preload
	 * @param cl Class Loader to use. You want something like "this.getClass().getClassLoader()".
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Map<String, Class<?>> preloadClasses(Map<String, String> classes, ClassLoader cl) throws ClassNotFoundException {
		HashMap<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		
		for(String k: classes.keySet()) {
			classMap.put(k, loadClass(classes.get(k), cl));
		}
		return classMap;
	}
	
	public static Class<?> loadClass( String className, ClassLoader cl ) throws ClassNotFoundException {
		
		Class<?> c = Class.forName(className, true, cl);
		return c;
	}
	
}
