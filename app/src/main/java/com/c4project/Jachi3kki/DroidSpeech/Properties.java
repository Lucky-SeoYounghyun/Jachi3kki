package com.c4project.Jachi3kki.DroidSpeech;

import java.util.ArrayList;
import java.util.List;

/**
 * Droid Speech Properties
 *
 * @author Vikram Ezhil
 */

public class Properties
{
    public List<String> supportedSpeechLanguages = new ArrayList<>();

    public String currentSpeechLanguage;

    public String listeningMsg;

    public String oneStepVerifySpeechResult;

    public long startListeningTime;

    public long pauseAndSpeakTime;

    public boolean offlineSpeechRecognition = false;

    public boolean continuousSpeechRecognition = true;

    public boolean showRecognitionProgressView = false;

    public boolean oneStepResultVerify = false;

    public boolean onReadyForSpeech = false;
    
    public boolean speechResultFound = false;
    
    public boolean closedByUser = false;
}
