package com.sendbird.uikit.customsample.openchannel.livestream;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sendbird.android.channel.OpenChannel;
import com.sendbird.uikit.customsample.R;
import com.sendbird.uikit.customsample.databinding.ViewLiveStreamListItemBinding;
import com.sendbird.uikit.customsample.models.LiveStreamingChannelData;
import com.sendbird.uikit.customsample.openchannel.OpenChannelListAdapter;
import com.sendbird.uikit.customsample.openchannel.OpenChannelListViewHolder;
import com.sendbird.uikit.customsample.utils.DrawableUtils;
import com.sendbird.uikit.interfaces.UserInfo;
import com.sendbird.uikit.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * RecyclerView adapter for <code>OpenChannel</code> list used for live stream.
 */
public class LiveStreamListAdapter extends OpenChannelListAdapter<LiveStreamListAdapter.LiveStreamingListViewHolder> {
    @NonNull
    @Override
    public LiveStreamingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewLiveStreamListItemBinding binding = ViewLiveStreamListItemBinding.inflate(inflater, parent, false);
        return new LiveStreamingListViewHolder(binding);
    }

    static class LiveStreamingListViewHolder extends OpenChannelListViewHolder {
        private final ViewLiveStreamListItemBinding binding;

        public LiveStreamingListViewHolder(@NonNull ViewLiveStreamListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        protected void bind(OpenChannel openChannel) {
            if (openChannel == null) return;
            int count = openChannel.getParticipantCount();
            String text = String.valueOf(count);
            if (count > 1000) {
                text = String.format(Locale.US, "%.1fK", count / 1000F);
            }
            binding.tvParticipantCount.setText(text);

            try {
                LiveStreamingChannelData channelData = new LiveStreamingChannelData(new JSONObject(openChannel.getData()));

                binding.tvLiveTitle.setVisibility(View.VISIBLE);
                binding.tvLiveTitle.setText(channelData.getName());

                UserInfo creatorInfo = channelData.getCreator();
                if (creatorInfo == null || TextUtils.isEmpty(creatorInfo.getNickname())) {
                    binding.tvCreator.setVisibility(View.GONE);
                } else {
                    binding.tvCreator.setVisibility(View.VISIBLE);
                    binding.tvCreator.setText(creatorInfo.getNickname());
                }

                if (channelData.getTags() == null || TextUtils.isEmpty(channelData.getTags().get(0))) {
                    binding.tvBadge.setVisibility(View.GONE);
                } else {
                    binding.tvBadge.setVisibility(View.VISIBLE);
                    binding.tvBadge.setText(channelData.getTags().get(0));
                }

                Glide.with(binding.getRoot().getContext())
                        .load(channelData.getLiveUrl())
                        .override(binding.ivLiveThumbnail.getWidth(), binding.ivLiveThumbnail.getHeight())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.color.background_600)
                        .into(binding.ivLiveThumbnail);

                binding.ivChannelThumbnail.setVisibility(View.VISIBLE);

                Drawable errorIcon = DrawableUtils.createOvalIcon(binding.getRoot().getContext(),
                        R.color.background_300, R.drawable.icon_channels, R.color.ondark_01);
                Glide.with(binding.getRoot().getContext())
                        .load(channelData.getThumbnailUrl())
                        .override(binding.ivChannelThumbnail.getWidth(), binding.ivChannelThumbnail.getHeight())
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(errorIcon)
                        .into(binding.ivChannelThumbnail);
            } catch (JSONException e) {
                e.printStackTrace();
                binding.ivLiveThumbnail.setImageDrawable(null);
                binding.ivChannelThumbnail.setVisibility(View.GONE);
                binding.tvLiveTitle.setVisibility(View.GONE);
                binding.tvBadge.setVisibility(View.GONE);
                binding.tvCreator.setVisibility(View.GONE);
            }
        }
    }
}
