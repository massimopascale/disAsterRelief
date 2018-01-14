/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ar.core.examples.java.helloar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.Camera;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.Trackable.TrackingState;
import com.google.ar.core.examples.java.helloar.rendering.BackgroundRenderer;
import com.google.ar.core.examples.java.helloar.rendering.ObjectRenderer;
import com.google.ar.core.examples.java.helloar.rendering.ObjectRenderer.BlendMode;
import com.google.ar.core.examples.java.helloar.rendering.PlaneRenderer;
import com.google.ar.core.examples.java.helloar.rendering.PointCloudRenderer;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore API. The application will display any detected planes and will allow the user to tap on a
 * plane to place a 3d model of the Android robot.
 */
public class HelloArActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    private static final String TAG = HelloArActivity.class.getSimpleName();

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView mSurfaceView;

    private Session mSession;
    private GestureDetector mGestureDetector;
    private Snackbar mMessageSnackbar;
    private DisplayRotationHelper mDisplayRotationHelper;
    private SensorManager mSensorManager;
    private SensorEventListener senseListen;
    private float[] B;

    private final BackgroundRenderer mBackgroundRenderer = new BackgroundRenderer();
    private final ObjectRenderer mVirtualObjectGreen = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectRed = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectYellow = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectBlack = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectMatt = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectMassimo = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectElliot = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectAlex = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectJake = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectZero = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectOne = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectTwo = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectThree = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectFour = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectFive = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectSix = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectSeven = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectEight = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectNine = new ObjectRenderer();
    private final ObjectRenderer mVirtualObjectM = new ObjectRenderer();
    private final PlaneRenderer mPlaneRenderer = new PlaneRenderer();
    private final PointCloudRenderer mPointCloud = new PointCloudRenderer();

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] mAnchorMatrix = new float[16];

    // Tap handling and UI.
    private final ArrayBlockingQueue<MotionEvent> mQueuedSingleTaps = new ArrayBlockingQueue<>(16);
    private final ArrayList<Anchor> mAnchors = new ArrayList<>();
    private final ArrayList<Pin> mPins = new ArrayList<>();

    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSurfaceView = findViewById(R.id.surfaceview);
        mDisplayRotationHelper = new DisplayRotationHelper(/*context=*/ this);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        senseListen = new MagneticField();
        // Set up tap listener.
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                onSingleTap(e);
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

        // Set up renderer.
        mSurfaceView.setPreserveEGLContextOnPause(true);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        mSurfaceView.setRenderer(this);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        Exception exception = null;
        String message = null;
        try {
            mSession = new Session(/* context= */ this);
        } catch (UnavailableArcoreNotInstalledException e) {
            message = "Please install ARCore";
            exception = e;
        } catch (UnavailableApkTooOldException e) {
            message = "Please update ARCore";
            exception = e;
        } catch (UnavailableSdkTooOldException e) {
            message = "Please update this app";
            exception = e;
        } catch (Exception e) {
            message = "This device does not support AR";
            exception = e;
        }

        if (message != null) {
            showSnackbarMessage(message, true);
            Log.e(TAG, "Exception creating session", exception);
            return;
        }

        // Create default config and check if supported.
        Config config = new Config(mSession);
        if (!mSession.isSupported(config)) {
            showSnackbarMessage("This device does not support AR", true);
        }
        mSession.configure(config);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ARCore requires camera permissions to operate. If we did not yet obtain runtime
        // permission on Android M and above, now is a good time to ask the user for it.
        if (CameraPermissionHelper.hasCameraPermission(this)) {
            if (mSession != null) {
                showLoadingMessage();
                // Note that order matters - see the note in onPause(), the reverse applies here.
                mSession.resume();
            }
            mSurfaceView.onResume();
            mDisplayRotationHelper.onResume();
        } else {
            CameraPermissionHelper.requestCameraPermission(this);
        }
        mSensorManager.registerListener(senseListen, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Note that the order matters - GLSurfaceView is paused first so that it does not try
        // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
        // still call mSession.update() and get a SessionPausedException.
        mDisplayRotationHelper.onPause();
        mSurfaceView.onPause();
        if (mSession != null) {
            mSession.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this,
                "Camera permission is needed to run this application", Toast.LENGTH_LONG).show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            // Standard Android full-screen functionality.
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void onSingleTap(MotionEvent e) {
        // Queue tap if there is space. Tap is lost if queue is full.
        mQueuedSingleTaps.offer(e);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Create the texture and pass it to ARCore session to be filled during update().
        mBackgroundRenderer.createOnGlThread(/*context=*/ this);
        if (mSession != null) {
            mSession.setCameraTextureName(mBackgroundRenderer.getTextureId());
        }

        // Prepare the other rendering objects.
        try {

            mVirtualObjectBlack.createOnGlThread(/*context=*/this, "Marker.obj", "markerBlack.png");
            mVirtualObjectBlack.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectGreen.createOnGlThread(/*context=*/this, "Marker.obj", "markerGreen.png");
            mVirtualObjectGreen.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectRed.createOnGlThread(/*context=*/this, "Marker.obj", "markerRed.png");
            mVirtualObjectRed.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectYellow.createOnGlThread(/*context=*/this, "Marker.obj", "markerYellow.png");
            mVirtualObjectYellow.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            mVirtualObjectMatt.createOnGlThread(/*context=*/this, "CubeFloat.obj", "MattCube.png");
            mVirtualObjectMatt.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectMassimo.createOnGlThread(/*context=*/this, "CubeFloat.obj", "MassimoCube.png");
            mVirtualObjectMassimo.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectElliot.createOnGlThread(/*context=*/this, "CubeFloat.obj", "ElliotCube.png");
            mVirtualObjectElliot.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectAlex.createOnGlThread(/*context=*/this, "CubeFloat.obj", "AlexCube.png");
            mVirtualObjectAlex.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectJake.createOnGlThread(/*context=*/this, "CubeFloat.obj", "JakeCube.png");
            mVirtualObjectJake.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

            mVirtualObjectZero.createOnGlThread(/*context=*/this, "Billboard.obj", "Zero.png");
            mVirtualObjectZero.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectOne.createOnGlThread(/*context=*/this, "Billboard.obj", "One.png");
            mVirtualObjectOne.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectTwo.createOnGlThread(/*context=*/this, "Billboard.obj", "Two.png");
            mVirtualObjectTwo.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectThree.createOnGlThread(/*context=*/this, "Billboard.obj", "Three.png");
            mVirtualObjectThree.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectFour.createOnGlThread(/*context=*/this, "Billboard.obj", "Four.png");
            mVirtualObjectFour.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectFive.createOnGlThread(/*context=*/this, "Billboard.obj", "Five.png");
            mVirtualObjectFive.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectSix.createOnGlThread(/*context=*/this, "Billboard.obj", "Six.png");
            mVirtualObjectSix.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectSeven.createOnGlThread(/*context=*/this, "Billboard.obj", "Seven.png");
            mVirtualObjectSeven.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectEight.createOnGlThread(/*context=*/this, "Billboard.obj", "Eight.png");
            mVirtualObjectEight.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectNine.createOnGlThread(/*context=*/this, "Billboard.obj", "Nine.png");
            mVirtualObjectNine.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);
            mVirtualObjectM.createOnGlThread(/*context=*/this, "Billboard.obj", "Meters.png");
            mVirtualObjectM.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f);

        } catch (IOException e) {
            Log.e(TAG, "Failed to read obj file");
        }
        try {
            mPlaneRenderer.createOnGlThread(/*context=*/this, "trigrid.png");
        } catch (IOException e) {
            Log.e(TAG, "Failed to read plane texture");
        }
        mPointCloud.createOnGlThread(/*context=*/this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDisplayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mSession == null) {
            return;
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        mDisplayRotationHelper.updateSessionIfNeeded(mSession);

        try {
            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = mSession.update();
            Camera camera = frame.getCamera();
            // Handle taps. Handling only one tap per frame, as taps are usually low frequency
            // compared to frame rate.
            /**MotionEvent tap = mQueuedSingleTaps.poll();
            if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {
                for (HitResult hit : frame.hitTest(tap)) {
                    // Check if any plane was hit, and if it was hit inside the plane polygon
                    Trackable trackable = hit.getTrackable();
                    if (trackable instanceof Plane
                            && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                        // Cap the number of objects created. This avoids overloading both the
                        // rendering system and ARCore.
                        if (mAnchors.size() >= 20) {
                            mAnchors.get(0).detach();
                            mAnchors.remove(0);
                        }
                        // Adding an Anchor tells ARCore that it should track this position in
                        // space. This anchor is created on the Plane to place the 3d model
                        // in the correct position relative both to the world and to the plane.
                        //mAnchors.add(hit.createAnchor());

                        // Hits are sorted by depth. Consider only closest hit on a plane.
                        break;
                    }
                }
            } **/

            // Draw background.
            mBackgroundRenderer.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                return;
            }

            // Get projection matrix.
            float[] projmtx = new float[16];
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

            // Get camera matrix and draw.
            float[] viewmtx = new float[16];
            camera.getViewMatrix(viewmtx, 0);

            // Compute lighting from average intensity of the image.
            final float lightIntensity = frame.getLightEstimate().getPixelIntensity();

            // Visualize tracked points.
            PointCloud pointCloud = frame.acquirePointCloud();
            mPointCloud.update(pointCloud);
            //mPointCloud.draw(viewmtx, projmtx);

            // Application is responsible for releasing the point cloud resources after
            // using it.
            pointCloud.release();

            // Check if we detected at least one plane. If so, hide the loading message.
            if (mMessageSnackbar != null) {
                for (Plane plane : mSession.getAllTrackables(Plane.class)) {
                    if (plane.getType() == com.google.ar.core.Plane.Type.HORIZONTAL_UPWARD_FACING
                            && plane.getTrackingState() == TrackingState.TRACKING) {
                        hideLoadingMessage();

                        double[] location = getMyLocation();
                        Population populus = new Population(5, location);
                        double[] n = new double[3];
                        float[] xAxis = camera.getPose().getXAxis();
                        float[] yAxis = camera.getPose().getYAxis();
                        float[] zAxis = camera.getPose().getZAxis();
                        n[0] = xAxis[0]*B[0] + yAxis[0]*B[1] + zAxis[0]*B[2];
                        n[1] = 0;
                        n[2] = xAxis[2]*B[0] + yAxis[2]*B[1] + zAxis[2]*B[2];
                        n[0] = n[0]/Math.sqrt(n[0]*n[0] + n[2]*n[2]);
                        n[2] = n[2]/Math.sqrt(n[0]*n[0] + n[2]*n[2]);
                        System.out.print(n[0]);
                        System.out.println(n[2]);
                        List<Person> people = populus.getPopulation();
                        for(Person p : people) {
                            float x = (float) p.getX(n, location);
                            float z = (float) p.getZ(n, location);
                            Anchor temp = mSession.createAnchor(Pose.makeTranslation(x,0,z));
                            mAnchors.add(temp);
                            Anchor bill1= mSession.createAnchor(Pose.makeTranslation(x,2,z));
                            Anchor bill2 = mSession.createAnchor(Pose.makeTranslation(x+ (float).33,2,z));
                            Anchor bill3 = mSession.createAnchor(Pose.makeTranslation(x+(float).66,2,z));
                            Pin temppin = new Pin(p,temp, bill1,bill2,bill3);
                            mPins.add(temppin);
                        }

                        break;
                    }
                }
            }

            // Visualize planes.
            //mPlaneRenderer.drawPlanes(mSession.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);

            // Visualize anchors created by touch.
            float scaleFactor = 1.0f;
            for (Pin pin : mPins) {
                Anchor anchor = pin.getAnchor();
                Person person = pin.getPerson();
                if (anchor.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }
                // Get the current pose of an Anchor in world space. The Anchor pose is updated
                // during calls to session.update() as ARCore refines its estimate of the world.
                anchor.getPose().toMatrix(mAnchorMatrix, 0);

                // Update and draw the model. Switch based on person status so model can be different colors
                if(person.getStatus() == 0) {
                        mVirtualObjectBlack.updateModelMatrix(mAnchorMatrix, 2*scaleFactor);
                        mVirtualObjectBlack.draw(viewmtx, projmtx, lightIntensity);
                    }
                else if(person.getStatus() == 1) {
                        mVirtualObjectRed.updateModelMatrix(mAnchorMatrix, 2*scaleFactor);
                        mVirtualObjectRed.draw(viewmtx, projmtx, lightIntensity);
                    }
                else if(person.getStatus() == 2) {
                        mVirtualObjectYellow.updateModelMatrix(mAnchorMatrix, 2*scaleFactor);
                        mVirtualObjectYellow.draw(viewmtx, projmtx, lightIntensity);
                    }
                else if(person.getStatus() == 3) {
                    mVirtualObjectGreen.updateModelMatrix(mAnchorMatrix, 2*scaleFactor);
                    mVirtualObjectGreen.draw(viewmtx, projmtx, lightIntensity);
                }
                //now draw cube with name, age, heartrate info
                if(person.getName().equals("Matt")) {
                    mVirtualObjectMatt.updateModelMatrix(mAnchorMatrix, scaleFactor);
                    mVirtualObjectMatt.draw(viewmtx, projmtx, lightIntensity);
                }
                else if(person.getName().equals("Massimo")) {
                    mVirtualObjectMassimo.updateModelMatrix(mAnchorMatrix, scaleFactor);
                    mVirtualObjectMassimo.draw(viewmtx, projmtx, lightIntensity);
                }
                else if(person.getName().equals("Elliot")) {
                    mVirtualObjectElliot.updateModelMatrix(mAnchorMatrix, scaleFactor);
                    mVirtualObjectElliot.draw(viewmtx, projmtx, lightIntensity);
                }
                else if(person.getName().equals("Alex")) {
                    mVirtualObjectAlex.updateModelMatrix(mAnchorMatrix, scaleFactor);
                    mVirtualObjectAlex.draw(viewmtx, projmtx, lightIntensity);
                }
                else if(person.getName().equals("Jake")) {
                    mVirtualObjectJake.updateModelMatrix(mAnchorMatrix, scaleFactor);
                    mVirtualObjectJake.draw(viewmtx, projmtx, lightIntensity);
                }

                //make two numbers side by side of testing purposes
                float[] cam = new float[3];
                camera.getPose().getTranslation(cam, 0);
                float[] per = new float[3];
                anchor.getPose().getTranslation(per, 0);
                int dx = Math.round(cam[0]-per[0]);
                int dz = Math.round(cam[2]-per[2]);
                int dist = (int) Math.sqrt(dx*dx + dz*dz);
                if(dist == 0) {
                    System.out.println("Distance is Zero, Prob Casting Issue");
                }
                int firstDigit = dist/10;
                int sDigit = dist%10;

                List<Anchor> bills = pin.getBillboardList();
                int count = 0;
                for(Anchor bill: bills) {
                    if (bill.getTrackingState() != TrackingState.TRACKING) {
                        continue;
                    }
                    bill.getPose().toMatrix(mAnchorMatrix, 0);
                    if (count == 0) {
                        if(firstDigit == 0) {
                            mVirtualObjectZero.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectZero.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(firstDigit == 1) {
                            mVirtualObjectOne.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectOne.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(firstDigit == 2) {
                            mVirtualObjectTwo.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectTwo.draw(viewmtx, projmtx, lightIntensity);
                        }
                    }
                    else if (count == 1) {
                        if(sDigit == 0) {
                            mVirtualObjectZero.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectZero.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(sDigit == 1) {
                            mVirtualObjectOne.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectOne.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(sDigit == 2) {
                            mVirtualObjectTwo.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectTwo.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(sDigit == 3) {
                            mVirtualObjectThree.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectThree.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(sDigit == 4) {
                            mVirtualObjectFour.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectFour.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(sDigit == 5) {
                            mVirtualObjectFive.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectFive.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(sDigit == 6) {
                            mVirtualObjectSix.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectSix.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(sDigit == 7) {
                            mVirtualObjectSeven.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectSeven.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(sDigit == 8) {
                            mVirtualObjectEight.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectEight.draw(viewmtx, projmtx, lightIntensity);
                        }
                        if(sDigit == 9) {
                            mVirtualObjectNine.updateModelMatrix(mAnchorMatrix, scaleFactor);
                            mVirtualObjectNine.draw(viewmtx, projmtx, lightIntensity);
                        }
                    }
                    else {
                        mVirtualObjectM.updateModelMatrix(mAnchorMatrix, scaleFactor);
                        mVirtualObjectM.draw(viewmtx, projmtx, lightIntensity);
                    }
                    count++;
                }

            }

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }

    private void showSnackbarMessage(String message, boolean finishOnDismiss) {
        mMessageSnackbar = Snackbar.make(
            HelloArActivity.this.findViewById(android.R.id.content),
            message, Snackbar.LENGTH_INDEFINITE);
        mMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        if (finishOnDismiss) {
            mMessageSnackbar.setAction(
                "Dismiss",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMessageSnackbar.dismiss();
                    }
                });
            mMessageSnackbar.addCallback(
                new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        finish();
                    }
                });
        }
        mMessageSnackbar.show();
    }

    private void showLoadingMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showSnackbarMessage("Searching for surfaces...", false);
            }
        });
    }

    private void hideLoadingMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMessageSnackbar != null) {
                    mMessageSnackbar.dismiss();
                }
                mMessageSnackbar = null;
            }
        });
    }
    //method uses the phones gps to get a location in terms of latitude (0th element) and longitude (1st element)
    public double[] getMyLocation() {
        double[] loc = new double[2];
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else {

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                loc[0] = lat;
                loc[1] = lon;
            }
        }
        return loc;
    }

    //private inner class for sensor event listener
    private class MagneticField implements SensorEventListener {


        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            B = sensorEvent.values;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}
