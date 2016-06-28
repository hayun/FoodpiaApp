package org.foodpia.foodpiaapp.search;

/**
 * Created by Yun on 2016-06-13.
 */
public class Food {
    public int food_id;
    public String title;
    public String content;
    public String nickname;
    public double latitude;
    public double longitude;
    public String thumbImgFileName;

    ///찬수 부분
    public String photo_id;
    public String filename;
    public int numberOfLikesFromFmember_id;

    //// 근호형 부분
    public int fmember_id;
}
