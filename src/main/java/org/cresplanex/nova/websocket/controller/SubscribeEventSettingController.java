package org.cresplanex.nova.websocket.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subscribe-event-setting")
@AllArgsConstructor
public class SubscribeEventSettingController {

    @RequestMapping(method = RequestMethod.GET)
    public String getSubscribeEventSetting() {
        return "Subscribe event setting retrieved successfully.";
    }
}
