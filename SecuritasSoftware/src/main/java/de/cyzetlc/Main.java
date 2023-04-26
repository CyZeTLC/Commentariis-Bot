package de.cyzetlc;

import de.cyzetlc.securitas.Securitas;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("--debug")) {
                Securitas.createNewDebugInstance();
            }
        } else {
            Securitas.createNewInstance();
        }
    }
}
