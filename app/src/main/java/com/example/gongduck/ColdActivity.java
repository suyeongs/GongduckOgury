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

public class ColdActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{
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
        setContentView(R.layout.activity_cold);

        //뒤로가기 이벤트
        Button back_btn=findViewById(R.id.btn_back_c);
        back_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linear_cold);
        tMapView = new TMapView(this);//초기 지도 중심을 공덕역으로 설정함
        tMapView.setLocationPoint(126.950876, 37.543926); //공덕역 : 37.543926, 126.950876
        tMapView.setCenterPoint(126.950876, 37.543926); //공덕역 : 37.543926, 126.950876
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setSKTMapApiKey( "l7xx09b683eb8c9f4fd09fd16aadee28f161" );//API key
        tMapView.setIconVisibility(true);//현재 위치 아이콘 표시


        //////////////이거 현재위치 추가/////////////////////////////////////
        tmapgps = new TMapGpsManager(ColdActivity.this);
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


        linearLayoutTmap.addView( tMapView );
        context = getApplicationContext();

        // 마커 아이콘
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_location_on_red_24dp);


        addColdShelter();
        showVulnerMarkerPoint();

        ///최소 거리 잡고 경로 그리기
        Button button=findViewById(R.id.btn_cold);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TMapPoint nowPoint=new TMapPoint(nowLogitude,nowLatitude);
                //TMapPoint nowPoint=new TMapPoint(126.950876, 37.543926);
                TMapPoint point = new TMapPoint(m_point4.get(0).getLatitude(), m_point4.get(0).getLongitude());
                final TMapPoint startpoint=new TMapPoint(nowPoint.getLongitude(),nowPoint.getLatitude());
                tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, point, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        tMapView.addTMapPolyLine("1", polyLine);
                        Log.d("distance",polyLine.getDistance()+"");
                        //거리 계산해서 배열에 넣기
                        distance.add(polyLine.getDistance());
                    }
                });
                Log.d("distance","now"+nowPoint);

            }
        });
        //////최소 경로 그림그리기/////

    }
    public void addColdShelter(){
        //한파쉼터
        m_point4.add(new MapPoint("대동경로당",37.541746,126.95202));
        m_point4.add(new MapPoint("큰덕경로당",37.550441, 126.960429));
        m_point4.add(new MapPoint("연봉경로당",37.551469,126.9622));
        m_point4.add(new MapPoint("아현1동경로당",37.554703, 126.961165));
    }
    public void showVulnerMarkerPoint(){
        for(int i=0;i<m_point4.size();i++){
            //마커표시 포인트 잡기
            TMapPoint point=new TMapPoint(m_point4.get(i).getLatitude(),m_point4.get(i).getLongitude());
            TMapMarkerItem item1=new TMapMarkerItem();
            Bitmap bitmap =null;
            bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);

            item1.setTMapPoint(point);
            item1.setName(m_point4.get(i).getName());
            //item1.setVisible(0);
            item1.setIcon(bitmap);


            bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);
            item1.setCalloutTitle(m_point4.get(i).getName());
            item1.setCalloutSubTitle("한파쉼터");

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
