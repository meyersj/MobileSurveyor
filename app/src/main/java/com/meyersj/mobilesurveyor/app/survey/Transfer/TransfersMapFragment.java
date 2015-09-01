package com.meyersj.mobilesurveyor.app.survey.Transfer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mapbox.mapboxsdk.overlay.ItemizedIconOverlay;
import com.mapbox.mapboxsdk.overlay.Marker;
import com.mapbox.mapboxsdk.views.MapView;
import com.meyersj.mobilesurveyor.app.R;
import com.meyersj.mobilesurveyor.app.survey.MapFragment;
import com.meyersj.mobilesurveyor.app.survey.SurveyManager;
import com.meyersj.mobilesurveyor.app.util.Cons;
import com.meyersj.mobilesurveyor.app.util.DataLoader;

import java.util.ArrayList;


public class TransfersMapFragment extends MapFragment {

    protected SurveyManager manager;
    protected LinearLayout routesLayout;
    protected Button transfersBtn;
    protected ArrayList<String> routesList;
    protected ViewPager pager;
    protected Bundle extras;
    protected String[] selectedRoutes;
    protected String[] selectedDirections;

    public void initialize(SurveyManager manager, ViewPager pager, Bundle extras) {
        this.manager = manager;
        this.pager = pager;
        this.extras = extras;
        this.line = extras != null ? extras.getString(Cons.LINE, Cons.DEFAULT_RTE) : Cons.DEFAULT_RTE;
        this.dir = extras != null ? extras.getString(Cons.DIR, Cons.DEFAULT_DIR) : Cons.DEFAULT_DIR;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_transfers_map, container, false);
        activity = getActivity();
        context = activity.getApplicationContext();
        routesLayout = (LinearLayout) view.findViewById(R.id.routes_list_layout);
        mv = (MapView) view.findViewById(R.id.mapview);
        setTiles(mv);
        transfersBtn = (Button) view.findViewById(R.id.transfers_btn);
        routesList = DataLoader.getRoutes(context);
        String[] rte = new String[] {line, dir};
        ArrayList<String> defaultRoutesList = (ArrayList<String>) routesList.clone();
        defaultRoutesList.add(0, "");


        //selectedRoutes[0] = rte[0];
        //selectedDirections[0] = rte[1];

        // make corrections if previous route information is known
        /*
        if(extras != null) {
            String key;
            for(int i = 0; i < 5; i++) {
                key = "route" + Integer.toString(i+1);
                String route = extras.getString(key, "");
                if(!route.isEmpty()) {
                    String[] split = route.split("-");
                    Log.d(TAG, split.toString());
                    selectedRoutes[i] = split[0];
                    selectedDirections[i] = split[1];
                }
            }
        }
        */

        selectedRoutes = manager.getTransfersRoutes();
        selectedDirections = manager.getTransfersDirections();

        //TODO create a factory to make RoutePicker objects and set Next/Previous
        final RoutePicker rp1 = new RoutePicker(this, activity, inflater, container,
                routesLayout, routesList, selectedRoutes[0], true, 1, selectedRoutes, selectedDirections, rte, manager);
        final RoutePicker rp2 = new RoutePicker(this, activity, inflater, container,
                routesLayout, defaultRoutesList, selectedRoutes[1], false, 2, selectedRoutes, selectedDirections, rte, manager);
        final RoutePicker rp3 = new RoutePicker(this, activity, inflater, container,
                routesLayout, defaultRoutesList, selectedRoutes[2], false, 3, selectedRoutes, selectedDirections, rte, manager);
        final RoutePicker rp4 = new RoutePicker(this, activity, inflater, container,
                routesLayout, defaultRoutesList, selectedRoutes[3], false, 4, selectedRoutes, selectedDirections, rte, manager);
        final RoutePicker rp5 = new RoutePicker(this, activity, inflater, container,
                routesLayout, defaultRoutesList, selectedRoutes[4], false, 5, selectedRoutes, selectedDirections, rte, manager);

        rp1.setNext(rp2);
        rp2.setNext(rp3);
        rp3.setNext(rp4);
        rp4.setNext(rp5);
        rp5.setNext(null);

        rp1.setPrevious(null);
        rp2.setPrevious(rp1);
        rp3.setPrevious(rp2);
        rp4.setPrevious(rp3);
        rp5.setPrevious(rp4);

        changeListVisibility(routesLayout.getVisibility());

        transfersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeListVisibility(routesLayout.getVisibility());
            }
        });

        updateView(manager);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //toggle visibility of list depending on current visibility
    private void changeListVisibility(int currentVisibility) {
        if (currentVisibility == View.INVISIBLE) {
            routesLayout.setVisibility(View.VISIBLE);
            transfersBtn.setBackground(
                    context.getResources().getDrawable(R.drawable.shape_rect_grey_fade_round_top));
        }
        else {
            routesLayout.setVisibility(View.INVISIBLE);
            transfersBtn.setBackground(
                    context.getResources().getDrawable(R.drawable.shape_rect_grey_fade_round_all));
        }
    }

    public void updateView(SurveyManager manager) {
        mv.removeOverlay(surveyOverlay);
        surveyOverlay.removeAllItems();
        addItem(surveyOverlay, manager.getOrig());
        addItem(surveyOverlay, manager.getDest());
        addItem(surveyOverlay, manager.getOnStop());
        addItem(surveyOverlay, manager.getOffStop());
        mv.addItemizedOverlay(surveyOverlay);
    }

    public void addItem(ItemizedIconOverlay overlay, Marker item) {
        if(item != null && overlay != null)
            overlay.addItem(item);
    }
}
