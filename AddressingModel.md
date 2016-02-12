Addressing model
---
Goal: Make it easy to identify a group of cells from a tabular output using a simple DSL that can be used from many different contexts. 

Use cases
----
- Select the full output (all columns and call rows)


Regions
--

Row/Column Range
 - Contiguous
 - Can be a single Row/Column
 - Can be all (need to have some sort of wildcard)

Row/Column Selection
 - An ordered list of Row/Column Range

TabularOutputRegion
 - Consists of a ColumnRegion and a RowRegion
 - Evaluated row by row, then each column
 

Operation principles:
--
- Build upon the success of Spreadsheets syntax
? Single delimiter (the dot? The slash?)
- Rows are always numbered (zero based)
- Column headers are never part of the output content (the header does not starts at row 0)
- Columns names never start with a number
- If no output has been specified, the last output is used
- If rows are not specified, all rows are included
- : specifies an inclusive range of addresses (from:to)
- , specifies a list of addresses
- Should regions have multiple columns/rows?
- Can specify the last row with ^
- Relative ordering of rows with ^6
? Row numbering can be negative
- Column names are case insensitive
- Way to specify columns by relative ordering?
- # indicates the 'current' row number, can be followed by relative counter


Syntax
---

`[<process>.][<ouput>.]<Column>[.<row>]` 

REF:REF

Selection:
--
If the expression starts with a +, then start with the empty set
If the expression starts with a -, then start with the full set

+A+C:E-D	==> A,C,E
,A,C:E-D

+10:20-15   ==> 10:14+16:20

+A+C:E-D+10:20-15


Row ranges:
---
A.0:9			//10 first rows
A				//Same as A or A.

Row arithmetics:
---
A.#-1			//Row before the current one
A.#+1 			//Row after the curent one
?				//What should be the value when the boundary is crossed? Empty string?

Examples
---
123.A.0		//Output 123, cell A0
123.A:C		//Output 123, column A thru C
name		//Last output has a region called name
name,size	//Selects name and size regions from last output
name.1:4	//Selects row 1 to 4 from region name
C$			//Select the last row of column C
d$-1		//Previous to last cell in column D
A0			//First cell of column A
A0,B0		//First cell of column A and B


More examples
---
ls | cut A0,B0 | pivot | sum		//Sums the first row of column A and B
ls | cut size | sum					//Sums whole column with alias size
ls | sum size						//Sum 



tutils
--
tcut			: Filters-in selected cell addresses
tgrep 		: Filters-in cells based upon cell content
thead		: Manipulates the header of each column (rename,show/hide)
tpiv		: Transforms columns into rows
tawk		: awk like transformation tool, works on rows
tjoin		: Joins columns with printf-like statements
tpaste		: Join columns from different sources (can tjoin do that?)


Utils examples
---
`x | cut name size`
`x | tjoin "_{A}{B}_\\{" %E {name} {size}`
`x | tjoin "_{A}{A.0}{A.#-1}_" {E} {name} {size}`


tjoin
---
tjoin [args...]

Each argument becomes an output column. Each argument is evaluated according to 


