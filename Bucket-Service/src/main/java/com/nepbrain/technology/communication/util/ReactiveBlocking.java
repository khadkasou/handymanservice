package com.nepbrain.technology.communication.util;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Runs blocking communication/JPA work on a bounded-elastic scheduler so controllers work on WebFlux.
 * Propagates the reactive security context so {@code CurrentUser} works inside the callable.
 */
public final class ReactiveBlocking {

    private ReactiveBlocking() {
    }

    public static <T> Mono<T> call(CallableWithException<T> callable) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext ->
                        Mono.fromCallable(() -> runWithSecurityContext(securityContext, callable))
                                .subscribeOn(Schedulers.boundedElastic())
                )
                .switchIfEmpty(
                        Mono.fromCallable(callable::call).subscribeOn(Schedulers.boundedElastic())
                );
    }

    private static <T> T runWithSecurityContext(SecurityContext securityContext, CallableWithException<T> callable)
            throws Exception {
        SecurityContext previous = SecurityContextHolder.getContext();
        try {
            SecurityContextHolder.setContext(securityContext);
            return callable.call();
        } finally {
            SecurityContextHolder.setContext(previous);
        }
    }

    @FunctionalInterface
    public interface CallableWithException<T> {
        T call() throws Exception;
    }
}
