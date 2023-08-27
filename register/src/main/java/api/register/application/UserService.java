package api.register.application;

import api.register.config.CircuitResilienceListener;
import api.register.domain.User;
import api.register.domain.UserRepository;
import api.register.presentation.mapper.UserMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CircuitResilienceListener circuitResilienceListener;
    @Autowired
    private TimeLimiterRegistry timeLimiterRegistry;
    @Autowired
    private UserMapper userMapper;

    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackGetAllUsers")
    @TimeLimiter(name = "userTimeLimiter")
    public Flux<User> findAll(){
        log.debug("findAll executed");
        return userRepository.findAll();
    }

    @Cacheable(value = "userCache", key = "#getById")
    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackFindById")
    @TimeLimiter(name = "userTimeLimiter")
    public Mono<User> findById(String userId)
    {
        log.debug("findById executed {}" , userId);
        return userRepository.findById(userId);
    }

    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackGetAllItems")
    @TimeLimiter(name = "userTimeLimiter")
    public Mono<User> findByIdentityDni(String identityDni){
        log.debug("findByIdentityDni executed {}" , identityDni);
        return userRepository.findByIdentityDni(identityDni);
    }

    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackFindByIdentityDni")
    @TimeLimiter(name = "userTimeLimiter")
    public Mono<User> create(User user){
        log.debug("create executed {}",user);
        String randomIMEI = generateRandomIMEI();
        user.setImei(randomIMEI);
        user.setDateRegister(LocalDate.now());
        return userRepository.save(user);
    }

    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackCreateUser")
    @TimeLimiter(name = "userTimeLimiter")
    private String generateRandomIMEI() {
        Random random = new Random();
        StringBuilder imei = new StringBuilder("35");
        for (int i = 0; i < 13; i++) {
            imei.append(random.nextInt(10));
        }
        return imei.toString();
    }

    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackUpdateUser")
    @TimeLimiter(name = "userTimeLimiter")
    public Mono<User> update(String userId, User user){
        log.debug("update executed {}:{}", userId, user);
        return userRepository.findById(userId)
                .flatMap(dbUser -> {
                    user.setImei(dbUser.getImei());
                    user.setDateRegister(dbUser.getDateRegister());
                    userMapper.update(dbUser, user);
                    return userRepository.save(dbUser);
                });
    }

    @CircuitBreaker(name = "userCircuit", fallbackMethod = "fallbackDeleteUser")
    @TimeLimiter(name = "userTimeLimiter")
    public Mono<User>delete(String userId){
        log.debug("delete executed {}",userId);
        return userRepository.findById(userId)
                .flatMap(existingUser -> userRepository.delete(existingUser)
                        .then(Mono.just(existingUser)));
    }

//    public Flux<User> fallbackGetAllUsers(Throwable throwable) {
//        log.error("Fallback executed for findAll: {}", throwable.getMessage());
//        // Puedes agregar lógica adicional aquí si es necesario
//        return Flux.empty();
//    }
//
//    public Mono<User> fallbackFindById(String userId, Throwable throwable) {
//        log.error("Fallback executed for findById {}: {}", userId, throwable.getMessage());
//        // Puedes agregar lógica adicional aquí si es necesario
//        return Mono.empty();
//    }
}
