package api.register.presentation;

import api.register.application.UserService;
import api.register.domain.User;
import api.register.presentation.mapper.UserMapper;
import api.register.presentation.model.UserModel;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/user")
public class UserController {

    @Autowired(required = true)
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Operation(summary = "Listar todos los usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se listaron todos los usuarios registrados",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @GetMapping("/findAll")
    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackGetAllUsers")
    @TimeLimiter(name = "userTimeLimiter")
    public Flux<UserModel> getAll() {
        log.info("getAll executed");
        return userService.findAll()
                .map(user -> userMapper.entityToModel(user));
    }


    @Operation(summary = "Listar todos los usuarios por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se listaron todos los usuarios por Id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @GetMapping("/findById/{id}")
    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackFindById")
    @TimeLimiter(name = "userTimeLimiter")
    public Mono<ResponseEntity<UserModel>> findById(@PathVariable String id){
        return userService.findById(id)
                .map(user -> userMapper.entityToModel(user))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar todos los usuarios por DNI")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se listaron todos los usuarios por DNI",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @GetMapping("/findByIdentityDni/{identityDni}")
    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackFindByIdentityDni")
    @TimeLimiter(name = "userTimeLimiter")
    public Mono<ResponseEntity<UserModel>> findByIdentityDni(@PathVariable String identityDni){
        log.info("findByIdentityDni executed {}", identityDni);
        Mono<User> response = userService.findByIdentityDni(identityDni);
        return response
                .map(user -> userMapper.entityToModel(user))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registro de los Usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se registro el Usuario de manera exitosa",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @PostMapping
    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackCreateUser")
    @TimeLimiter(name = "userTimeLimiter")
    public Mono<ResponseEntity<UserModel>> create(@Valid @RequestBody UserModel request){
        log.info("create executed {}", request);
        return userService.create(userMapper.modelToEntity(request))
                .map(user -> userMapper.entityToModel(user))
                .flatMap(c -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", "register", "9080", "user", c.getId())))
                        .body(c)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar el usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se actualizará el usuario por el ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @PutMapping("/{id}")
    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackUpdateUser")
    @TimeLimiter(name = "userTimeLimiter")
    public Mono<ResponseEntity<UserModel>> updateById(@PathVariable String id, @Valid @RequestBody UserModel request){
        log.info("updateById executed {}:{}", id, request);
        return userService.update(id, userMapper.modelToEntity(request))
                .map(user -> userMapper.entityToModel(user))
                .flatMap(c -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", "register", "9080", "user", c.getId())))
                        .body(c)))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Eliminar Usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se elimino el usuario por ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackDeleteUser")
    @TimeLimiter(name = "userTimeLimiter")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id){
        log.info("deleteById executed {}", id);
        return userService.delete(id)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


}
