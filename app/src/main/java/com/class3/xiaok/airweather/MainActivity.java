package com.class3.xiaok.airweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity implements View.OnClickListener{

    private String updateCityCode;
    TodayWeather todayWeather = null;

    //title
    private ImageView UpdateBtn,SelectCityBtn,LocateBtn;

    //todayWeather
    private TextView cityT,timeT,weekT,humidityT,pmDataT,pmQualityT,temperatureT,
                    climateT,windT,cityNameT,detail;
    private ImageView weatherStateImg,pmStateImg,PM25Img;

    //future
    private TextView week1T,week2T,week3T,temperature1T,temperature2T,temperature3T,
                    wind1T,wind2T,wind3T,climate1T,climate2T,climate3T;

    private Handler mHandler = new Handler()
    {
      public void handleMessage(android.os.Message message)
      {
          switch(message.what)
          {
              case 1:
                  updateTodayWeather((TodayWeather)message.obj);
                  break;
              default:
                  break;
          }
      }
    };

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //获取资源文件
        UpdateBtn = (ImageView)findViewById(R.id.title_city_update);
        UpdateBtn.setOnClickListener(this);
        SelectCityBtn = (ImageView)findViewById(R.id.title_city_manager);
        SelectCityBtn.setOnClickListener(this);
        LocateBtn = (ImageView)findViewById(R.id.title_city_locate);
        LocateBtn.setOnClickListener(this);
        initView();
        updateCityCode = getIntent().getStringExtra("citycode");
        if(updateCityCode!="-1")
        {
//            Toast.makeText(MainActivity.this, updateCityCode, Toast.LENGTH_SHORT).show();
            getWeatherDatafromNet(updateCityCode);
        }
//        Toast.makeText(MainActivity.this, updateCityCode, Toast.LENGTH_LONG).show();
        //检查网络连接状态
        if(CheckNet.getNetState(this)==CheckNet.NET_NONE) {
            Log.d("AirWeaher", "网络不通！");
            Toast.makeText(MainActivity.this, "网络不通!", Toast.LENGTH_SHORT).show();
        }else{
            Log.d("AirWeaher", "网络OK！");
            Toast.makeText(MainActivity.this, "网络OK!", Toast.LENGTH_SHORT).show();
//            getWeatherDatafromNet("101010100");     //101010100为北京的区域代码
        }
    }


    //初始化
    void initView()
    {
        //title
        cityNameT = (TextView)findViewById(R.id.city_name);

        //today weather
        cityT = (TextView)findViewById(R.id.todayinfo1_cityName);
        timeT = (TextView)findViewById(R.id.todayinfo1_updateTime);
        humidityT = (TextView)findViewById(R.id.todayinfo1_humidity);
        weekT = (TextView)findViewById(R.id.todayinfo2_week);
        pmDataT = (TextView)findViewById(R.id.todayinfo1_pm25);
        pmQualityT = (TextView)findViewById(R.id.todayinfo1_pm25status);
        temperatureT = (TextView)findViewById(R.id.todayinfo2_temperature);
        climateT = (TextView)findViewById(R.id.todayinfo2_weatherState);
        windT = (TextView)findViewById(R.id.todayinfo2_wind);
        detail = (TextView)findViewById(R.id.detail);

        //future
        week1T = (TextView)findViewById(R.id.futureinfo1_week);
        temperature1T = (TextView)findViewById(R.id.futureinfo1_temperature);
        climate1T = (TextView)findViewById(R.id.futureinfo1_weatherState);
        wind1T = (TextView)findViewById(R.id.futureinfo1_wind);

        week2T = (TextView)findViewById(R.id.futureinfo2_week);
        temperature2T = (TextView)findViewById(R.id.futureinfo2_temperature);
        climate2T = (TextView)findViewById(R.id.futureinfo2_weatherState);
        wind2T = (TextView)findViewById(R.id.futureinfo2_wind);

        week3T = (TextView)findViewById(R.id.futureinfo3_week);
        temperature3T = (TextView)findViewById(R.id.futureinfo3_temperature);
        climate3T = (TextView)findViewById(R.id.futureinfo3_weatherState);
        wind3T = (TextView)findViewById(R.id.futureinfo3_wind);


        weatherStateImg = (ImageView)findViewById(R.id.todayinfo2_weatherStatusImg);
//        pmStateImg = (ImageView)findViewById(R.id.todayinfo1_pm25img);
        PM25Img = (ImageView)findViewById(R.id.todayinfo1_pm25img);
        cityNameT.setText("N/A");

        cityT.setText("N/A");
        timeT.setText("N/A");
        humidityT.setText("N/A");
        weekT.setText("N/A");
        pmDataT.setText("N/A");
        pmQualityT.setText("N/A");
        temperatureT.setText("N/A");
        climateT.setText("N/A");
        windT.setText("N/A");
    }


    @Override
    public void onClick(View v){
        if(v.getId() == R.id.title_city_update)
        {
            SharedPreferences mySharePrev = getSharedPreferences("CityCodePreference", Activity.MODE_PRIVATE);
            String sharecode = mySharePrev.getString("citycode","");
            if(!sharecode.equals(""))
            {
                Log.d("sharecode",sharecode);
                getWeatherDatafromNet(sharecode);
            }else{
                getWeatherDatafromNet("101280101");
            }
//            getWeatherDatafromNet("101011100");
        }
        if(v.getId()==R.id.title_city_manager)
        {
            Intent intent = new Intent(this,SelectCity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.title_city_locate)
        {
            Toast.makeText(MainActivity.this, "我要定位!", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(this,Locate.class);
            startActivity(intent1);
        }
    }

    //借助HttpUrlConnection(java.net.HttpUrlConnection),获取Url网页的数据
    private void getWeatherDatafromNet(String cityCode)
    {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("Address:",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(address);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(8000);
                    urlConnection.setReadTimeout(8000);
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer sb = new StringBuffer();
                    String str;
                    while((str=reader.readLine())!=null)
                    {
                        sb.append(str);
                        Log.d("date from url",str);
                    }
                    String response = sb.toString();
                    Log.d("response",response);
//                    parseXML(response);
                    todayWeather = parseXML(response);
                    if(todayWeather!=null){
//                        Log.d("todayWeatherDate",todayWeather.toString());
                        Message message = new Message();
                        message.what = 1;
                        message.obj = todayWeather;
                        mHandler.sendMessage(message);
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //根据标签值，获取标签数据
    private TodayWeather parseXML(String xmlData)
    {
        TodayWeather todayWeather = null;

        int fengliCount = 0;
        int fengxiangCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        int detailcount = 0;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));

            int eventType = xmlPullParser.getEventType();
            Log.d("MWeater","start parse xml");

            while(eventType!=xmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                    //文档开始位置
                    case XmlPullParser.START_DOCUMENT:
                        Log.d("parse","start doc");
                        break;
                    //标签元素开始位置
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp"))
                        {
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather!=null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                Log.d("city", xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                Log.d("updatetime", xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                Log.d("wendu", xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("fengli", xmlPullParser.getText());
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                Log.d("shidu", xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("fengxiang", xmlPullParser.getText());
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                Log.d("pm25", xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                Log.d("quelity", xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("date", xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("high", xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("low", xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("type", xmlPullParser.getText());
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;

                            } else if (xmlPullParser.getName().equals("detail") && detailcount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("detail", xmlPullParser.getText());
                                todayWeather.setDetail(xmlPullParser.getText());
                                detailcount++;


                            } else if (xmlPullParser.getName().equals("date") && dateCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("date", xmlPullParser.getText());
                                todayWeather.setDate1(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("high", xmlPullParser.getText());
                                todayWeather.setHigh1(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("low", xmlPullParser.getText());
                                todayWeather.setLow1(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("type", xmlPullParser.getText());
                                todayWeather.setType1(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 1) {
                                eventType = xmlPullParser.next();
                                Log.d("fengli", xmlPullParser.getText());
                                todayWeather.setFengli1(xmlPullParser.getText());
                                fengliCount++;

                            } else if (xmlPullParser.getName().equals("date") && dateCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("date", xmlPullParser.getText());
                                todayWeather.setDate2(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("high", xmlPullParser.getText());
                                todayWeather.setHigh2(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("low", xmlPullParser.getText());
                                todayWeather.setLow2(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("type", xmlPullParser.getText());
                                todayWeather.setType2(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 2) {
                                eventType = xmlPullParser.next();
                                Log.d("fengli", xmlPullParser.getText());
                                todayWeather.setFengli2(xmlPullParser.getText());
                                fengliCount++;

                            }else if (xmlPullParser.getName().equals("date") && dateCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("date", xmlPullParser.getText());
                                todayWeather.setDate3(xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("high", xmlPullParser.getText());
                                todayWeather.setHigh3(xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("low", xmlPullParser.getText());
                                todayWeather.setLow3(xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("type", xmlPullParser.getText());
                                todayWeather.setType3(xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 3) {
                                eventType = xmlPullParser.next();
                                Log.d("fengli", xmlPullParser.getText());
                                todayWeather.setFengli3(xmlPullParser.getText());
                                fengliCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType=xmlPullParser.next();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return todayWeather;
    }

    //更新组件
    void updateTodayWeather(TodayWeather todayWeather)
    {
        cityNameT.setText(todayWeather.getCity()+"天气");
        cityT.setText(todayWeather.getCity());
        timeT.setText(todayWeather.getUpdatetime());
        humidityT.setText("湿度:"+todayWeather.getShidu());
        pmDataT.setText(todayWeather.getPm25());
        pmQualityT.setText(todayWeather.getQuality());
        weekT.setText(todayWeather.getDate());
        temperatureT.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateT.setText(todayWeather.getType());
        windT.setText("风力:"+todayWeather.getFengli());
        detail.setText("  "+todayWeather.getDetail());

        week1T.setText(todayWeather.getDate1());
        temperature1T.setText(todayWeather.getHigh1()+"~"+todayWeather.getLow1());
        climate1T.setText(todayWeather.getType1());
        wind1T.setText("风力:"+todayWeather.getFengli1());

        week2T.setText(todayWeather.getDate2());
        temperature2T.setText(todayWeather.getHigh2()+"~"+todayWeather.getLow2());
        climate2T.setText(todayWeather.getType2());
        wind2T.setText("风力:"+todayWeather.getFengli2());

        week3T.setText(todayWeather.getDate3());
        temperature3T.setText(todayWeather.getHigh3()+"~"+todayWeather.getLow3());
        climate3T.setText(todayWeather.getType3());
        wind3T.setText("风力:"+todayWeather.getFengli3());

        if(todayWeather.getType()!=null) {
            Log.d("type", todayWeather.getType());
            switch (todayWeather.getType()) {
                case "晴":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                    break;
                case "阴":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                    break;
                case "雾":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                    break;
                case "多云":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                    break;
                case "小雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                    break;
                case "中雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                    break;
                case "大雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_daxu);
                    break;
                case "阵雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                    break;
                case "雷阵雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                    break;
                case "雷阵雨加暴":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyujiabingbao);
                    break;
                case "暴雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                    break;
                case "大暴雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                    break;
                case "特大暴雨":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                    break;
                case "阵雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                    break;
                case "暴雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                    break;
                case "大雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                    break;
                case "小雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                    break;
                case "雨夹雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                    break;
                case "中雪":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                    break;
                case "沙尘暴":
                    weatherStateImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                    break;
                default:
                    break;
            }
        }

        if(todayWeather.getPm25()!=null) {
            Log.d("quality", todayWeather.getQuality());
            switch (todayWeather.getQuality()) {
                case "优":
                    PM25Img.setImageResource(R.drawable.biz_plugin_weather_0_50);
                    break;
                case "良":
                    PM25Img.setImageResource(R.drawable.biz_plugin_weather_51_100);
                    break;
                case "轻度污染":
                    PM25Img.setImageResource(R.drawable.biz_plugin_weather_101_150);
                    break;
                case "中度污染":
                    PM25Img.setImageResource(R.drawable.biz_plugin_weather_151_200);
                    break;
                case "重度污染":
                    PM25Img.setImageResource(R.drawable.biz_plugin_weather_201_250);
                    break;
                default:
                    PM25Img.setImageResource(R.drawable.biz_plugin_weather_00);
                    break;
            }
        }

            Toast.makeText(MainActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
//        Toast.makeText(MainActivity.this, updateCityCode, Toast.LENGTH_SHORT).show();
    }
}
