package com.wt.sample.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.wt.sample.R;
import com.wt.sample.utill.Utill;
import com.wt.sdk.MediaConfiguration;
import com.wt.sdk.Participant;
import com.wt.sdk.Session;
import com.wt.sdk.SessionError;
import com.wt.sdk.SessionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SessionListener, ParticipantsAdapter.SwitchCamCallback {

    private Session mSession;
    private ParticipantsAdapter mParticipantAdapter;
    private PublisherAdapter mPublisherAdapter;
    private String mDisplayName, mSessionId, mUrl, mToken = "";
    private AppCompatTextView mAudioQos, mVideoQos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // To prevent screen to be switched off
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Extract session's params
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                mDisplayName = String.valueOf(getIntent().getExtras().get(LoginActivity.DISPLAY_NAME_CODE));
                mSessionId = String.valueOf(getIntent().getExtras().get(LoginActivity.SESSION_ID_CODE));
                mUrl = String.valueOf(getIntent().getExtras().get(LoginActivity.SESSION_URL_CODE));
                mToken = String.valueOf(getIntent().getExtras().get(LoginActivity.SESSION_TOKEN_CODE));
            }
        }
        initViewsAndListeners();
        initSession();
    }

    private void initViewsAndListeners() {
        final AppCompatButton btnStartCameraPreview = findViewById(R.id.btn_cam_preview);
        final AppCompatButton btnConnectSessionVideo = findViewById(R.id.btn_connect_session_video);
        final AppCompatButton btnConnectSessionAudio = findViewById(R.id.btn_connect_session_audio);
        final AppCompatButton btnAdjustVideoQuality = findViewById(R.id.btn_adjust_frame_rate);
        final AppCompatButton btnConnectViewer = findViewById(R.id.btn_connect_viewer);
        final AppCompatTextView txtWidth = findViewById(R.id.txt_width_value);
        final AppCompatTextView txtHeight = findViewById(R.id.txt_height_value);
        final AppCompatTextView txtFrameRate = findViewById(R.id.txt_frame_rate_value);
        mAudioQos = findViewById(R.id.txt_audio_qos_value);
        mVideoQos = findViewById(R.id.txt_video_qos_value);
        final SeekBar widthSeek = findViewById(R.id.seek_width);
        final SeekBar heightSeek = findViewById(R.id.seek_height);
        final SeekBar frameSeek = findViewById(R.id.seek_frame_rate);
        txtWidth.setText(String.valueOf(widthSeek.getProgress()));
        txtHeight.setText(String.valueOf(heightSeek.getProgress()));
        txtFrameRate.setText(String.valueOf(frameSeek.getProgress()));

        btnStartCameraPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mSession != null) {
                    btnStartCameraPreview.setVisibility(View.GONE);
                    mSession.setVideoResolution(widthSeek.getProgress(), heightSeek.getProgress());
                    mSession.setVideoFrameRate(frameSeek.getProgress());
                    mSession.startCameraPreview();
                }
            }
        });

        btnConnectSessionVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mSession != null) {
                    btnConnectSessionVideo.setVisibility(View.GONE);
                    btnConnectSessionAudio.setVisibility(View.GONE);
                    btnStartCameraPreview.setVisibility(View.GONE);
                    btnConnectViewer.setVisibility(View.GONE);
                    setSessionParams();
                    mSession.setVideoResolution(widthSeek.getProgress(), heightSeek.getProgress());
                    mSession.setVideoFrameRate(frameSeek.getProgress());
                    mSession.connect(mSessionId);
                }
            }
        });

        btnConnectSessionAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mSession != null) {
                    btnConnectSessionVideo.setVisibility(View.GONE);
                    btnConnectSessionAudio.setVisibility(View.GONE);
                    btnStartCameraPreview.setVisibility(View.GONE);
                    btnConnectViewer.setVisibility(View.GONE);
                    setSessionParams();
                    mSession.connectWithAudio(mSessionId);
                }
            }
        });

        btnAdjustVideoQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSession != null) {
                    mSession.adjustResolution(widthSeek.getProgress(), heightSeek.getProgress());
                    mSession.adjustFrameRate(frameSeek.getProgress());
                }
            }
        });

        btnConnectViewer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSession != null) {
                    btnConnectSessionVideo.setVisibility(View.GONE);
                    btnConnectSessionAudio.setVisibility(View.GONE);
                    btnStartCameraPreview.setVisibility(View.GONE);
                    btnConnectViewer.setVisibility(View.GONE);
                    setSessionParams();
                    mSession.connectAsViewer(mSessionId);
                }
            }
        });

        widthSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    progress = getResolutionProgress(progress);
                    widthSeek.setProgress(progress);
                    txtWidth.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        heightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    progress = getResolutionProgress(progress);
                    heightSeek.setProgress(progress);
                    txtHeight.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        frameSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress < 5) {
                        progress = 1;
                    } else if (progress < 10) {
                        progress = 5;
                    } else if (progress < 15) {
                        progress = 10;
                    } else if (progress < 20) {
                        progress = 15;
                    } else if (progress < 25) {
                        progress = 20;
                    } else if (progress < 30) {
                        progress = 25;
                    } else if (progress < 40) {
                        progress = 30;
                    } else if (progress < 50) {
                        progress = 40;
                    } else if (progress < 60) {
                        progress = 50;
                    } else {
                        progress = 60;
                    }
                    frameSeek.setProgress(progress);
                    txtFrameRate.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Create ParticipantsAdapter instance
        mParticipantAdapter = new ParticipantsAdapter(this);
        // Set ParticipantsAdapter to RecyclerView
        ((RecyclerView) findViewById(R.id.participants)).setAdapter(mParticipantAdapter);

        // Create PublisherAdapter instance
        mPublisherAdapter = new PublisherAdapter();
        // Set PublisherAdapter to RecyclerView
        ((RecyclerView) findViewById(R.id.publishers)).setAdapter(mPublisherAdapter);
    }

    private int getResolutionProgress(int progress) {
        if (progress < 144) {
            progress = 120;
        } else if (progress < 160) {
            progress = 144;
        } else if (progress < 176) {
            progress = 160;
        } else if (progress < 240) {
            progress = 176;
        } else if (progress < 288) {
            progress = 240;
        } else if (progress < 320) {
            progress = 288;
        } else if (progress < 352) {
            progress = 320;
        } else if (progress < 360) {
            progress = 352;
        } else if (progress < 480) {
            progress = 360;
        } else if (progress < 600) {
            progress = 480;
        } else if (progress < 640) {
            progress = 600;
        } else if (progress < 720) {
            progress = 640;
        } else if (progress < 768) {
            progress = 720;
        } else if (progress < 800) {
            progress = 768;
        } else if (progress < 1024) {
            progress = 800;
        } else if (progress < 1200) {
            progress = 1024;
        } else if (progress < 1280) {
            progress = 1200;
        } else {
            progress = 1280;
        }
        return progress;
    }

    private void initSession() {
        mSession = new Session.SessionBuilder(this)
                .build(this);
    }

    private void setSessionParams() {
        mSession.setUserName(mDisplayName);
        mSession.setUrl(mUrl);
        mSession.setToken(mToken);
        mSession.setSubscriberId("subscriber_id");
        mSession.enableStats();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnect();
    }

    @Override
    public void onBackPressed() {
        disconnect();
        super.onBackPressed();
    }

    private void disconnect() {
        clearAdapters();
        if (mSession != null) {
            mSession.disconnect();
        }
    }

    private void clearAdapters() {
        // Clear participants and disconnect from the session
        if (mParticipantAdapter != null) {
            mParticipantAdapter.clearParticipants();
        }
        if (mPublisherAdapter != null) {
            mPublisherAdapter.clearPublishers();
        }
    }

    @Override
    protected void onDestroy() {
        clearAdapters();
        super.onDestroy();
    }

    @Override
    public void onConnected(final String sessionId, final ArrayList<String> publishers) {
        Utill.showMessage(this, "Connected to session with id:\n".concat(sessionId));
        for (final String publisher : publishers) {
            mPublisherAdapter.addPublisher(publisher);
        }
    }

    @Override
    public void onOrganizerReceived() {
        // Method is triggered when user becomes an organizer of the Session.
        Utill.showMessage(this, "You got organizer session's role");
    }

    @Override
    public void onDisconnected() {
        Utill.showError(this, "Disconnected from session");
        finish();
    }

    @Override
    public void onError(final SessionError sessionError) {
        if (sessionError != null && !TextUtils.isEmpty(sessionError.getName())) {
            String errStr = sessionError.getName();
            if (!TextUtils.isEmpty(sessionError.getDescription())) {
                errStr += " " + sessionError.getDescription();
            }
            Utill.showError(this, errStr);
        }
    }

    @Override
    public void onLocalParticipantJoined(final Participant participant) {
        if (mParticipantAdapter != null) {
            mParticipantAdapter.addLocalParticipant(participant);
        }
    }

    @Override
    public void onParticipantJoined(final Participant participant) {
        if (mParticipantAdapter != null) {
            mParticipantAdapter.addParticipant(participant);
        }
        // Just to test
//        if (mSession != null) {
//            new Handler().postDelayed(() -> mSession.sendPlayerData("{'time' : '5345345345345'}"), 10000);
//        }
    }

    @Override
    public void onParticipantLeft(final Participant participant) {
        if (mParticipantAdapter != null) {
            mParticipantAdapter.removeParticipant(participant);
        }
    }

    @Override
    public void onPublisherJoined(final String name) {
        if (mPublisherAdapter != null) {
            mPublisherAdapter.addPublisher(name);
        }
    }

    @Override
    public void onPublisherLeft(final String name) {
        if (mPublisherAdapter != null) {
            mPublisherAdapter.removePublisher(name);
        }
    }

    @Override
    public void onSwitchCameraClicked() {
        if (mSession != null) {
            mSession.switchCamera();
        }
    }

    @Override
    public void onParticipantMediaStateChanged(final String participantId, final MediaConfiguration.MediaType mediaType, final MediaConfiguration.MediaState mediaState) {
        if (mParticipantAdapter != null) {
            mParticipantAdapter.updateParticipantMedia(participantId, mediaType, mediaState);
        }
    }

    @Override
    public void onStatsReceived(final JSONObject stats) {
        if (stats != null) {
            try {
                mAudioQos.setText(String.valueOf(stats.get("audioAvg")).concat(" -> ").concat(String.valueOf(stats.get("audioQosMos"))));
                mVideoQos.setText(String.valueOf(stats.get("videoAvg")).concat(" -> ").concat(String.valueOf(stats.get("videoQosMos"))));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPlayerDataReceived(final String data) {
        // Method is triggered when user gets sync notification from session's organizer.
        Utill.showMessage(this, "Player data received = ".concat(data));

        final long receivedOrganizerTime = 1589811614; // extract received time from data
        final long currentPlayerTime = 1589814614L;

        compareStreamTime(currentPlayerTime, receivedOrganizerTime);
    }

    private void compareStreamTime(final long currentTime, final long receivedOrganizerTime) {
        final long delta = receivedOrganizerTime - currentTime;
        if (Math.abs(delta) > 500) { // Check if delay more then half a second
            final float speed = defineRequiredPlayerSpeed(delta);
            if (speed == 0) {
//                seekPlayer(receivedOrganizerTime + 3000); // seek position with 3 sec more
            } else {
//                setStreamPlaybackSpeed(speed); // if player allows as ExoPlayer to slow down or increase the speed, can be more smooth then seeking on short time delays
            }
        } else {
//            setStreamPlaybackSpeed(1.0f); // Delay is acceptable set player with normal speed
        }
    }

    private float defineRequiredPlayerSpeed(final long delta) {
        if (delta > 0) {
            if (delta < 5000) {
                return 1.2f;
            } else if (delta < 10_000) {
                return 1.5f;
            } else {
                return 0;
            }
        } else {
            if (delta > -5000) {
                return 0.9f;
            } else if (delta > -10_000) {
                return 0.8f;
            } else {
                return 0.7f;
            }
        }
    }
}