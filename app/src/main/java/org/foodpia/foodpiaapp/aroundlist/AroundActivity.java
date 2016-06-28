package org.foodpia.foodpiaapp.aroundlist;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.foodpia.foodpiaapp.R;

import java.util.List;

/**
 * Created by thnt on 2016-06-14.
 */
public class AroundActivity extends Activity {

    ListView around_listView;
    AroundListAdapter aroundListAdapter;
    TextView lati, longi;
    private GpsInfo gps;

    static public Double phone_latituede;
    static public Double phone_longitude;

    boolean lastitemVisibleFlag = false;

    int ratio = 2; // 스크롤 아래가 갱신되면 6개가보여지고 한번 더 갱신되면 9개가 보여짐..!
    int numberWannaShow = 3; // aroundListAdapter.foodList.size()/3 과 값이 같아야됨

    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.around_list_layout);

        lati = (TextView) findViewById(R.id.lati); // 해당 핸드폰의 위도와 경도를 담고 있을 textView들...
        longi = (TextView) findViewById(R.id.longi);

        getGps(); // gps얻어오는거

        Intent intent = getIntent();
        user_id = intent.getIntExtra("fmember_id", -1);

        around_listView = (ListView) findViewById(R.id.around_listView);
        aroundListAdapter = new AroundListAdapter(this, lati, longi, user_id);

        around_listView.setAdapter(aroundListAdapter);

        // 스크롤 이벤트 감지 리스너 장착 !!
        around_listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                lastitemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //OnScrollListener.SCROLL_STATE_IDLE은 스크롤이 이동하다가 멈추었을때 발생되는 스크롤 상태
                //즉 스크롤이 바닦에 닿아 멈춘 상태에 처리를 하겠다는 뜻
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastitemVisibleFlag) {

                    AroundListAdapter.countShowList = numberWannaShow * ratio;
                    if (AroundListAdapter.countShowList > aroundListAdapter.foodList.size()) {
                        Toast.makeText(getApplicationContext(), "더 이상 보여줄 페이가 없습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ratio++;
                    aroundListAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "갱신중", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*around_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Toast.makeText(getApplicationContext(), position, Toast.LENGTH_SHORT).show();
            }
        }) ;*/
    }

    public void getGps() {
        gps = new GpsInfo(this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            phone_latituede = latitude;
            phone_longitude = longitude;

            lati.setText(String.valueOf(latitude));
            longi.setText(String.valueOf(longitude));

            Toast.makeText(
                    getApplicationContext(),
                    "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                    Toast.LENGTH_LONG).show();
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }
    }


}
