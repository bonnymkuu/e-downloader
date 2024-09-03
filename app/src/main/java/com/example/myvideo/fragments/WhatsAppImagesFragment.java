package com.example.myvideo.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myvideo.R;
import com.example.myvideo.StatusModel;
import com.example.myvideo.adapters.WhatsAppImageAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class WhatsAppImagesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private ArrayList<StatusModel> arrayList;
    private RecyclerView recyclerView;
    private TextView textView;
    private SwipeRefreshLayout swipeRefreshLayout;

    public WhatsAppImagesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_whats_app_images, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        textView = view.findViewById(R.id.emptyText);
        swipeRefreshLayout = view.findViewById(R.id.contentView);
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
        recyclerView.setHasFixedSize(true);
        loadData();

        return view;
    }

    private void loadData(){
        swipeRefreshLayout.setRefreshing(true);
        if (arrayList == null){
            arrayList = new ArrayList<>();
        } else {
            arrayList.clear();
        }

        String path;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Android 11+
            path = "/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/.Statuses/";
        } else { // Below Android 11
            path = "/storage/emulated/0/WhatsApp/Media/";
        }
        File directory = new File(path);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            //Arrays.sort(files,(f1,f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            String[] paths = {""};
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        for (int i = 0; i < files.length; i++) {
                            if (files[i].getName().endsWith(".jpg") || files[i].getName().endsWith("gif")){
                                paths[0] = path + "" + files[i].getName();
                                StatusModel statusModel = new StatusModel(paths[0],
                                        files[i].getName().substring(0, files[i].getName().length() - 4),
                                        0);
                                arrayList.add(statusModel);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                    swipeRefreshLayout.setRefreshing(false);
                    if (arrayList.isEmpty()) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("No Status Available \n Check Out some Status & Come Back");
                    }
                    WhatsAppImageAdapter adapter = new WhatsAppImageAdapter(requireContext(), arrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    LinearLayoutManager linearLayoutManager = new GridLayoutManager(requireContext(), 2);
                    recyclerView.setLayoutManager(linearLayoutManager);
                }
            }.execute();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            textView.setVisibility(View.VISIBLE);
            textView.setText("No Status Available \n Check Out some Status & Come Back");
            Toast.makeText(requireContext(), "WhatsApp Not Installed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRefresh() {
        loadData();
    }
}