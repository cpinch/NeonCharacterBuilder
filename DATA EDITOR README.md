# NeonCharacterBuilder Data Editor
The data editor is intended to allow users to enter in the data from the Neon Odyssey system (or, technically, most any 5.5E system, though only Neon Odyssey features are guarenteed to be supported) as distributing the class/species/etc features would not be ok.

THIS TOOL IS A WORK IN PROGRESS. Some of its functionality is a bit clunky right now. I intend to clean up and improve it as level-up support is built in and I use it for adding higher level class features to my own private data libraries.

## Getting started
As noted in the main README, to start the Data Editor, open the NeonCharacterBuilder jar and click the Data Editor button in the bottom left of the details tab, where the Back button normally is.

You will open to a page with the data types in a selectable list on the left and a Save and Return button at the bottom. Click on any of the types to select and edit or create new objects of that type. When you are done making changes, click the Save and Return button to generate the Custom .nlib file with all of your changes for future use and your own group-internal distribution.

Each data type sub-panel will have a "New" and "Clear" button at the bottom of its list. New will prompt you for a name and then create a new element of that name and add it to the list of options to edit. Clear will mark the item as no longer customized, which means that the next time you save it will not be saved in your custom data library. If the item is in another library in your data folder this will return it to defaul. Otherwise it will remove it.

### Backgrounds
Backgrounds are TBD. I am waiting for actual playtest sample backgrounds before building the save/load functionality for these. Most likely they'll look like standard 5.5E backgrounds, but until that's settled down all backgrounds in the app are custom.

### Classes
+ Name - The class name
+ Desc - I put in the little flavor line from the class splash page here, this is intended as a "here's what this class is about" primer for users, so put whatever you want in.
+ Primary Ability - For classes with a choice for primary ability, put "(ability 1) or (ability 2)". For classes with a single set primary ability just put in that ability. For classes with multiple primary abilities put "(ability 1) and (ability 2)". In all cases, abilities are abbreviated to their 3 letter versions (Str, Dex, Con, Int, Wis, Cha)
+ HD - Number field, supports 4, 6, 8, 10, or 12
+ Save Profs - Put each ability the class has save proficiency in, comma separated. If the class has proficiency in its selected Primary ability, put "Primary" for that proficiency (ie "Str, Primary")
+ Skill Options - Put each Skill the class can get proficiency in, comma separated. If the class can be proficient in any skills, put "Any"
+ Skill Count - Number field, put how many skills the class gets to choose, supports 1-5
+ Weapon Profs - Put whatever weapon proficiencies the class gets, comma separated.
+ Tool Profs - Put whatever tool proficiencies the class gets, comma separated.
+ Armor Trainings - Put whatever armor types the class gets training in, comma separated.
+ Equipment - Pipe (|) separated equipment options. Each option should be in the format "(items) / Notes: (notes)"
+ Known Spells/Lvl - Comma separated list of levels. This sets the set of dropdowns to be shown on the spells page.
	* For classes that can swap out spells known (or prepared) with ones of any level they have access to, enter (class level)-(cantrips known)-(total known spells lvl 1+) for each level. You can skip duplicates, the highest class level with an entry will be used. For example: "1-2-2, 2--2-3, 4-2-4".
	* For classes that cannot swap out spells and gain a certain number each level (simiilar to the 5.5E Wizard), enter (class level)+(new known cantrips)+(new known spells lvl 1+) for each level. Any levels skipped will not gain new spells. New spells will automatically be of the highest level slot available at that level. For example: "1+2+4, 2+0+2, 3+1+2"
+ Spell Slots/Lvl - Comma separated list of levels. This sets the slots put into the character sheet spells area.
	* For classes that only have a single group of slots that increase in level (similar to the 5.5E Warlock), enter (class level)-(spell level)- (count), for example "1-1-1, 2-1-2, 3-2-3". Duplicate levels can be skipped, the last level with values will be used.
	* For classes that have multiple levels of slots they should be entered as (class level) [(spell level)-(count) | (spell level)-count], for example "1 [1-2], 2 [1-3], 3 [1-4 | 2-1]" would indicate that the class gets 2 level 1 slots at level 1, 3 level 1 slots at level 2, and 4 level 1 and 1 level 2 slot at level 3. Duplicate values can be skipped, for example if 2 levels give 4 level 1 spells you can just enter [1-4] for the first and then skip straight to level 2+ for the second and it will pick up the max count from prior levels for the unlisted spell level.

