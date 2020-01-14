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

public class EmergencyActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{
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
        setContentView(R.layout.activity_emergency);

        //뒤로가기 이벤트
        Button back_btn=findViewById(R.id.btn_back_em);
        back_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linear_emer);
        tMapView = new TMapView(this);//초기 지도 중심을 공덕역으로 설정함
        tMapView.setLocationPoint(126.950876, 37.543926); //공덕역 : 37.543926, 126.950876
        tMapView.setCenterPoint(126.950876, 37.543926); //공덕역 : 37.543926, 126.950876
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setSKTMapApiKey( "l7xx09b683eb8c9f4fd09fd16aadee28f161" );//API key
        tMapView.setIconVisibility(true);//현재 위치 아이콘 표시


        //////////////이거 현재위치 추가/////////////////////////////////////
        tmapgps = new TMapGpsManager(EmergencyActivity.this);
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

        addEmergency();
        showMarkerPoint();

        ///최소 거리 잡고 경로 그리기
        Button button=findViewById(R.id.btn_emer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TMapPoint nowPoint=new TMapPoint(nowLogitude,nowLatitude);
                //TMapPoint nowPoint=new TMapPoint(126.950876, 37.543926);
                TMapPoint point = new TMapPoint(m_point5.get(0).getLatitude(), m_point5.get(0).getLongitude());
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
            /*


                TMapPoint nowPoint=new TMapPoint(nowLogitude,nowLatitude);

                for(int i=0;i<m_point.size();i++) {
                    TMapPoint point = new TMapPoint(m_point.get(i).getLatitude(), m_point.get(i).getLongitude());
                    final TMapPoint startpoint=new TMapPoint(nowPoint.getLongitude(),nowPoint.getLatitude());
                    //final TMapPoint endpoint = new TMapPoint(37.546650, 126.954041);
                    tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, point, new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            //tMapView.addTMapPolyLine("1", polyLine);
                            Log.d("distance",polyLine.getDistance()+"");
                            //거리 계산해서 배열에 넣기
                            distance.add(polyLine.getDistance());
                        }
                    });
                    Log.d("distance","now"+nowPoint);
                }

                //기존 배열을 distance2에 저장
                distance2=distance;
                for(int i=0;i<distance.size();i++){
                    Log.d("distance"+i,distance.get(i)+"");
                }
                //정렬
                Collections.sort(distance);

                //정렬후 확인
                for(int i=0;i<distance.size();i++){
                    Log.d("distance"+i+"22",distance.get(i)+"");
                }
                //배열에 자꾸 0이 들어가서 일단 예외처리
                for (int i = 0; i < distance.size(); i++) {
                    minDistance = distance.get(i);
                    if (minDistance == 0) {
                        i++;
                    } else {
                        for (int k = 0; k < distance.size(); k++) {
                            //거리가 같은 것 발견하면 좌표그리기
                            if (minDistance == distance2.get(k)) {
                                TMapPoint point = new TMapPoint(m_point.get(k).getLatitude(), m_point.get(k).getLongitude());
                                final TMapPoint startpoint = new TMapPoint(nowPoint.getLongitude(), nowPoint.getLatitude());
                                tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, startpoint, point, new TMapData.FindPathDataListenerCallback() {
                                    @Override
                                    public void onFindPathData(TMapPolyLine polyLine) {
                                        tMapView.addTMapPolyLine("1", polyLine);
                                        Log.d("distance", "찍기");
                                    }
                                });
                            }
                        }
                    }
                    break;
                }
                // }

             */
            }
        });
        //////최소 경로 그림그리기/////

    }
    public void addEmergency(){
        //비상대피시설
        m_point5.add(new MapPoint("공덕 전철역",37.544275,126.951619));
        m_point5.add(new MapPoint("창강빌딩",37.543601,126.950427));
        m_point5.add(new MapPoint("서울가든호텔",37.540916,126.948441));
        m_point5.add(new MapPoint("마포한화오벨리스크",37.541133,126.945377));
        m_point5.add(new MapPoint("마포 전철역",37.540657,126.945911));
        m_point5.add(new MapPoint("다보빌딩",37.539351,126.944903));
        m_point5.add(new MapPoint("한신코아빌딩",37.537445,126.94386));
        m_point5.add(new MapPoint("재화스퀘어 지하대피시설",37.544896,126.947508));
        m_point5.add(new MapPoint("효성빌딩",37.553653,126.952484));
        m_point5.add(new MapPoint("태영빌딩",37.553068,126.953514));
        m_point5.add(new MapPoint("한겨레신문",37.548877,126.958863));
        m_point5.add(new MapPoint("서울지방검찰청 서부지청",37.549801,126.955503));
        m_point5.add(new MapPoint("애오개 전철역",37.553838,126.956762));
        m_point5.add(new MapPoint("별정우체국 연금관리단빌딩",37.546729,126.953042));

    }

    public void showMarkerPoint(){
        for(int i=0;i<m_point5.size();i++){
            //마커표시 포인트 잡기
            TMapPoint point=new TMapPoint(m_point5.get(i).getLatitude(),m_point5.get(i).getLongitude());
            TMapMarkerItem item1=new TMapMarkerItem();
            Bitmap bitmap =null;
            bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_location_on_red_24dp);

            item1.setTMapPoint(point);
            item1.setName(m_point5.get(i).getName());
            //item1.setVisible(0);
            item1.setIcon(bitmap);


            bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_location_on_red_24dp);
            item1.setCalloutTitle(m_point5.get(i).getName());
            item1.setCalloutSubTitle("비상 대피소");

            item1.setCanShowCallout(true);
            item1.setAutoCalloutVisible(true);

            //오른쪽 비트맵 모양 추가
            Bitmap bitmap_r=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_chevron_right_black_24dp);
            item1.setCalloutRightButtonImage(bitmap_r);

            String strID=String.format("pmarker%d",mMarkerID++);

            //티맵뷰에 추가하기
            tMapView.addMarkerItem(strID,item1);
            mArrayMarkerId.add(strID);
        }
    }




}
