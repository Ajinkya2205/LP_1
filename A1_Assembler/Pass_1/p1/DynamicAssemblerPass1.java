import java.io.*;
import java.util.*;

class DynamicAssemblerPass1 {
    static Map<String, Integer> symbolTable = new HashMap<>();
    static List<Literal> literalTable = new ArrayList<>();
    static List<Integer> poolTable = new ArrayList<>();

    static int locationCounter = 0;
    static int literalIndex = 0;
    static int poolIndex = 0;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide the assembly file path as an argument.");
            return;
        }

        String filePath = args[0];
        List<String> assemblyCode = readAssemblyFile(filePath);

        if (assemblyCode == null) {
            System.out.println("Error reading the assembly file.");
            return;
        }

        processAssembly(assemblyCode);
        printTables();
    }

    // Read assembly code from file
    private static List<String> readAssemblyFile(String filePath) {
        List<String> assemblyCode = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                assemblyCode.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return assemblyCode;
    }

    // Process each line of assembly code
    private static void processAssembly(List<String> assemblyCode) {
        for (String line : assemblyCode) {
            String[] parts = line.trim().split("\\s+");
            String label = parts.length > 2 ? parts[0] : "";
            String instruction = parts.length > 1 ? parts[parts.length - 2] : parts[0];
            String operand = parts[parts.length - 1];

            // Handle START directive
            if (instruction.equals("START")) {
                locationCounter = Integer.parseInt(operand);
                System.out.println("(AD,01)(C," + locationCounter + ")");
                continue;
            }

            // Handle symbols (labels)
            if (!label.isEmpty()) {
                symbolTable.put(label, locationCounter);
            }

            // Process declarative statements (DL)
            if (instruction.equals("DS")) {
                int size = Integer.parseInt(operand);
                System.out.println(locationCounter + "\t(DL,02)(C," + size + ")");
                locationCounter += size;
            } else if (instruction.equals("DC")) {
                int constant = Integer.parseInt(operand.replace("'", ""));
                System.out.println(locationCounter + "\t(DL,01)(C," + constant + ")");
                locationCounter++;
            }

            // Handle imperative statements (IS)
            else if (instruction.equals("MOVER") || instruction.equals("ADD") || instruction.equals("MOVEM") || instruction.equals("MULT") || instruction.equals("PRINT") || instruction.equals("STOP")) {
                int opcode = getOpcode(instruction);
                String registerCode = getRegisterCode(parts[parts.length - 2]);
                String address = resolveOperand(operand);
                System.out.println(locationCounter + "\t(IS," + opcode + ")" + "(" + registerCode + ")" + address);
                locationCounter++;
            }

            // Handle EQU directive
            else if (instruction.equals("EQU")) {
                String[] equParts = operand.split("\\+");
                int baseAddress = symbolTable.getOrDefault(equParts[0], 0);
                int offset = Integer.parseInt(equParts[1]);
                symbolTable.put(label, baseAddress + offset);
                System.out.println("(AD,03)(" + equParts[0] + ")" + "+" + offset);
            }

            // Handle ORIGIN directive
            else if (instruction.equals("ORIGIN")) {
                String[] originParts = operand.split("\\+");
                int baseAddress = symbolTable.getOrDefault(originParts[0], 0);
                int offset = Integer.parseInt(originParts[1]);
                locationCounter = baseAddress + offset;
                System.out.println("(AD,03)(" + originParts[0] + ")" + "+" + offset);
            }

            // Handle LTORG directive
            else if (instruction.equals("LTORG") || instruction.equals("END")) {
                for (int i = poolIndex; i < literalTable.size(); i++) {
                    Literal lit = literalTable.get(i);
                    lit.address = locationCounter++;
                    System.out.println(locationCounter + "\t(DL,01)(C," + lit.value + ")");
                }
                poolIndex = literalTable.size();
                if (instruction.equals("END")) {
                    System.out.println("(AD,02)");
                }
            }
        }
    }

    // Get opcode for given instruction
    private static int getOpcode(String instruction) {
        Map<String, Integer> opcodes = Map.of(
            "STOP", 0,
            "ADD", 1,
            "SUB", 2,
            "MULT", 3,
            "MOVER", 4,
            "MOVEM", 5,
            "COMP", 6,
            "BC", 7,
            "DIV", 8,
            "READ", 9,
            "PRINT", 10
        );
        return opcodes.getOrDefault(instruction, -1);
    }

    // Get register code
    private static String getRegisterCode(String register) {
        Map<String, String> registers = Map.of(
            "AREG", "1",
            "BREG", "2",
            "CREG", "3",
            "DREG", "4"
        );
        return registers.getOrDefault(register, "0");
    }

    // Resolve operand, handling literals and symbols
    private static String resolveOperand(String operand) {
        if (operand.startsWith("=")) {
            String literalValue = operand.replace("=", "").replace("'", "");
            literalTable.add(new Literal(literalValue));
            return "(L," + (literalTable.size() - 1) + ")";
        } else if (symbolTable.containsKey(operand)) {
            return "(S," + operand + ")";
        }
        return "(C," + operand + ")";
    }

    // Print symbol and literal tables
    private static void printTables() {
        System.out.println("\nSymbol Table:");
        for (var entry : symbolTable.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }

        System.out.println("\nLiteral Table:");
        for (int i = 0; i < literalTable.size(); i++) {
            System.out.println("L" + i + " = " + literalTable.get(i).value + " at " + literalTable.get(i).address);
        }
    }

    // Class for literals
    static class Literal {
        String value;
        int address;

        Literal(String value) {
            this.value = value;
            this.address = -1;
        }
    }
}
