package org.foodpia.foodpiaapp.search;

/**
 * Created by Yun on 2016-06-10.
 */
public class Address {
    private String regionId;//동 ID
    private String region;//동까지 나오는 주소
    //new_//신주소
    private String new_name;//상세 주소
    private String new_ho;//호
    private String new_bunji;//길 번호
    private String new_roadName;//길 이름
    //old_//구주소
    private String old_san;//산 : Y, N
    private String old_name;//상세 주소
    private String old_ho;//호
    private String old_bunji;//번지
    private float x;//경도 좌표 (WGS84) : longtitude
    private float y;//위도 좌표 (WGS84) : latitude

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getNew_name() {
        return new_name;
    }

    public void setNew_name(String new_name) {
        this.new_name = new_name;
    }

    public String getNew_ho() {
        return new_ho;
    }

    public void setNew_ho(String new_ho) {
        this.new_ho = new_ho;
    }

    public String getNew_bunji() {
        return new_bunji;
    }

    public void setNew_bunji(String new_bunji) {
        this.new_bunji = new_bunji;
    }

    public String getNew_roadName() {
        return new_roadName;
    }

    public void setNew_roadName(String new_roadName) {
        this.new_roadName = new_roadName;
    }

    public String getOld_san() {
        return old_san;
    }

    public void setOld_san(String old_san) {
        this.old_san = old_san;
    }

    public String getOld_name() {
        return old_name;
    }

    public void setOld_name(String old_name) {
        this.old_name = old_name;
    }

    public String getOld_ho() {
        return old_ho;
    }

    public void setOld_ho(String old_ho) {
        this.old_ho = old_ho;
    }

    public String getOld_bunji() {
        return old_bunji;
    }

    public void setOld_bunji(String old_bunji) {
        this.old_bunji = old_bunji;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
