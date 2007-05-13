package com.technosophos.rhizome.command.util;

import java.util.List;
import java.util.Map;

import com.technosophos.rhizome.controller.CommandConfiguration;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.controller.RhizomeCommand;
import com.technosophos.rhizome.repository.RepositoryManager;

/**
 * This is a simple command that returns a {@link StringBuffer} object of info.
 * The info contains string representations of teh command configuration, the parameters
 * passed in, and the other items in the result list. 
 * @author mbutcher
 *
 */
public class DumpConfiguration implements RhizomeCommand {
	
	CommandConfiguration cc = null;

	// inherit javadoc
	public void doCommand(Map<String, Object> params,
			List<CommandResult> results) {
		StringBuffer sb = new StringBuffer();
		sb.append("Command Configuration: \n");
		Map<String, String[]> p = this.cc.getParameters();
		String [] vals = null;
		for (String k: p.keySet()) {
			sb.append("Param ");
			sb.append(k);
			sb.append(":\n");
			vals = p.get(k);
			for(String pp: vals) sb.append("\t" + pp + "\n");
		}
		
		sb.append("\nParameters Passed by Controller\n");
		for(String kk: params.keySet()) {
			sb.append(kk);
			sb.append(": ");
			sb.append(params.get(kk));
		}
		CommandResult cr = new CommandResult(this.cc, sb);
		//System.out.print(sb.toString());
		results.add(cr);

	}

	// inherit javadoc
	public void init(CommandConfiguration comConf, RepositoryManager rm) {
		this.cc = comConf;
	}

}
