package com.nepbrain.technology.catalog.annotation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE)
@RestController @RequestMapping("/api/v2/catalog")
public @interface CustomCatalogRestController {}
