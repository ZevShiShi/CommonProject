package com.qljm.swh.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.qljm.swh.mvp.base.IPresenterContractEx
import com.qljm.swh.mvp.view.BaseView

/**
 * 实现了懒加载的fragment
 * onViewCreated->onActivityCreated
 *
 */
abstract class BaseMvpLazyFragment<out P : IPresenterContractEx> : MvpFragmentEx<P>(), BaseView {

    private var hasLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenter().registerLifecycle(this)
        init(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    protected abstract fun getLayoutId(): Int
    protected open fun init(savedInstanceState: Bundle?) {}
    protected open fun initView() {}
    protected open fun initData() {}
    protected open fun loadLazyData() {}

    override fun showToast(resId: Int) {
        Toast.makeText(activity, resId, Toast.LENGTH_SHORT).show()
    }

    override fun showToast(text: String?) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show()
    }

    protected open fun goActivity(cls: Class<*>, bundle: Bundle?) {
        val intent = Intent(activity, cls)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    protected open fun goActivity(cls: Class<*>) {
        goActivity(cls, null)
    }

    override fun onResume() {
        super.onResume()
        // todo:懒加载 FragmentPagerAdapter 使用这种方式BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        if (!hasLoaded) {
            hasLoaded = true
            loadLazyData()
        }
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }
}