#### Class Features
The Add Class Feature button will create a new Class Feature for the class. Each feature has a name and level by default and shows a dropdown will all the supported options and a "Add Feature" button. To set these up properly, choose each option the feature has, add it, and then fill it in. For example, if you had a class feature that gave +5 speed always and let you perform a surge of speed some # of times per day with text rules you would select "Increase Speed", click Add Feature, enter 5 in the resulting number field, then select "Text", click Add Feature, and type in the text rules.

All of these options add to their respective areas of the sheet (spells go in the spells area, resistances in the resistance box, etc). Text goes straight into the Features box (unless it's text for a Feat, which goes in the Feat field) and Sheet Notes goes straight into the "Notes" section of the PDF.

Class Features whose name contains "Magic" or "Spellcasting" also allow the selection of a spellcasting ability. Only use this dropdown for a class's main spellcasting feature.

See the "Features" section below for exact details on how each of these option's corresponding fields behave.

#### Class Selectables
The Add Class Selectable button will create a new Selectable for this class. This is intended for classes that have features that let you choose from multiple options. See "Selectables" below for more details on how these work.

### Feats
+ Name - The feat name
+ Feat Type - The feat type, ie "Origin", "Fighting Style", etc
+ Prereqs - The prerequisites for the feat to be selected. Only feats of the correct type and whose prereqs are met will be shown to users.
+ Features - See below for how features work

### Homeworlds
+ Name - The world name
+ Traits - Comma separated list of homeworld traits, used for marking what origin traits are valid
+ Desc - Simple text

### Languages
+ Name - The language name
+ Spoken Locations - Comma separated list of locations the language is commonly spoken at, used for suggestions

## Selectables
This category is a catch-all for choices the user can select from that work like feats but aren't feats, ala the old 5E lineages. These are associated to Features via their type. You can have any number of Selectables with the same type as long as they all have unique names. They work identical to feats, just with Selectable Type instead of Feat Type. Make sure you have a feature that allows selection of their types if you want them to be used.

### Species
+ Name - The species name
+ Desc - I put in the couple of paragphs of descripotion species page here, this is intended as a "here's what this species is about" primer for users, so put whatever you want in.
+ Type - Simple text, defaults to Humanoid
+ Size - Choose M, S, or "M or S"
+ Speed - Number field, defaults to 30
+ Homeworld - Enter the homeworld for the species or leave blank for species without one, used for suggestions

#### Species Traits
The Add Trait button will create a new Species Trait for the Species. These work identically to Class Features.

See the "Features" section below for exact details on how each of these option's corresponding fields behave.

#### Species Selectables
The Add Selectable button will create a new Selectable for this species. This is intended for species that have features that let you choose from multiple options. See "Selectables" below for more details on how these work.

### SpellList
+ Name - The class name the spell list is associated with
+ Cantrips - Comma separated list of level 0 cantrips the class can choose from
+ Level N - Comma separated list of spells of that level the class can choose from

### Spell
+ Name - The spell name
+ Spell Level - The level of the spell, 0-9
+ School - The school of the spell
+ Cast Time - Should be one of "A" (1 action), "B" (bonus action), "R" (reaction), or a listed casting time if longer
+ Trigger (Opt) - Optional field for the spell trigger, used for reaction and bonus action spells
+ Components - Should be abbreviated, ie "V, S, M"
+ Materials (Opt) - Optional field for the spell materials, really only needed for costly materials, but can be entered for any spell with materials
+ Range - The spell's listed range
+ Duration - The spell's duration, for concentration effects list the max duration with a (C), ie "1 minute (C)"
+ Ritual - Check if the spell is a ritual spell
+ Text - The text of the spell

## Selectable
Some classes and species have extra selectable options built-in that are unique to them. These are handled by the "Selectable" setup in the app. For these classes and species the following steps need to be done.

1. Create a Class Feature or Species Trait with the "Choose Selectable" option set and the selectable type entered.
2. For each option that can be selected, create and fill in a new Selectable

Selectables have 3 parts.
+ Selectable Type - Should be set to match whatever was entered in the "Choose Selectable" above, this is how these are located for each class feature/species trait.
+ Selectable Prereqs - Current 3 types of prereqs are supported. Class Level expects a comma separated list of required class levels in the format "(class)-(level)", ie "Cryptist-2". Prior Selection expects a name of a Class Feature, Selectable, or Feat that must already be on the character sheet for this option to be available. Homeworld Trait expects a comma separated list of homeworld traits, at least one of which must be present in the user's chosen homeworld.
+ Selectable Features - These are Features. See below for details.

## Features
A lot of things in this application are classified as "Features", a generic catch-all term for "part of a character that changes something about that character". The Features data editor is generic and shared across Classes, Feats, and Species. It has so many fields that it uses a dropdown to select which you want to include. Details on all the fields are below:

+ Text - A simple text field
+ Gives Skill Profs - Comma separated list of Skills
+ Choose Skill Profs - Comma separated list of Skills plus number field (supports 0-5)
+ Gives Save Profs - Comma separated list of Abilities
+ Gives Spells - Pipe (|) separated list of Spells. Each spell may optionally have an attached note in parenthesis () after it with specific details. For example, "Eldritch Blast (1/long rest double damage)" (the | is used instead of , because you may want to include commas in your note). Spells with alternate names should put the original spell name in [], ie "Eldritch Super Blast [Eldritch Blast] (1/long rest double damage)"
+ Choose Spells - Expects a comma separated list of spell choices in the format "(level)-(count)-(spelllist)". For example, if the feature gives the player the option to choose 2 level 1 spells from the Oracle list, you would enter "2 1 Oracle"
+ Choose Specific Spells - Expects a pipe (|) separated list of spells for the user to choose one from in the standard spell format "spell name [optional spell base name if renamed] (optional notes)"
+ Gives Armor Train - Comma separated list of Armor Trainings
+ Gives Languages - Comma separated list of Languages
+ Choose Languages - Comma separated list of Languages the user can choose from plus number field for how many they can choose (supports 0-5). Use "Any" for any languages.
+ Gives Resistances - Comma separated list of Resistances
+ Gives Resistances based on Homeworld Traits - Comma separated list of "(Homeworld Trait)-(Resistance given)". For example, if you had homeworld traits of "Acidic" and "Basic" that you wanted to give resistances to Acid and Bludgeoning respectively, you would enter "Acidic-Acid, Basic-Bludgeoning"
+ Choose Resistances - Comma separated list of Resistance options (note - currently only supports 1 resistance choice per feature, so no number is provided at this time)
+ Choose Skill Expertise - Comma separated list of Skills, users will be able to select Skills from this list only if they already have proficiency, "Any" for any.
+ Gives Tool Profs - Comma separated list of Tool Proficiencies.
+ Gives Weapon Profs - Comma separated list of Weapon Proficiencies.
+ Sets AC Abilities - Comma separated list of Ability mods to add to base AC (ie "Dex, Wis" or "Con, Int")
+ Adds Extra Ability to Skills - Comma separated list of Skill-Ability mappings. For example, if your feature gives Wis to Religion and Arcana, you would enter "Religion-Wis, Arcana-Wis"
+ Increase Speed - Number field, supports 0-50 in increments of 5
+ +HP Lvl 1 Only - Number field, supports 0-5
+ +HP/Level - Number field, supports 0-5
+ Gives Half Proficiency to all non-Proficient Skills - Exactly what it says, used for stuff like the 5.5E Bard Jack of All Trades feature. Simple on/off checkbox.
+ Gives Proficiency to Initiative - Exactly what it says. Simple on/off checkbox.
+ Choose Feat - Expects a single feat type (ie "Origin" or "Fighting Style", etc). Optionally you may add "(ignores prereqs)" after the feat trait name to indicate that it ignores feat prereqs (for example, a feature that allows picking any Origin feat, regardless of homeworld)
+ Choose Selectable - Expects a single selectable type (see Selectables above for more details). Note that if both Feat and Selectable are added for a single feature they will be combined into a single list, useful for features that offer selections like "Choose a feat of type. You may also choose from the options below"
+ Puts Notes on Sheet - Text field, goes straight into the Notes field on the character sheet without being shown anywhere in the app.