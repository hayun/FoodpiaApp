package org.foodpia.foodpiaapp.aroundlist;


import org.foodpia.foodpiaapp.search.Food;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thnt on 2016-06-17.
 */
public interface OnAroundListRequestListener {
    public void success(List<Food> list);
    public void fail();
}
