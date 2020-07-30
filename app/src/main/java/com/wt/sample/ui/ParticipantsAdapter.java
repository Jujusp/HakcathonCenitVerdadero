package com.wt.sample.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.wt.sample.R;
import com.wt.sdk.MediaConfiguration;
import com.wt.sdk.Participant;
import com.wt.sdk.VideoRenderer;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    private final List<Participant> mParticipants = new ArrayList<>();
    private final SwitchCamCallback mSwitchCamCallback;

    ParticipantsAdapter(final SwitchCamCallback callback) {
        mSwitchCamCallback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_participant, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Participant participant = mParticipants.get(viewHolder.getAdapterPosition());
        if (participant != null) {
            participant.setVideoRenderer(viewHolder.videoRenderer);
            // Attaching participant's media stream to video renderer
            participant.attachStream();
            viewHolder.mic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (participant.isAudioEnabled()) {
                        participant.disableAudio();
                    } else {
                        participant.enableAudio();
                    }
                }
            });
            viewHolder.cam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (participant.isVideoEnabled()) {
                        participant.disableVideo();
                    } else {
                        participant.enableVideo();
                    }
                }
            });

            viewHolder.mic.setBackgroundResource(participant.isAudioEnabled() ? R.drawable.ic_mic_on : R.drawable.ic_mic_off);
            viewHolder.cam.setBackgroundResource(participant.isVideoEnabled() ? R.drawable.ic_video_on : R.drawable.ic_video_off);

            viewHolder.name.setText(participant.getDisplayName());

            // Check if its local participant
            if (viewHolder.getAdapterPosition() == 0) {
                viewHolder.switchCam.setVisibility(View.VISIBLE);
                viewHolder.volumeLevel.setVisibility(View.GONE);
                viewHolder.switchCam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSwitchCamCallback != null) {
                            mSwitchCamCallback.onSwitchCameraClicked();
                        }
                    }
                });
            } else {
                viewHolder.volumeLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            participant.setVolumeLevel(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }

    void addParticipant(final Participant participant) {
        mParticipants.add(participant);
        notifyItemInserted(getItemCount());
    }

    void removeParticipant(final Participant participant) {
        final int positionToRemove = mParticipants.indexOf(participant);
        mParticipants.remove(participant);
        notifyItemRemoved(positionToRemove);
        if (participant != null) {
            participant.releaseRenderer();
        }
    }

    void addLocalParticipant(final Participant participant) {
        if (mParticipants.size() > 0) {
            removeParticipant(mParticipants.get(0));
        }
        addParticipant(participant);
    }

    void clearParticipants() {
        for (final Participant participant : mParticipants) {
            if (participant != null && participant.getVideoRenderer() != null) {
                participant.getVideoRenderer().release();
            }
        }
    }

    void updateParticipantMedia(final String participantId, final MediaConfiguration.MediaType mediaType, final MediaConfiguration.MediaState mediaState) {
        if (!mParticipants.isEmpty()) {
            for (int position = 0; position < mParticipants.size(); position++) {
                final Participant participant = mParticipants.get(position);
                if (participant != null) {
                    if (!TextUtils.isEmpty(participant.getId()) && !TextUtils.isEmpty(participantId) && participant.getId().equals(participantId)) {
                        switch (mediaType) {
                            case AUDIO:
                                participant.setAudioEnabled(mediaState);
                                break;
                            case VIDEO:
                                participant.setVideoEnabled(mediaState);
                                break;
                            default:
                                break;
                        }
                        notifyItemChanged(position, participant); // Update exactly view after its changed
                        break;
                    }
                }
            }
        }
    }

    interface SwitchCamCallback {
        void onSwitchCameraClicked();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final VideoRenderer videoRenderer;
        final AppCompatImageView mic, cam, switchCam;
        final AppCompatTextView name;
        final SeekBar volumeLevel;

        ViewHolder(View v) {
            super(v);
            videoRenderer = v.findViewById(R.id.video_renderer);
            name = v.findViewById(R.id.participant_name);
            mic = v.findViewById(R.id.mic);
            cam = v.findViewById(R.id.cam);
            switchCam = v.findViewById(R.id.switch_cam);
            volumeLevel = v.findViewById(R.id.volume_level);
        }
    }
}