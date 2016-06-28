package org.foodpia.foodpiaapp.search;

import java.util.List;

public interface OnFinishSearchListener {
	public void onSuccess(List<Item> itemList);
	public void onFail();
}
