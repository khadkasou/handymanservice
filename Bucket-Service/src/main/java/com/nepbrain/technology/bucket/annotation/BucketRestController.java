package com.nepbrain.technology.bucket.annotation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * @Created At 10/03/2024
 * @Author suroj.awal
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RestController
@RequestMapping("/api/v2/bucket")
public @interface BucketRestController {
}
