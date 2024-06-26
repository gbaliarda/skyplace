package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.service.FavoriteService;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

import static ar.edu.itba.paw.webapp.helpers.Utils.parseInt;

@Controller
public class FavoriteController {

    private final UserService userService;
    private final FavoriteService favoriteService;

    @Autowired
    public FavoriteController(UserService userService, FavoriteService favoriteService) {
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    @RequestMapping(value = "/favorite/{productId:\\d+}/add", method = RequestMethod.POST)
    public String addFavorite(@PathVariable int productId, HttpServletRequest request){
        userService.getCurrentUser().ifPresent(user -> favoriteService.addNftFavorite(productId, user));
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }

    @RequestMapping(value = "/favorite/{productId:\\d+}/remove", method = RequestMethod.POST)
    public String removeFavorite(@PathVariable int productId, HttpServletRequest request){
        userService.getCurrentUser().ifPresent(user -> favoriteService.removeNftFavorite(productId, user));
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }
}
