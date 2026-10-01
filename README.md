# NeonCharacterBuilder
This tool is designed to aid in the creation of Neon Odyssey 5.5E characters by providing an interactive walkthrough interface and auto-filling the form-fillable character sheet pdf with all selected and auto-calculated fields.

It does not provide any of the content of the Neon Odyssey PDFs, nor the form-fillable character sheet, just the setup to work with that content/sheet. You will need to provide your own content/sheet for best usage.

### Notes
Currently the app supports characters of up to level 3. Features at level 4 and above are not yet guaranteed to be implemented but will be in the future.

## Installation
You have 2 options for installation
1. Download the latest NeonCharacterBuilder-Windows.zip folder and unzip it somewhere on your machine.
or 2. Download the source code and compile it yourself (I'm assuming if you take this option that you know what you're doing, the main file is ncb.main.BaseCharacterWindow)

Regardless, you should end up with a NeonCharacterBuilder exe in a folder on your computer. In that same folder:
1. Copy the contents of the data folder from this repo into a data folder in that folder
2. Create a folder in that folder named "resources" and place a copy of the form fillable Neon Odyssey character sheet (you will need to get this from the kickstarter files) in it.

You should end up with a structure that looks like:
Folder
--NeonCharacterBuilder.exe
--data
----0_5E_SRD_Content.nlib
----1_Outrunners_Handbook_Public.nlib
----2_Overdrive_Expansion_Public.nlib (skip this file if you don't want the species/subclasses from the overdrive expansion to appear in the character builder)
--resources
----Neon-Odysset-Character-Sheet.pdf

## Setting up Data
By default the data library files are extremely limited. They don't contain any content that is not Creative Commons licensed or publicly released. This means that the character builder is extremely limited in functionality initially, offering only a selection of class/subclass/species and then setup of background/abilities with no handling of class features, species characteristics, etc.

You can enter those yourself using the built-in Data Editor tool. On the initial Details tab of the Character Builder the "back" button in the bottom left is replaced with a "Data Editor" button that will open the data editor in a new window. You can create/update all required data using this tool. See "DATA EDITOR README.md" for more details on how each object type can be configured.

Once you have setup your data as you wish, you can save your changes to a custom .nlib file, which will be placed in the /data folder. You can easily distribute this custom file to others in your gaming group so you can all share the same object data and don't need to individually set it up. 

Note that the character builder Data Editor determines if a file is customized by checking if it originated in a library file with "Custom" in the name, so if you change the name of this .nlib file to something else it won't show the data files as customimzed.

## Output
The character builder can output 3 different types of files. All files are named based on the character name provided.

WARNING - The character builder will currently override any files of the same names it finds. Keep unique names for your unique characters or move the files elsewhere to avoid data loss.

(character name).nchar - The nchar file is a record of all choices made for the character, designed for re-importing into the app. This file is generated when you hit the "Save" button at the bottom of the app or the "Save and Export" button on the Summary tab. It can be loaded in using the "Load" button on the Details tab and will override all current selections.

(character name).pdf - A copy of the form-fillable pdf you provided will all fields filled as best as the character builder can. The accuracy and usefulness of this file is heavily dependent on the data setup being done, the less data that is provided the less can be auto-filled.

(character name) Spells.docx - Only output if spells have been entered into the data and spell selection/granting has been setup in the data editor, this doc provides a list of all spells the character knows with full details for ease of spellcaster lookup.