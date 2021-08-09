package rx;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.List;

@Data
@Getter
@Setter
@Builder
public class Response {

    @Singular
    private List<User> users;
    private Boolean success;
}
