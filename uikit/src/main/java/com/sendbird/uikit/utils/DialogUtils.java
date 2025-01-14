package com.sendbird.uikit.utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

import com.sendbird.android.SendbirdChat;
import com.sendbird.android.channel.GroupChannel;
import com.sendbird.android.params.GroupChannelCreateParams;
import com.sendbird.android.user.User;
import com.sendbird.uikit.R;
import com.sendbird.uikit.SendbirdUIKit;
import com.sendbird.uikit.activities.ChannelActivity;
import com.sendbird.uikit.consts.DialogEditTextParams;
import com.sendbird.uikit.interfaces.CustomParamsHandler;
import com.sendbird.uikit.interfaces.LoadingDialogHandler;
import com.sendbird.uikit.interfaces.OnEditTextResultListener;
import com.sendbird.uikit.interfaces.OnItemClickListener;
import com.sendbird.uikit.internal.ui.reactions.DialogView;
import com.sendbird.uikit.internal.ui.widgets.UserProfile;
import com.sendbird.uikit.internal.ui.widgets.WaitingDialog;
import com.sendbird.uikit.log.Logger;
import com.sendbird.uikit.model.DialogListItem;

import java.util.Collections;

@SuppressWarnings("UnusedReturnValue")
public final class DialogUtils {

    private DialogUtils(){
        throw new UnsupportedOperationException();
    }

