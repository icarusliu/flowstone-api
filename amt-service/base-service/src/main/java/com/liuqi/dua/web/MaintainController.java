package com.liuqi.dua.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 导入导出控制器
 *
 * @author  LiuQi 2024/8/17-14:22
 * @version V1.0
 **/
@RestController
@RequestMapping("/base/api/maintain")
public class MaintainController {
    /**
     * 接口导入
     */
    @PostMapping("import")
    public void importApis(@RequestParam MultipartFile file) {

    }

    @GetMapping("export")
    public void exportApis() {

    }
}
