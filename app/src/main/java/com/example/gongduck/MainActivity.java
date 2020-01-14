package com.example.gongduck;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private TMapView tMapView;
    //private TextView weatherTextView = findViewById(R.id.weather);
    //지진대피소 마크
    final ArrayList alTMapPoint=new ArrayList();
    final ArrayList<MapPoint> m_point=new ArrayList<MapPoint>();
    private int mMarkerID;
    final ArrayList<String> mArrayMarkerId=new ArrayList<String>();
    private Location location;
    private boolean m_bTrackingMode = true;
    private TMapGpsManager tmapgps = null;
    private List<Double> distance=new ArrayList<Double>();
    private List<Double> distance2=new ArrayList<Double>();
    private double nowLogitude;
    private double nowLatitude;
    private double minDistance;
    final ArrayList<MapPoint> m_point2=new ArrayList<MapPoint>();
    final ArrayList<MapPoint> m_point3=new ArrayList<MapPoint>();
    final ArrayList<MapPoint> m_point4=new ArrayList<MapPoint>();
    final ArrayList<MapPoint> m_point5=new ArrayList<MapPoint>();
    private static int REQUEST_SMS_RECEIVE = 1000;

    TextView nav_header_weather;
    ImageView nav_header_weather_icon;
    private String weatherCode;

    //길찾기
    TMapData tmapdata = new TMapData();

    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
            nowLogitude = location.getLongitude();
            nowLatitude = location.getLatitude();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //행동요령
        Button emergency_info = findViewById(R.id.emergencyinfo);
        emergency_info.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent info = new Intent(getApplicationContext(), EmergencyInfo.class);
                startActivity(info);
            }
        });

        //오늘의 신고내역
        Button record = findViewById(R.id.record);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rec = new Intent(getApplicationContext(), DisasterActivity.class);
                startActivity(rec);
            }
        });

        //퍼미션
        int permissionCheck = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                // no permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_SMS_RECEIVE);
            } else {
                // already have permission
            }
        } else {
            // OS version is lower than marshmallow
        }


        //신고버튼
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                 */

                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(intent);
            }
        });

        //탭바 드로어
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View nav_header_view = navigationView.inflateHeaderView(R.layout.nav_header_main);

        nav_header_weather = (TextView)nav_header_view.findViewById(R.id.weather);
        nav_header_weather_icon = (ImageView)nav_header_view.findViewById(R.id.weather_icon);
        displaySelectedScreen(R.id.nav_home);

        new ReceiveShortWeather().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // if granted
        } else {
            // if denied
        }
    }

    //메시지에서 앱호출
    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String content = intent.getStringExtra("contents");

        if (content == null) {
            //webView.setWebViewClient(new WebViewClient());
        } else {
            if (content.contains("폭염")) {
                //finish();
                Intent intent1 = new Intent(getApplicationContext(), HotActivity.class);
                startActivity(intent1);
                finish();
                //displaySelectedScreen(R.id.nav_menu1);
            } else if (content.contains("한파")) {
                //finish();
                Intent intent2 = new Intent(getApplicationContext(), ColdActivity.class);
                startActivity(intent2);
                finish();
                //displaySelectedScreen(R.id.nav_menu2);
            } else if (content.contains("화재")) {
                //finish();
                Intent intent3 = new Intent(getApplicationContext(), EmergencyActivity.class);
                startActivity(intent3);
                finish();
                //displaySelectedScreen(R.id.nav_menu3);
            } else if (content.contains("지진")) {
                //finish();
                Intent intent4 = new Intent(getApplicationContext(), EarthquakeActivity.class);
                startActivity(intent4);
                finish();
                //displaySelectedScreen((R.id.nav_menu4));
            } else {
                //Toast.makeText(this, "검색어 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void displaySelectedScreen(int itemId) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        int id=item.getItemId();

        switch (id) {
            case R.id.nav_home:
// 기능 없음
                break;
            case R.id.nav_menu1:
                Intent hotIntent=new Intent(getApplicationContext(),HotActivity.class);
                startActivity(hotIntent);
                break;
            case R.id.nav_menu2:
                //한파 대피소
                Intent coldIntent=new Intent(getApplicationContext(),ColdActivity.class);
                startActivity(coldIntent);
                break;
            case R.id.nav_menu3:
                //화재 취약건물
                Intent fireIntent=new Intent(getApplicationContext(),FireActivity.class);
                startActivity(fireIntent);
                break;
            case R.id.nav_menu4:
                //지진 대피소
                Intent earthIntent=new Intent(getApplicationContext(),EarthquakeActivity.class);
                startActivity(earthIntent);
                break;

            case R.id.nav_menu5:
                //비상대피소
                Intent emergencyIntent=new Intent(getApplicationContext(),EmergencyActivity.class);
                startActivity(emergencyIntent);
                break;
        }


        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }
    public class ReceiveShortWeather extends AsyncTask<URL, Integer, Long> {

        ArrayList<WeatherClass> weathers = new ArrayList<WeatherClass>();

        protected Long doInBackground(URL... urls) {

            String url = "http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=1144056500";

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = null;

            try {
                response = client.newCall(request).execute();
                parseXML(response.body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Long result) {
            String data = "";

            data = weathers.get(0).getWfKor()+ '\n' +'\n' + "기온 : " + weathers.get(0).getTemp() + "℃" + ' '  + '\n' + "강수 확률 : " + weathers.get(0).getPop()+"%";
            nav_header_weather.setText(data);

            weatherCode = weathers.get(0).getWfKor();
            switch(weatherCode) {
                case "맑음":
                    Drawable sdrawable = getResources().getDrawable(R.drawable.sunny);
                    nav_header_weather_icon.setImageDrawable(sdrawable);
                    break;
                case "구름 조금":
                    Drawable cdrawable = getResources().getDrawable(R.drawable.cloudy);
                    nav_header_weather_icon.setImageDrawable(cdrawable);
                    break;
                case "구름 많음":
                    Drawable mcdrawable = getResources().getDrawable(R.drawable.many_cloudy);
                    nav_header_weather_icon.setImageDrawable(mcdrawable);
                    break;
                case "흐림":
                    Drawable rdrawable = getResources().getDrawable(R.drawable.rainy);
                    nav_header_weather_icon.setImageDrawable(rdrawable);
                    break;
            }
        }

        void parseXML(String xml) {
            try {
                String tagName = "";
                boolean onHour = false;
                boolean onDay = false;
                boolean onTem = false;
                boolean onWfKor = false;
                boolean onPop = false;
                boolean onEnd = false;
                boolean isItemTag1 = false;
                int i = 0;

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();

                parser.setInput(new StringReader(xml));

                int eventType = parser.getEventType();

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        tagName = parser.getName();
                        if (tagName.equals("data")) {
                            weathers.add(new WeatherClass());
                            onEnd = false;
                            isItemTag1 = true;
                        }
                    } else if (eventType == XmlPullParser.TEXT && isItemTag1) {
                        if (tagName.equals("hour") && !onHour) {
                            weathers.get(i).setHour(parser.getText());
                            onHour = true;
                        }
                        if (tagName.equals("day") && !onDay) {
                            weathers.get(i).setDay(parser.getText());
                            onDay = true;
                        }
                        if (tagName.equals("temp") && !onTem) {
                            weathers.get(i).setTemp(parser.getText());
                            onTem = true;
                        }
                        if (tagName.equals("wfKor") && !onWfKor) {
                            weathers.get(i).setWfKor(parser.getText());
                            onWfKor = true;
                        }
                        if (tagName.equals("pop") && !onPop) {
                            weathers.get(i).setPop(parser.getText());
                            onPop = true;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        if (tagName.equals("s06") && onEnd == false) {
                            i++;
                            onHour = false;
                            onDay = false;
                            onTem = false;
                            onWfKor = false;
                            onPop = false;
                            isItemTag1 = false;
                            onEnd = true;
                        }
                    }

                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}