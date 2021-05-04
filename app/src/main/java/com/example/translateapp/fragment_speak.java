package com.example.translateapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_speak#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_speak extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RECOGNIZE_RESULT = 1;
    TextToSpeech mTTS;
    private String getLang;
    private String getLangForTranslate;
    Spinner speakLang1;
    Spinner speakLang2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_speak() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_speak.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_speak newInstance(String param1, String param2) {
        fragment_speak fragment = new fragment_speak();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speak, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button speakBtn = getView().findViewById(R.id.speakBtn);
        speakLang1 = getView().findViewById(R.id.speakLang1);
        speakLang2 = getView().findViewById(R.id.speakLang2);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item,getResources().getStringArray(R.array.languages));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> speakAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item,getResources().getStringArray(R.array.languagesSpeak));
        speakAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speakLang1.setAdapter(myAdapter);
        speakLang2.setAdapter(speakAdapter);

        speakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent speechToText = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                speechToText.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,fragment_translate.getLanguageTag(getLang));
                speechToText.putExtra(RecognizerIntent.EXTRA_LANGUAGE,fragment_translate.getLanguageTag(getLang));
                speechToText.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, fragment_translate.getLanguageTag(getLang));
                speechToText.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,fragment_translate.getLanguageTag(getLang));
                startActivityForResult(speechToText,RECOGNIZE_RESULT);
                fragment_history.deleter = false;
            }
        });
        super.onViewCreated(view, savedInstanceState);

        speakLang1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLang = speakLang1.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        speakLang2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getLangForTranslate = speakLang2.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==RECOGNIZE_RESULT&&resultCode== -1) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mTTS = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status== TextToSpeech.SUCCESS){
                        Locale loc = new Locale(fragment_translate.getLanguageTag(getLangForTranslate));
                       int result = mTTS.setLanguage(loc);


                               if(result==TextToSpeech.LANG_MISSING_DATA || result==TextToSpeech.LANG_NOT_SUPPORTED){
                                   mTTS.setLanguage(Locale.ENGLISH);
                                   Log.d("TTS","Language not supported setting locale to English");

                               }
                                   TranslatorOptions source = new TranslatorOptions.Builder().setTargetLanguage(TranslateLanguage.fromLanguageTag(fragment_translate.getLanguageTag(getLangForTranslate))).setSourceLanguage(TranslateLanguage.fromLanguageTag(fragment_translate.getLanguageTag(getLang))).build();
                                   final Translator translator = Translation.getClient(source);
                                   DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();
                                   translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           Log.d("Translator model","Successful");
                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Log.d("Translator model",e.getMessage());
                                       }
                                   });
                                   translator.translate(matches.get(0)).addOnSuccessListener(new OnSuccessListener<String>() {
                                       @Override
                                       public void onSuccess(String s) {
                                           mTTS.speak(s,TextToSpeech.QUEUE_FLUSH,null);
                                           SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
                                           SharedPreferences.Editor editor = sharedPreferences.edit();
                                           editor.putString("firstLanguage",speakLang1.getSelectedItem().toString());
                                           editor.putString("secondLanguage",speakLang2.getSelectedItem().toString());
                                           editor.putString("firstInput",matches.get(0));
                                           editor.putString("secondInput",s);
                                           editor.apply();
                                           fragment_history.counter++;
                                           fragment_history.data.add(new HistoryData(speakLang1.getSelectedItem().toString(),speakLang2.getSelectedItem().toString(),matches.get(0),s,fragment_history.counter));
                                           Log.d("testLang", s);
                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           mTTS.speak("Getting language model please wait a few seconds and try again ",TextToSpeech.QUEUE_FLUSH,null);
                                       }
                                   });
                    }
                }
            });
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if(mTTS!=null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }
}