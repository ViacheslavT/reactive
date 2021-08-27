package webflux.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {

    private Object message;
}
