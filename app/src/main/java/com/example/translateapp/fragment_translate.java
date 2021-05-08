package com.example.translateapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_translate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_translate extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextView setTranslatedText;
    EditText textInput;
    TextView langName1;
    TextView langName2;

    public fragment_translate() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_translate.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_translate newInstance(String param1, String param2) {
        fragment_translate fragment = new fragment_translate();
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
        return inflater.inflate(R.layout.fragment_translate, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Spinner lang1 =(Spinner) getView().findViewById(R.id.lang1);
        Spinner lang2 =(Spinner) getView().findViewById(R.id.lang2);
        langName1 = (TextView) getView().findViewById(R.id.langName1);
        langName2 = (TextView) getView().findViewById(R.id.langName2);
        Button translateBtn = (Button) getView().findViewById(R.id.translateBtn);
        textInput = (EditText) getView().findViewById(R.id.textInput);
        setTranslatedText = (TextView) getView().findViewById(R.id.translatedText);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item,getResources().getStringArray(R.array.languages));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang1.setAdapter(myAdapter);
        lang2.setAdapter(myAdapter);
        super.onViewCreated(view, savedInstanceState);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textInput.getText().toString().isEmpty()) {
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    TranslatorOptions source = new TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.fromLanguageTag(getLanguageTag(langName1.getText().toString()))).setTargetLanguage(TranslateLanguage.fromLanguageTag(getLanguageTag(langName2.getText().toString()))).build();
                    final Translator translator = Translation.getClient(source);
                    DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();
                    fragment_history.deleter = false;
                    translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Translator model", "Successful");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Translator model", e.getMessage());
                            setTranslatedText.setText("Failed getting model");
                        }
                    });
                    translator.translate(textInput.getText().toString()).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            setTranslatedText.setText(s);
                            fragment_history.counter++;
                            fragment_history.data.add(new HistoryData(lang1.getSelectedItem().toString(), lang2.getSelectedItem().toString(), textInput.getText().toString(), setTranslatedText.getText().toString(), fragment_history.counter));
                            editor.putString("firstLanguage", lang1.getSelectedItem().toString());
                            editor.putString("secondLanguage", lang2.getSelectedItem().toString());
                            editor.putString("firstInput", textInput.getText().toString());
                            editor.putString("secondInput", setTranslatedText.getText().toString());
                            editor.apply();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            setTranslatedText.setText("Getting translation model please wait...");
                        }
                    });

                }
                else {
                    Toast.makeText(getContext(),"Field is empty",Toast.LENGTH_LONG).show();
                }
            }
        });
        lang1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                langName1.setText(lang1.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        lang2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                langName2.setText(lang2.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    public static String getLanguageTag(String langName){
        Map<String,String> languageHolder = new HashMap<>();
        languageHolder.put("Arabic","ar");
        languageHolder.put("Bulgarian","bg");
        languageHolder.put("Czech","cs");
        languageHolder.put("German","de");
        languageHolder.put("Greek","el");
        languageHolder.put("English","en");
        languageHolder.put("Spanish","es");
        languageHolder.put("Finish","fi");
        languageHolder.put("French","fr");
        languageHolder.put("Irish","ga");
        languageHolder.put("Hindi","hi");
        languageHolder.put("Japanese","ja");
        languageHolder.put("Korean","ko");
        languageHolder.put("Romanian","ro");
        languageHolder.put("Russian","ru");
        languageHolder.put("Chinese","zh");
        languageHolder.put("Turkish","tr");
        for(Map.Entry<String,String> entry:languageHolder.entrySet()){
            if(entry.getKey().equals(langName)){
                return entry.getValue();
            }
        }
        return null;
    }
   @Override
    public void onResume() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref2",Context.MODE_PRIVATE);
        Spinner lang1 =(Spinner) getView().findViewById(R.id.lang1);
        Spinner lang2 =(Spinner) getView().findViewById(R.id.lang2);
        lang1.setSelection(getIndex(lang1,sharedPreferences.getString("returnFirstName","")));
        lang2.setSelection(getIndex(lang2,sharedPreferences.getString("returnSecondName","")));
        textInput.setText(sharedPreferences.getString("returnFirstInput",""));
        setTranslatedText.setText(sharedPreferences.getString("returnSecondInput",""));
        super.onResume();
    }
    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

}