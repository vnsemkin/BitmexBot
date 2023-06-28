package bitmex.bitmexspring.controllers.endpoints;

import bitmex.bitmexspring.services.BotService;
import bitmex.bitmexspring.services.BitmexBot;
import bitmex.bitmexspring.models.bitmex.ClientData;
import bitmex.bitmexspring.services.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
public class IndexController {
    private final UserInfo userInfo;
    private final BotService botService;
    private List<BitmexBot> botList;

    public IndexController(UserInfo userInfo, BotService botService) {
        this.botService = botService;
        this.userInfo = userInfo;
    }

    public List<BitmexBot> getBotList() {
        return botList;
    }


    @GetMapping("/")
    public String getHandler(Model model) {
        botList = botService.getBotList();
        model.addAttribute("botList", botList);
        return "home";
    }

    @PostMapping("/")
    public String postHandler(@RequestParam(value = "id", required = false) String id,
                              ClientData clientData,//Spring parsing parameters and create Object ClientData
                              Model model) {
        botList = botService.getBotList();
        if (Objects.nonNull(id)) {
            botService.deleteBot(Integer.parseInt(id));
            model.addAttribute("botList", botList);
            return "home";
        }

        clientData = userInfo.getUserInfo(clientData);
        botList = botService.call(clientData);
        model.addAttribute("botList", botList);
        return "home";
    }
}

