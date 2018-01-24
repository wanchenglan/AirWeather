package com.class3.xiaok.airweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaok on 18-1-18.
 */

public class SelectCity extends Activity implements View.OnClickListener{

    //定义组件
    private ImageView backBtn,searchBtn;
    private ListView cityListlv;
    private TextView select_title;
    private SearchView searchEt;

    private List<City> mCityList;
    private MyApplication mApplication;
    private ArrayList<String>  mArrayList;
    private ArrayList<String> mSearchList;
    private ArrayAdapter<String> adapter;

    private static String updateCityCode = "-1";
    static int code;
    static String number = null;
    static String number1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

//        backBtn = (ImageView)findViewById(R.id.title_selectCity_back);
//        backBtn.setOnClickListener(this);
        searchEt = (SearchView) findViewById(R.id.selectcity_search);
        searchBtn = (ImageView)findViewById(R.id.selectcity_search_button);
        searchBtn.setOnClickListener(this);

        select_title = (TextView)findViewById(R.id.title_selectCity_name);

//        String[] listData = {"1","2","3"};
//        cityListlv = (ListView)findViewById(R.id.selectcity_lv);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,listData);
//        cityListlv.setAdapter(adapter);
        mApplication = (MyApplication)getApplication();
        mCityList = mApplication.getCityList();
        mArrayList = new ArrayList<String>();   //不new会指向空
        for(int i=0;i<mCityList.size();i++)
        {
            //仅显示:城市名称
//            String cityName = mCityList.get(i).getCity();
//            mArrayList.add(cityName);

//            显示id,citycode,省份,城市信息
            String NO_ = Integer.toString(i+1);
            number = mCityList.get(i).getNumber();
            String provinceName = mCityList.get(i).getProvince();
            String cityName = mCityList.get(i).getCity();
            mArrayList.add("NO_"+NO_+":"+number+"-"+provinceName+cityName);
        }
            cityListlv = (ListView) findViewById(R.id.selectcity_lv);
            adapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, mArrayList);
            cityListlv.setAdapter(adapter);


        //添加ListView项的点击事件的动作
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mSearchList == null){
                    updateCityCode = String.valueOf(mCityList.get(position).getNumber());
                    Log.d("Update city code",updateCityCode);
                }else{
// 好用
                    char []huan = mSearchList.toString().toCharArray();
                    int startindex = 0;
                    int endindex = 0;
                    for(int searchindex=0;searchindex<huan.length;searchindex++){
                        if(huan[searchindex]==':')
                            startindex = searchindex+1;
                            endindex = startindex + 9;
                    }
                    updateCityCode = (mSearchList.toString()).substring(startindex,endindex);
                }
                //用SharedPreferences存储最近一次的citycode
                SharedPreferences sharedPreferences = getSharedPreferences("CityCodePreference",Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("citycode",updateCityCode);
                editor.commit();

                Log.d("Update City code",updateCityCode);
                Toast.makeText(SelectCity.this,updateCityCode,Toast.LENGTH_LONG).show();
                Intent it = new Intent(SelectCity.this,MainActivity.class);
                it.putExtra("citycode",updateCityCode);
                startActivity(it);
            }       //"当前城市:"
        };
        cityListlv.setOnItemClickListener(itemClickListener);
    }

    public void onClick(View v){
        switch(v.getId()){
//            case R.id.title_selectCity_back:
////                finish();
//                Intent intent = new Intent(SelectCity.this,MainActivity.class);
//                intent.putExtra("citycode",updateCityCode);
//                startActivity(intent);
//                finish();
//                break;
            case R.id.selectcity_search_button:
                //通过输入城市代号来进行跳转查询
//                String citycode = searchEt.getText().toString();
//                Log.d("Search",citycode);
//                Intent intent = new Intent(SelectCity.this,MainActivity.class);
//                intent.putExtra("citycode",citycode);
//                startActivity(intent);

                if(mSearchList==null&&searchEt.getQuery()=="搜索城市(中文)")
                {
                    adapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, mArrayList);
                    cityListlv.setAdapter(adapter);
                }else {
                    String citycode = searchEt.getQuery().toString();
                    Log.d("Search", citycode);
                    mSearchList = new ArrayList<String>();
                    for (int i = 0; i < mArrayList.size(); i++) {
                        //显示id,citycode,省份,城市信息
                        String NO_ = Integer.toString(i + 1);
                        number1 = mCityList.get(i).getNumber();
                        String provinceName = mCityList.get(i).getProvince();
                        String cityName = mCityList.get(i).getCity();
                        if (number1.equals(citycode)||cityName.equals(citycode)) {
                            mSearchList.add("NO_" + NO_ + ":" + number1 + "-" + provinceName + cityName);
                            Log.d("change adapter data", "NO_" + NO_ + ":" + number1 + "-" + provinceName + cityName);
                        }
                        adapter = new ArrayAdapter<String>(SelectCity.this, android.R.layout.simple_list_item_1, mSearchList);
                        cityListlv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            default:
                break;
        }
    }
}
