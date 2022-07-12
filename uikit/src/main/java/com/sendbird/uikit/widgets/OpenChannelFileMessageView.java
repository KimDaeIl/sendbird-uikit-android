package com.sendbird.uikit.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.sendbird.android.channel.OpenChannel;
import com.sendbird.android.message.BaseMessage;
import com.sendbird.android.message.FileMessage;
import com.sendbird.uikit.R;
import com.sendbird.uikit.SendbirdUIKit;
import com.sendbird.uikit.consts.MessageGroupType;
import com.sendbird.uikit.consts.StringSet;
import com.sendbird.uikit.databinding.SbViewOpenChannelFileMessageComponentBinding;
import com.sendbird.uikit.utils.DateUtils;
import com.sendbird.uikit.utils.DrawableUtils;
import com.sendbird.uikit.utils.ViewUtils;

public class OpenChannelFileMessageView extends OpenChannelMessageView {
    private final SbViewOpenChannelFileMessageComponentBinding binding;
    private final int nicknameAppearance;
    private final int operatorAppearance;
    private final int marginLeftEmpty;
    private final int marginLeftNor;

    @NonNull
    @Override
    public SbViewOpenChannelFileMessageComponentBinding getBinding() {
        return binding;
    }

    @NonNull
    @Override
    public View getLayout() {
        return binding.getRoot();
    }

    public OpenChannelFileMessageView(@NonNull Context context) {
        this(context, null);
    }

    public OpenChannelFileMessageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.sb_widget_file_message);
    }

    public OpenChannelFileMessageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MessageView, defStyle, 0);
        try {
            this.binding = SbViewOpenChannelFileMessageComponentBinding.inflate(LayoutInflater.from(getContext()), this, true);
            int timeAppearance = a.getResourceId(R.styleable.MessageView_sb_message_time_text_appearance, R.style.SendbirdCaption4OnLight03);
            int contentBackground = a.getResourceId(R.styleable.MessageView_sb_message_background, R.drawable.selector_open_channel_message_bg_light);
            int messageAppearance = a.getResourceId(R.styleable.MessageView_sb_message_text_appearance, R.style.SendbirdBody3OnLight01);
            nicknameAppearance = a.getResourceId(R.styleable.MessageView_sb_message_sender_name_text_appearance, R.style.SendbirdCaption1OnLight02);
            operatorAppearance = a.getResourceId(R.styleable.MessageView_sb_message_operator_name_text_appearance, R.style.SendbirdCaption1Secondary300);

            binding.tvSentAt.setTextAppearance(context, timeAppearance);
            binding.tvNickname.setTextAppearance(context, nicknameAppearance);
            binding.contentPanel.setBackgroundResource(contentBackground);
            binding.tvFileName.setTextAppearance(context, messageAppearance);
            binding.tvFileName.setPaintFlags(binding.tvFileName.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG);

            marginLeftEmpty = getResources().getDimensionPixelSize(R.dimen.sb_size_40);
            marginLeftNor = getResources().getDimensionPixelSize(R.dimen.sb_size_12);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void drawMessage(@NonNull OpenChannel channel, @NonNull BaseMessage message, @NonNull MessageGroupType messageGroupType) {
        final FileMessage fileMessage = (FileMessage) message;

        binding.tvFileName.setText(fileMessage.getName());
        binding.ivStatus.drawStatus(message, channel);
        if (channel.isOperator(message.getSender())) {
            binding.tvNickname.setTextAppearance(getContext(), operatorAppearance);
        } else {
            binding.tvNickname.setTextAppearance(getContext(), nicknameAppearance);
        }

        int backgroundTint = SendbirdUIKit.isDarkMode() ? R.color.background_600 : R.color.background_50;
        int iconTint = SendbirdUIKit.getDefaultThemeMode().getPrimaryTintResId();
        int inset = (int) getContext().getResources().getDimension(R.dimen.sb_size_12);
        if ((fileMessage.getType().toLowerCase().startsWith(StringSet.audio))) {
            Drawable background = DrawableUtils.setTintList(getContext(), R.drawable.sb_rounded_rectangle_corner_24, backgroundTint);
            Drawable icon = DrawableUtils.setTintList(getContext(), R.drawable.icon_file_audio, iconTint);
            binding.ivIcon.setImageDrawable(DrawableUtils.createLayerIcon(background, icon, inset));
        } else {
            Drawable background = DrawableUtils.setTintList(getContext(), R.drawable.sb_rounded_rectangle_corner_24, backgroundTint);
            Drawable icon = DrawableUtils.setTintList(getContext(), R.drawable.icon_file_document, iconTint);
            binding.ivIcon.setImageDrawable(DrawableUtils.createLayerIcon(background, icon, inset));
        }

        if (messageGroupType == MessageGroupType.GROUPING_TYPE_SINGLE || messageGroupType == MessageGroupType.GROUPING_TYPE_HEAD) {
            binding.ivProfileView.setVisibility(View.VISIBLE);
            binding.tvNickname.setVisibility(View.VISIBLE);
            binding.tvSentAt.setVisibility(View.VISIBLE);
            binding.tvSentAt.setText(DateUtils.formatTime(getContext(), message.getCreatedAt()));

            ViewUtils.drawNickname(binding.tvNickname, message);
            ViewUtils.drawProfile(binding.ivProfileView, message);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.contentPanel.getLayoutParams();
            params.leftMargin = marginLeftNor;
            binding.contentPanel.setLayoutParams(params);
        } else {
            binding.ivProfileView.setVisibility(View.GONE);
            binding.tvNickname.setVisibility(View.GONE);
            binding.tvSentAt.setVisibility(View.INVISIBLE);

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.contentPanel.getLayoutParams();
            params.leftMargin = marginLeftEmpty;
            binding.contentPanel.setLayoutParams(params);
        }
    }
}
