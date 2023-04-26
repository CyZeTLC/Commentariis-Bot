package de.cyzetlc.securitas;

public class Securitas {
    private Securitas(boolean debugMode) {

    }

    public static Securitas createNewDebugInstance() {
        return new Securitas(true);
    }

    public static Securitas createNewInstance() {
        return new Securitas(false);
    }
}
