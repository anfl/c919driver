package com.example.driverappcg19;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import com.example.driverappcg19.ForegroundService;
import com.example.driverappcg19.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoiceGuidanceOptions;
import com.here.android.mpa.guidance.VoicePackage;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;

import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class encapsulates the properties and functionality of the Map view.It also triggers a
 * turn-by-turn navigation from HERE Burnaby office to Langley BC.There is a sample voice skin
 * bundled within the SDK package to be used out-of-box, please refer to the Developer's guide for
 * the usage.
 */
public class MapFragmentView {
    private SupportMapFragment m_mapFragment;
    private AppCompatActivity m_activity;
    private FloatingActionButton m_naviControlButton;
    private Map m_map;
    TextToSpeech t1;
    int count=0;
    Double l1,l2;
    GeoCoordinate final_destination,source_point;
    private NavigationManager m_navigationManager;
    private GeoBoundingBox m_geoBoundingBox;
    private Route m_route;
    private boolean m_foregroundServiceStarted;
    TextView instructionText,speedText;
    PositioningManager posManager;
    boolean paused;
    public MapFragmentView(AppCompatActivity activity, Double l1, Double l2) {
        m_activity = activity;
        this.l1=l1;
        this.l2=l2;
        initMapFragment();

        t1=new TextToSpeech(m_activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

    }



    private SupportMapFragment getMapFragment() {
        instructionText=m_activity.findViewById(R.id.instruction);
        speedText=m_activity.findViewById(R.id.speed);
        return (SupportMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.mapfragment);
    }

    private void initMapFragment() {
        /* Locate the mapFragment UI element */


        m_mapFragment = getMapFragment();

        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-mapsdriver";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("intent_testt");

            System.out.println(
                    "in try"
            );
        } catch (PackageManager.NameNotFoundException e) {
            System.out.println("in caatch");
            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }

        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, "enna");
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
            System.out.println("CHECK"+"IF IN");
        } else {
            System.out.println("CHECK"+"IN");
            if (m_mapFragment != null) {
                /* Initialize the SupportMapFragment, results will be given via the called back. */
                m_mapFragment.init(new OnEngineInitListener() {
                    @Override
                    public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                        if (error == Error.NONE) {

                            posManager = PositioningManager.getInstance();
                            if (posManager != null) {
                                posManager.start(
                                        PositioningManager.LocationMethod.GPS_NETWORK);
                            }
                            m_map = m_mapFragment.getMap();

                            m_map.setTrafficInfoVisible(true);
                            posManager.addListener(
                                    new WeakReference<PositioningManager.OnPositionChangedListener>(positionListener));
                            m_map.getPositionIndicator().setVisible(true);


                            m_map.setZoomLevel(13.2);
                            m_navigationManager = NavigationManager.getInstance();

                        } else {
                            Toast.makeText(m_activity,
                                    "ERROR: Cannot initialize Map with error " + error,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
        initNaviControlButton();
    }

    private void createRoute() {
        CoreRouter coreRouter = new CoreRouter();

        RoutePlan routePlan = new RoutePlan();

        RouteOptions routeOptions = new RouteOptions();
        /* Other transport modes are also available e.g Pedestrian */
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
        /* Disable highway in this route. */
        routeOptions.setHighwaysAllowed(false);
        /* Calculate the shortest route available. */
        routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
        /* Calculate 1 route. */
        routeOptions.setRouteCount(1);
        /* Finally set the route option */
        routePlan.setRouteOptions(routeOptions);

        RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(source_point));
        /* END: Langley BC */


        RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(l1, l2));

        routePlan.addWaypoint(startPoint);
        routePlan.addWaypoint(destination);

        com.here.android.mpa.common.Image image = new com.here.android.mpa.common.Image();

        try {
            image.setImageResource(R.drawable.marker);
        } catch (IOException e) {
            e.printStackTrace();
        }

        m_map.addMapObject(new MapMarker(new GeoCoordinate(source_point),image));
        m_map.addMapObject(new MapMarker(new GeoCoordinate(l1,l2),image));


        /* Trigger the route calculation,results will be called back via the listener */
        coreRouter.calculateRoute(routePlan,
                new Router.Listener<List<RouteResult>, RoutingError>() {

                    @Override
                    public void onProgress(int i) {
                        /* The calculation progress can be retrieved in this callback. */
                    }

                    @Override
                    public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                                         RoutingError routingError) {
                        /* Calculation is done.Let's handle the result */
                        if (routingError == RoutingError.NONE) {
                            if (routeResults.get(0).getRoute() != null) {

                                m_route = routeResults.get(0).getRoute();
                                /* Create a MapRoute so that it can be placed on the map */
                                MapRoute mapRoute = new MapRoute(routeResults.get(0).getRoute());

                                /* Show the maneuver number on top of the route */
                                mapRoute.setManeuverNumberVisible(true);

                                /* Add the MapRoute to the map */
                                m_map.addMapObject(mapRoute);

                                m_geoBoundingBox = routeResults.get(0).getRoute().getBoundingBox();
                                m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE,
                                        Map.MOVE_PRESERVE_ORIENTATION);

                                startNavigation();
                            } else {
                                Toast.makeText(m_activity,
                                        "Error:route results returned is not valid",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(m_activity,
                                    "Error:route calculation returned error code: " + routingError,
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private void initNaviControlButton() {
        m_naviControlButton = (FloatingActionButton) m_activity.findViewById(R.id.naviCtrlButton);
//        m_naviControlButton.setText("start");
        m_naviControlButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                /*
                 * To start a turn-by-turn navigation, a concrete route object is required.We use
                 * the same steps from Routing sample app to create a route from 4350 Still Creek Dr
                 * to Langley BC without going on HWY.
                 *
                 * The route calculation requires local map data.Unless there is pre-downloaded map
                 * data on device by utilizing MapLoader APIs,it's not recommended to trigger the
                 * route calculation immediately after the MapEngine is initialized.The
                 * INSUFFICIENT_MAP_DATA error code may be returned by CoreRouter in this case.
                 *
                 */
                if (m_route == null) {
                    createRoute();
                } else {
                    m_navigationManager.stop();
                    /*
                     * Restore the map orientation to show entire route on screen
                     */
                    m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE, 0f);
//                    m_naviControlButton.setText("start");
                    m_route = null;
                }
            }
        });
    }

    /*
     * Android 8.0 (API level 26) limits how frequently background apps can retrieve the user's
     * current location. Apps can receive location updates only a few times each hour.
     * See href="https://developer.android.com/about/versions/oreo/background-location-limits.html
     * In order to retrieve location updates more frequently start a foreground service.
     * See https://developer.android.com/guide/components/services.html#Foreground
     */
    private void startForegroundService() {
        if (!m_foregroundServiceStarted) {
            m_foregroundServiceStarted = true;
            Intent startIntent = new Intent(m_activity, ForegroundService.class);
            startIntent.setAction(ForegroundService.START_ACTION);
            m_activity.getApplicationContext().startService(startIntent);
        }
    }

    private void stopForegroundService() {
        if (m_foregroundServiceStarted) {
            m_foregroundServiceStarted = false;
            Intent stopIntent = new Intent(m_activity, ForegroundService.class);
            stopIntent.setAction(ForegroundService.STOP_ACTION);
            m_activity.getApplicationContext().startService(stopIntent);
        }
    }

    private void startNavigation() {
        m_navigationManager.setMap(m_map);



        /* Choose navigation modes between real time navigation and simulation */
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_activity);
        alertDialogBuilder.setTitle("Navigation");
        alertDialogBuilder.setMessage("Choose Mode");
        alertDialogBuilder.setNegativeButton("Navigation",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                m_navigationManager.startNavigation(m_route);
                m_map.setTilt(60);
                startForegroundService();
            };
        });
        alertDialogBuilder.setPositiveButton("Simulation",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                m_navigationManager.simulate(m_route,40);//Simualtion speed is set to 60 m/s
                m_map.setTilt(60);
                startForegroundService();
            };
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        /*
         * Set the map update mode to ROADVIEW.This will enable the automatic map movement based on
         * the current location.If user gestures are expected during the navigation, it's
         * recommended to set the map update mode to NONE first. Other supported update mode can be
         * found in HERE Android SDK API doc
         */
        m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);

        /*
         * NavigationManager contains a number of listeners which we can use to monitor the
         * navigation status and getting relevant instructions.In this example, we will add 2
         * listeners for demo purpose,please refer to HERE Android SDK API documentation for details
         */
        addNavigationListeners();
    }

    private void addNavigationListeners() {

        /*
         * Register a NavigationManagerEventListener to monitor the status change on
         * NavigationManager
         */
        m_navigationManager.addNewInstructionEventListener(new WeakReference<NavigationManager.NewInstructionEventListener>(instlistener));

        m_navigationManager.addNavigationManagerEventListener(
                new WeakReference<NavigationManager.NavigationManagerEventListener>(
                        m_navigationManagerEventListener));

        /* Register a PositionListener to monitor the position updates */
        m_navigationManager.addPositionListener(
                new WeakReference<NavigationManager.PositionListener>(m_positionListener));
    }

    private NavigationManager.PositionListener m_positionListener = new NavigationManager.PositionListener() {
        @Override
        public void onPositionUpdated(GeoPosition position) {
            /* Current position information can be retrieved in this callback */

            FirebaseDatabase database = FirebaseDatabase.getInstance();

            Log.e( "onPositionUpdated: ",position.getTimestamp().toString() );

            speedText.setText(position.getSpeed()+"KM/Hour");
            DatabaseReference myRef = database.getReference("message");
            myRef.child("latitude").setValue(position.getCoordinate().getLatitude()+"");
            myRef.child("longitude").setValue(position.getCoordinate().getLongitude());
        }
    };

    private NavigationManager.NavigationManagerEventListener m_navigationManagerEventListener = new NavigationManager.NavigationManagerEventListener() {
        @Override
        public void onRunningStateChanged() {
//            Toast.makeText(m_activity, "Running state changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNavigationModeChanged() {
//            Toast.makeText(m_activity, "Navigation mode changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnded(NavigationManager.NavigationMode navigationMode) {
//            Toast.makeText(m_activity, navigationMode + " was ended", Toast.LENGTH_SHORT).show();
            stopForegroundService();
        }

        @Override
        public void onMapUpdateModeChanged(NavigationManager.MapUpdateMode mapUpdateMode) {
//            Toast.makeText(m_activity, "Map update mode is changed to " + mapUpdateMode,
//                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRouteUpdated(Route route) {
//            Toast.makeText(m_activity, "Route updated", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCountryInfo(String s, String s1) {
//            Toast.makeText(m_activity, "Country info updated from " + s + " to " + s1,
//                    Toast.LENGTH_SHORT).show();
        }
    };

    public void onDestroy() {
        /* Stop the navigation when app is destroyed */
        if (m_navigationManager != null) {
            stopForegroundService();
            m_navigationManager.stop();
        }
    }

    NavigationManager.NewInstructionEventListener instlistener = new NavigationManager.NewInstructionEventListener() {
        @Override
        public void onNewInstructionEvent() {
            super.onNewInstructionEvent();
            Maneuver maneuver= m_navigationManager.getNextManeuver();

            Maneuver.Turn turn = maneuver.getTurn();
            String turnName=turn.name();
            int distance = maneuver.getDistanceFromPreviousManeuver();
            String nextRoadName = maneuver.getNextRoadName();
            instructionText.setText("Take a "+turnName+"to the "+nextRoadName+" in "+distance+"mts");
            Log.e("ins",nextRoadName);
            instructionText.setText("Will take a "+turnName+"to the "+nextRoadName+" in "+distance+"mts");


            String a="Take a "+turnName+"to the "+nextRoadName+" in "+distance+"meters";
            t1.speak(a,TextToSpeech.QUEUE_FLUSH, null);

            ImageView img=m_activity.findViewById(R.id.imgview);
            if (turnName.toLowerCase().contains("right"))
                img.setImageResource(R.drawable.rightturn);
            else
                img.setImageResource(R.drawable.leftturn);

        }
    };



    private PositioningManager.OnPositionChangedListener positionListener = new
            PositioningManager.OnPositionChangedListener() {

                public void onPositionUpdated(PositioningManager.LocationMethod method,
                                              GeoPosition position, boolean isMapMatched) {
                    // set the center only when the app is in the foreground
                    // to reduce CPU consumption
                        m_map.setCenter(position.getCoordinate(),
                                Map.Animation.BOW);



                        source_point=position.getCoordinate();


                    if (count==0){
                        showRoute();
                    }
                    count++;



                }


                public void onPositionFixChanged(PositioningManager.LocationMethod method,
                                                 PositioningManager.LocationStatus status) {
                }
            };

    private void showRoute(){
        // Declare the variable (the CoreRouter)
        CoreRouter router = new CoreRouter();
        RoutePlan routePlan = new RoutePlan();
        routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(source_point)));
        routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(l1,l2)));
        router.calculateRoute(routePlan, new RouteListener());

        com.here.android.mpa.common.Image image = new com.here.android.mpa.common.Image();

        try {
            image.setImageResource(R.drawable.marker);
        } catch (IOException e) {
            e.printStackTrace();
        }

        m_map.addMapObject(new MapMarker(new GeoCoordinate(source_point),image));
        m_map.addMapObject(new MapMarker(new GeoCoordinate(l1,l2),image));


    }

    private class RouteListener implements CoreRouter.Listener {

        // Method defined in Listener
        public void onProgress(int percentage) {
            // Display a message indicating calculation progress
        }

        // Method defined in Listener
        public void onCalculateRouteFinished(List<RouteResult> routeResult, RoutingError error) {
            // If the route was calculated successfully
            if (error == RoutingError.NONE) {
                // Render the route on the map
                MapRoute mapRoute = new MapRoute(routeResult.get(0).getRoute());
                m_map.addMapObject(mapRoute);
            }
            else {
                // Display a message indicating route calculation failure
            }
        }
    }

}
