package com.sanzuriver.oneblue.Controller;

import com.sanzuriver.oneblue.Entity.VO.ResponseCodeEnums;
import com.sanzuriver.oneblue.Entity.VO.ResponseInfo;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自定义ErrorController处理404
 */
@RestController
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public Object handleError() {
        return new ResponseInfo<>(ResponseCodeEnums.NOT_FOUND);
    }
}
