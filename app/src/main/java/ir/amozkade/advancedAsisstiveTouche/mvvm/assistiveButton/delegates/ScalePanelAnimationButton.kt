package ir.amozkade.advancedAsisstiveTouche.mvvm.assistiveButton.delegates

import android.animation.Animator
import android.view.View
import android.view.animation.OvershootInterpolator
import ir.mobitrain.applicationcore.helper.animations.AnimationListener

abstract class ScalePanelAnimationButton {

   lateinit var rootView: View
   lateinit var delegate: FloatingWindowDelegate

    fun scaleAnimation(show: Boolean) {

        rootView.scaleX = if (show) 0f else 1f
        rootView.scaleY = if (show) 0f else 1f
        rootView.animate()
                .setDuration(if (show) 300 else 50)
                .setInterpolator(OvershootInterpolator(.8f))
                .scaleX(if (show) 1f else 0f)
                .scaleY(if (show) 1f else 0f)
                .setListener(object : AnimationListener() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        if (!show) {
                            delegate.removeViewSubWindow(rootView)
                            delegate.closePanel()
                        }
                    }
                })
                .start()
    }


}