package com.technosophos.sinciput.commands.course;

import java.util.Map;
import java.util.HashMap;
import com.technosophos.sinciput.commands.ListDocuments;
import com.technosophos.sinciput.types.CourseEnum;

/**
 * Command for getting a list of courses.
 * This class provides a command for retrieving listings of courses in named
 * repositories.
 * @author mbutcher
 *
 */
public class ListCourses extends ListDocuments {
	
	public Map<String, String> narrower() {
		Map<String, String> narrower = new HashMap<String, String>();
		narrower.put(CourseEnum.TYPE.getKey(), CourseEnum.TYPE.getFieldDescription().getDefaultValue());
		
		return narrower;
	}
	
	public String[] additionalMetadata() {
		return new String[] {
			CourseEnum.COURSE_NUMBER.getKey(),
			CourseEnum.TITLE.getKey(),
			CourseEnum.COURSE_TIMES.getKey(),
			CourseEnum.INSTRUCTOR.getKey(),
			CourseEnum.INSTRUCTOR_EMAIL.getKey()
		};
	}
}
