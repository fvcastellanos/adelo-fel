package net.cavitos.aldelo.fel.builder;

import org.springframework.http.HttpStatus;
import net.cavitos.aldelo.fel.domain.exception.BusinessException;

import static java.lang.String.format;

import java.util.List;

public final class BusinessExceptionBuilder {

    public static BusinessException createBusinessException(final HttpStatus httpStatus,
            final String message,
            final Object... values) {

        return new BusinessException(httpStatus, format(message, values));
    }

    public static BusinessException createBusinessException(final HttpStatus httpStatus,
            final String message,
            final List<String> details,
            final Object... values) {

        return new BusinessException(httpStatus, format(message, values), details);
    }

    public static BusinessException createBusinessException(final String message,
            final Object... values) {

        return createBusinessException(HttpStatus.INTERNAL_SERVER_ERROR, message, values);
    }
}
