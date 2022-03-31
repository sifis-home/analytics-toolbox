package it.cnr.iit.anonymousvoice.enums;

public enum LanguageEnum {

    US_ENGLISH("en-us"),
    ITALIAN("it");

    private final String language;

    LanguageEnum(String language){
        this.language = language;
    }

    @Override
    public String toString() {
        return language;
    }
}
