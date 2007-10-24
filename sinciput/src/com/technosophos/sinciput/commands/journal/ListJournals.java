package com.technosophos.sinciput.commands.journal;

import java.util.HashMap;
import java.util.Map;

import com.technosophos.sinciput.commands.ListDocuments;
import com.technosophos.sinciput.types.JournalEnum;

public class ListJournals extends ListDocuments {

	// Inherit javadoc
	protected Map<String, String> narrower() {
		Map<String, String> narrower = new HashMap<String, String>();
		narrower.put(JournalEnum.TYPE.getKey(), JournalEnum.TYPE.getFieldDescription().getDefaultValue());
		
		return narrower;
	}
	/**
	 * Retrieve title, tag, and created_on.
	 */
	public String[] additionalMetadata() {
		return new String[] {
			JournalEnum.TITLE.getKey(),
			JournalEnum.CREATED_ON.getKey(),
			JournalEnum.TAG.getKey(),
		};
	}
}
