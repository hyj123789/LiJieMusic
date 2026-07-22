package com.example.search

import SearchResultAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.base.BaseFragment
import com.example.base.PlayerManager
import com.example.search.Adapter.GuessAdapter
import com.example.search.Adapter.HotSearchWordAdapter
import com.example.search.Adapter.SuggestAdapter
import com.example.search.databinding.FragmentSearchBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.getValue

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private val viewModel : SearchViewModel by viewModels()
    private val HotsearcgAdapter = HotSearchWordAdapter()
    private val guessAdapter = GuessAdapter()
    private val searchSuggestAdapter = SuggestAdapter { rawText ->

        //取出格式，只搜索歌名
        val noPrefixText = rawText.removePrefix("歌曲：").removePrefix("专辑：")
        //再截取 " - " 前面的部分作为歌名
        val keyword = noPrefixText.substringBeforeLast(" - ")
        binding.search.setText(keyword)

        viewModel.searchit(keyword)

        binding.rvSeachResponse.visibility = View.VISIBLE
        binding.layoutHotSearch.visibility = View.GONE
        //设置adapter
        binding.rvSeachResponse.adapter = searchResultAdapter
    }

    private val searchResultAdapter = SearchResultAdapter(
        {   songItem->
            PlayerManager.addSongToPlaylist(songItem.id.toString(),songItem.name,songItem.ar?.get(0)?.name?:"未知歌手")
        },
        { songItem ->
            PlayerManager.playSong(songItem.id.toString(),songItem.name,songItem.ar?.get(0)?.name?:"未知歌手")
        }
    )

    override fun initView() {

        binding.rvHotSearch.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHotSearch.adapter = HotsearcgAdapter

        binding.rvGuessLike.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGuessLike.adapter = guessAdapter

        binding.rvSeachResponse.layoutManager = LinearLayoutManager(requireContext())


        //输入就展示
        showSearchResponse()

        //仍保留按钮功能
        binding.tvSearch.setOnClickListener {
            val keyword = binding.search.text.toString().trim()
            //取出搜索结果
            viewModel.searchit(keyword)

            if(keyword.isEmpty()){
                binding.rvSeachResponse.visibility = View.GONE
                binding.layoutHotSearch.visibility = View.VISIBLE

            }else{
                binding.rvSeachResponse.visibility = View.VISIBLE
                binding.layoutHotSearch.visibility = View.GONE
                //设置adapter
                binding.rvSeachResponse.adapter = searchResultAdapter
            }
        }

        viewModel.fetchRecommendPlaylists()

        binding.back.setOnClickListener {

            //告诉导航仪退回上一个页面
            findNavController().navigateUp()
        }

    }

    private fun showSearchResponse() {
        binding.search.doAfterTextChanged { keyword ->
                val key = keyword.toString().trim()
                Log.d("hyj", "👉 输入关键词：$keyword")
               viewModel.fetchSearchSuggestion(key)

                if (key.isNotEmpty()) {
                    binding.rvSeachResponse.visibility = View.VISIBLE
                    binding.layoutHotSearch.visibility = View.GONE
                    binding.rvSeachResponse.adapter = searchSuggestAdapter

                } else {
                    binding.rvSeachResponse.visibility = View.GONE
                    binding.layoutHotSearch.visibility = View.VISIBLE
                }
            }
        }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {

            //只有在页面可见时才监听，不可见就没有必要监听
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.hotSearchFlow
                    .onEach { realData ->
                        if (realData.isNotEmpty()) {
                            HotsearcgAdapter.submitList(realData)
                        }
                    }
                    .launchIn(this)


                viewModel.GuessFlow
                    .onEach { realData ->
                        if (realData.isNotEmpty()) {
                            guessAdapter.submitList(realData)
                        }
                    }
                    .launchIn(this)

                viewModel.SearchFlow
                    .onEach { realData ->
                        if (realData.isNotEmpty()) {
                            searchResultAdapter.submitList(realData)
                        }
                    }
                    .launchIn(this)



                //搜集建议列表
                viewModel.SearchFlowSuggest
                    .onEach { realData ->
                        if (realData.isNotEmpty()) {
                            searchSuggestAdapter.submitList(realData);
                        }
                    }
                    .launchIn(this)

            }
        }
    }
}

