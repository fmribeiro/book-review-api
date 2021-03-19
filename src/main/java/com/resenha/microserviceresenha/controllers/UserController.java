package com.resenha.microserviceresenha.controllers;

import com.resenha.microserviceresenha.controllers.assemblers.UserModelAssembler;
import com.resenha.microserviceresenha.data.model.User;
import com.resenha.microserviceresenha.dto.UserDTO;
import com.resenha.microserviceresenha.dto.model.UserModelDTO;
import com.resenha.microserviceresenha.exceptions.RecordNotFoundException;
import com.resenha.microserviceresenha.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Api(value = "UserController")
@AllArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final UserModelAssembler assembler;

    @ApiOperation(value = "Fetch all users", response = UserDTO.class)
    @GetMapping(value = "/users")
    public ResponseEntity<List<UserDTO>> findAllUsers() {
        List<UserDTO> allUsers = userService.getAllUsers();
        if(allUsers.isEmpty()){
            throw new RecordNotFoundException("sem filtro");
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(allUsers);
    }

    @ApiOperation(value = "Fetch all followed users", response = User.class)
    @GetMapping(value = "/users/following/{userId}")
    public ResponseEntity<List<UserDTO>> mountFollowersProfile(@PathVariable String userId) {
        List<UserDTO> allUsers = userService.mountFollowersProfile(userId);
        if (allUsers.isEmpty()) {
            throw new RecordNotFoundException(userId);
        }

        return ResponseEntity
                .ok()
//                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(allUsers);
    }

    @ApiOperation(value = "Fetch books, reviews, follow users of the user", response = User.class)
    @GetMapping(value = "/users/profile/{userId}")
    public ResponseEntity<List<UserDTO>> mountUserProfile(@PathVariable String userId) {
        List<UserDTO> allUsers = userService.mountSingleUserProfile(userId);
        if (allUsers.isEmpty()) {
            throw new RecordNotFoundException(userId);
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(allUsers);
    }

    @ApiOperation(value = "Search a user by id", response = UserModelDTO.class)
    @GetMapping(value = "/users/{id}")
    public ResponseEntity<UserModelDTO> findById(@PathVariable String id) {
        UserModelDTO byId = userService.findById(id);
        if (Objects.isNull(byId)) {
            throw new RecordNotFoundException(id);
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                .body(byId);
    }

    @ApiOperation(value = "Search a user by idToken", response = UserModelDTO.class)
    @GetMapping(value = "/users/idToken/{idToken}")
    public ResponseEntity<UserModelDTO> findByIdToken(@PathVariable String idToken) {
        UserModelDTO byId = userService.findByIdToken(idToken);
        if (Objects.isNull(byId)) {
            throw new RecordNotFoundException(idToken);
        }

        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                .body(byId);
    }

    @ApiOperation(value = "Create a user", response = UserModelDTO.class)
    @PostMapping(value = "/users")
    public ResponseEntity<?> createUser(@RequestBody UserModelDTO userModelDTO) {
        Optional<User> user = userService.saveOrUpdate(userModelDTO);
        if(user.isPresent()){
            EntityModel<User> savedBook = assembler.toModel(user.get());
            return ResponseEntity
                    .created(savedBook.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(savedBook);
        }else{
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }

    @ApiOperation(value = "Update a user", response = UserModelDTO.class)
    @PutMapping(value = "/users")
    public ResponseEntity<?> updateUser(@RequestBody UserModelDTO userModelDTO) {
        Optional<User> review = userService.saveOrUpdate(userModelDTO);
        if(review.isPresent()){
            EntityModel<User> savedBook = assembler.toModel(review.get());
            return ResponseEntity
                    .created(savedBook.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(savedBook);
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Delete a book user")
    @DeleteMapping("/users/{userId}")
    ResponseEntity<?> deleteUser(@PathVariable String userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

}
