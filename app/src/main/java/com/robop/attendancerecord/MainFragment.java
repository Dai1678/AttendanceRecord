package com.robop.attendancerecord;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainFragment extends Fragment implements AdapterView.OnItemClickListener {

    ArrayList<ListItemModel> listItems = new ArrayList<>();    //ListViewのAdapterに入れる情報

    ListAdapter adapter;
    ListView listView;

    Activity activity;
    int currentPageNum = 0;
    Realm realm;

    public static MainFragment newInstance( int position){
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("currentViewPage", position);
        mainFragment.setArguments(bundle);
        return mainFragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        activity = (Activity)context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(realmConfiguration);

        Bundle bundle = getArguments();
        if (bundle != null){
            currentPageNum = bundle.getInt("currentViewPage");
            Log.d("currentPage", String.valueOf(currentPageNum));
        }

        ListItemModel itemModel;

        RealmResults<ListRealmModel> result = realm.where(ListRealmModel.class).equalTo("dayOfWeekId", currentPageNum).findAll();   //曜日合わせる

        if (result.size() > 0){
            for (int i=0; i<5; i++){
                ListRealmModel realmModel = result.get(i);

                //Realm上にデータがある教科の場合
                if (realmModel != null){

                    itemModel = new ListItemModel(realmModel.getSubjectName(), realmModel.getAttendResult(), realmModel.getAbsentResult(), realmModel.getLateResult());

                } else {

                    itemModel = new ListItemModel("未設定", 0, 0, 0);
                }

                listItems.add(itemModel);

            }

            //Realm上にデータが一つも無い場合
        } else {
            for (int i=0; i<5; i++){

                itemModel = new ListItemModel("未設定", 0, 0, 0);

                listItems.add(itemModel);

                initRealmData(currentPageNum, i+1);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, final Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listView);

        adapter = new ListAdapter(activity.getApplicationContext(), R.layout.list_item, listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View dialogView = getLayoutInflater().inflate(R.layout.activity_subjectdialog, null);
        builder.setView(dialogView)
                .setPositiveButton("決定", (dialogInterface, i) -> {

                    EditText editText = dialogView.findViewById(R.id.edit_subject_name);

                    ListItemModel listItemModel = new ListItemModel();
                    listItemModel.setSubjectName(editText.getText().toString());
                    listItems.set(position, listItemModel);
                    adapter.notifyDataSetChanged();

                    //Realmに教科名書き込み
                    RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                            .deleteRealmIfMigrationNeeded()
                            .build();

                    realm = Realm.getInstance(realmConfiguration);

                    RealmResults<ListRealmModel> results = realm.where(ListRealmModel.class).equalTo("dayOfWeekId", currentPageNum).and().equalTo("classId", position + 1).findAll();
                    ListRealmModel realmModel = results.get(0);

                    realm.beginTransaction();
                    if (realmModel != null) {
                        realmModel.setSubjectName(editText.getText().toString());
                    }
                    realm.commitTransaction();

                })
                .setNegativeButton("キャンセル", (dialogInterface, i) -> {

                })

                .show();

    }

    private void initRealmData(int currentPage, int currentClassNum) {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();

        realm = Realm.getInstance(realmConfiguration);

        ListRealmModel realmModel = new ListRealmModel();
        realmModel.setSubjectName("未設定");
        realmModel.setAttendResult(0);
        realmModel.setAbsentResult(0);
        realmModel.setLateResult(0);
        realmModel.setClassId(currentClassNum);
        realmModel.setDayOfWeekId(currentPage);

        realm.beginTransaction();
        realm.copyToRealm(realmModel);
        realm.commitTransaction();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        realm.close();
    }

}
