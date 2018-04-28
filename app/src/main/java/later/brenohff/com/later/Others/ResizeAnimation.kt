package later.brenohff.com.later.Others

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

/**
 * Created by Breno Henrique on 12/02/2018.
 */

class ResizeAnimation(internal var view: View, internal var endH: Int) : Animation() {
    internal var startH: Int = 0
    internal var diff: Int = 0

    init {
        startH = view.layoutParams.height
        diff = endH - startH
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        view.layoutParams.height = startH + (diff * interpolatedTime).toInt()
        view.requestLayout()
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
    }

    override fun willChangeBounds(): Boolean {
        return true
    }
}