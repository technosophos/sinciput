package com.technosophos.sinciput.commands.notes;

import java.util.HashMap;
import java.util.Map;

import com.technosophos.sinciput.commands.ListDocuments;
import com.technosophos.sinciput.types.NotesEnum;

public class ListNotes extends ListDocuments {

	public Map<String, String> narrower() {
		Map<String, String> narrower = new HashMap<String, String>();
		narrower.put(NotesEnum.TYPE.getKey(), NotesEnum.TYPE.getFieldDescription().getDefaultValue());
		
		return narrower;
	}
	
	public String[] additionalMetadata() {
		return new String[] {
			NotesEnum.TITLE.getKey(),
			NotesEnum.SUBTITLE.getKey(),
			NotesEnum.TAG.getKey(),
			NotesEnum.CREATED_ON.getKey(),
			NotesEnum.LAST_MODIFIED.getKey(),
		};
	}

}
