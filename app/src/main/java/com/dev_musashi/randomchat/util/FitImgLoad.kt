package com.dev_musashi.randomchat.util

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dev_musashi.randomchat.R
import com.dev_musashi.randomchat.module.GlideApp

@Composable
fun FitImgLoad(
    imgUrl: String?,
    modifier: Modifier = Modifier
) {
    val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }
    if (imgUrl == null) {
        GlideApp
            .with(LocalContext.current)
            .asBitmap()
            .load(R.drawable.ic_person)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap.value = resource
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    } else {
        GlideApp
            .with(LocalContext.current)
            .asBitmap()
            .load(imgUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmap.value = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    bitmap.value?.asImageBitmap()?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier,
        )
    } ?: Icon(
        painter = painterResource(id = R.drawable.ic_person),
        contentDescription = null,
        modifier = modifier,
        tint = Color.White
    )
}
