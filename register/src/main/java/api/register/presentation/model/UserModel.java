package api.register.presentation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModel implements Serializable
{
    @JsonIgnore
    private String id;

    @NotBlank(message="DNI Number cannot be null or empty")
    private String identityDni;

    @NotBlank(message="Name cannot be null or empty")
    private String firstName;

    @NotBlank(message="LastName cannot be null or empty")
    private String lastName;

    @NotBlank(message="BusinessName cannot be null or empty")
    private String address;

    @NotNull(message="Phone cannot be null or empty")
    private Integer phone;

    @NotBlank(message="Email cannot be null or empty")
    private String email;

    private String imei;

    private LocalDate dateRegister;

    @JsonIgnore
    private boolean scanAvailable;
    @JsonIgnore
    private int prefetch;
}
