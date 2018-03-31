package kr.rinc.ch.activity

import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*
import kr.rinc.ch.R
import kr.rinc.ch.util.GlideUtil
import kr.rinc.ch.util.IntentUtil

class SplashActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    GlideUtil.setImage(this@SplashActivity, R.drawable.ic_c, logo)
    logo.startAnimation(AnimationUtils.loadAnimation(this@SplashActivity, R.anim.splash_logo))
    Handler().postDelayed({
      IntentUtil.finishMoveActivity(this@SplashActivity, MainActivity::class.java)
    }, 1500)

  }
}
