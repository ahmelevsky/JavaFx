package te;

import java.util.ListResourceBundle;

public class Language_en extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		// TODO Auto-generated method stub
		return contents;
	}
	
	private Object[][] contents = {
			 { "ui.menu.data.header"   , "File"},
	            { "ui.menu.write.header"   , "Write"},
	            { "ui.menu.settings.header"   , "Settings"},
	            { "ui.menu.data.item1"   , "Import"},
	            { "ui.menu.data.item2"   , "Export"},
	            { "ui.menu.data.item3"   , "Save State"},
	            { "ui.menu.data.item4"   , "Clear All"},
	            { "ui.menu.data.item5"   , "Close"},
	            { "ui.menu.settings.write1"   , "Write to jpg only"},
	            { "ui.menu.settings.write2"   , "Write to eps only"},
	            { "ui.menu.settings.write3"   , "Write to both eps and jpg"},
	            { "ui.menu.settings.lang1"   , "Interface Language: Russian"},
	            { "ui.menu.settings.lang2"   , "Interface Language: English"},
	            { "ui.menu.settings.autosave"   , "Autosave every 5 minutes"},
	            { "ui.menu.settings.about"   , "About"},
	            { "alert.error.title", "Error" },
	            { "alert.error.loaderror.header", "Load Error" },
	            { "alert.error.loaderror.content", "Can't load file" },
	            { "alert.error.saveerror.header", "Save Error" },
	            { "alert.error.saveerror.content", "Can't save file" },
	            { "ui.tabs.keyvars.header"   , "Keywords \nvariables"},
	            { "ui.tabs.descvars.header"   , "Description \nvariables"},
	            { "ui.tabs.targets.header"   , "Targets"},
	            { "ui.tabs.foldervars.header"   , "Folder \nvariables"},
	            { "ui.tabs.keys.header"   , "Keywords"},
	            { "ui.tabs.descriptions.header"   , "Descriptions"},
	            { "ui.tabs.titles.header"   , "Titles"},
	            { "log.message.unnalloweddescription"   , "Unallowed symbols in some of the text fields on Description tab"},
	            { "log.message.unnallowedkeywords"   , "Unallowed symbols in some of the text fields on Title tab"},
	            { "log.message.unnallowedvars"   , "Unallowed symbols in some of the variable values"},
	            { "log.message.unnallowedtarget"   , "Unallowed symbols in some of the Target fields"},
	            { "log.message.unnallowedtarget1"   , "Unallowed symbols in some of the Target1 fields"},
	            { "log.message.unnallowedtarget2"   , "Unallowed symbols in some of the Target2 fields"},
	            { "alert.error.duplicatevariables", "Duplicated variables! Rename duplicates to continue" },
	            { "alert.error.nofolder", "Folder is not selected or doesn't exist" },
	            { "alert.error.nofiles", "There is no folders with files under the root folder " },
	            { "alert.error.incorrectdata", "Incorrect input data, can't write metadata. See the log file for details" },
	            { "alert.error.writeerror", "ERROR: metadata write error in " },
	            { "alert.info.done", "DONE: Metadata was written to the files count: " },
	            { "alert.warn.done", "WARNING! Metadata was written to the files count: " },
	            { "alert.warn.done2", ", but writing failed for: " },
	            { "alert.problem.done", "\nThere were problems during compiling metadata, see the log file" },
	            { "alert.error.done", "Writing of metadata was finished with an error, see the log file" },
	            { "alert.write.cancelled", "Writing of metadata was cancelled " },
	            { "alert.info.selectfolder", "Select a root folder (there should be folders with your files under the root)" },
	            { "alert.conf.clear", "Are you sure? All the data in forms will be cleared" },
	            { "alert.conf.clear.title", "Cleaning" },
	            { "alert.conf.clear.header", "Confirmation is needed" },
	            { "log.message.write", "Metadata writing to file" },
	            { "parser.unexistedvar.begin", "Variable with a name " },
	            { "parser.unexistedvar.end", " doesn't exist"},
	            { "parser.badsymbols", "You put special symbols in the text like: <>[] without the correct variable syntax" },
	            { "ui.selector.text", "<text>"},
	            { "ui.tabs.descriptions.maxchars", "Max symbols count: "},
	            { "alert.error.pastevars", "ERROR: Error pasting variable to the string: "},
	            { "ui.tabs.keys.all", "All"},
	            { "ui.tabs.keys.wordscount", "Words count: "},
	            { "ui.tabs.vars.delimiter.space", "SPACE"},
	            { "ui.tabs.vars.delimiter.newline", "LINE BREAK"},
	            { "ui.tabs.vars.loadhint", "Load dataset from a file"},
	            { "ui.tabs.vars.savehint", "Save dataset to a file"},
	            { "alert.error.addtoform.content", "Can't add elements to the form" },
	            { "alert.error.removeelements.content", "Cant remove elements from the form" },
	            { "alert.load.content", "Select file with data" },
	            { "alert.save.content", "Save data to a file" },
	            { "ui.tabs.desc.random"   , "Random"},
	            { "ui.tabs.desc.vars"   , "Variables"},
	            { "ui.tabs.desc.desc"   , "Description"},
	            { "ui.tabs.desc.refresh"   , "Refresh"},
	            { "ui.tabs.keys.vars"   , "Keywords variables"},
	            { "ui.tabs.keys.count"   , "Keywords count"},
	            { "ui.tabs.keys.main"   , "Main block"},
	            { "ui.tabs.keys.addvar"   , "Add Folder Variable"},
	            { "ui.tabs.keys.addtarget"   , "Add Target"},
	            { "ui.tabs.keys.refresh"   , "REFRESH"},
	            { "ui.tabs.main.writebtn"   , "Write metadata to the files"},
	            { "ui.tabs.main.cancelbtn"   , "Cancel writing metadata"},
	            { "ui.tabs.main.clearbtn"   , "Clear all"},
	            { "ui.tabs.main.selectfolder"   , "<Select root folder>"},
	            { "ui.tabs.targets.save"   , "Save"},
	            { "ui.tabs.targets.load"   , "Load"},
	            { "ui.tabs.title.takefromdesc"   , "Take title from the description"},
	            { "ui.tabs.titles.cut"   , "Cut to symbols:"},
	            { "ui.tabs.varcont.name"   , "Variable name"},
	            { "ui.tabs.varcont.devider"   , "Devider"},
	            { "ui.tabs.varcont.values"   , "Values (with the delimiter)"},
	            { "ui.tabs.varcont.delete"   , "Delete"},
	            { "ui.tabs.varcont.save"   , "Save"},
	            { "ui.tabs.varcont.load"   , "Load"},
	            { "log.warn.epsnotfound"   , "EPS file not found for: "}
    };

}