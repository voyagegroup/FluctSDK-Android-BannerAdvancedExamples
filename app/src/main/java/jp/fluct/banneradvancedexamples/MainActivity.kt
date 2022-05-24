package jp.fluct.banneradvancedexamples

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.fluct.banneradvancedexamples.databinding.MainActivityBinding
import jp.fluct.fluctsdk.FluctAdRequestTargeting
import jp.fluct.fluctsdk.FluctAdSize
import jp.fluct.fluctsdk.FluctAdView
import jp.fluct.fluctsdk.FluctErrorCode
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: MainActivityBinding

    private var isFirstLayout: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater).apply { setContentView(this.root) }

        // ターゲティング設定
        val targeting = FluctAdRequestTargeting().apply {
            // ユーザIDを設定してください
            this.publisherProvidedId = "ppid_1"
        }

        // 親ViewGroupサイズに従い比率計算を行う場合、ViewTreeObserverの使用を推奨します
        binding.container.viewTreeObserver.addOnGlobalLayoutListener {
            if (isFirstLayout) {
                // 初回レイアウト時のみ実行
                return@addOnGlobalLayoutListener
            }
            isFirstLayout = true

            // region 広告サイズ計算
            val density = resources.displayMetrics.density
            val margin = 8 * density
            val containerWidth = binding.container.measuredWidth
            val adWidth = containerWidth - margin * 2
            val adHeight = adWidth * 0.5625
            // endregion

            val adView = FluctAdView(
                this,
                "1000146486",
                "1000242372",
                (adWidth/density).roundToInt(),
                (adHeight/density).roundToInt(),
                targeting,
                listener
            )
            binding.container.addView(adView)
            adView.loadAd()
        }
    }

    private val listener = object : FluctAdView.Listener {

        override fun onLoaded() {
            logging("onLoaded: 広告表示が完了しました")
        }

        override fun onFailedToLoad(p0: FluctErrorCode) {
            val msg: String = when (p0) {
                FluctErrorCode.UNKNOWN -> "Unknown Error"
                FluctErrorCode.NOT_CONNECTED_TO_INTERNET -> "ネットワークエラー"
                FluctErrorCode.SERVER_ERROR -> "サーバーエラー"
                FluctErrorCode.NO_ADS -> "表示する広告がありません"
                FluctErrorCode.BAD_REQUEST -> "groupId / unitId / 登録されているbundleのどれかが間違っています"
                FluctErrorCode.ADVERTISING_ID_UNAVAILABLE -> "Google Ad ID取得エラー"
                FluctErrorCode.UNEXPECTED_WEBVIEW_RELEASE -> "Android OSによる予期しないWebView破棄"
                FluctErrorCode.WEBVIEW_CRASHED -> "予期しないWebViewクラッシュ"
                else -> "その他のエラー (${p0.label})"
            }
            logging("onFailedToLoad: $msg")
        }

        override fun onLeftApplication() {
            logging("onLeftApplication: 広告へ遷移します")
        }

        override fun onUnloaded() {
            logging("onUnloaded: 広告が破棄されました")
        }

    }

    private fun logging(msg: String) {
        Log.d(TAG, msg)
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT)
            .show()
    }

}
