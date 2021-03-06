package com.meyersj.locationsurvey.app.stops;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.meyersj.locationsurvey.app.R;
import com.meyersj.locationsurvey.app.util.Cons;


public class SelectedStops {

    private final String TAG = "SaveSelectedStops";

    private Context context;
    private StopSequenceAdapter onAdapter;
    private StopSequenceAdapter offAdapter;
    private ItemizedIconOverlay selOverlay;
    private Marker board = null;
    private Marker alight = null;
    private Marker current = null;
    private String currentType = null;
    private Drawable onIcon;
    private Drawable offIcon;
    private Drawable stopIcon;



    public SelectedStops(
            Context context, StopSequenceAdapter onAdapter, StopSequenceAdapter offAdapter,
            ItemizedIconOverlay selOverlay) {
        this.context = context;
        this.onAdapter = onAdapter;
        this.offAdapter = offAdapter;
        this.selOverlay = selOverlay;

        onIcon = context.getResources().getDrawable(R.drawable.transit_green_40);
        offIcon = context.getResources().getDrawable(R.drawable.transit_red_40);
        stopIcon = context.getResources().getDrawable(R.drawable.circle_filled_black_30);


    }


    public void setOnAdapter(StopSequenceAdapter onAdapter) {
        this.onAdapter = onAdapter;
    }

    public void setOffAdapter(StopSequenceAdapter offAdapter) {
        this.offAdapter = offAdapter;
    }

    public String getCurrentType() {
        return this.currentType;
    }

    public void setCurrentMarker(Marker current, String currentType) {
        this.current = current;
        this.currentType = currentType;
    }

    private void refreshOverlay() {
        selOverlay.removeAllItems();

        if (board != null) {
            selOverlay.addItem(board);
        }
        if (alight != null) {
            selOverlay.addItem(alight);
        }
    }

    protected void saveCurrentMarker(Marker marker) {

        if (currentType != null) {

            //if board or alight marker is already set switch it back to default icon
            if (alight != null && alight == current) {
                alight.setMarker(stopIcon);
                alight = null;
            }
            if (board != null && board == current) {
                board.setMarker(stopIcon);
                board = null;
            }

            if (currentType.equals(Cons.BOARD)) {
                if(board != null) {
                    board.setMarker(stopIcon);
                }
                board = current;
                board.setMarker(onIcon);
                Log.d(TAG, Cons.BOARD + ": " + board.getTitle());
                Log.d(TAG, String.valueOf(onAdapter.getItemIndex(board.getTitle())));
                onAdapter.setSelectedIndex(onAdapter.getItemIndex(board.getTitle()));

            }
            else {
                if (alight != null) {
                    alight.setMarker(stopIcon);
                }
                alight = current;
                alight.setMarker(offIcon);
                Log.d(TAG, Cons.ALIGHT + ": " + alight.getTitle());
                offAdapter.setSelectedIndex(offAdapter.getItemIndex(alight.getTitle()));

            }
            clearCurrentMarker();
            refreshOverlay();
        }
    }

    public void clearSequenceMarker(String mode) {
        if (mode.equals(Cons.BOARD)) {
            if(board != null) {
                board.setMarker(stopIcon);
                board = null;
            }
        }
        else {
            if(alight != null) {
                alight.setMarker(stopIcon);
                alight = null;
            }
        }
        refreshOverlay();
    }

    public void saveSequenceMarker(String mode, Marker newMarker) {

        if (mode.equals(Cons.BOARD)) {
            if (board != null) {
                board.setMarker(stopIcon);
            }
            if(newMarker == alight) {
                alight.setMarker(stopIcon);
            }
            board = newMarker;
            board.setMarker(onIcon);
        }
        else {
            if (alight != null) {
                alight.setMarker(stopIcon);
            }
            if(newMarker == board) {
                board.setMarker(stopIcon);
            }
            alight = newMarker;
            alight.setMarker(offIcon);
        }

        refreshOverlay();
    }

    public void clearCurrentMarker() {
        this.current = null;
        this.currentType = null;
    }

    public boolean validateSelection() {
        if (board == null || alight == null) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validateStopSequence() {
        Stop onStop = (Stop) board;
        Stop offStop = (Stop) alight;

        if ( onStop.compareSeq(offStop) < 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public Marker getBoard() {
        return board;
    }

    public Marker getAlight() {
        return alight;
    }

}
