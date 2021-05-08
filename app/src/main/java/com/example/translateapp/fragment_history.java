package com.example.translateapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_history#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_history extends Fragment {
    static ArrayList<HistoryData> data = new ArrayList<>();
    private ListView historyHolder;
    static int counter=0;
    Button delete;
    static int starter =0;
    static boolean deleter = false;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_history() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_history.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_history newInstance(String param1, String param2) {
        fragment_history fragment = new fragment_history();
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
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        historyHolder= (ListView) getView().findViewById(R.id.historyHolder);
        delete = getView().findViewById(R.id.btn_delete);
        historyHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("pref2", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("returnFirstName",data.get(position).getLangName1());
                editor.putString("returnSecondName",data.get(position).getLangName2());
                editor.putString("returnFirstInput",data.get(position).getFirstInput());
                editor.putString("returnSecondInput",data.get(position).getSecondInput());
                editor.apply();
                getParentFragmentManager().popBackStack();
            }
        });
        historyHolder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
               View popUpView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_up,null);
                TextView firstPreviewText = popUpView.findViewById(R.id.langPreview1);
                TextView secondPreviewText = popUpView.findViewById(R.id.langPreview);
                firstPreviewText.setText(data.get(position).getFirstInput().substring(0, Math.min(data.get(position).getFirstInput().length(),10)) + "...");
                secondPreviewText.setText(data.get(position).getSecondInput().substring(0, Math.min(data.get(position).getSecondInput().length(),10)) + "...");
                Dialog builder = new Dialog(getActivity());
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                builder.setContentView(popUpView);
                builder.show();

                return true;
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        starter++;
        SharedPreferences getData = getContext().getSharedPreferences("list", Context.MODE_PRIVATE);
        String serialized = getData.getString("myData", null);
        if(starter==1) {
            if (serialized != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<HistoryData>>() {
                }.getType();
                data.addAll(gson.fromJson(serialized, type));
            }
        }
        CustomAdapter adapter = new CustomAdapter(getContext(),data);
        historyHolder.setAdapter(adapter);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<HistoryData> list = data;
                data.removeAll(list);
                counter = 0;
                getData.edit().clear();
                adapter.notifyDataSetChanged();
                deleter = true;
            }
        });
        super.onResume();
    }

    @Override
    public void onDestroy() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("list",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("myData",json);
        editor.commit();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("list",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("myData",json);
        editor.commit();
        super.onPause();
    }
}