package  com.qljm.swh.mvp.model

import com.qljm.swh.bean.BannerBean
import com.qljm.swh.bean.base.BaseBean
import com.qljm.swh.http.api.AppApi
import com.qljm.swh.mvp.base.BaseModelEx
import com.qljm.swh.mvp.contract.SplashContract
import io.reactivex.Observable

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/07/26
 * @Description input description
 **/
class SplashModel : BaseModelEx(), SplashContract.IModel {

    override fun getBanner(): Observable<BaseBean<BannerBean>> {
        return retrofitFactory.create(AppApi::class.java).getBanner()
    }

}