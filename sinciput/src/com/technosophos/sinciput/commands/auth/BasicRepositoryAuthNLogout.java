package com.technosophos.sinciput.commands.auth;

//import java.util.List;
//import java.util.Map;
import com.technosophos.sinciput.servlet.SinciputSession;
import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.ReRouteRequest;
import static com.technosophos.sinciput.servlet.ServletConstants.*;
/**
 * Command to do logout.
 * 
 * Logout equiv of {@link BasicRepositoryAuthN}
 * @author mbutcher
 *
 */
public class BasicRepositoryAuthNLogout extends AbstractCommand {
	/**
	 * Name of request reroute directive.
	 * After logout, get next request from {@link CommandConfiguration.getDirective(String)}
	 * using this string.
	 * "auth"
	 */
	public static final String DIR_AUTH_REQ = "auth";
	
	public void execute() throws ReRouteRequest {

		((SinciputSession)this.params.get(REQ_PARAM_SESSION)).invalidate();
		results.add(new CommandResult("Logged Out"));
		
		String[] rrc = this.comConf.getDirective(DIR_AUTH_REQ);
		// Make sure we have a command to forward to.
		if(rrc != null && rrc.length != 0) {
			throw new ReRouteRequest(rrc[0], "You are now logged out.");	
		}
	}
	
	
}
