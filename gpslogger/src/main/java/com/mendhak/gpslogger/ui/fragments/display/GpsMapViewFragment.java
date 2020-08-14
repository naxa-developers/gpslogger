/*
 * Copyright (C) 2016 mendhak
 *
 * This file is part of GPSLogger for Android.
 *
 * GPSLogger for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * GPSLogger for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSLogger for Android.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mendhak.gpslogger.ui.fragments.display;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mendhak.gpslogger.R;
import com.mendhak.gpslogger.common.EventBusHook;
import com.mendhak.gpslogger.common.Session;
import com.mendhak.gpslogger.common.events.ServiceEvents;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.TilesOverlay;


public class GpsMapViewFragment extends GenericViewFragment implements View.OnClickListener {

    View rootView;
    private MapView mMapView;
    private Context mContext;
    private Session session = Session.getInstance();

    public static GpsMapViewFragment newInstance() {
        GpsMapViewFragment fragment = new GpsMapViewFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        final LinearLayout mapContainer = rootView.findViewById(R.id.map_container);

        mMapView = new MapView(mContext);
        // mMapView.setTilesScaledToDpi(true);
        mapContainer.addView(this.mMapView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        mMapView.getZoomController().setVisibility(
                CustomZoomButtonsController.Visibility.NEVER);
        mMapView.setMultiTouchControls(true);

        // zoom to the nepal
        mMapView.getController().setZoom(18.);
        mMapView.getController().setCenter(new GeoPoint(28.11069, 84.10040));

        // Add tiles layer
        MapTileProviderBasic provider = new MapTileProviderBasic(getActivity().getApplicationContext());
        provider.setTileSource(TileSourceFactory.MAPNIK);
        TilesOverlay tilesOverlay = new TilesOverlay(provider, getActivity().getBaseContext());
        tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
        mMapView.getOverlays().add(tilesOverlay);

        return rootView;
    }
    @EventBusHook
    public void onEventMainThread(ServiceEvents.LocationUpdate locationUpdate){
        displayLocationOnMap(locationUpdate.location);
    }

    public void displayLocationOnMap(Location locationInfo){

        if (locationInfo != null) {
            Marker m = new Marker(mMapView);
            m.setPosition(new GeoPoint(locationInfo.getLatitude(),locationInfo.getLongitude()));
            m.setTextLabelBackgroundColor(
                    Color.TRANSPARENT
            );
            m.setTextLabelForegroundColor(
                    Color.RED
            );
            m.setTextLabelFontSize(40);
            m.setTextIcon("+");
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);
            mMapView.getOverlays()
                    .add(m);
            mMapView.getController().setCenter(new GeoPoint(locationInfo.getLatitude(),locationInfo.getLongitude()));
            Toast.makeText(getActivity().getApplicationContext(), "Point Recorded \n"+"Lat: "+locationInfo.getLatitude()+"\n Long: "+locationInfo.getLongitude(), Toast.LENGTH_SHORT).show();
            mMapView.invalidate();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }
    @Override
    public void onClick(View v) {

    }
}
