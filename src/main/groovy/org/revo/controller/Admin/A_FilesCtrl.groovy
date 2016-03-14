package org.revo.controller.Admin

import org.revo.service.impl.FileUtils
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Created by revo on 3/14/16.
 */
@Controller
@RequestMapping(value = "/api/admin/files")
class A_FilesCtrl {
    @RequestMapping(value = "/{name}/{ext}", method = RequestMethod.GET)
    @ResponseBody
    def download(@PathVariable("name") String name,@PathVariable("ext") String ext) {
        FileUtils.DownloadFile(name + "."+ext)
    }
}
