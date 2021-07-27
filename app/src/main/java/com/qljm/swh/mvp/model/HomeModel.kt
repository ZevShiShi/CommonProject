package  com.qljm.swh.mvp.model

import com.qljm.swh.bean.base.BaseArrayBean
import com.qljm.swh.bean.news.ChannelGroupBean
import com.qljm.swh.http.api.NewsApi
import com.qljm.swh.mvp.base.BaseModelEx
import com.qljm.swh.mvp.contract.HomeContract
import io.reactivex.Observable

/**
 * @Author Kotlin MVP Plugin
 * @Date 2021/07/26
 * @Description input description
 **/
class HomeModel : BaseModelEx(), HomeContract.IModel {

    override fun getChannels(): Observable<BaseArrayBean<ChannelGroupBean>> {
        return retrofitFactory.create(NewsApi::class.java).getChannels()
    }
}