package api.register.domain.user;

import api.register.application.UserService;
import api.register.config.CircuitResilienceListener;
import api.register.domain.User;
import api.register.domain.UserRepository;
import api.register.presentation.mapper.UserMapper;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveHashOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CircuitResilienceListener circuitResilienceListener;

    @Mock
    private TimeLimiterRegistry timeLimiterRegistry;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ReactiveHashOperations<String, String, User> hashOperations;

    @Test
    void testFindByIdentityDni() {
        User user = createUser("1", "12345678", "John", "Doe", "123 Main St", 987654321, "john@example.com", "1234567890123456", LocalDate.of(2022, 1, 15),true,0);
        when(userRepository.findByIdentityDni("12345678")).thenReturn(Mono.just(user));
        Mono<User> result = userService.findByIdentityDni("12345678");
        StepVerifier.create(result)
                .expectNext(user)
                .expectComplete()
                .verify();
    }

    @Test
    void testCreate() {
        User newUser = createUser("3", "12345678", "John", "Doe", "123 Main St", 987654321, "john@example.com", "1234567890123456", LocalDate.of(2022, 1, 15),true,0);
        when(userRepository.save(newUser)).thenReturn(Mono.just(newUser));
        Mono<User> result = userService.create(newUser);
        StepVerifier.create(result)
                .expectNext(newUser)
                .expectComplete()
                .verify();
    }

    @Test
    void testUpdate() {
        User existingUser = createUser("1", "12345678", "John", "Doe", "123 Main St", 987654321, "john@example.com", "1234567890123456", LocalDate.of(2022, 1, 15),true,0);
        User updatedUser = createUser("1", "12345678", "John", "Doe", "123 Main St", 987654321, "john@example.com", "1234567890123456", LocalDate.of(2022, 1, 15),true,0);

        updatedUser.setFirstName("Updated");
        when(userRepository.findById("1")).thenReturn(Mono.just(existingUser));
        when(userRepository.save(existingUser)).thenReturn(Mono.just(updatedUser));
        Mono<User> result = userService.update("1", updatedUser);

        StepVerifier.create(result)
                .expectNext(updatedUser)
                .expectComplete()
                .verify();
    }

    @Test
    void testDelete() {
        User userToDelete = createUser("1", "12345678", "John", "Doe", "123 Main St", 987654321, "john@example.com", "1234567890123456", LocalDate.of(2022, 1, 15),true,0);
        when(userRepository.findById("1")).thenReturn(Mono.just(userToDelete));
        when(userRepository.delete(userToDelete)).thenReturn(Mono.empty());

        Mono<User> result = userService.delete("1");

        StepVerifier.create(result)
                .expectNext(userToDelete)
                .expectComplete()
                .verify();
    }

    private User createUser(String id,String identityDni,String firstName, String lastName, String address, Integer phone, String email, String imei, LocalDate dateRegister, Boolean scanAvailable, Integer prefetch) {
        User user = new User();
        user.setId(id);
        user.setIdentityDni(identityDni);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setAddress(address);
        user.setPhone(phone);
        user.setEmail(email);
        user.setImei(imei);
        user.setDateRegister(dateRegister);
        user.setScanAvailable(scanAvailable);
        user.setPrefetch(prefetch);
        // Set other properties here
        return user;
    }


}
