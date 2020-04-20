Title of manuscript: Visualization of  evolutionary relationships through geologic time in Timescale Creator

Author details:
1. Abdullah Khan Zehady 
	Department of Earth, Atmospheric and Planetary Science, Purdue University, West Lafayette, IN 47907 USA
2. James G. Oggb
	State Key Laboratory of Oil and Gas Reservoir Geology and Exploitation, Chengdu University of Technology, Chengdu, Sichuan 610059, China
3. Barry G. Fordhamc
	Research School of Earth Sciences, Australian National University, Canberra, ACT 2601, Australia
4. Gangi Palemd
	Google Inc., 1600 Amphitheatre Parkway, Mountain View, CA 94043
5. Jason Bobicke
	Department of Computer and Information Technology, Purdue University, West Lafayette, IN 47907 USA
6. Gabi Ogg
	Geologic TimeScale Foundation, 1224 N. Salisbury St., West Lafayette, IN 47906

Email addresses: azehady@purdue.edu (A. K. Zehady); jogg@purdue.edu (J. G. Ogg, corresponding author); barry.fordham@anu.edu.au (B. G. Fordham); palemgangireddy@gmail.com (G. Palem); jbobick@purdue.edu (J. Bobick); gabiogg@hotmail.com (G. Ogg)

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

This is the code repository for TSCreator evolutionary tree visualization.
All the datapacks are provided from the Datapack tab from our website(http://timescalecreator.org).

Provided source code includes three files: 
	(a) RangeColumn.java 
	(b) ImageGenerator.java and 
	(c) TSCreator.java. 
        (d) EvTree.java

   RangeColumn Java file includes the detailed data structures for evolutionary range points, ranges and tree drawing utility functions. ImageGenerator Java file is responsible for drawing evolutionary tree data column and generating the final chart. TSCreator Java file includes the main function which is the entry point to our software and spawns java process on Java virtual machine on an operating system. Currently our compiled Jar (Java archive file) and Exe files run on all platforms such as Windows, Mac OS and Linux machines. We are providing the most recent Jar which can be used to generate all the figures used in this paper after loading the evolutionary tree datapacks mentioned in the “Data availability” section. These datapacks will also be provided from our website (www.timescalecreator.org). Our website provides other datapacks which can be loaded simultaneously with the integrated tree datapacks and we also provide detailed manuals and tutorials for exploration and data visualization there. EvTree java file includes all the algorithm related to evolutionary path generation and tree exporting and importing features. 

Instruction File: Instructions_for_TSCreator_making_evolutionary_charts.docx provides the instruction to install the program, generate tree charts and explore various tree features. To generate output charts, you can use the datapacks inside the "TestData" directory. See below.

TestData directory includes datapacks mentioned as additional files in the manuscript and also downloadable from our website.

Latest compiled executable jar (Compatible for all operating systems): TSCreatorBASE-7.4_latest_evtree.jar

