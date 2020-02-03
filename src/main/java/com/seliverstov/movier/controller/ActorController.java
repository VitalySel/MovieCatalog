package com.seliverstov.movier.controller;

import com.seliverstov.movier.repository.ActorRepository;
import com.seliverstov.movier.repository.GenresRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ActorController {

    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private GenresRepository genresRepository;

    @GetMapping(value = "/actors")
    public String actor(Model model){
        model.addAttribute("genres",genresRepository.findAll(Sort.by(Sort.Direction.ASC,"genreName")));
        model.addAttribute("actors",actorRepository.findAll(Sort.by(Sort.Direction.ASC,"name")));
        return "actors";
    }

    @GetMapping(value = "/actor/search")
    public String searchActor(@RequestParam(required = false, defaultValue = "") String name,Model model) {

        if (name.isEmpty()){
            model.addAttribute("genres",genresRepository.findAll(Sort.by(Sort.Direction.ASC,"genreName")));
            model.addAttribute("actors",actorRepository.findAll(Sort.by(Sort.Direction.ASC,"name")));
            return "searchActor";
        }

        if (actorRepository.findByName(name) == null) {
            model.addAttribute("message", "Actor not exists");
            model.addAttribute("genres",genresRepository.findAll(Sort.by(Sort.Direction.ASC,"genreName")));
            return "searchActor";
        }

        model.addAttribute("actors",actorRepository.findByName(name));
        model.addAttribute("genres",genresRepository.findAll(Sort.by(Sort.Direction.ASC,"genreName")));
        return "searchActor";
    }
}
