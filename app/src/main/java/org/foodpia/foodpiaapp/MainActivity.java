package org.foodpia.foodpiaapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import org.foodpia.foodpiaapp.aroundlist.AroundActivity;
import org.foodpia.foodpiaapp.detail.DetailAsync;
import org.foodpia.foodpiaapp.regist.RegistActivity;
import org.foodpia.foodpiaapp.search.Address;
import org.foodpia.foodpiaapp.search.AddressSearcher;
import org.foodpia.foodpiaapp.search.Food;
import org.foodpia.foodpiaapp.search.FoodImageLoader;
import org.foodpia.foodpiaapp.search.FoodSearcher;
import org.foodpia.foodpiaapp.search.Item;
import org.foodpia.foodpiaapp.search.OnFinishImageLoaderListener;
import org.foodpia.foodpiaapp.search.OnFinishSearchAddressListener;
import org.foodpia.foodpiaapp.search.OnFinishSearchFoodListener;
import org.foodpia.foodpiaapp.search.OnFinishSearchListener;
import org.foodpia.foodpiaapp.search.Searcher;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.CurrentLocationEventListener, MapView.POIItemEventListener {
    String TAG = this.getClass().getName();//테스트 용

    private final String api_key = "3a0a31e995748c5eac71d13ebbce2638";//우리 API키


    RelativeLayout containerLayout;//맵뷰를 안고 있는 뷰그룹
    MapView mapView;//지도가 보여질 뷰. 전체 뷰
    int currentLocationMode = 0;//현재위치 나타내는 모드 기준 값.     0: 미사용     1: 사용    2: 나침반모드


    //검색창
    SearchView searchView;



    //단말기 사이즈 구하기
    Display display;
    int mDisplayWidth;
    int mDisplayHeight;

    //간략정보 팝업창
    public PopupWindow pwindo;

    //// 크로스헤어 관련 ////
    ViewGroup mapViewContainer;//지도가 보여질 MapView를 떠안고 있는 뷰그룹
    ImageView crossHair;
    boolean registFoodMode;


    //// 임시 푸드 리스트 ////
    List<Food> foods;
    boolean searchDone;//검색했는지 안했는지 여부

    //// 플로팅버튼 ////
    RelativeLayout floatingButtonContainerLayout;
    FloatingActionButton locationButton;

    //// 플로팅버튼(more) ////
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    // 플로팅 애니메이션
    Animation show_fab_1;
    Animation hide_fab_1;
    Animation show_fab_2;
    Animation hide_fab_2;
    Animation show_fab_3;
    Animation hide_fab_3;
    boolean isShowButtons;

    //////fmember_id
    public static int fmember_id;


    ////찬수 ///

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey(api_key);

        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        containerLayout = (RelativeLayout) mapViewContainer;

        mapView.setMapViewEventListener(this); // this에 MapView.MapViewEventListener 구현.
        mapView.setPOIItemEventListener(this); // this에 마커 리스너 구현
        mapViewInit();//초기 맵뷰의 중심 위치 및 줌 레벨 초기화

        displayInit();//각 단말기 사이즈 구하기 위한 초기화

        crossHairInit();//등록모드 관련

        toggleButtonInit();//

        floatingButtonInit();//


        /// 로그인 관련
        int temp_id=getIntent().getIntExtra("fmember_id",-1);
        if(temp_id!=-1){
            fmember_id=getIntent().getIntExtra("fmember_id",-1);
        }
        Log.d(TAG,"fmember_id는? "+fmember_id);


        mapView.removeAllPOIItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.searchBtn);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                popupClose();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                searchView.clearFocus();

                search(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.moreBtn:
                if(isShowButtons){
                    isShowButtons=false;
                    hideFloatingButton();
                }else{
                    isShowButtons=true;
                    showFloatingButton();
                }break;
        }
        return super.onOptionsItemSelected(item);
    }

    //// 토글버튼의 온클릭
    public void modeBtnClick(View view) {
        if (mapView.getPOIItems().length > 0) {
            mapView.removeAllPOIItems();
        }
        switch (view.getId()){
            case R.id.search:
                if (registFoodMode) {
                    showToast("검색모드");
                    if (crossHair.getScaleX() > 0.5) {
                        crossHair.setScaleX(0);
                        crossHair.setScaleY(0);
                    }
                    registFoodMode = false;
                }break;

            case R.id.regist:

                if(!registFoodMode){
                    showToast("등록모드");
                    if (crossHair.getScaleX() < 0.5) {
                        crossHair.setScaleX(1);
                        crossHair.setScaleY(1);
                    }
                    registFoodMode = true;
                    mapView.removeAllPOIItems();
                }break;
        }
    }


    //// 플로팅버튼의 온클릭
    public void floatingBtnClick(View view){
        switch (view.getId()){
            case R.id.btn_current_location:
                changeCurrentLocationMode();
                break;
            case R.id.fab_1:
                Intent intent=new Intent(this, AroundActivity.class);
                intent.putExtra("fmember_id",fmember_id);
                startActivity(intent);break;
            case R.id.fab_2:
                showToast("내정보 추후 업데이트 예정");break;
            case R.id.fab_3:
                showToast("고객센터 추후 업데이트 예정");break;
        }
    }



    /////////////////맵뷰 이닛/////////////////////////////
    private void mapViewInit() {
        // 중심점 변경
        //mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);

        // 줌 레벨 변경
        mapView.setZoomLevel(7, true);

        // 중심점 변경 + 줌 레벨 변경
        //mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);

        // 줌 인
        mapView.zoomIn(true);

        // 줌 아웃
        mapView.zoomOut(true);

        //범위 표시
        //mapView.setCurrentLocationRadius(100);
        //mapView.setCurrentLocationRadiusFillColor(Color.RED);
    }


    private void search(final String query) {

        if (query == null || query.length() == 0) {
            showToast("검색어를 입력하세요.");
            return;
        }
        //hideSoftKeyboard(); // 키보드 숨김
        MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();
        double latitude = geoCoordinate.latitude; // 위도
        double longitude = geoCoordinate.longitude; // 경도
        int count = 15;
        int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개

        Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
        searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, count, page, api_key, new OnFinishSearchListener() {
            @Override
            public void onSuccess(List<Item> itemList) {
                mapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                showResult(itemList, query); // 검색 결과 보여줌
            }

            @Override
            public void onFail() {
                showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
            }
        });
    }


    ///////////////내 단말기 사이즈 가져오는 초기화 이닛//////////////////
    private void displayInit() {
        Point size = new Point();
        display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        mDisplayWidth = size.x;
        mDisplayHeight = size.y;
    }


    ////////// 크로스헤어 이닛 ////////////
    private void crossHairInit() {
        //크로스헤어(ImageView)의 속성
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 150);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);


        RelativeLayout relativeLayout = (RelativeLayout) mapViewContainer;
        crossHair = new ImageView(this);
        crossHair.setImageResource(R.drawable.crosshair);
        relativeLayout.addView(crossHair, layoutParams);

        crossHair.setScaleX(0);
        crossHair.setScaleY(0);
    }


    //////// 토글버튼 이닛 ////////
    private void toggleButtonInit() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(400, 150);
        //layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        //layoutParams.setMargins(0,50,0,0);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.toggle_button, containerLayout);

        //relativeLayout.addView(radioGroup, layoutParams);
    }


    //// 플로팅 버튼 초기화 ////
    public void floatingButtonInit(){
        View floatingButtonContainer=getLayoutInflater().inflate(R.layout.floating_menu_button, containerLayout);
        floatingButtonContainerLayout=(RelativeLayout) floatingButtonContainer.findViewById(R.id.floating_layout);
        locationButton=(FloatingActionButton) floatingButtonContainerLayout.findViewById(R.id.btn_current_location);



        ////플로팅버튼2 초기화 ////
        FrameLayout main_layout=(FrameLayout)findViewById(R.id.main_layout);
        getLayoutInflater().inflate(R.layout.more_button_layout, main_layout);


        fab1= (FloatingActionButton) findViewById(R.id.fab_1);
        fab2= (FloatingActionButton) findViewById(R.id.fab_2);
        fab3= (FloatingActionButton) findViewById(R.id.fab_3);

        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);
    }



    //// GPS 사용 유무 체크 ////
    public void gpsCheck() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            new AlertDialog.Builder(this).setMessage("위치 서비스 기능이 꺼져있습니다.\n위치 서비스 설정을 하시겠습니까?\n멍청한 변규현")
                    .setCancelable(false).setPositiveButton("설정하기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    movingConfigGPS();
                }
            })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).create().show();
        } else {
            changeCurrentLocationMode();
        }
    }

    public void movingConfigGPS() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }


    /////////현재위치 찾기////////////////////////
    public void changeCurrentLocationMode() {
        switch (currentLocationMode) {
            case 0:
                floatingButtonContainerLayout.startAnimation(AnimationUtils.loadAnimation(this,R.anim.pade_in_out_anim));
                //현재위치로
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                currentLocationMode = 1;
                break;
            case 1:
                //현재위치로 + 나침반 모드
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                mapView.setZoomLevel(2, true);
                currentLocationMode = 2;
                break;
            case 2:
                floatingButtonContainerLayout.clearAnimation();
                //현재위치 해제
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                mapView.setShowCurrentLocationMarker(false);
                currentLocationMode = 0;
                break;
        }
    }


    //////검색시 넘어오는 곳///////
    private void showResult(List<Item> itemList, String query) {

        //검색시 중심점찾기 관련 변수들
        double cLatitude=0, cLongitude=0;

        MapPointBounds mapPointBounds = new MapPointBounds();

        for (int i = 0; i < itemList.size(); i++) {
            Item item = itemList.get(i);
            if (i == 0) {
                cLatitude = item.latitude;
                cLongitude = item.longitude;
            } else {
                cLatitude = (cLatitude + item.latitude) / 2.;
                cLongitude = (cLongitude + item.longitude) / 2.;
            }
            /*
            MapPOIItem poiItem = new MapPOIItem();
            poiItem.setItemName(item.title);
            poiItem.setTag(i);
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
            poiItem.setMapPoint(mapPoint);

            mapPointBounds.add(mapPoint);

            poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
            poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
            poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
            poiItem.setCustomImageAutoscale(false);
            poiItem.setCustomImageAnchor(0.5f, 1.0f);

            mapView.addPOIItem(poiItem);
            mTagItemMap.put(poiItem.getTag(), item);
            */
        }
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(cLatitude, cLongitude);
        if(mapView.getZoomLevel()>=4){
            mapView.moveCamera(CameraUpdateFactory.newMapPoint(mapPoint,4));
        }else {
            mapView.moveCamera(CameraUpdateFactory.newMapPoint(mapPoint));
        }
        /*
        MapPOIItem[] poiItems = mapView.getPOIItems();
        if (poiItems.length > 0) {
            mapView.selectPOIItem(poiItems[0], false);
        }
        */

        if(!registFoodMode) {
            FoodSearcher foodSearcher = new FoodSearcher();
            foodSearcher.searchFood(cLatitude, cLongitude, mapView.getZoomLevel(), new OnFinishSearchFoodListener() {
                @Override
                public void onSuccess(List<Food> foodList) {
                    showFood(foodList);
                }

                @Override
                public void onFail() {
                    showToast("푸드리스트 없음");
                }
            });
        }
    }


    ///food
    private void showFood(List<Food> foodList) {
        //showToast("showFood() 호출");
        Log.d(TAG, "foodList size" + foodList.size());

        for (Food food : foodList) {
            addFoodItem(food);
        }
        foods = foodList;
        searchDone=true;
    }

    private void addFood(MapPoint mapPoint){

        FoodSearcher foodSearcher=new FoodSearcher();
        foodSearcher.searchFood(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude, mapView.getZoomLevel(),
                new OnFinishSearchFoodListener() {
                    @Override
                    public void onSuccess(List<Food> foodList) {
                        Outer:for(Food newFood : foodList){
                            for(int i=foods.size()-1;i>=0;i--){
                                Food food=foods.get(i);
                                if(newFood.food_id==food.food_id){
                                    continue Outer;
                                }
                            }
                            foods.add(newFood);
                            addFoodItem(newFood);
                        }
                    }

                    @Override
                    public void onFail() {

                    }
                });
    }

    //// food마커추가
    private void addFoodItem(Food food){
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(food.latitude, food.longitude);

        MapPOIItem marker = new MapPOIItem();
        marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
        marker.setItemName(food.title);
        marker.setTag(food.food_id);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);
    }


    //////////////맵뷰 리스너/////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onMapViewInitialized(MapView mapView) {
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        popupClose();
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        if (crossHair != null && crossHair.getScaleX() < 0.5 && registFoodMode) {
            crossHair.setScaleX(1);
            crossHair.setScaleY(1);
            crossHair.startAnimation(AnimationUtils.loadAnimation(this, R.anim.sizeup_anim));
            mapView.removeAllPOIItems();

            popupClose();
        }

        if (currentLocationMode != 0) {// 현재위치 추적모드나 나침반 모드라면
            currentLocationMode = 2;
            changeCurrentLocationMode();
        }
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        if(searchDone&&!registFoodMode){
            addFood(mapPoint);
        }
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        if (crossHair != null && crossHair.getScaleX() > 0.5 && registFoodMode) {
            crossHair.setScaleX(0);
            crossHair.setScaleY(0);
            final MapPOIItem marker = new MapPOIItem();
            marker.setShowAnimationType(MapPOIItem.ShowAnimationType.SpringFromGround);
            marker.setItemName("여기로 할래?");
            marker.setTag(0);
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.


            AddressSearcher addressSearcher=new AddressSearcher();
            addressSearcher.searchAddress(mapPoint.getMapPointGeoCoord().latitude, mapPoint.getMapPointGeoCoord().longitude,
                    new OnFinishSearchAddressListener() {
                        @Override
                        public void onSuccess(Address address) {
                            marker.setItemName(address.getOld_name());
                        }

                        @Override
                        public void onFail() {

                        }
                    });




            mapView.addPOIItem(marker);
        }
        //String ll="위도: "+mapPoint.getMapPointGeoCoord().latitude+"   경도: "+mapPoint.getMapPointGeoCoord().longitude;
        //Log.d(TAG,ll);
    }
    ///////////////////현재위치 리스너/////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    /////////마커 리스너//////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        popupClose();
        if (!registFoodMode) {
            popup(mapPOIItem);
        }

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, final MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        if (registFoodMode) {

            double latitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
            double longitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;

            AddressSearcher addressSearcher = new AddressSearcher();
            addressSearcher.searchAddress(latitude, longitude, new OnFinishSearchAddressListener() {

                @Override
                public void onSuccess(Address address) {
                    moveRegist(mapPOIItem, address);
                }

                @Override
                public void onFail() {
                    showToast("등록 실패");
                }
            });

        } else {
            if (foods != null) {
                for (Food food : foods) {
                    if (food.food_id == mapPOIItem.getTag()) {
                        //showToast("제대로 돼. 상세페이지로");

                        DetailAsync detailAsync=new DetailAsync(this);
                        detailAsync.execute(food.food_id, fmember_id);
                    }
                }
            }
        }
    }

    private void moveRegist(MapPOIItem mapPOIItem, Address address){
        double latitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude;
        double longitude = mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;

        // 찬수로 넘어가기
        Intent intent=new Intent(getApplicationContext(), RegistActivity.class);
        intent.putExtra("latitude",latitude);
        intent.putExtra("longitude",longitude);
        intent.putExtra("food_address",address.getOld_name());
        intent.putExtra("fmember_id",fmember_id);



        startActivity(intent);
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
    }


    /////////////간략정보 윈도우////////////////////////
    public void popup(MapPOIItem mapPOIItem) {
        //  LayoutInflater 객체와 시킴
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.foodinfo_layout, (ViewGroup) findViewById(R.id.foodinfo));

        TextView info_location = (TextView) layout.findViewById(R.id.info_location);
        TextView info_nickname = (TextView) layout.findViewById(R.id.info_nickname);
        TextView info_content = (TextView) layout.findViewById(R.id.info_content);
        ImageView info_img=(ImageView)layout.findViewById(R.id.info_img);

        for (Food food : foods) {
            if (food.food_id == mapPOIItem.getTag()) {
                info_location.setText("~ " + food.title + " ~ ");
                info_nickname.setText("닉네임 : " + food.nickname);
                info_content.setText(food.content);

                //이미지 넣기
                FoodImageLoader foodImageLoader=new FoodImageLoader();
                foodImageLoader.loadImage(info_img, food.thumbImgFileName, new OnFinishImageLoaderListener() {
                    @Override
                    public void onSuccess() {
                        //showToast("이미지 로드 성공");
                    }

                    @Override
                    public void onFail() {
                        //showToast("이미지를 불러오지 못하였습니다");
                    }
                }, true);

            }
        }

        pwindo = new PopupWindow(layout, mDisplayWidth, mDisplayHeight / 8, true);
        pwindo.setOutsideTouchable(true);
        pwindo.setFocusable(false);
        pwindo.setAnimationStyle(R.style.popAnimation);
        pwindo.showAtLocation(layout, Gravity.NO_GRAVITY, 0, (mDisplayHeight * 7) / 8);

    }

    public void popupClose() {
        if (pwindo != null) {
            pwindo.dismiss();
            pwindo = null;
        }
    }


    ////////잡기능들/////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }


    //// 플로팅 버튼들 띄우기 ////
    private void showFloatingButton(){
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);


        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);


        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);
    }

    ////// 플로팅버튼 없애기
    private void hideFloatingButton(){
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);


        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);


        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);
    }
}
