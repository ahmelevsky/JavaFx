;This file will be executed next to the application bundle image
;I.e. current directory will contain folder AddressApp with application files
[Setup]
AppId={{fxApplication}}
AppName=AddressApp
AppVersion=1.0
AppVerName=AddressApp 1.0
AppPublisher=Artiom
AppComments=TestJavaFX
AppCopyright=Copyright (C) 2017
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={localappdata}\AddressApp
DisableStartupPrompt=Yes
DisableDirPage=Yes
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=Artiom
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=AddressApp-1.0
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=AddressApp\AddressApp.ico
UninstallDisplayIcon={app}\AddressApp.ico
UninstallDisplayName=AddressApp
WizardImageStretch=No
WizardSmallImageFile=AddressApp-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "AddressApp\AddressApp.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "AddressApp\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\AddressApp"; Filename: "{app}\AddressApp.exe"; IconFilename: "{app}\AddressApp.ico"; Check: returnTrue()
Name: "{commondesktop}\AddressApp"; Filename: "{app}\AddressApp.exe";  IconFilename: "{app}\AddressApp.ico"; Check: returnFalse()


[Run]
Filename: "{app}\AddressApp.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\AddressApp.exe"; Description: "{cm:LaunchProgram,AddressApp}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\AddressApp.exe"; Parameters: "-install -svcName ""AddressApp"" -svcDesc ""AddressApp"" -mainExe ""AddressApp.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\AddressApp.exe "; Parameters: "-uninstall -svcName AddressApp -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
