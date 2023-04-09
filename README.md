
# Oxide
An application for assisting the modding process of the grand strategy game Hearts of Iron 4

Currently working towards a stable **VERSION ONE** of this application, that can achieve the following:

 - Parsing provinces via bitmap reading (map/provinces.bmp)
 - Parsing states (history/states/*.txt)
 - Parsing resources, buildings, terrain
 - Parsing countries (see note 1)
 - Display the map as part of a GUI
 - Assign an alias RGB value to each province to show which state owns it

It would also need to support the following actions:

- Adding, editing, and deleting a province/state
- Adding a new resource, building, or terrain
- Modifying supported values (terrain modifiers, resources CIC's, etc.)
- Correctly updating all files to reflect the changes

## Looking ahead

A future **VERSION TWO** may sport:

- Parsing and manipulating all remaining map features (rivers, lakes, railways, weather, etc.)  
- Manipulating starting units  
- Localisation support (possibly featuring google translate API for automation)
- Limited graphical asset support (such as adding a new flag or unit icon)  
- Repairing a mod (such as fixing a broken state/province link)  
- Incorporating other modding tools like MapGen by u/Jamestom999

And in the far flung future of **VERSION THREE**, I would like to try implementing: 

- Full graphical asset support (such as adding a new unit model)  
- Comprehensive editors for other game systems (focus trees, tech trees, characters, etc.)
- Custom systems (vague, but some existing mods have these and I would like to support and possibly use their implementations)

---
Note 1:
A country feature may be as simple as just the rgb value it has on the map, like it is in (common/countries/) or as complicated as all the information pertaining to a nation in HOI4, such as focus trees, technologies, and national spirits. It would be dishonest to promise the latter for a version one, so I will not. Hearts of Iron is a tangled knot of interconnected data stored in formats that are unintuitive to conceptualise as one person, and incredibly difficult to edit without inadvertently  breaking something, and countries are the pinnacle of this.
