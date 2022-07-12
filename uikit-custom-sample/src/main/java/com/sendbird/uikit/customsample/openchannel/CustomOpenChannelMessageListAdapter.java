package com.sendbird.uikit.customsample.openchannel;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.sendbird.android.channel.OpenChannel;
import com.sendbird.android.message.BaseMessage;
import com.sendbird.android.message.UserMessage;
import com.sendbird.uikit.activities.adapter.OpenChannelMessageListAdapter;
import com.sendbird.uikit.activities.viewholder.MessageViewHolder;
import com.sendbird.uikit.customsample.consts.StringSet;
import com.sendbird.uikit.customsample.databinding.ViewOpenChannelHighlightMessageHolderBinding;
import com.sendbird.uikit.utils.MessageUtils;

/**
 * Implements the customized <code>OpenChannelMessageListAdapter</code> to adapt the customized message items.
 */
public class CustomOpenChannelMessageListAdapter extends OpenChannelMessageListAdapter {

    public static final int VIEW_MAP_MESSAGE_ME_TYPE = 1001;
    public static final int VIEW_MAP_MESSAGE_OTHER_TYPE = 1002;

    public CustomOpenChannelMessageListAdapter(@NonNull OpenChannel channel, boolean useMessageGroupUI) {
        super(channel, useMessageGroupUI);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO: Create the custom ViewHolder and return it.
        // Create your custom ViewHolder or call super.onCreateViewHolder() if you want to use the default.
        if (viewType == VIEW_MAP_MESSAGE_ME_TYPE || viewType == VIEW_MAP_MESSAGE_OTHER_TYPE) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new HighlightOpenChannelMessageViewHolder(ViewOpenChannelHighlightMessageHolderBinding.inflate(inflater, parent, false));
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        // You must call the super. You can use methods that MessageViewHolder provides
        super.onBindViewHolder(holder, position);
        // TODO: Bind the custom ViewHolder
    }

    @Override
    public int getItemViewType(int position) {
        BaseMessage message = getItem(position);

        String customType = message.getCustomType();

        if (!TextUtils.isEmpty(customType) &&
                customType.equals(StringSet.highlight) &&
                message instanceof UserMessage) {
            if (MessageUtils.isMine(message)) {
                return VIEW_MAP_MESSAGE_ME_TYPE;
            } else {
                return VIEW_MAP_MESSAGE_OTHER_TYPE;
            }
        }

        return super.getItemViewType(position);
    }
}

