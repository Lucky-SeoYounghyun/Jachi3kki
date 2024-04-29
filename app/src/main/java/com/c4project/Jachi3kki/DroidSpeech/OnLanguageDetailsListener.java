package com.c4project.Jachi3kki.DroidSpeech;

import java.util.List;

/**
 * Droid Speech Language Details Listener
 *
 * @author Vikram Ezhil
 */

public interface OnLanguageDetailsListener
{
    /**
     * Sends an update with the device language details
     *
     * @param defaultLanguage The default language
     *
     * @param otherLanguages The other supported languages
     */
    public void onLanguageDetailsInfo(String defaultLanguage, List<String> otherLanguages);
}
