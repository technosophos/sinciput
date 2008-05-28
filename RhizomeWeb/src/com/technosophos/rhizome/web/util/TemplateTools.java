package com.technosophos.rhizome.web.util;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
//import java.util.ArrayList;
//import java.util.Collection;
import java.util.Map;
import java.util.Arrays;
import java.lang.Iterable;

import com.technosophos.rhizome.util.Timestamp;

/**
 * Utilities for use in Velocity templates.
 * 
 * This was ported from LanternNotes. Many of the Lantern-specific methods have been
 * removed. (Though some, like the tagcloud and LabeledURI functions were left here
 * even though they are not truly general-purpose methods).
 * 
 * This class is instantiated by the template engine and passed into the Velocity 
 * context where it can be used by template authors.
 * 
 * @author mbutcher
 *
 */
public class TemplateTools {
	private DateFormat inDF;
	private SimpleDateFormat outDF;
	public final static String DEFAULT_FORMAT = "h:mm a 'on' MMM. d, yyyy";
	
	public final static String CSS_CLASS_TOP_TAG = "top-tag";
	public final static String CSS_CLASS_MIDDLE_TAG = "middle-tag";
	public final static String CSS_CLASS_LOW_TAG = "low-tag";
	
	public TemplateTools() {
		// Get the timestamp formatter used by Rhizome.
		this.inDF = Timestamp.getDateFormatter();
		try {
			this.outDF = (SimpleDateFormat)Timestamp.getDateFormatter();
		} catch (RuntimeException e) {
			// This can happen if Locale is weird.
			this.outDF = new SimpleDateFormat();
		}
		this.outDF.applyPattern(DEFAULT_FORMAT);
	}
	
	public String ts2Date(String timestamp) {
		try {
			long ts = Long.parseLong(timestamp);
			Date d = new Date(ts);
			return this.outDF.format(d);
		} catch (Exception e) {
			return timestamp;
		}
	}
	
	public String ts2Date(String timestamp, String format) {
		try {
			long ts = Long.parseLong(timestamp);
			Date d = new Date(ts);
			this.outDF.applyPattern(format);
			return this.outDF.format(d);
		} catch (Exception e) {
			return timestamp;
		}
	}
	
	/**
	 * Format a date for display.
	 * @param date Date string from Rhizome.
	 * @return Formatted date string
	 */
	public String formatDate(String date) {
		if(date == null || date.length() == 0)return "";
		try {
			Date d = this.inDF.parse(date);
			//this.outDF.applyPattern(DEFAULT_FORMAT);
			return this.outDF.format(d);
		} catch (ParseException e) {
			return date;
		}
	}
	
	/**
	 * Format a date for display.
	 * @param date Date string from Rhizome.
	 * @param format String format for this date
	 * @return Formatted date string
	 * @see java.text.SimpleDateFormat
	 */
	public String formatDate(String date, String format) {
		try {
			Date d = this.inDF.parse(date);
			this.outDF.applyPattern(format);
			return this.outDF.format(d);
		} catch (ParseException e) {
			return date;
		}
	}
	
	public String formatTags(String[] tags, String uri) {
		StringBuilder sb = new StringBuilder();
		boolean cm = false;
		for(String tag: tags) {
			if(cm) sb.append(", ");
			sb.append("<a href=\"").append(uri).append(Scrubby.URLEncode(tag)).append("\">")
				.append(tag).append("</a>");
			cm = true;
		}
		return sb.toString();
	}
	
	/**
	 * Format a list of tags.
	 * @param tags An Interable containing the tags that should be formatted.
	 * @param uri The URI that the tags should link to. The tag will be appended to the string.
	 * @param separator The string used to separate the tags in display.
	 */
	public String formatTags(Iterable<String> tags, String uri, String separator) {
		//System.out.println("TemplateTools: Formatting Iterable as tag string.");
		StringBuilder sb = new StringBuilder();
		boolean cm = false;
		for(String tag: tags) {
			//System.out.print("Ping;");
			if(cm) sb.append(separator); else cm = true;
			sb.append("<a class=\"tag\" href=\"").append(uri).append(Scrubby.URLEncode(tag)).append("\">")
				.append(tag).append("</a>");
		}
		//System.out.println("TemplateTools:" + sb.toString());
		return sb.toString();
	}
	public String formatTags(Iterable<String> tags, String uri) {
		return this.formatTags(tags, uri, ", ");
	}
	public String tagCloud(Map<String, Integer> tags, String uri) {
		StringBuilder sb = new StringBuilder();
		String format = "<a href=\""+ uri +"%s\" class=\"tag %s\">%s</a>";
		int highest = 1; int lowest = 0;
		/*String[] classes = {
			CSS_CLASS_LOW_TAG,
			CSS_CLASS_MIDDLE_TAG,
			CSS_CLASS_TOP_TAG	
		};*/
		
		String[] keys = tags.keySet().toArray(new String[0]);
		for(String k: keys) {
			Integer s = tags.get(k);
			if(s > highest) highest = s;
			else if(s == 0 ) s = lowest; // Assuming no tag can have 0 hits.
			else if(s < lowest) s = lowest;
		}
		int median = (highest - lowest) / 2;
		int upperq = (highest - median) / 2;
		int lowerq = (median - lowest) / 2;
		
		Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);
		String c;
		Integer val;
		boolean punc = false;
		final String sep = ", ";
		for(String k: keys) {
			val = tags.get(k);
			c = val < lowerq ? CSS_CLASS_LOW_TAG : 
					val <= upperq ? CSS_CLASS_MIDDLE_TAG : CSS_CLASS_TOP_TAG;
			if(punc) sb.append(sep); else punc = true;
			sb.append(String.format(format, k, c, k));
		}
		
