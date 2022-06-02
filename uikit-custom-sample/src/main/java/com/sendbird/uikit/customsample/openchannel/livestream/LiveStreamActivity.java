package com.sendbird.uikit.customsample.openchannel.livestream;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.User;
import com.sendbird.uikit.customsample.R;
import com.sendbird.uikit.customsample.consts.StringSet;
import com.sendbird.uikit.customsample.databinding.ActivityLiveStreamBinding;
import com.sendbird.uikit.customsample.models.LiveStreamingChannelData;
import com.sendbird.uikit.fragments.OpenChannelFragment;
import com.sendbird.uikit.utils.ContextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * Displays an open channel screen used for live stream.
 */
public class LiveStreamActivity extends AppCompatActivity {
    private final String CHANNEL_HANDLER_KEY = getClass().getSimpleName() + System.currentTimeMillis();

    private ActivityLiveStreamBinding binding;
    private String creatorName;
    private String channelUrl;
    private String inputText;

    private final HideHandler hideHandler = new HideHandler(this);

    /**
     * Hides the system UI for full screen.
     */
    private static class HideHandler extends Handler {
        private final WeakReference<LiveStreamActivity> weakReference;

        public HideHandler(@NonNull LiveStreamActivity activity) {
            super(Looper.getMainLooper());
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            LiveStreamActivity activity = weakReference.get();
            if (activity != null) {
                activity.hideSystemUI();
            }
        }
    }

    @NonNull
    public static Intent newIntent(@NonNull Context context, @NonNull String channelUrl) {
        Intent intent = new Intent(context, LiveStreamActivity.class);
        intent.putExtra(StringSet.KEY_CHANNEL_URL, channelUrl);
        return intent;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(StringSet.KEY_INPUT_TEXT, inputText);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveStreamBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        addChannelHandler();
        binding.ivLive.setVisibility(View.VISIBLE);
        binding.ivLive.setOnClickListener(v -> {
            if (binding.groupLiveControl.getVisibility() == View.VISIBLE) {
                binding.groupLiveControl.setVisibility(View.GONE);
            } else {
                binding.groupLiveControl.setVisibility(View.VISIBLE);
            }
        });

        binding.ivClose.setOnClickListener(v -> finish());
        if (binding.ivChatToggle != null) {
            binding.ivChatToggle.setOnClickListener(v -> {
                if (binding.sbFragmentContainer.getVisibility() == View.GONE) {
                    binding.sbFragmentContainer.animate()
                            .setDuration(300)
                            .alpha(1.0f)
                            .translationX(0.0f)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    binding.sbFragmentContainer.setVisibility(View.VISIBLE);
                                }
                            });
                    binding.ivChatToggle.animate()
                            .setDuration(300)
                            .translationX(0.0f);
                    binding.ivChatToggle.setImageResource(R.drawable.ic_chat_hide);
                } else {
                    binding.sbFragmentContainer.animate()
                            .setDuration(300)
                            .alpha(0.0f)
                            .translationX(binding.sbFragmentContainer.getWidth())
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    binding.sbFragmentContainer.setVisibility(View.GONE);
                                }
                            });
                    binding.ivChatToggle.animate()
                            .setDuration(300)
                            .translationX(binding.sbFragmentContainer.getWidth());
                    binding.ivChatToggle.setImageResource(R.drawable.ic_chat_show);
                }
            });
        }

        channelUrl = getIntent().getStringExtra(StringSet.KEY_CHANNEL_URL);
        if (TextUtils.isEmpty(channelUrl)) {
            ContextUtils.toastError(this, R.string.sb_text_error_get_channel);
        } else {
            OpenChannel.getChannel(channelUrl, (openChannel, e) -> {
                if (e != null) {
                    return;
                }

                if (binding == null) return;
                updateParticipantCount(openChannel.getParticipantCount());
                try {
                    LiveStreamingChannelData channelData = new LiveStreamingChannelData(new JSONObject(openChannel.getData()));
                    if (channelData.getCreator() == null) {
                        creatorName = "";
                    } else {
                        creatorName = channelData.getCreator().getNickname();
                    }
                    Glide.with(binding.getRoot().getContext())
                            .load(channelData.getLiveUrl())
                            .override(binding.ivLive.getMeasuredWidth(), binding.ivLive.getHeight())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.color.background_600)
                            .into(binding.ivLive);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

                if(savedInstanceState != null) {
                    inputText = savedInstanceState.getString(StringSet.KEY_INPUT_TEXT);
                    savedInstanceState.clear();
                }

                OpenChannelFragment fragment = createOpenChannelFragment(openChannel.getUrl());
                FragmentManager manager = getSupportFragmentManager();
                manager.popBackStack();
                manager.beginTransaction()
                        .replace(R.id.sb_fragment_container, fragment)
                        .commit();
            });
        }
    }

    private void addChannelHandler() {
        SendBird.addChannelHandler(CHANNEL_HANDLER_KEY, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {

            }

            @Override
            public void onUserEntered(OpenChannel channel, User user) {
                if (channel.getUrl().equals(channelUrl)) {
                    updateParticipantCount(channel.getParticipantCount());
                }
            }

            @Override
            public void onUserExited(OpenChannel channel, User user) {
                if (channel.getUrl().equals(channelUrl)) {
                    updateParticipantCount(channel.getParticipantCount());
                }
            }
        });
    }

    private void updateParticipantCount(int count) {
        String text = String.valueOf(count);
        if (count > 1000) {
            text = String.format(Locale.US, "%.1fK", count / 1000F);
        }
        binding.tvParticipantCount.setText(String.format(getString(R.string.text_participants), text));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideHandler.removeMessages(0);
        SendBird.removeChannelHandler(CHANNEL_HANDLER_KEY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            delayedHide();
        } else {
            hideHandler.removeMessages(0);
        }
    }

    private void hideSystemUI() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LOW_PROFILE |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    private void delayedHide() {
        hideHandler.removeMessages(0);
        hideHandler.sendEmptyMessageDelayed(0, 300);
    }

    /**
     * Creates <code>OpenChannelFragment</code> with channel url.
     * <p>
     *     In preparation for screen configuration change, the value is initialized.
     * </p>
     * @param channelUrl The channel url to be applied to this screen
     * @return <code>OpenChannelFragment</code> instance
     */
    @NonNull
    protected OpenChannelFragment createOpenChannelFragment(@NonNull String channelUrl) {
        final Bundle args = new Bundle();
        args.putString("CHANNEL_URL", channelUrl);
        args.putString("DESCRIPTION", creatorName);
        args.putString("INPUT_TEXT", inputText);

        LiveStreamChannelFragment fragment = new LiveStreamChannelFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
