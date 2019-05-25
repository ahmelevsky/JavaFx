package te;

import java.util.ListResourceBundle;

public class Language_ru  extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		// TODO Auto-generated method stub
		return contents;
	}
	
	private Object[][] contents = {
            { "ui.menu.data.header"   , "����"},
            { "ui.menu.write.header"   , "������"},
            { "ui.menu.settings.header"   , "���������"},
            { "ui.menu.data.item1"   , "������"},
            { "ui.menu.data.item2"   , "�������"},
            { "ui.menu.data.item3"   , "��������� ���������"},
            { "ui.menu.data.item4"   , "�������� ���"},
            { "ui.menu.data.item5"   , "�������"},
            { "ui.menu.settings.write1"   , "������ ������ � jpg"},
            { "ui.menu.settings.write2"   , "������ ������ � eps"},
            { "ui.menu.settings.write3"   , "������ � � eps � � jpg"},
            { "ui.menu.settings.lang1"   , "���� ����������: �������"},
            { "ui.menu.settings.lang2"   , "���� ����������: ����������"},
            { "ui.menu.settings.autosave"   , "�������������� ������ 5 ���"},
            { "ui.menu.settings.about"   , "� ���������"},
            { "alert.error.title", "������" },
            { "alert.error.loaderror.header", "������ ��������" },
            { "alert.error.loaderror.content", "���������� ��������� ����" },
            { "alert.error.saveerror.header", "������ ����������" },
            { "alert.error.saveerror.content", "���������� ��������� ����" },
            { "ui.tabs.keyvars.header"   , "���������� \n������"},
            { "ui.tabs.descvars.header"   , "���������� \n��������"},
            { "ui.tabs.targets.header"   , "�������"},
            { "ui.tabs.foldervars.header"   , "���������� �����"},
            { "ui.tabs.keys.header"   , "�����"},
            { "ui.tabs.descriptions.header"   , "��������"},
            { "ui.tabs.titles.header"   , "���������"},
            { "log.message.unnalloweddescription"   , "������������ ������� � ����� �� ��������� ����� ������� ��������"},
            { "log.message.unnallowedkeywords"   , "������������ ������� � ��������� ���� ������� ���������"},
            { "log.message.unnallowedvars"   , "������������ ������� � ����� �������� ���������� "},
            { "log.message.unnallowedtarget"   , "������������ ������� � ����� �� ����� ������"},
            { "log.message.unnallowedtarget1"   , "������������ ������� � ����� �� ����� ������1"},
            { "log.message.unnallowedtarget2"   , "������������ ������� � ����� �� ����� ������2"},
            { "log.message.unnallowedsym"   , "������������ ������� � ��������� ����"},
            { "alert.error.duplicatevariables", "����� ���������� �� ������ �����������, ��������� ������ ��� ����������" },
            { "alert.error.nofolder", "����� �� ������� ��� �� ����������" },
            { "alert.error.nofiles", "� ��������� �������� ����� ��� ����� � ������� " },
            { "alert.error.incorrectdata", "������������ ������� ������, ���������� ����������� ������. ����������� � ����� ����" },
            { "alert.error.writeerror", "ERROR: ������ ������ ���������� � " },
            { "alert.info.done", "������: ���������� ���� �������� ��� ������: " },
            { "alert.warn.done", "WARNING! ���������� ���� �������� ��� ������: " },
            { "alert.warn.done2", ", �� ���������� �������� ������: " },
            { "alert.problem.done", "\n�� ����� ���������� ���������� ���� ��������, �������� ���� ����" },
            { "alert.error.done", "������ ���������� ��������� � �������. �������� ���� ����" },
            { "alert.write.cancelled", "������ ���������� ���� ��������" },
            { "alert.info.selectfolder", "�������� �������� ����� (� ��� ������ ���� ����� � ������ �������)" },
            { "alert.conf.clear", "�� �������? ��� ������ � ���������� ����� �������" },
            { "alert.conf.clear.title", "�������" },
            { "alert.conf.clear.header", "���������� �������������" },
            { "log.message.write", "������ ���������� � ���� " },
            { "parser.unexistedvar.begin", "���������� � ������ " },
            { "parser.unexistedvar.end", " �� ����������"},
            { "parser.badsymbols", "� ������ ������������ ����������� ������� <>[] ��� ����������� ���������� ������� ����������" },
            { "ui.selector.text", "<�����>"},
            { "ui.tabs.descriptions.maxchars", "������������ ����� ��������: "},
            { "alert.error.pastevars", "ERROR: ������ ������� ���������� � ������: "},
            { "ui.tabs.keys.all", "���"},
            { "ui.tabs.keys.wordscount", "����: "},
            { "ui.tabs.vars.delimiter.space", "������"},
            { "ui.tabs.vars.delimiter.newline", "������� ������"},
            { "ui.tabs.vars.loadhint", "��������� �� ����� ����� ������"},
            { "ui.tabs.vars.savehint", "��������� � ���� ����� ������"},
            { "alert.error.addtoform.content", "�� ���� �������� �������� �� �����" },
            { "alert.error.removeelements.content", "�� ���� ������� ��������" },
            { "alert.load.content", "�������� ���� � �������" },
            { "alert.save.content", "��������� ������ � ����" },
            { "ui.tabs.desc.random"   , "������"},
            { "ui.tabs.desc.vars"   , "����������"},
            { "ui.tabs.desc.desc"   , "��������"},
            { "ui.tabs.desc.refresh"   , "��������"},
            { "ui.tabs.keys.vars"   , "���������� ������"},
            { "ui.tabs.keys.count"   , "����� ������"},
            { "ui.tabs.keys.main"   , "�������� ����"},
            { "ui.tabs.keys.addvar"   , "�������� ���������� �����"},
            { "ui.tabs.keys.addtarget"   , "�������� ������"},
            { "ui.tabs.keys.refresh"   , "��������"},
            { "ui.tabs.main.writebtn"   , "�������� ���������� � �����"},
            { "ui.tabs.main.cancelbtn"   , "�������� ������"},
            { "ui.tabs.main.clearbtn"   , "�������� ���"},
            { "ui.tabs.main.selectfolder"   , "<�������� ����� � �������>"},
            { "ui.tabs.targets.save"   , "���������"},
            { "ui.tabs.targets.load"   , "���������"},
            { "ui.tabs.title.takefromdesc"   , "��������� ������� �� ��������"},
            { "ui.tabs.titles.cut"   , "�������� ��:"},
            { "ui.tabs.varcont.name"   , "��� ����������"},
            { "ui.tabs.varcont.devider"   , "�����������"},
            { "ui.tabs.varcont.values"   , "�������� (����� �����������)"},
            { "ui.tabs.varcont.delete"   , "�������"},
            { "ui.tabs.varcont.save"   , "���������"},
            { "ui.tabs.varcont.load"   , "���������"},
            { "ui.tabs.foldervars.savepreset"   , "��������� ����"},
            { "ui.tabs.foldervars.labeltext"   , "��������� ����:"},
            { "log.warn.epsnotfound"   , "�� ������ ������ eps ���� ���: "}
    };
}
