package api.register.presentation.mapper;

import api.register.domain.User;
import api.register.presentation.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User modelToEntity (UserModel model);
    UserModel entityToModel(User event);
    @Mapping(target = "id", ignore=true)
    void update(@MappingTarget User entity, User updateEntity);

}
