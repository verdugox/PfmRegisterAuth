package api.register.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@ToString
@Builder
@EqualsAndHashCode(of={"identityDni"})
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "users")
public class User implements Serializable
{
    @Id
    private String id;
    @NotNull
    @Indexed(unique = true)
    @Column(nullable = false, length = 8)
    private String identityDni;
    @NotEmpty
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String firstName;
    @NotEmpty
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String lastName;
    @NotEmpty
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String address;
    @NotNull
    @Indexed(unique = true)
    @Column(nullable = false, length = 9)
    private Integer phone;
    @NotEmpty
    @Indexed(unique = true)
    @Size(max = 100)
    @Column(nullable = false, length = 100)
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
