package org.foodpia.foodpiaapp.search;

import java.util.List;

/**
 * Created by Yun on 2016-06-13.
 */
public interface OnFinishSearchFoodListener {
    public void onSuccess(List<Food> foodList);
    public void onFail();
}