		return sb.toString();
	}
	
	public String implode(String sep, List<String> values) {
		boolean punc = false;
		if(values == null) return "";
		if(sep == null) sep=", ";
		StringBuilder sb = new StringBuilder();
		for(String s: values) {
			// Using ternary generates spurious exception
			if(punc) sb.append(sep); else punc = true;
			sb.append(s);
		}
		return sb.toString();
	}
	
	public Map<String, String> parseLabeledURI(String[] items) {
		return this.parseLabeledURI(Arrays.asList(items));
	}
	/**
	 * Take a labeled URI string (that's a URI with a label at the end) and split it.
	 * This returns a map where URI is the key, and label is the value. If no label is found,
	 * the URI is also set as the label.
	 * @param items List of labeled URIs
	 * @return Key is URI and value is label.
	 */
	public Map<String, String> parseLabeledURI(List<String> items) {
		Map<String, String> map = new java.util.HashMap<String, String>();
		//System.out.println("-----------------------------> parsing label");
		if(items == null) return map;
		String[] parts;
		String val;
		for(String item: items) {
			if(item != null) {
				parts = item.split("\\s",2);
				val = (parts.length > 1) ? parts[1] : parts[0]; 
				map.put(parts[0], val);
			}
		}
		return map;
	}
	
	/**
	 * Turn an array into a list, which is easier to manipulate in Velocity.
	 * Helper for templates. Since {@link Arrays} is not in scope for velocity, this
	 * helper simply uses the toList() function of {@link Arrays} to convert an array to a
	 * {@link List}.
	 * @param array Array to convert
	 * @return List containing array.
	 */
	public List<Object> toList(Object[] array){
		return Arrays.asList(array);
	}
	
	/**
	 * Check to see if a given object is not empty.
	 * This can check the following types:
	 * <ul>
	 * <li>Collection (true if not null and size > 0)</li>
	 * <li>Map (true if not null and size > 0)</li>
	 * <li>Object[] (True if not null and length > 0)</li>
	 * <li>String (true if not null and length > 0)</li>
	 * <li>Boolean (true if true)</li>
	 * <li>Number (true if intval is > 0)</li>
	 * </ul>
	 * <p>For any other object, if it's not null, it is not empty.</p>
	 * @param o
	 * @return
	 */
	public boolean notEmpty(Object o) {
		if (o == null) return false;
		if (o instanceof Iterable) {
			// TODO: rework this with generics?
			Iterable<Object> c = (Iterable<Object>)o;
			//System.err.format("\nIterable being tested: %s.\n", o.toString());
			java.util.Iterator<Object> i = c.iterator();
			if(!i.hasNext()) return false;
			Object oo = i.next();
			if(oo.toString().length() > 0) return true;
			return i.hasNext();
		//} else if (o instanceof Collection) {
		//	Collection<Object> c = (Collection<Object>)o;
		//	return c.size() > 0;
		} else if( o instanceof Map) {
			Map<Object, Object> c = (Map<Object,Object>)o;
			return c.size() > 0;
		} else if( o instanceof Object[]) {
			Object[] c = (Object[])o;
			//System.err.println("\nObject array being tested.\n");
			if(c.length == 1) {
				return c[0].toString().length() > 0;
			} else if (c.length > 1) {
				return true;
			}
			return false;
		//} else if (o instanceof String[]) {
		//	Object[] c = (Object[])o;
		//	System.err.println("\nString array being tested.\n");
		//	return c.length > 0;
		} else if( o instanceof String) {
			return ((String)o).length() > 0;
		} else if( o instanceof Boolean) {
			return (Boolean)o;
		} else if( o instanceof Number) {
			return ((Number)o).intValue() > 0;
		}
		return true; // Default case: assume that if not null, not empty
	}
	/**
	 * Shorthand alias for {@link notEmpty(Object)}
	 * @param o Object to test
	 * @return true if the object is not empty, false otherwise.
	 */
	public boolean ne(Object o){ return this.notEmpty(o); }
	
	
	/**
	 * Escape for HTML/XML display.
	 * @param str
	 * @return
	 */
	public String escape(String str) {
		return Scrubby.encodeHTMLChars(str, false);
	}
	
	public String e(String str) {
		return this.escape(str);
	}
	
	public Scrubby scrubby() { return new Scrubby(); }

}
