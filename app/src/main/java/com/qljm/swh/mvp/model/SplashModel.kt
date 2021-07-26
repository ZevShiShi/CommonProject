package  com.qljm.swh.mvp.model

import com.lihui.base.data.http.RetrofitFactory
import com.qljm.swh.bean.BannerBean
import com.qljm.swh.bean.base.BaseBean
import com.qljm.swh.http.api.AppApi
import com.qljm.swh.mvp.contract.SplashContract
import io.reactivex.Observable
import mvp.ljb.kt.model.BaseModel
import javax.inject.Inject

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/07/26
 * @Description input description
 **/
class SplashModel @Inject constructor() : BaseModel(), SplashContract.IModel {

//    @Inject
//    lateinit var retrofitFactory: RetrofitFactory

    override fun getBanner(): Observable<BaseBean<BannerBean>> {
        return RetrofitFactory.instance.create(AppApi::class.java).getBanner()
    }

}