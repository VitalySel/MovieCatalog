package com.seliverstov.movier.controller;

import com.seliverstov.movier.domain.*;
import com.seliverstov.movier.repository.*;
import com.seliverstov.movier.service.*;
import com.sun.org.apache.regexp.internal.RE;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ProducerRepository producerRepository;
    @Autowired
    private ActorRepository actorRepository;
    @Autowired
    private GenresRepository genresRepository;


    @Autowired
    private UserService userService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private ProducerService producerService;
    @Autowired
    private ActorService actorService;
    @Autowired
    private GenresService genresService;


    @Value("${upload.path}")
    private String uploadPath;

    @RequestMapping(value = "/admin")
    public String admin(Map<String,Object> map)
    {
        map.put("userCount",userRepository.count());
        map.put("movieCount", movieRepository.count());
        map.put("actorCount", actorRepository.count());
        map.put("producerCount",producerRepository.count());
        map.put("genresCount",genresRepository.count());
        return "admin";
    }

    @RequestMapping(value = {"/userList"}, method = RequestMethod.GET)
    public String getUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "userList";
    }

    @RequestMapping(value = {"{userid}/userProfileAdmin"}, method = RequestMethod.GET)
    public String getUserProfileAdmin(@PathVariable String userid, Model model) throws NotFoundException {
        User user = userService.getUserId(Long.valueOf(userid));
        model.addAttribute("users", user);
        return "userProfileAdmin";
    }

    @RequestMapping(value = {"{userid}/userEditAdmin"}, method = RequestMethod.GET)
    public String getUserEditAdmin(@PathVariable String userid,Model model) throws NotFoundException {
        User user = userService.getUserId(Long.valueOf(userid));
        model.addAttribute("users",user);
        return "userEditAdmin";
    }

    @RequestMapping(value = "userEdit" ,method = RequestMethod.POST)
    public String getUserEditAdmin(@RequestParam String username, String realname,String mail, @RequestParam String userId) throws NotFoundException {
        User usr = userService.getUserId(Long.valueOf(userId));
        usr.setUsername(username);
        usr.setRealname(realname);
        usr.setMail(mail);
        userService.update(usr);
        return  "redirect:"+userId+"/userProfileAdmin";
    }

    @RequestMapping(value = {"/userMakeAdmin"},method = RequestMethod.GET)
    public String userMakeAdmin(Model model) throws NotFoundException {
        List<User> users =new ArrayList<>(userRepository.findAll());
        List<User> userNotAdmin =new ArrayList<>();
        for (User user: users) {
            Set userRoles = user.getRoles();
            if (!userRoles.contains(Role.ADMIN)){
                 userNotAdmin.add(user);
            }
        }
        model.addAttribute("users", userNotAdmin);
        return "userMakeAdmin";
    }

    @RequestMapping(value = {"/makeAdmin"},method = RequestMethod.POST)
    public String makeAdmin(@RequestParam String userid, @RequestParam Map<String,String> form) throws NotFoundException {
        User user = userService.getUserId(Long.valueOf(userid));
        user.getRoles().add(Role.ADMIN);

        userService.update(user);
        return "redirect:/userList";
    }

    @RequestMapping(value = {"/movieAdmin"},method = RequestMethod.GET)
    public String movieAdmin(Model model) {
        model.addAttribute("movies", movieRepository.findAll());
        return "movieAdmin";
    }

    @RequestMapping(value = {"/addMovieAdmin"},method = RequestMethod.GET)
        public String addMovieAdmin(Model model) {
        return "addMovieAdmin";
    }

    @RequestMapping(value = {"/addMovie"},method = RequestMethod.POST)
    public String addMovieAdminForm(@RequestParam String name, @RequestParam String releaseDate,
                                    @RequestParam String description, @RequestParam String duration, @RequestParam String budget,@RequestParam String producerName) {
        Movie movie = new Movie(name,releaseDate,description,duration,budget,producerRepository.findByName(producerName));
        movieRepository.save(movie);
        return "redirect:/movieAdmin";
    }


    @RequestMapping(value = {"/producerAdmin"}, method = RequestMethod.GET)
    public String producerAdmin(Model model) {
        model.addAttribute("producers", producerRepository.findAll());
        return "producerAdmin";
    }

    @RequestMapping(value = {"/addProducerAdmin"}, method = RequestMethod.GET)
    public String addProducerAdmin(Model model) {
        return "addProducerAdmin";
    }

    @RequestMapping(value = {"/addProducer"}, method = RequestMethod.POST)
    public String addProducerAdminForm(@RequestParam String name, @RequestParam String country, @RequestParam String date) {
        Producer producer = new Producer(name,country,date);
        producerRepository.save(producer);
        return "redirect:/producerAdmin";
    }


    @RequestMapping(value = {"{movieid}/movierInfoAdmin"}, method = RequestMethod.GET)
    public String getMovieInfoAdmin(@PathVariable String movieid, Model model) throws NotFoundException {
        Movie movie = movieService.getMovieId(Integer.parseInt(movieid));
        model.addAttribute("movies",movie);
        return "movierInfoAdmin";
    }

    @RequestMapping(value = {"{movieid}/movierEditAdmin"}, method = RequestMethod.GET)
    public String getMovieEditAdmin(@PathVariable String movieid, Model model) throws NotFoundException {
        Movie movie = movieService.getMovieId(Integer.parseInt(movieid));
        model.addAttribute("movies",movie);
        return "movierEditAdmin";
    }

    @RequestMapping(value = "movieEdit" ,method = RequestMethod.POST)
    public String getMovieEditAdminForm(@RequestParam String name, String releaseDate,String description, String duration, String budget, @RequestParam String movieId) throws NotFoundException {
        Movie movie = movieService.getMovieId(Integer.parseInt(movieId));
        movie.setName(name);
        movie.setReleaseDate(releaseDate);
        movie.setDescription(description);
        movie.setDuration(duration);
        movie.setBudget(budget);
        movieService.update(movie);
        return  "redirect:"+movieId+"/movierInfoAdmin";
    }

    @RequestMapping(value = {"{producerid}/producerInfoAdmin"}, method = RequestMethod.GET)
    public String getProducerInfoAdmin(@PathVariable String producerid, Model model) throws NotFoundException {
        Producer producer = producerService.getProducerId(Integer.parseInt(producerid));
        model.addAttribute("producers", producer);
        return "producerInfoAdmin";
    }

    @RequestMapping(value = {"{producerid}/producerEditAdmin"}, method = RequestMethod.GET)
    public String getProducerEditAdmin(@PathVariable String producerid, Model model) throws NotFoundException {
        Producer producer = producerService.getProducerId(Integer.parseInt(producerid));
        model.addAttribute("producers",producer);
        return "producerEditAdmin";
    }

    @RequestMapping(value = "producerEdit", method = RequestMethod.POST)
    public String getProducerEditAdminForm(@RequestParam String name, String country, String date, @RequestParam String producerId) throws NotFoundException {
        Producer producer = producerService.getProducerId(Integer.parseInt(producerId));
        producer.setName(name);
        producer.setCountry(country);
        producer.setDate(date);
        producerService.update(producer);
        return "redirect:" + producerId + "/producerInfoAdmin";
    }

    @RequestMapping(value = "/actorAdmin",method = RequestMethod.GET)
    public String actorAdmin(Model model){
        model.addAttribute("actors", actorRepository.findAll());
        return "actorAdmin";
    }

    @RequestMapping(value = "{actorid}/actorInfoAdmin",method = RequestMethod.GET)
    public String getActorInfoAdmin(@PathVariable String actorid, Model model) throws NotFoundException {
        Actor actor = actorService.getActorId(Integer.parseInt(actorid));
        model.addAttribute("actors", actor);
        return "actorInfoAdmin";
    }

    @RequestMapping(value = "addActorAdmin", method = RequestMethod.GET)
    public String addActorAdmin() {
        return "addActorAdmin";
    }

    @RequestMapping(value = {"/addActor"}, method = RequestMethod.POST)
    public String addActorAdminForm(@RequestParam String name, @RequestParam String country, @RequestParam String date) {
        Actor actor = new Actor(name,country,date);
        actorRepository.save(actor);
        return "redirect:/actorAdmin";
    }

    @RequestMapping(value = {"{actorid}/actorEditAdmin"}, method = RequestMethod.GET)
    public String getActorEditAdmin(@PathVariable String actorid, Model model) throws NotFoundException {
        Actor actor = actorService.getActorId(Integer.parseInt(actorid));
        model.addAttribute("actors",actor);
        return "actorEditAdmin";
    }

    @RequestMapping(value = "actorEdit", method = RequestMethod.POST)
    public String getActorEditAdminForm(@RequestParam String name, String country, String date, @RequestParam String actorId) throws NotFoundException {
        Actor actor = actorService.getActorId(Integer.parseInt(actorId));
        actor.setName(name);
        actor.setCountry(country);
        actor.setDate(date);
        actorService.save(actor);
        return "redirect:" + actorId + "/actorInfoAdmin";
    }

    @RequestMapping(value = {"{movieid}/addMovieActor"}, method = RequestMethod.GET)
    public String addMovieActor(@PathVariable String movieid, Model model) throws NotFoundException {
        Movie movie = movieService.getMovieId(Integer.parseInt(movieid));
        model.addAttribute("movies",movie);
        return "addMovieActor";
    }

    @RequestMapping(value = {"addMovieActor"}, method = RequestMethod.POST)
    public String addMovieActorForm(@RequestParam String movieId, @RequestParam String actorName) throws NotFoundException {
        List<String> actorNameInput = Arrays.asList(actorName.split(","));
        Movie movie = movieService.getMovieId(Integer.parseInt(movieId));
        movie.setActors(actorService.findActorName(actorNameInput));
        movieService.update(movie);
        return "redirect:" + movieId + "/movierInfoAdmin";
    }

    @RequestMapping(value = "/genresAdmin",method = RequestMethod.GET)
    public String genresAdmin(Model model){
        model.addAttribute("genres", genresRepository.findAll());
        return "genresAdmin";
    }

    @RequestMapping(value = "/addGenresAdmin",method = RequestMethod.GET)
    public String addGenresAdmin(Model model){
        return "addGenresAdmin";
    }

    @RequestMapping(value = "/addGenre",method = RequestMethod.POST)
    public String addGenresAdminForm(@RequestParam String name, Map<String,Object> map) throws Exception {
        Genres genreFromDb = genresService.getGenresName(name);

        if (genreFromDb != null) {
            map.put("message","Genres already exists");
            return "addGenresAdmin";
        }

        Genres genres = new Genres(name);
        genresService.save(genres);

        return "redirect:/genresAdmin";
    }

    @RequestMapping(value = {"{genreid}/genresInfoAdmin"},method = RequestMethod.GET)
    public String genresInfoAdmin(@PathVariable String genreid, Model model) throws Exception {
        Genres genres = genresService.getGenresId(Integer.parseInt(genreid));
        model.addAttribute("genres", genres);
        return "genresInfoAdmin";
    }

    @RequestMapping(value = {"{genreid}/genresEditAdmin"},method = RequestMethod.GET)
    public String genresEditAdmin(@PathVariable String genreid, Model model) throws Exception {
        Genres genres = genresService.getGenresId(Integer.parseInt(genreid));
        model.addAttribute("genres", genres);
        return "genresEditAdmin";
    }

    @RequestMapping(value = "genreEdit", method = RequestMethod.POST)
    public String getGenresEditAdminForm(@RequestParam String name, @RequestParam String genreId, Map<String, Object> map) throws Exception {
        Genres genresFromDb = genresService.getGenresName(name);
        if (genresFromDb != null) {
            map.put("message","Genres already exists");
            return "genresEditAdmin";
        }

        Genres genres = genresService.getGenresId(Integer.parseInt(genreId));
        genres.setGenreName(name);
        genresService.save(genres);

        return "redirect:" + genreId + "/genresInfoAdmin";
    }

    @RequestMapping(value = {"{movieid}/addMovieGenres"}, method = RequestMethod.GET)
    public String addMovieGenres(@PathVariable String movieid, Model model) throws NotFoundException {
        Movie movie = movieService.getMovieId(Integer.parseInt(movieid));
        model.addAttribute("movies",movie);
        return "addMovieGenres";
    }

    @RequestMapping(value = {"addMovieGenre"}, method = RequestMethod.POST)
    public String addMovieGenreForm(@RequestParam String movieId, @RequestParam String genreName) throws NotFoundException {
        List<String> genreNameInput = Arrays.asList(genreName.split(","));
        Movie movie = movieService.getMovieId(Integer.parseInt(movieId));
        movie.setGenres(genresService.findGenreName(genreNameInput));
        movieService.update(movie);
        return "redirect:" + movieId + "/movierInfoAdmin";
    }

    @RequestMapping(value = {"{actorid}/addActorGenres"}, method = RequestMethod.GET)
    public String addActorGenres(@PathVariable String actorid, Model model) throws NotFoundException {
        Actor actor = actorService.getActorId(Integer.parseInt(actorid));
        model.addAttribute("actors",actor);
        return "addActorGenres";
    }

    @RequestMapping(value = {"addActorGenre"}, method = RequestMethod.POST)
    public String addActorGenreForm(@RequestParam String actorId, @RequestParam String genreName) throws NotFoundException {
        List<String> genreNameInput = Arrays.asList(genreName.split(","));
        Actor actor = actorService.getActorId(Integer.parseInt(actorId));
        actor.setGenres(genresService.findGenreName(genreNameInput));
        actorService.save(actor);
        return "redirect:" + actorId + "/actorInfoAdmin";
    }

    @RequestMapping(value = {"{producerid}/addProducerGenres"}, method = RequestMethod.GET)
    public String addProducerGenres(@PathVariable String producerid, Model model) throws NotFoundException {
        Producer producer = producerService.getProducerId(Integer.parseInt(producerid));
        model.addAttribute("producers",producer);
        return "addProducerGenres";
    }


    @RequestMapping(value = {"addProducerGenre"}, method = RequestMethod.POST)
    public String addProducerGenreForm(@RequestParam String producerId, @RequestParam String genreName) throws NotFoundException {
        List<String> genreNameInput = Arrays.asList(genreName.split(","));
        Producer producer = producerService.getProducerId(Integer.parseInt(producerId));
        producer.setGenres(genresService.findGenreName(genreNameInput));
        producerService.update(producer);
        return "redirect:" + producerId + "/producerInfoAdmin";
    }

    @RequestMapping(value = "/dataParsing",method = RequestMethod.GET)
    public String parsing(Model model){
        return "dataparsing";
    }

}
