package com.example.gongduck;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FireActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{
    private Context context;
    private  TMapView tMapView;
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


    //길찾기
    TMapData tmapdata=new TMapData();

    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
            nowLogitude=location.getLongitude();
            nowLatitude=location.getLatitude();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire);

        //뒤로가기 이벤트
        Button back_btn=findViewById(R.id.btn_back_f);
        back_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linear_fire);
        tMapView = new TMapView(this);//초기 지도 중심을 공덕역으로 설정함
        tMapView.setLocationPoint(126.950876, 37.543926); //공덕역 : 37.543926, 126.950876
        tMapView.setCenterPoint(126.950876, 37.543926); //공덕역 : 37.543926, 126.950876
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setSKTMapApiKey( "l7xx09b683eb8c9f4fd09fd16aadee28f161" );//API key
        tMapView.setIconVisibility(true);//현재 위치 아이콘 표시

        /*
        //////////////이거 현재위치 추가/////////////////////////////////////
        tmapgps = new TMapGpsManager(FireActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER); //연결된 인터넷으로 현 위치를 받습니다.
        //실내일 때 유용합니다.
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER); //gps로 현 위치를 잡습니다.

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }
        tmapgps.OpenGps();

        //  화면중심을 단말의 현재위치로 이동
        tMapView.setTrackingMode(true);
        tMapView.setSightVisible(true);
        //현재 위치
        TMapPoint point2 = tmapgps.getLocation();


        //////////////이거 현재위치 추가////////////////////////////////////
        */

        linearLayoutTmap.addView( tMapView );
        context = getApplicationContext();

        // 마커 아이콘
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_location_on_red_24dp);
        //markerItem1.setName("SKT타워"); // 마커의 타이틀 지정
        //tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가

        //위 주석꺼 안쓰고 아래 두개 함수 사용

        addVulnerablePoint();
        showVulnerMarkerPoint();

        ///화재취약건물은 경로 그리기 없음

    }
    public void addVulnerablePoint(){
        //취약건물
        m_point2.add(new MapPoint("롯데캐슬 프레지던트",37.544886, 126.950719));
        m_point2.add(new MapPoint("대우 월드마크 마포",37.542309, 126.953383));
        m_point2.add(new MapPoint("신라스테이 마포",37.542805, 126.949657));
        m_point2.add(new MapPoint("나눔빌딩",37.541884, 126.950309));
        m_point2.add(new MapPoint("태영빌딩",37.546988, 126.953658));
        m_point2.add(new MapPoint("한국사회복지회관 르네상스타워",37.543865, 126.952668));
        m_point2.add(new MapPoint("마포공덕파크팰리스Ⅱ",37.547559, 126.953077));
        m_point2.add(new MapPoint("마포현대하이엘",37.549912, 126.954401));
        m_point2.add(new MapPoint("메트로디오빌",37.543509, 126.952873));
        m_point2.add(new MapPoint("고려아카데미텔",37.551380, 126.956100));
        m_point2.add(new MapPoint("성우빌딩",37.540833, 126.946883));
        m_point2.add(new MapPoint("고려빌딩",37.540849, 126.945891));
        m_point2.add(new MapPoint("마포 한화 오벨리스크",37.540077, 126.945569));
        m_point2.add(new MapPoint("마포트라팰리스",37.541549, 126.947250));
    }
    public void showVulnerMarkerPoint(){
        for(int i=0;i<m_point2.size();i++){
            //마커표시 포인트 잡기
            TMapPoint point=new TMapPoint(m_point2.get(i).getLatitude(),m_point2.get(i).getLongitude());
            TMapMarkerItem item1=new TMapMarkerItem();
            Bitmap bitmap =null;
            bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);

            item1.setTMapPoint(point);
            item1.setName(m_point2.get(i).getName());
            //item1.setVisible(0);
            item1.setIcon(bitmap);


            bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);
            item1.setCalloutTitle(m_point2.get(i).getName());
            item1.setCalloutSubTitle("화재취약건물");

            item1.setCanShowCallout(true);
            item1.setAutoCalloutVisible(true);

            //오른쪽 비트맵 모양 추가
            Bitmap bitmap_r=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_chevron_right_black_24dp_2);
            item1.setCalloutRightButtonImage(bitmap_r);

            String strID=String.format("pmarker%d",mMarkerID++);

            //티맵뷰에 추가하기
            tMapView.addMarkerItem(strID,item1);
            mArrayMarkerId.add(strID);
        }
    }




}
