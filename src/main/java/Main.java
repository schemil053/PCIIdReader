import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Throwable {
        System.out.println("Reading DB, please wait...");
        InputStream is = Main.class.getResourceAsStream("/pci.ids");

        if (is == null) {
            if (new File("pci.ids").isFile()) {
                is = new FileInputStream("pci.ids");
            } else {
                System.out.println("Check pci.ids file.");
                return;
            }
        }

        Scanner scanner = new Scanner(is);

        Map<Integer, String> vendorNameMap = new HashMap<>();
        Map<Integer, Map<Integer, String>> vendorDevicesMap = new HashMap<>();

        String current = null;
        int currentID = 0;

        int iter = 0;

        while (scanner.hasNextLine()) {
            iter++;
            String line = scanner.nextLine();
            try {
                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("C ")) {
                    System.out.println("Exit on line " + iter + " because " + line);
                    break;
                }
                if (!line.startsWith("\t")) {
                    if (line.split(" {2}", 2).length == 2) {
                        current = line.split(" {2}", 2)[1];
                        currentID = Integer.parseInt(line.split(" {2}", 2)[0], 16);
                        vendorNameMap.put(currentID, current);
                        if (!vendorDevicesMap.containsKey(currentID)) {
                            vendorDevicesMap.put(currentID, new HashMap<>());
                        }
                    }
                    continue;
                }
                if (!line.startsWith("\t\t") && line.startsWith("\t")) {
                    String tabRem = line.replaceFirst("\t", "");
                    String[] sp = tabRem.split(" {2}", 2);
                    if (sp.length == 2) {
                        if (!vendorDevicesMap.containsKey(currentID)) {
                            vendorDevicesMap.put(currentID, new HashMap<>());
                        }
                        vendorDevicesMap.get(currentID).put(Integer.parseInt(sp[0], 16), sp[1]);
                    }
                }
            } catch (Throwable throwable) {
                System.err.println("Err on line: " + iter + " Content: " + line);
                throwable.printStackTrace();
            }
        }

        scanner.close();

        System.out.println("Success! Search for an ID!");

        while (true) {
            scanner = new Scanner(System.in);
            System.out.println("Enter RADIX (10 = DECIMAL, 16 = HEXADECIMAL, OR -1 to generate all into a c-style like array file.)");
            int r = Integer.parseInt(scanner.nextLine());

            if (r == -1) {
                File file = new File("generated.txt");
                file.delete();
                FileWriter writer = new FileWriter(file);
                writer.write("/*\n" +
                        "typedef struct {\n" +
                        "    uint16_t vendor_id;\n" +
                        "    uint16_t device_id;\n" +
                        "    char vendor[256];\n" +
                        "    char desc[256];\n" +
                        "} pci_device_desc_t;\n*/\n");
                writer.write("static pci_device_desc_t device_table[] =\n" +
                        "{");

                for (Map.Entry<Integer, String> integerStringEntry : vendorNameMap.entrySet()) {
                    int venID = integerStringEntry.getKey();
                    String vendorName = integerStringEntry.getValue();

                    writer.write("\n");
                    writer.write("    /* " + vendorName + " */\n");
                    for (Map.Entry<Integer, String> stringEntry : vendorDevicesMap.getOrDefault(venID, new HashMap<>()).entrySet()) {
                        int devID = stringEntry.getKey();
                        String devName = stringEntry.getValue();

                        writer.write("    ");
                        writer.write("{0x"+Integer.toHexString(venID)+", 0x"+Integer.toHexString(devID)+", \""+vendorName+"\", \""+devName+"\"},");
                        writer.write("\n");
                    }
                }

                writer.write("\n};");

                writer.close();
                continue;
            }

            System.out.println("Vendor ID");
            int vendor = Integer.parseInt(scanner.nextLine(), r);

            System.out.println("DEV ID");
            int dev = Integer.parseInt(scanner.nextLine(), r);

            System.out.println("Vendor: " + vendorNameMap.getOrDefault(vendor, "Not found"));
            System.out.println("Device: " + vendorDevicesMap.getOrDefault(vendor, new HashMap<>()).getOrDefault(dev, "Not found"));
        }
    }
}
