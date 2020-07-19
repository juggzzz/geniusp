package com.enjaja.youche.ui.charger

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.enjaja.youche.R
import com.enjaja.youche.base.BaseFragment
import com.enjaja.youche.databinding.FragmentChargerListDeviceBinding
import com.enjaja.youche.ext.*
import com.enjaja.youche.ui.charger.adapter.ChargerDeviceAdapter
import com.enjaja.youche.view.recyclerview.DefineLoadMoreView
import com.enjaja.youche.view.recyclerview.SpaceItemDecoration
import com.enjaja.youche.viewmodel.RequestChargerDeviceViewModel
import com.enjaja.youche.viewmodel.RequestHomeViewModel
import com.enjaja.youche.viewmodel.state.ChargerListDeviceViewModel
import com.enjaja.youchelibrary.utils.DensityUtils
import com.kingja.loadsir.core.LoadService
import com.yanzhenjie.recyclerview.SwipeRecyclerView
import kotlinx.android.synthetic.main.include_list.*
import kotlinx.android.synthetic.main.include_recyclerview.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "id"
private const val ARG_PARAM2 = "param2"

/**
 *终端设备列表
 */
class ChargerListFragment : BaseFragment<ChargerListDeviceViewModel,FragmentChargerListDeviceBinding>() {
    private var param1: String? = null
    private var param2: String? = null
    private val chargerDeviceAdapter:ChargerDeviceAdapter by lazy{ ChargerDeviceAdapter(arrayListOf())}
    private val requestChargerDeviceViewModel: RequestChargerDeviceViewModel by viewModels()

    //界面状态管理者
    private lateinit var loadsir: LoadService<Any>
    //recyclerview的底部加载view 因为在首页要动态改变他的颜色，所以加了他这个字段
    private lateinit var footView: DefineLoadMoreView


    override fun layoutId(): Int =R.layout.fragment_charger_list_device

    override fun initView(savedInstanceState: Bundle?) {

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        //状态页配置
        loadsir = loadServiceInit(swipeRefresh) {
            //点击重试时触发的操作
            loadsir.showLoading()

            requestChargerDeviceViewModel.getChargerEquipmentConnectorInfoData(true,
                param1.toString()
            )
        }
        //初始化 SwipeRefreshLayout
        swipeRefresh.init {
            //触发刷新监听时请求数据
            requestChargerDeviceViewModel.getChargerEquipmentConnectorInfoData(true,
                param1.toString()
            )
        }
        //初始化recyclerView
        recyclerView.init(LinearLayoutManager(context), chargerDeviceAdapter).let {
            //因为首页要添加轮播图，所以我设置了firstNeedTop字段为false,即第一条数据不需要设置间距
            it.addItemDecoration(SpaceItemDecoration(0, DensityUtils.dip2px(8), false))
            footView = it.initFooter(SwipeRecyclerView.LoadMoreListener {
                requestChargerDeviceViewModel.getChargerEquipmentConnectorInfoData(false,
                    param1.toString()
                )
            })

        }
        chargerDeviceAdapter.run {
            setCollectClick { item, position ->
                startActivity(Intent(activity, ChargerDetailActivity::class.java).apply {
                    putExtra(
                        "id",
                        item.id
                    )
                })
            }

        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChargerListFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChargerListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}