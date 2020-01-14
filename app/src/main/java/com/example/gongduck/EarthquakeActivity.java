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

public class EarthquakeActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{
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
        setContentView(R.layout.activity_earthquake);

        //뒤로가기 이벤트
        Button back_btn=findViewById(R.id.btn_back_e);
        back_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linear_earth);
        tMapView = new TMapView(this);//초기 지도 중심을 공덕역으로 설정함
        tMapView.setLocationPoint(126.950876, 37.543926); //공덕역 : 37.543926, 126.950876
        tMapView.setCenterPoint(126.950876, 37.543926); //공덕역 : 37.543926, 126.950876
        tMapView.setLanguage(TMapView.LANGUAGE_KOREAN);
        tMapView.setSKTMapApiKey( "l7xx09b683eb8c9f4fd09fd16aadee28f161" );//API key
        tMapView.setIconVisibility(true);//현재 위치 아이콘 표시


        //////////////이거 현재위치 추가/////////////////////////////////////
        tmapgps = new TMapGpsManager(EarthquakeActivity.this);
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
        //markerItem1.setName("SKT타워"); // 마커의 타이틀 지정
        //tMapView.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가

        //위 주석꺼 안쓰고 아래 두개 함수 사용

        addPoint();
        showMarkerPoint();

        ///최소 거리 잡고 경로 그리기
        Button button=findViewById(R.id.btn_earth);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TMapPoint nowPoint=new TMapPoint(nowLogitude,nowLatitude);
                //TMapPoint nowPoint=new TMapPoint(126.950876, 37.543926);
                TMapPoint point = new TMapPoint(m_point.get(10).getLatitude(), m_point.get(10).getLongitude());
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
    public void addPoint(){
        //지진대피시설
        m_point.add(new MapPoint("공덕역 어린이공원",37.545166, 126.951337));
        m_point.add(new MapPoint("공덕초등학교 운동장",37.546650, 126.954041));
        m_point.add(new MapPoint("공덕 소공원",37.548496, 126.95458));
        m_point.add(new MapPoint("큰더기 어린이공원",37.550108, 126.953232));
        m_point.add(new MapPoint("아현초등학교 운동장",37.556965, 126.956183));
        m_point.add(new MapPoint("아현중학교 운동장",37.557568, 126.957005));
        m_point.add(new MapPoint("소의초등학교 운동장",37.553738, 126.960444));
        m_point.add(new MapPoint("염라초등학교 운동장",37.543125, 126.94585));
        m_point.add(new MapPoint("도화소 어린이공원",37.542200, 126.94533));
        m_point.add(new MapPoint("삼개 어린이공원",37.540600, 126.945174));
        m_point.add(new MapPoint("마포초등학교 운동장",37.539378, 126.949555));
        m_point.add(new MapPoint("마포 어린이공원",37.536759, 126.943478));
    }

    public void showMarkerPoint(){
        for(int i=0;i<m_point.size();i++){
            //마커표시 포인트 잡기
            TMapPoint point=new TMapPoint(m_point.get(i).getLatitude(),m_point.get(i).getLongitude());
            TMapMarkerItem item1=new TMapMarkerItem();
            Bitmap bitmap =null;
            bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_location_on_red_24dp);

            item1.setTMapPoint(point);
            item1.setName(m_point.get(i).getName());
            //item1.setVisible(0);
            item1.setIcon(bitmap);


            bitmap=BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_location_on_red_24dp);
            item1.setCalloutTitle(m_point.get(i).getName());
            item1.setCalloutSubTitle("지진 대피소");

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
