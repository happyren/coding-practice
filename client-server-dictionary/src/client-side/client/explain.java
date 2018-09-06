package main.java.client;

public class explain {
    private String def;
    private String sample;
    private String part;

    public explain(String def, String sample, String part) {
        this.def = def;
        this.sample = sample;
        this.part = part;
    }

    @Override
    public String toString() {
        String result = "Definition: " + def
                + "\nSample Usage: " + sample
                + "\nSpeech Part: " + part;
        return result;
    }
}