    @NonNull
    public static AlertDialog showListDialog(@NonNull Context context,
                                      @NonNull String title,
                                      @NonNull DialogListItem[] items,
                                      @Nullable OnItemClickListener<DialogListItem> itemClickListener) {
        int themeResId = SendbirdUIKit.isDarkMode() ? R.style.Widget_Sendbird_Dark_DialogView : R.style.Widget_Sendbird_DialogView;
        final Context themeWrapperContext = new ContextThemeWrapper(context, themeResId);
        final DialogView dialogView = new DialogView(themeWrapperContext);
        dialogView.setTitle(title);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Sendbird_Dialog);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialogView.setItems(items, (view, position, data) -> {
            dialog.dismiss();
            if (itemClickListener != null) itemClickListener.onItemClick(view, position, items[position]);
        }, false);

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout((int) context.getResources().getDimension(R.dimen.sb_dialog_width_280), WRAP_CONTENT);
        }

        return dialog;
    }

    @NonNull
    public static AlertDialog showListBottomDialog(@NonNull Context context,
                                                   @NonNull DialogListItem[] items,
                                                   @Nullable OnItemClickListener<DialogListItem> itemClickListener) {
        return showListBottomDialog(context, items, itemClickListener, false);
    }

    @NonNull
    public static AlertDialog showListBottomDialog(@NonNull Context context,
                                                   @NonNull DialogListItem[] items,
                                                   @Nullable OnItemClickListener<DialogListItem> itemClickListener,
                                                   boolean useOverlay) {
        int themeResId;
        if (useOverlay) {
            themeResId = R.style.Widget_Sendbird_Overlay_DialogView;
        } else {
            themeResId = SendbirdUIKit.isDarkMode() ? R.style.Widget_Sendbird_Dark_DialogView : R.style.Widget_Sendbird_DialogView;
        }
        final Context themeWrapperContext = new ContextThemeWrapper(context, themeResId);
        final DialogView dialogView = new DialogView(themeWrapperContext);
        dialogView.setBackgroundBottom();

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Sendbird_Dialog_Bottom);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialogView.setItems(items, (view, position, data) -> {
            dialog.dismiss();
            if (itemClickListener != null) itemClickListener.onItemClick(view, position, items[position]);
        }, true);

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        }

        return dialog;
    }

    @NonNull
    public static AlertDialog showInputDialog(@NonNull Context context,
                                              @NonNull String title,
                                              @NonNull DialogEditTextParams editTextParams,
                                              @Nullable OnEditTextResultListener editTextResultListener,
                                              @NonNull String positiveButtonText,
                                              @Nullable View.OnClickListener positiveButtonListener,
                                              @NonNull String negativeButtonText,
                                              @Nullable View.OnClickListener negativeButtonListener) {
        int themeResId = SendbirdUIKit.isDarkMode() ? R.style.Widget_Sendbird_Dark_DialogView : R.style.Widget_Sendbird_DialogView;
        final Context themeWrapperContext = new ContextThemeWrapper(context, themeResId);
        final DialogView dialogView = new DialogView(themeWrapperContext);
        dialogView.setTitle(title);
        dialogView.setEditText(editTextParams, editTextResultListener);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Sendbird_Dialog);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialogView.setPositiveButton(positiveButtonText, 0, v -> {
            dialog.dismiss();
            if (positiveButtonListener != null) positiveButtonListener.onClick(v);
        });
        dialogView.setNegativeButton(negativeButtonText, 0, v -> {
            dialog.dismiss();
            if (negativeButtonListener != null) negativeButtonListener.onClick(v);
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout((int) context.getResources().getDimension(R.dimen.sb_dialog_width_280), WRAP_CONTENT);
        }

        return dialog;
    }

    @NonNull
    public static AlertDialog showWarningDialog(@NonNull Context context,
                                                @NonNull String title,
                                                @NonNull String warningButtonText,
                                                @Nullable View.OnClickListener warningButtonListener,
                                                @NonNull String negativeButtonText,
                                                @Nullable View.OnClickListener negativeButtonListener) {
        return showWarningDialog(context, title, warningButtonText, warningButtonListener, negativeButtonText, negativeButtonListener, false);
    }

    @NonNull
    public static AlertDialog showWarningDialog(@NonNull Context context,
                                                @NonNull String title,
                                                @NonNull String message,
                                                @NonNull String warningButtonText,
                                                @Nullable View.OnClickListener warningButtonListener,
                                                @NonNull String negativeButtonText,
                                                @Nullable View.OnClickListener negativeButtonListener) {
        return showWarningDialog(context, title, message, warningButtonText, warningButtonListener, negativeButtonText, negativeButtonListener, false);
    }

    @NonNull
    public static AlertDialog showWarningDialog(@NonNull Context context,
                                                @NonNull String title,
                                                @NonNull String warningButtonText,
                                                @Nullable View.OnClickListener warningButtonListener,
                                                @NonNull String negativeButtonText,
                                                @Nullable View.OnClickListener negativeButtonListener,
                                                boolean useOverlay) {
        return showWarningDialog(context, title, "", warningButtonText, warningButtonListener, negativeButtonText, negativeButtonListener, useOverlay);
    }

    @NonNull
    public static AlertDialog showWarningDialog(@NonNull Context context,
                                                @NonNull String title,
                                                @NonNull String message,
                                                @NonNull String warningButtonText,
                                                @Nullable View.OnClickListener warningButtonListener,
                                                @NonNull String negativeButtonText,
                                                @Nullable View.OnClickListener negativeButtonListener,
                                                boolean useOverlay) {
        int negativeButtonTextColor = SendbirdUIKit.isDarkMode() ?
                R.color.sb_button_uncontained_text_color_cancel_dark :
                R.color.sb_button_uncontained_text_color_cancel_light;
        int positiveButtonTextColor = SendbirdUIKit.isDarkMode() ?
                R.color.sb_button_uncontained_text_color_alert_dark :
                R.color.sb_button_uncontained_text_color_alert_light;

        int themeResId;
        if (useOverlay) {
            themeResId = R.style.Widget_Sendbird_Overlay_DialogView;
        } else {
            themeResId = SendbirdUIKit.isDarkMode() ? R.style.Widget_Sendbird_Dark_DialogView : R.style.Widget_Sendbird_DialogView;
        }
        final Context themeWrapperContext = new ContextThemeWrapper(context, themeResId);
        final DialogView dialogView = new DialogView(themeWrapperContext);
        dialogView.setTitle(title);
        dialogView.setMessage(message);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Sendbird_Dialog);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialogView.setPositiveButton(warningButtonText, positiveButtonTextColor, v -> {
            dialog.dismiss();
            if (warningButtonListener != null) warningButtonListener.onClick(v);
        });
        dialogView.setNegativeButton(negativeButtonText, negativeButtonTextColor, v -> {
            dialog.dismiss();
            if (negativeButtonListener != null) negativeButtonListener.onClick(v);
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout((int) context.getResources().getDimension(R.dimen.sb_dialog_width_280), WRAP_CONTENT);
        }

        return dialog;
    }

    @NonNull
    public static AlertDialog showContentDialog(@NonNull Context context,
                                         @NonNull View contentView) {
        int themeResId = SendbirdUIKit.isDarkMode() ? R.style.Widget_Sendbird_Dark_DialogView : R.style.Widget_Sendbird_DialogView;
        final Context themeWrapperContext = new ContextThemeWrapper(context, themeResId);
        final DialogView dialogView = new DialogView(themeWrapperContext);
        dialogView.setContentView(contentView);
        dialogView.setBackgroundBottom();

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Sendbird_Dialog_Bottom);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        }

        return dialog;
    }

    @NonNull
    public static AlertDialog showContentTopDialog(@NonNull Context context,
                                            @NonNull View contentView) {
        int themeResId = SendbirdUIKit.isDarkMode() ? R.style.Widget_Sendbird_Dark_DialogView : R.style.Widget_Sendbird_DialogView;
        final Context themeWrapperContext = new ContextThemeWrapper(context, themeResId);
        final DialogView dialogView = new DialogView(themeWrapperContext);
        dialogView.setContentView(contentView);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Sendbird_Dialog);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.TOP);
            dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        }

        return dialog;
    }

    @NonNull
    public static AlertDialog showContentViewAndListDialog(@NonNull Context context,
                                                    @NonNull View contentView,
                                                    @NonNull DialogListItem[] items,
                                                    @Nullable OnItemClickListener<DialogListItem> itemClickListener) {
        int themeResId = SendbirdUIKit.isDarkMode() ? R.style.Widget_Sendbird_Dark_DialogView : R.style.Widget_Sendbird_DialogView;
        final Context themeWrapperContext = new ContextThemeWrapper(context, themeResId);
        final DialogView dialogView = new DialogView(themeWrapperContext);
        dialogView.setContentView(contentView);
        dialogView.setBackgroundBottom();

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Sendbird_Dialog_Bottom);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialogView.setItems(items, (view, position, data) -> {
            dialog.dismiss();
            if (itemClickListener != null) itemClickListener.onItemClick(view, position, data);
        }, true);

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        }

        return dialog;
    }

    @NonNull
    public static AlertDialog showUserProfileDialog(@NonNull Context context,
                                                    @NonNull User user,
                                                    boolean useChannelCreatable,
                                                    @Nullable OnItemClickListener<User> userProfileItemClickListener,
                                                    @Nullable LoadingDialogHandler handler) {
        return showUserProfileDialog(context, user, useChannelCreatable, userProfileItemClickListener, handler, false);
    }

    @NonNull
    public static AlertDialog showUserProfileDialog(@NonNull Context context,
                                                    @NonNull User user,
                                                    boolean useChannelCreatable,
                                                    @Nullable OnItemClickListener<User> userProfileItemClickListener,
                                                    @Nullable LoadingDialogHandler handler,
                                                    boolean useOverlay) {
        int themeResId;
        if (useOverlay) {
            themeResId = R.style.Widget_Sendbird_Overlay_UserProfile;
        } else {
            themeResId = SendbirdUIKit.isDarkMode() ? R.style.Widget_Sendbird_Dark_UserProfile : R.style.Widget_Sendbird_UserProfile;
        }
        final Context userProfileThemeContext = new ContextThemeWrapper(context, themeResId);
        UserProfile userProfile = new UserProfile(userProfileThemeContext);
        userProfile.drawUserProfile(user);
        userProfile.setUseChannelCreateButton(useChannelCreatable);

        if (useOverlay) {
            themeResId = R.style.Widget_Sendbird_Overlay_DialogView;
        } else {
            themeResId = SendbirdUIKit.isDarkMode() ? R.style.Widget_Sendbird_Dark_DialogView : R.style.Widget_Sendbird_DialogView;
        }
        final Context themeWrapperContext = new ContextThemeWrapper(context, themeResId);
        final DialogView dialogView = new DialogView(themeWrapperContext);
        dialogView.setContentView(userProfile);
        dialogView.setBackgroundBottom();

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Sendbird_Dialog_Bottom);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        userProfile.setOnItemClickListener((view, position, menuItem) -> {
            dialog.dismiss();
            if (userProfileItemClickListener != null) {
                userProfileItemClickListener.onItemClick(view, position, menuItem);
            } else {
                createDirectChannel(context, user, handler);
            }
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        }

        return dialog;
    }

    private static void createDirectChannel(Context context, User user, LoadingDialogHandler handler) {
        GroupChannelCreateParams params = new GroupChannelCreateParams();
        params.setUserIds(Collections.singletonList(user.getUserId()));
        params.setName("");
        params.setCoverUrl("");
        params.setOperators(Collections.singletonList(SendbirdChat.getCurrentUser()));

        CustomParamsHandler customHandler = SendbirdUIKit.getCustomParamsHandler();
        if (customHandler != null) {
            customHandler.onBeforeCreateGroupChannel(params);
        }

        if (handler == null) {
            WaitingDialog.show(context);
        } else {
            handler.shouldShowLoadingDialog();
        }
        GroupChannel.createChannel(params, (channel, e) -> {
            if (handler == null) {
                WaitingDialog.dismiss();
            } else {
                handler.shouldDismissLoadingDialog();
            }
            if (e != null) {
                ContextUtils.toastError(context, R.string.sb_text_error_create_channel);
                Logger.e(e);
                return;
            }

            Intent intent = ChannelActivity.newIntent(context, channel.getUrl());
            context.startActivity(intent);
        });
    }
}
