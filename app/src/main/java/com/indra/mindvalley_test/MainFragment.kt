/*
 * Copyright 2020 indra953@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.indra.mindvalley_test


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.indra.mindvalley_test.utils.getRootDirPath
import com.indra.slide.Slide
import com.indra.slide.common.Priority
import com.indra.slide.error.ANError
import com.indra.slide.interfaces.DownloadListener
import com.indra.slide.interfaces.ParsedRequestListener
import com.indra.slide.utils.Utils
import com.shiburagi.imageloader.entities.Image
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    private var dirPath: String? = null
    lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { dirPath = getRootDirPath(it) }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainAdapter = MainAdapter(object: MainAdapter.Listener{
            override fun onDownloadClicked(url: String) {
                dirPath?.let {
                    Slide.download(url, dirPath, UUID.randomUUID().toString())
                        .setTag("downloadTest")
                        ?.setPriority(Priority.MEDIUM)
                        ?.build()
                        ?.startDownload(object: DownloadListener{
                            override fun onDownloadComplete() {

                            }

                            override fun onError(anError: ANError?) {

                            }
                        })
                }
            }
        })
        rvContent.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = mainAdapter
        }

        Slide.get("https://pastebin.com/raw/wgkJgazE")
            .setTag(this)
            ?.setPriority(Priority.HIGH)
            ?.build()
            ?.getAsObjectList(Image::class.java, object: ParsedRequestListener<List<Image>>{
                override fun onResponse(response: List<Image>) {
                    mainAdapter.updateData(response)

                }
                override fun onError(anError: ANError?) {

                }
            })
    }
}
