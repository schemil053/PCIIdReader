# PCI ID Reader

## Setup

Download pci.ids here: [https://admin.pci-ids.ucw.cz/](https://admin.pci-ids.ucw.cz/) and put it into the src/main/resources

After that, you can launch Main.java with IntelliJ. You can also copy the Main.java-File outside the Project and run it with `java Main.java`, but you have to put the pci.ids in the same folder as the Main.java file!

Another way is to build the jar-file with maven.

## Usage
The programm asks for a radix. If you want to input hex numbers, you have to input 16. If you want decimal numbers, you have to input 10.
Then it asks for a vendor and device id. You enter it, and it will search the database for you.
After that, you will see The vendor and device name.