package com.seliverstov.movier.controller;

import com.seliverstov.movier.domain.Genres;
import com.seliverstov.movier.domain.Movie;
import com.seliverstov.movier.domain.User;
import com.seliverstov.movier.repository.GenresRepository;
import com.seliverstov.movier.repository.MovieRepository;
import com.seliverstov.movier.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieService movieService;
    @Autowired
    private GenresRepository genresRepository;

    @GetMapping(value = "/movie")
    public String movie(Model model){
        model.addAttribute("movies",movieRepository.findAll(Sort.by(Sort.Direction.DESC,"rating")));
        model.addAttribute("genres",genresRepository.findAll(Sort.by(Sort.Direction.ASC,"genreName")));
        return "movie";
    }

    @GetMapping(value = "/movie/search")
    public String movieBySearch(@RequestParam(required = false, defaultValue = "") String name, Model model) {

        if (name.isEmpty() || movieRepository.findByName(name) == null) {
            model.addAttribute("message", "Movie not exists");
            model.addAttribute("genres",genresRepository.findAll(Sort.by(Sort.Direction.ASC,"genreName")));
            return "search";
        }

        model.addAttribute("movies",movieRepository.findByName(name));
        model.addAttribute("genres",genresRepository.findAll(Sort.by(Sort.Direction.ASC,"genreName")));
        return "search";
    }


}
