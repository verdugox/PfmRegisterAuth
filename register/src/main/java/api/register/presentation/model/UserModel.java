package api.register.presentation.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate dateRegister;

    @JsonIgnore
    private boolean scanAvailable;
    @JsonIgnore
    private int prefetch;
}
