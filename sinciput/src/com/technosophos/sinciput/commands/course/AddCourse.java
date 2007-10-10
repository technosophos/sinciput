package com.technosophos.sinciput.commands.course;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.*;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.sinciput.SinciputException;
import com.technosophos.sinciput.types.CourseEnum;
import com.technosophos.sinciput.commands.SinciputCommand;
import com.technosophos.sinciput.util.Scrubby;

import static com.technosophos.sinciput.servlet.ServletConstants.*;
/*
import com.technosophos.rhizome.repository.util.RepositoryUtils;
import com.technosophos.sinciput.SinciputException;
import com.technosophos.sinciput.servlet.SinciputSession;
import com.technosophos.sinciput.types.admin.RepoDescriptionEnum;
import com.technosophos.rhizome.command.AbstractCommand;
//import com.technosophos.rhizome.repository.DocumentNotFoundException;
//import com.technosophos.sinciput.types.CourseEnum;
 */

/**
 * Add a course to a given repository.
 * <p>A Course represents a class, course, or seminar.</p>
 * <p>This default AddCourse class assumes that the note is in HTML. For other body types, you
 * can simply override {@link prepareBody(RhizomeDocument)}.</p>
 * <p>What this object expects in parameters:</p>
 * <ul>
 * <li>title (REQUIRED): Title of the course</li>
 * <li>description: Description of the course</li>
 * <li>instructor: name of instructor(s)</li>
 * <li>instructor_email: Email of the instructor(s)</li>
 * <li>location: Course location</li>
 * <li>course_number: The organizational number assigned. This is free-form: Phil 479 or 
 * 89796-002 should both work.</li>
 * <li>course_times: Day/time info on when the course meets. Free form.</li>
 * <li>start_date: Date course starts</li>
 * <li>end_date: Date course ends</li>
 * <li>tag(s): Zero or more tags</li>
 * <li>body: A place to put a syllabus or so on.</li>
 * </ul> 
 * <p>Additionally, it expects to be able to fetch a username from the session.</p>
 * @author mbutcher
 *
 */
public class AddCourse extends SinciputCommand {
	
	public final static String COURSE_BODY = "body";

