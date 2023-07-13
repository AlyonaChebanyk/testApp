package com.example.testapp

import android.os.Bundle
import android.view.*
import android.webkit.CookieManager
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.testapp.databinding.FragmentWebViewBinding
import timber.log.Timber

class WebViewFragment : Fragment() {

    private lateinit var binding: FragmentWebViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            webView.settings.javaScriptEnabled = true

            webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

            if (savedInstanceState != null) {
                webView.restoreState(savedInstanceState)
            } else {
                webView.loadUrl("https://www.google.com")
            }

            webView.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event.action == MotionEvent.ACTION_UP && webView.canGoBack()) {
                    webView.goBack()
                    true
                } else {
                    false
                }
            }
            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    Timber.d("Page finished: $url")
                    // Handle page loading finished event
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.webView.saveState(outState)
    }
}