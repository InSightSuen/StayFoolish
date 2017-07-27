package com.insightsuen.stayfoolish.ui.drag;

/**
 * Created by InSight Suen on 2017/7/17.
 */
public interface ItemTouchCallbacks {

    void onItemMove(int formPosition, int toPosition);

    void onItemDismiss(int position);

}
