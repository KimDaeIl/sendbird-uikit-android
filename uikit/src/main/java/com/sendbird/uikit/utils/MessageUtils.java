package com.sendbird.uikit.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sendbird.android.SendbirdChat;
import com.sendbird.android.message.AdminMessage;
import com.sendbird.android.message.BaseMessage;
import com.sendbird.android.message.CustomizableMessage;
import com.sendbird.android.message.FileMessage;
import com.sendbird.android.message.SendingStatus;
import com.sendbird.android.message.UserMessage;
import com.sendbird.android.user.User;
import com.sendbird.uikit.SendbirdUIKit;
import com.sendbird.uikit.activities.viewholder.MessageType;
import com.sendbird.uikit.activities.viewholder.MessageViewHolderFactory;
import com.sendbird.uikit.consts.MessageGroupType;
import com.sendbird.uikit.consts.ReplyType;
import com.sendbird.uikit.model.MessageListUIParams;
import com.sendbird.uikit.model.TimelineMessage;

public class MessageUtils {
    public static boolean isMine(@NonNull BaseMessage message) {
        if (message.getSender() == null) {
            return false;
        }
        return isMine(message.getSender().getUserId());
    }

    public static boolean isMine(@Nullable String senderId) {
        User currentUser = SendbirdChat.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUserId().equals(senderId);
        }
        return false;
    }

    public static boolean isDeletableMessage(@NonNull BaseMessage message) {
        if (message instanceof UserMessage || message instanceof FileMessage) {
            return isMine(message.getSender().getUserId()) && !hasThread(message);
        }
        return false;
    }

    public static boolean isUnknownType(@NonNull BaseMessage message) {
        MessageType messageType = MessageViewHolderFactory.getMessageType(message);
        return messageType == MessageType.VIEW_TYPE_UNKNOWN_MESSAGE_ME || messageType == MessageType.VIEW_TYPE_UNKNOWN_MESSAGE_OTHER;
    }

    public static boolean isFailed(@NonNull BaseMessage message) {
        final SendingStatus status = message.getSendingStatus();
        return status == SendingStatus.FAILED || status == SendingStatus.CANCELED;
    }

    public static boolean isGroupChanged(@Nullable BaseMessage frontMessage, @Nullable BaseMessage backMessage, @NonNull MessageListUIParams messageListUIParams) {
        return frontMessage == null ||
                frontMessage.getSender() == null ||
                frontMessage instanceof AdminMessage ||
                frontMessage instanceof TimelineMessage ||
                (messageListUIParams.shouldUseQuotedView() && hasParentMessage(frontMessage)) ||
                backMessage == null ||
                backMessage.getSender() == null ||
                backMessage instanceof AdminMessage ||
                backMessage instanceof TimelineMessage ||
                (messageListUIParams.shouldUseQuotedView() && hasParentMessage(backMessage)) ||
                !backMessage.getSendingStatus().equals(SendingStatus.SUCCEEDED) ||
                !frontMessage.getSendingStatus().equals(SendingStatus.SUCCEEDED) ||
                !frontMessage.getSender().equals(backMessage.getSender()) ||
                !DateUtils.hasSameTimeInMinute(frontMessage.getCreatedAt(), backMessage.getCreatedAt()) ||
                (SendbirdUIKit.getReplyType() == ReplyType.THREAD && (
                        (!(frontMessage instanceof CustomizableMessage) && frontMessage.getThreadInfo().getReplyCount() > 0) ||
                        (!(backMessage instanceof CustomizableMessage) && backMessage.getThreadInfo().getReplyCount() > 0)
                ));
    }

    @NonNull
    public static MessageGroupType getMessageGroupType(@Nullable BaseMessage prevMessage,
                                                       @NonNull BaseMessage message,
                                                       @Nullable BaseMessage nextMessage,
                                                       @NonNull MessageListUIParams messageListUIParams) {
        if (!messageListUIParams.shouldUseMessageGroupUI()) {
            return MessageGroupType.GROUPING_TYPE_SINGLE;
        }

        if (!message.getSendingStatus().equals(SendingStatus.SUCCEEDED)) {
            return MessageGroupType.GROUPING_TYPE_SINGLE;
        }

        if (messageListUIParams.shouldUseQuotedView() && hasParentMessage(message)) {
            return MessageGroupType.GROUPING_TYPE_SINGLE;
        }

        if (SendbirdUIKit.getReplyType() == ReplyType.THREAD &&
                !(message instanceof CustomizableMessage) &&
                message.getThreadInfo().getReplyCount() > 0) {
            return MessageGroupType.GROUPING_TYPE_SINGLE;
        }

        MessageGroupType messageGroupType = MessageGroupType.GROUPING_TYPE_BODY;
        boolean isHead = messageListUIParams.shouldUseReverseLayout() ? MessageUtils.isGroupChanged(prevMessage, message, messageListUIParams) : MessageUtils.isGroupChanged(message, nextMessage, messageListUIParams);
        boolean isTail = messageListUIParams.shouldUseReverseLayout() ? MessageUtils.isGroupChanged(message, nextMessage, messageListUIParams) : MessageUtils.isGroupChanged(prevMessage, message, messageListUIParams);

        if (!isHead && isTail) {
            messageGroupType = MessageGroupType.GROUPING_TYPE_TAIL;
        } else if (isHead && !isTail) {
            messageGroupType = MessageGroupType.GROUPING_TYPE_HEAD;
        } else if (isHead) {
            messageGroupType = MessageGroupType.GROUPING_TYPE_SINGLE;
        }

        return messageGroupType;
    }

    public static boolean hasParentMessage(@NonNull BaseMessage message) {
        return message.getParentMessageId() != 0L;
    }

    public static boolean hasThread(@NonNull BaseMessage message) {
        if (message instanceof CustomizableMessage) return false;
        return message.getThreadInfo().getReplyCount() > 0;
    }
}
