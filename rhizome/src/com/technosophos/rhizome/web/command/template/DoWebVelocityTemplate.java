package com.technosophos.rhizome.web.command.template;

import org.apache.velocity.VelocityContext;

import com.technosophos.rhizome.web.util.TemplateTools;
import com.technosophos.rhizome.command.template.DoVelocityTemplate;

/**
 * Provide Velocity template rendering services as a command.
 * 
 * In relation to its parent class, this one adds some template tools to the velocity context.
 * @author mbutcher
 * @see TemplateTools
 */
public class DoWebVelocityTemplate extends DoVelocityTemplate {
	protected VelocityContext createContext() {
		VelocityContext cxt = super.createContext();
		TemplateTools tt = new TemplateTools();
		cxt.put("tpl", tt);
		return cxt;
	}
}
