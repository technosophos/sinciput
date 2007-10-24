package com.technosophos.sinciput.commands.journal;

import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.sinciput.commands.ViewDocument;
import com.technosophos.sinciput.types.JournalEnum;

/**
 * Command to view a journal.
 * <p>This returns information about the journal itself; not the entries.</p>
 * @author mbutcher
 *
 */
public class ViewJournal extends ViewDocument {

//	 Inherit Javadoc
	protected boolean verifyDocument(RhizomeDocument doc) {
		String type = JournalEnum.TYPE.getFieldDescription().getDefaultValue();
		Metadatum m = doc.getMetadatum(JournalEnum.TYPE.getKey());
		return ( m != null && m.hasValue(type));
	}

}
