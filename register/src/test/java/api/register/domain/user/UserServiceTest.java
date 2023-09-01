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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

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

    @Test
    public void testFindAllWithCircuitBreakerAndTimeLimiter() {
        // Configurar el comportamiento del mock userRepository
        List<User> userList = Arrays.asList(
                new User("1", "12345678", "John", "Doe", "123 Main St", 987654321, "john@example.com", "1234567890123456", LocalDate.of(2022, 1, 15),true,0),
                new User("2", "87654321", "Jane", "Smith", "456 Elm St", 987654322, "jane@example.com", "9876543210123456", LocalDate.of(2021, 5, 20),false,0)
        );
        when(userRepository.findAll()).thenReturn(Flux.fromIterable(userList));
        // Testing
        Flux<User> result = userService.findAll();
        // Verificar el comportamiento esperado
        StepVerifier.create(result)
                .expectNextCount(userList.size()) // Espera el mismo número de elementos que userList
                .verifyComplete();
    }

    @Test
    public void testFindAllWithCircuitBreakerAndTimeLimiterError() {
        // Configurar el comportamiento del mock userRepository para lanzar una excepción
        when(userRepository.findAll()).thenReturn(Flux.error(new RuntimeException("Error fetching users")));

        // Testing
        Flux<User> result = userService.findAll();

        // Verificar el comportamiento esperado
        StepVerifier.create(result)
                .expectError(RuntimeException.class) // Espera una excepción
                .verify();

        // Verificar que el CircuitBreaker y el TimeLimiter registraron el evento
        // Puedes agregar aserciones adicionales aquí si el CircuitBreaker y el TimeLimiter tienen métodos para comprobar el estado o el registro de eventos
    }

}
