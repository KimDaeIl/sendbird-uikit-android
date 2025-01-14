package com.sendbird.uikit.internal.ui.reactions

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.sendbird.android.SendbirdChat
import com.sendbird.android.user.User
import com.sendbird.uikit.R
import com.sendbird.uikit.SendbirdUIKit
import com.sendbird.uikit.databinding.SbViewEmojiReactionUserComponentBinding
import com.sendbird.uikit.internal.extensions.setAppearance
import com.sendbird.uikit.utils.UserUtils

internal class EmojiReactionUserView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.sb_widget_emoji_message
) : FrameLayout(context, attrs, defStyle) {
    val binding: SbViewEmojiReactionUserComponentBinding
    val layout: View
        get() = this

    init {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.EmojiReactionUser, defStyle, 0)
        try {
            binding = SbViewEmojiReactionUserComponentBinding.inflate(LayoutInflater.from(getContext()))
            addView(binding.root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val nicknameAppearance = a.getResourceId(
                R.styleable.EmojiReactionUser_sb_emoji_reaction_user_nickname_appearance,
                R.style.SendbirdSubtitle2OnLight01
            )
            binding.tvNickname.setAppearance(context, nicknameAppearance)
            binding.tvNickname.ellipsize = TextUtils.TruncateAt.END
            binding.tvNickname.maxLines = 1
        } finally {
            a.recycle()
        }
    }

    fun drawUser(user: User?) {
        val context = binding.ivUserCover.context
        val nickname = UserUtils.getDisplayName(context, user)
        val urls: MutableList<String> = ArrayList()
        binding.tvNickname.text = nickname
        user?.let {
            urls.add(user.profileUrl)
            binding.ivUserCover.loadImages(urls)
            if (user.userId == SendbirdChat.currentUser?.userId) {
                val meBadge = context.resources.getString(R.string.sb_text_user_list_badge_me)
                val spannable: Spannable = SpannableString(meBadge)
                val badgeAppearance =
                    if (SendbirdUIKit.isDarkMode()) R.style.SendbirdSubtitle2OnDark02 else R.style.SendbirdSubtitle2OnLight02
                spannable.setSpan(
                    TextAppearanceSpan(context, badgeAppearance),
                    0,
                    meBadge.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.tvNickname.append(spannable)
            }
        }
    }
}