	/**
	 * Store the note as a document in the repository.
	 */
	public void execute() throws ReRouteRequest {

		// Get user from session.
		String uname = this.ses.getUserName();
		String userid = this.ses.getUserUUID();
		String repoName;
		RepositorySearcher s_search;
		DocumentRepository s_repo;
		
		if(uname == null || userid == null) {
			String err = "No user object!";
			String ferr = "We can not verify the user ID at this time. Are you logged in?";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}


		try {
			s_search = this.repoman.getSearcher(SETTINGS_REPO);
			s_repo = this.repoman.getRepository(SETTINGS_REPO);
		} catch (RhizomeInitializationException e1) {
			String err = "Failed to initialize searcher and repo: " + e1.getMessage();
			String ferr = "Our system cannot initialize your repository. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Get repository
		try {
			repoName = this.getCurrentRepository(s_search);
		} catch (SinciputException e) {
			String err = "Repository not found: " + e.getMessage();
			String ferr = "Our system cannot find your repository. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		if(repoName == null)  return; // This should be cleaner... fix SinciputCommand
		
		// Check write access to repo
		if( ! this.userCanWriteRepo(s_repo)) {
			String err = "No write permissions to " + repoName;
			String ferr = "You are not allowed to write notes in this repository.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Create required fields
		RhizomeDocument doc = new RhizomeDocument(DocumentID.generateDocumentID());
		String title = this.getFirstParam(CourseEnum.TITLE.getKey(), null).toString();
		if( title == null ) {
			String err = "No title specified";
			String ferr = "You need to give a title. Course titles are required.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Fetch optional fields
		// - get all fields
		
		String description = this.getFirstParam(CourseEnum.DESCRIPTION.getKey(), "").toString();
		String location = this.getFirstParam(CourseEnum.LOCATION.getKey(), "").toString();
		String startDate = this.getFirstParam(CourseEnum.START_DATE.getKey(), "").toString();
		String endDate = this.getFirstParam(CourseEnum.END_DATE.getKey(), "").toString();
		String instructor = this.getFirstParam(CourseEnum.INSTRUCTOR.getKey(), "").toString();
		String instructorEmail = this.getFirstParam(CourseEnum.INSTRUCTOR_EMAIL.getKey(), "").toString();
		String courseNumber = this.getFirstParam(CourseEnum.COURSE_NUMBER.getKey(), "").toString();
		String courseTimes = this.getFirstParam(CourseEnum.COURSE_TIMES.getKey(), "").toString();
		Object tags = this.getParam(CourseEnum.TAG.getKey(), null);
		
		// - find out how tags are stored
		String[] ta;
		if( tags == null) {
			ta = new String[0];
		} else if( tags instanceof String[] ) {
			 ta = (String[])tags;
			
		} else ta = tags.toString().split(",");//new String[]{ tags.toString() };
		
		// - clean fields
		title = Scrubby.cleanText(title);
		description = Scrubby.cleanText(description);
		location = Scrubby.cleanText(location);
		startDate = Scrubby.cleanText(startDate);
		endDate = Scrubby.cleanText(endDate);
		instructor = Scrubby.cleanText(instructor);
		instructorEmail = Scrubby.cleanText(instructorEmail);
		courseNumber = Scrubby.cleanText(courseNumber);
		courseTimes = Scrubby.cleanText(courseTimes);
		
		int i, j = ta.length;
		for( i = 0; i < j; ++i) {
			ta[i] = ta[i] != null ? Scrubby.cleanText(ta[i]) : "Empty";
		}
		// - put em in metadata objects:
		doc.addMetadatum(new Metadatum(CourseEnum.TITLE.getKey(), title));
		doc.addMetadatum(new Metadatum(CourseEnum.DESCRIPTION.getKey(), description));
		doc.addMetadatum(new Metadatum(CourseEnum.LOCATION.getKey(), location));
		doc.addMetadatum(new Metadatum(CourseEnum.START_DATE.getKey(), startDate));
		doc.addMetadatum(new Metadatum(CourseEnum.END_DATE.getKey(), endDate));
		doc.addMetadatum(new Metadatum(CourseEnum.INSTRUCTOR.getKey(), instructor));
		doc.addMetadatum(new Metadatum(CourseEnum.INSTRUCTOR_EMAIL.getKey(), instructorEmail));
		doc.addMetadatum(new Metadatum(CourseEnum.COURSE_NUMBER.getKey(), courseNumber));
		doc.addMetadatum(new Metadatum(CourseEnum.COURSE_TIMES.getKey(), courseTimes));
		doc.addMetadatum(new Metadatum(CourseEnum.TAG.getKey(), ta));
		
		// - set automatic fields
		String time = com.technosophos.rhizome.util.Timestamp.now();
		doc.addMetadatum(new Metadatum(CourseEnum.TYPE.getKey(), 
					CourseEnum.TYPE.getFieldDescription().getDefaultValue()));
		doc.addMetadatum(new Metadatum(CourseEnum.CREATED_ON.getKey(), time ));
		doc.addMetadatum(new Metadatum(CourseEnum.LAST_MODIFIED.getKey(), time));
		doc.addMetadatum(new Metadatum(CourseEnum.CREATED_BY.getKey(), uname ));
		doc.addMetadatum(new Metadatum(CourseEnum.MODIFIED_BY.getKey(), uname ));

		// Do the body:
		String body = this.getFirstParam(COURSE_BODY, "").toString();
		
		try {
			this.prepareBody(body, doc);
		} catch (SinciputException se) {
			String err = String.format("HTML in \"%s\" was bad: %s", title, se.getMessage());
			String ferr = String.format("We encountered a problem when safety-checking %s.", title);
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// - Store in repo
		try {
			this.repoman.storeDocument(repoName, doc);
		} catch (RhizomeException e) {
			String err = String.format("Could not store \"%s\" in %s.", title, repoName);
			String ferr = String.format("We could not store \"%s\" in your repository.", title);
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Might as well pass the doc on
		this.results.add(this.createCommandResult(doc));
	}
	
	/**
	 * Insert the body into the document.
	 * This method assumes that the body is HTML (or, rather RHTML). It is safe to 
	 * override this method to perform other processing (including setting the text type)
	 * on the text, and then storing it in the document.
	 * @param body The body text, as extracted from params. No cleaning has been done, yet.
	 * @param doc The document that will hold the note.
	 */
	protected void prepareBody( String body, RhizomeDocument doc ) throws SinciputException {
		// 1. Do cleaning
		body = Scrubby.cleanSafeHTML(body);
		
		// 2. Set the type and store the document
		doc.setBody("text/html", body);
	}

}
