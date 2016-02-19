Custom commands
===

Interacting with data systems.
--

Queries & searching
---

a suite of command to locate objects from your system should output machine readable identifiers. If you can make them somewhat readable by a human, all the better, but they need to uniquely identify a resource. Alternatively, you can output a second column that shows a short name for each resource. This column can be ignored by scripts, but still be present for debugging purposes.

Basic verbs
---

connect
disconnect
find
show
rm
mk
promote
set

How are find and show different? With traditional byte oriented streams, you needed two commands to be able to distinguish between attribute values and identifiers. Maybe we can flag columns as being identifiers? Or use a specific column name?

Naming commands
---

Verb then noun: findperson, rminvoice, mkproject

Examples
--

> bigcorp findperson -name %bob%
ID			ShortName
123123123	Bob
324242342	Tabob
a2bd35b11   Nabob

