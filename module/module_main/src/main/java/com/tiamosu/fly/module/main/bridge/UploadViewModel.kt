package com.tiamosu.fly.module.main.bridge

import com.tiamosu.fly.http.callback.StringCallback
import com.tiamosu.fly.http.model.Progress
import com.tiamosu.fly.http.model.Response
import com.tiamosu.fly.http.request.base.ProgressRequestBody
import com.tiamosu.fly.module.common.base.BaseViewModel
import com.tiamosu.fly.module.main.data.repository.HttpRequestManager
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
        HttpRequestManager.uploadFile(object : StringCallback() {
            override fun onSuccess(response: Response<String>) {
                showInfo("文件上传成功！")
            }

            override fun uploadProgress(progress: Progress) {
            }

            override fun onError(response: Response<String>) {
                showError("上传文件失败！")
            }
        }, progressCallBack, "avatar", File(""))
    }
}