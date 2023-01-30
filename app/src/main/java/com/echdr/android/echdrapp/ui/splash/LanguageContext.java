package com.echdr.android.echdrapp.ui.splash;

public class LanguageContext {
    private String language;
    private static LanguageContext languageContext;

    private LanguageContext(){
        this.language = "en";
    }

    public String getLanguage() {
        return language;
    }

    public static LanguageContext getLanguageContext() {
        if(languageContext == null) {
            languageContext = new LanguageContext();
        }
        return languageContext;
    }

    public void setLanguageEnglish() {
        this.language = "en";
    }

    public void setLanguageTamil() {
        this.language = "ta";
    }

    public void setLanguageSinhala() {
        this.language = "si";
    }

}
