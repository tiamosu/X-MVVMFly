package com.tiamosu.fly.demo.bridge

import com.tiamosu.fly.core.base.BaseViewModel
import com.tiamosu.fly.demo.data.repository.DataRepository
import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.http.model.Progress
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.http.request.base.ProgressRequestBody
import java.io.File

/**
 * @author tiamosu
 * @date 2020/3/22.
 */
class UploadViewModel : BaseViewModel() {

    fun uploadFile() {
        val progressCallBack = object : ProgressRequestBody.ProgressResponseCallBack {
            override fun onResponseProgress(progress: Progress) {
                //与 uploadProgress(progress: Progress) 返回一致，二选一
            }
        }
        DataRepository.instance.uploadFile(object : StringCallback() {
            override fun onSuccess(response: Response<String>) {
                showToastInfo("文件上传成功！")
            }

            override fun uploadProgress(progress: Progress) {
            }

            override fun onError(response: Response<String>) {
                showToastError("上传文件失败！")
            }
        }, progressCallBack, "avatar", File(""))
    }
}