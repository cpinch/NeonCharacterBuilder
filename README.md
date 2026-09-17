# NeonCharacterBuilder
This tool is designed to aid in the creation of Neon Odyssey 5.5E characters by providing an interactive walkthrough interface and auto-filling the form-fillable character sheet pdf with all selected and auto-calculated fields.

It does not provide any of the content of the Neon Odyssey PDFs, nor the form-fillable character sheet, just the setup to work with that content/sheet. You will need to provide your own content/sheet for best usage.

### Notes
Currently the app only supports creation of characters, levels past 1 are not yet supported. This functionality is being worked on and it is intended that this app will support levels 1-20 with multiclassing support before v2.

## Installation
Prereq - This tool is built in Java 16. You will need a JRE of that version or newer to run it.

1. Download (or compile yourself) the NeonCharacterBuilder.jar runnable jar file from the releases page. Place it in a folder somewhere on your machine.
2. In the same folder, place a subfolder named "data", copy the contents of the data folder from this repo there (you may wish to skip the Overdrive Expansion public data file if you don't own that expansion).
3. In the same folder as the .jar file, place a subfolder named "resources". Place a copy of the form fillable Neon Odyssey character sheet (you will need to get this from the kickstarter files) in that subfolder.
4. You are good to go. You can run the character builder however your local OS runs jars ("java -jar NeonCharacterBuilder.jar" in a command prompt or terminal window by default)

## Setting up Data
By default the data library files are extremely limited. They don't contain any content that is not Creative Commons licensed or publicly released. This means that the character builder is extremely limited in functionality initially, offering only a selection of class/species and then setup of background/abilities with no handling of class features, species characteristics, etc.

You can enter those yourself using the built-in Data Editor tool. On the initial Details tab of the Character Builder the "back" button in the bottom left is replaced with a "Data Editor" button that will open the data editor in a new window. You can create/update all required data using this tool. See "DATA EDITOR README.md" for more details on how each object type can be configured.

Once you have setup your data as you wish, you can save your changes to a custom .nlib file, which will be placed in the /data folder next to the NeonCharacterBuilder.jar file. You can easily distribute this custom file to others in your gaming group so you can all share the same object data and don't need to individually set it up.

## Output
The character builder can output 3 different types of files. All files are named based on the character name provided.

WARNING - The character builder will currently override any files of the same names it finds. Keep unique names for your unique characters or move the files elsewhere to avoid data loss.

(character name).nchar - The nchar file is a record of all choices made for the character, designed for re-importing into the app. This file is generated when you hit the "Save" button at the bottom of the app or the "Save and Export" button on the Summary tab. It can be loaded in (overriding whatever is currently selected, be warned) using the "Load" button on the Details tab.

(character name).pdf - A copy of the form-fillable pdf you provided will all fields filled as best as the character builder can. The accuracy and usefulness of this file is heavily dependent on the data setup being done, the less data that is provided the less can be auto-filled.

(character name) Spells.docx - Only output if spells have been entered into the data and spell selection/granting has been setup in the data editor, this doc provides a list of all spells the character knows with full details for ease of spellcaster lookup.