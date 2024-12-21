package ru.job4j.site.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.site.dto.CategoryDTO;
import ru.job4j.site.dto.InterviewDTO;
import ru.job4j.site.service.*;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.job4j.site.controller.RequestResponseTools.getToken;

@Controller
@AllArgsConstructor
@Slf4j
public class IndexController {
    private final CategoriesService categoriesService;
    private final InterviewsService interviewsService;
    private final AuthService authService;
    private final NotificationService notifications;
    private final ProfilesService profilesService;

    @GetMapping({"/", "index"})
    public String getIndexPage(Model model, HttpServletRequest req) throws JsonProcessingException {
        RequestResponseTools.addAttrBreadcrumbs(model,
                "Главная", "/"
        );
        try {
            var categories = categoriesService.getMostPopular();
            model.addAttribute("categories", categories);
            Map<Integer, Integer> newByCategories = new HashMap<>();
            for (CategoryDTO category: categories) {
                var interviews = interviewsService.getByType(category.getId());
                int i = 0;
                for (InterviewDTO interview : interviews) {
                    if (LocalDateTime.parse(interview.getCreateDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")).isAfter(LocalDateTime.now().minusDays(7))) {
                        i++;
                    }
                }
                newByCategories.put(category.getId(), i);
            }
            model.addAttribute("new_categories", newByCategories);
            var token = getToken(req);
            if (token != null) {
                var userInfo = authService.userInfo(token);
                model.addAttribute("userInfo", userInfo);
                model.addAttribute("userDTO", notifications.findCategoriesByUserId(userInfo.getId()));
                RequestResponseTools.addAttrCanManage(model, userInfo);
            }
        } catch (Exception e) {
            log.error("Remote application not responding. Error: {}. {}, ", e.getCause(), e.getMessage());
        }
        var interviewsDTO = interviewsService.getByType(1);
        var profiles = interviewsDTO.stream().collect(Collectors.toMap(
                i -> i.getId(),
                i -> profilesService.getProfileById(i.getSubmitterId()).orElseThrow()
        ));
        model.addAttribute("new_interviews", interviewsDTO);
        model.addAttribute("profiles", profiles);
        return "index";
    }
}