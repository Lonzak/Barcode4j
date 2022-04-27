# Barcode4J

This is an update to the known Barcode4j library. Barcode4J is a flexible generator for barcodes written in Java.

Original Website: http://barcode4j.sourceforge.net

For instructions on how to get started please read the documentation on the website or the one included in the binary distribution.
If you need any assistance please post questions on the following mailing list: barcode4j-users@lists.sourceforge.net
You can subscribe here: http://sourceforge.net/mail/?group_id=96670

We would like to invite you to participate in the development of Barcode4J: The development mailing list is barcode4j-developers@lists.sourceforge.net

Barcode4J is licensed under the the Apache License, Version 2.0. The license text can be found in the project.

# Changes

So what are the changes?

1. Imported the original code 1:1 from the SVN trunk @ sourceforge
2. First changes:
- moved to maven structure and build system (now it is a maven multi-module project)
- updated libraries
- moved to junit4
- removed some java warnings (annotations missing / Java5 syntax)
- removed old saxon project (use saxon8 instead)
- moved documentation to documentation folder (decide later what to do with HP and stuff)
- added license plugin
