package org.foodpia.foodpiaapp.aroundlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.foodpia.foodpiaapp.R;
import org.foodpia.foodpiaapp.detail.DetailAsync;
import org.foodpia.foodpiaapp.search.Food;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thnt on 2016-06-14.
 */
public class AroundListAdapter extends BaseAdapter {
    AroundListAdapter aroundListAdapter;
    String TAG = getClass().getName();
    AroundActivity context;
    ArrayList<Food> foodList = new ArrayList<Food>();
    ArrayList<Bitmap> imgList = new ArrayList<Bitmap>();
    ArrayList<double[]> disList = new ArrayList<double[]>();
    ImageView around_img;
    TextView lati, longi;
    public static int countShowList = 3;

    int fmember_id;
    int user_id; // 내가 누구냐 user_id
    int food_id;

    boolean flag; // 좋아요

    public AroundListAdapter(AroundActivity context, TextView lati, TextView longi, int user_id) {
        this.context = context;
        this.lati = lati;
        this.longi = longi;
        this.user_id = user_id;

        aroundListAdapter = this;

        AroundListRequest aroundListRequest = new AroundListRequest();
        aroundListRequest.getFoodList(lati.getText().toString(), longi.getText().toString(), new OnAroundListRequestListener() {
            @Override
            public void success(List<Food> newFoodList) {
                foodList = (ArrayList<Food>) newFoodList;
                setImg(newFoodList);
            }

            @Override
            public void fail() {

            }
        });
        Log.d(TAG, "AroundListAdapter 생성자 호출하니?");
    }

    public void setImg(final List<Food> foodList) {
        imgList.removeAll(imgList);
        for (int i = 0; i < foodList.size(); i++) {
            setDisList(foodList.get(i));
            Food food = foodList.get(i);
            String ext = food.filename.substring(food.filename.lastIndexOf(".") + 1, food.filename.length()); // filename을 바탕으로 확장자를 알아내자

            AroundImgListRequest aroundImgListRequest = new AroundImgListRequest();
            aroundImgListRequest.getImgList(this, "http://bbungbbunge.cafe24.com/data/" + food.photo_id + "." + ext, new OnAroundImgListRequestListener() {
                @Override
                public void success(Bitmap img) {
                    imgList.add(img);
                    Log.d(TAG, "imgListSize=" + imgList.size());
                }

                @Override
                public void fail() {
                }
            });
        }
    }

    private void setDisList(Food food) {
        double[] arr = {
                food.latitude, food.longitude
        };
        disList.add(arr);
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // tempPosition=position;
        View view = null;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.around_list_item, parent, false);
        } else {
            view = convertView;
        }

        around_img = (ImageView) view.findViewById(R.id.around_img); // 이미지는 또 Async를해서......
        TextView around_txt_nick = (TextView) view.findViewById(R.id.around_txt_nick);
        TextView around_txt_content = (TextView) view.findViewById(R.id.around_title);
        TextView around_txt_dis = (TextView) view.findViewById(R.id.around_txt_dis);
        TextView aroundFmember_id = (TextView) view.findViewById(R.id.aroundFmember_id);
        TextView numberOfLike = (TextView) view.findViewById(R.id.numberOfLike);
        ImageView bt_like = (ImageView) view.findViewById(R.id.bt_like);
        ImageView bt_message = (ImageView) view.findViewById(R.id.bt_msg);
        ImageView bt_info = (ImageView) view.findViewById(R.id.bt_info);
        food_id = foodList.get(position).food_id;

        around_txt_nick.setText(foodList.get(position).nickname);
        around_txt_content.setText(foodList.get(position).title);
        around_img.setImageBitmap(imgList.get(position));
        around_img.setScaleType(ImageView.ScaleType.FIT_XY);
        fmember_id = foodList.get(position).fmember_id; // 어쨰서 이렇게 받아주고 난 뒤 하지않으면 안되는거지 !?
        int numberOfLikes = foodList.get(position).numberOfLikesFromFmember_id;
        aroundFmember_id.setText(String.valueOf(fmember_id));
        numberOfLike.setText(String.valueOf(numberOfLikes));

        bt_like.setOnClickListener(new View.OnClickListener() { // 좋아요버튼 리스너 정의
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "food_id는 " + foodList.get(position).food_id + " 의 좋아요 버튼", Toast.LENGTH_SHORT).show();
                //LikeState likeState = new LikeState(String.valueOf(user_id), String.valueOf(fmember_id));
                //likeState.iconImg(flag, );
            }
        });

        bt_message.setOnClickListener(new View.OnClickListener() { // 메세지버튼 리스너 정의
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "food_id는 " + foodList.get(position).food_id + " 의 메세지 버튼", Toast.LENGTH_SHORT).show();
            }
        });

        bt_info.setOnClickListener(new View.OnClickListener() { // 자세히 버튼 리스너 정의
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "food_id는 " + foodList.get(position).food_id + " 의 자세히 버튼", Toast.LENGTH_SHORT).show();
                DetailAsync detailAsync = new DetailAsync(context);
                detailAsync.execute(food_id, user_id);
            }
        });

        around_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context.getApplicationContext(), "food_id는 " + foodList.get(position).food_id + " 입니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 현재 위치의 위도경도와 해당 마커가 표시된 위치의 위도경도를 구한뒤 둘 사이의 거리를 구하자 !
        Double cur_lati = Double.parseDouble(lati.getText().toString());
        Double cur_longi = Double.parseDouble(longi.getText().toString());

        Double dis_lati = null;
        Double dis_longi = null;
        for (int i = 0; i < 1; i++) {
            dis_lati = disList.get(position)[0];
            dis_longi = disList.get(position)[1];
        }

        Double result_dis = calDistance(cur_lati, cur_longi, dis_lati, dis_longi);
        Long final_dis = Math.round(result_dis);

        if (final_dis >= 1000) { // m로 표시해주는데 1000미터 이상이면 km단위로 변환
            around_txt_dis.setText("~ " + (final_dis / 1000) + "km");
        } else {
            around_txt_dis.setText("~ " + final_dis + "m");
        }

        return view;
    }

    public double calDistance(double lat1, double lon1, double lat2, double lon2) {

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg) {
        return (double) (deg * Math.PI / (double) 180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad) {
        return (double) (rad * (double) 180d / Math.PI);
    }
}
