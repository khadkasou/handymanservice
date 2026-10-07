package com.nepbrain.technology.auth.config.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

/** Replacement for the deprecated SecurityJackson2Modules helper. */
public final class SecurityModuleRegistrar {

  private static final List<String> BASE_MODULES =
      List.of(
          "org.springframework.security.jackson2.CoreJackson2Module",
          "org.springframework.security.web.jackson2.WebJackson2Module",
          "org.springframework.security.web.server.jackson2.WebServerJackson2Module");

  private SecurityModuleRegistrar() {}

  public static List<Module> getModules(ClassLoader classLoader) {
    List<Module> modules = new ArrayList<>();
    BASE_MODULES.forEach(module -> addIfAvailable(module, classLoader, modules));

    addConditionalModule(
        "jakarta.servlet.http.Cookie",
        "org.springframework.security.web.jackson2.WebServletJackson2Module",
        classLoader,
        modules);
    addConditionalModule(
        "org.springframework.security.oauth2.client.OAuth2AuthorizedClient",
        "org.springframework.security.oauth2.client.jackson2.OAuth2ClientJackson2Module",
        classLoader,
        modules);
    addConditionalModule(
        "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule",
        "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule",
        classLoader,
        modules);
    addConditionalModule(
        "org.springframework.security.ldap.jackson2.LdapJackson2Module",
        "org.springframework.security.ldap.jackson2.LdapJackson2Module",
        classLoader,
        modules);
    addConditionalModule(
        "org.springframework.security.saml2.jackson2.Saml2Jackson2Module",
        "org.springframework.security.saml2.jackson2.Saml2Jackson2Module",
        classLoader,
        modules);
    addConditionalModule(
        "org.springframework.security.cas.jackson2.CasJackson2Module",
        "org.springframework.security.cas.jackson2.CasJackson2Module",
        classLoader,
        modules);
    return modules;
  }

  public static void enableDefaultTyping(ObjectMapper mapper) {
    if (mapper == null) {
      return;
    }
    TypeResolverBuilder<?> typeBuilder = mapper.getDeserializationConfig().getDefaultTyper(null);
    if (typeBuilder == null) {
      mapper.setDefaultTyping(createAllowlistedDefaultTyping());
    }
  }

  private static TypeResolverBuilder<?> createAllowlistedDefaultTyping() {
    TypeResolverBuilder<?> builder =
        new AllowlistTypeResolverBuilder(ObjectMapper.DefaultTyping.NON_FINAL);
    builder = builder.init(JsonTypeInfo.Id.CLASS, null);
    builder = builder.inclusion(JsonTypeInfo.As.PROPERTY);
    return builder;
  }

  private static void addConditionalModule(
      String classToCheck,
      String moduleClassName,
      ClassLoader classLoader,
      List<Module> modules) {
    if (ClassUtils.isPresent(classToCheck, classLoader)) {
      addIfAvailable(moduleClassName, classLoader, modules);
    }
  }

  @SuppressWarnings("unchecked")
  private static void addIfAvailable(
      String moduleClassName, ClassLoader classLoader, List<Module> modules) {
    try {
      Class<? extends Module> moduleClass =
          (Class<? extends Module>) ClassUtils.forName(moduleClassName, classLoader);
      Constructor<? extends Module> constructor = moduleClass.getDeclaredConstructor();
      constructor.setAccessible(true);
      modules.add(constructor.newInstance());
    } catch (Exception ignored) {
      // Module not on classpath, nothing to register.
    }
  }

  @SuppressWarnings("serial")
  private static final class AllowlistTypeResolverBuilder
      extends ObjectMapper.DefaultTypeResolverBuilder {

    AllowlistTypeResolverBuilder(ObjectMapper.DefaultTyping defaultTyping) {
      super(
          defaultTyping,
          BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build());
    }

    @Override
    protected TypeIdResolver idResolver(
        MapperConfig<?> config,
        JavaType baseType,
        PolymorphicTypeValidator subtypeValidator,
        Collection<NamedType> subtypes,
        boolean forSer,
        boolean forDeser) {
      TypeIdResolver delegate =
          super.idResolver(config, baseType, subtypeValidator, subtypes, forSer, forDeser);
      return new AllowlistTypeIdResolver(delegate);
    }
  }

  private static final class AllowlistTypeIdResolver implements TypeIdResolver {

    private static final Set<String> ALLOWLIST_CLASS_NAMES;

    static {
      Set<String> names = new HashSet<>();
      names.add("java.util.ArrayList");
      names.add("java.util.Collections$EmptyList");
      names.add("java.util.Collections$EmptyMap");
      names.add("java.util.Collections$UnmodifiableRandomAccessList");
      names.add("java.util.Collections$SingletonList");
      names.add("java.util.Date");
      names.add("java.time.Instant");
      names.add("java.net.URL");
      names.add("java.util.TreeMap");
      names.add("java.util.HashMap");
      names.add("java.util.LinkedHashMap");
      names.add("org.springframework.security.core.context.SecurityContextImpl");
      names.add("java.util.Arrays$ArrayList");
      ALLOWLIST_CLASS_NAMES = Collections.unmodifiableSet(names);
    }

    private final TypeIdResolver delegate;

    AllowlistTypeIdResolver(TypeIdResolver delegate) {
      this.delegate = delegate;
    }

    @Override
    public void init(JavaType baseType) {
      delegate.init(baseType);
    }

    @Override
    public String idFromValue(Object value) {
      return delegate.idFromValue(value);
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
      return delegate.idFromValueAndType(value, suggestedType);
    }

    @Override
    public String idFromBaseType() {
      return delegate.idFromBaseType();
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) {
      try {
        return resolveType(context, id);
      } catch (Exception ex) {
        throw new IllegalArgumentException(ex);
      }
    }

    private JavaType resolveType(DatabindContext context, String id) throws Exception {
      DeserializationConfig config = (DeserializationConfig) context.getConfig();
      JavaType type = delegate.typeFromId(context, id);
      String className = type.getRawClass().getName();
      if (ALLOWLIST_CLASS_NAMES.contains(className)) {
        return type;
      }
      boolean hasMixin = config.findMixInClassFor(type.getRawClass()) != null;
      if (hasMixin) {
        return type;
      }
      JacksonAnnotation jacksonAnnotation =
          AnnotationUtils.findAnnotation(type.getRawClass(), JacksonAnnotation.class);
      if (jacksonAnnotation != null) {
          return type;
      }
      throw new IllegalArgumentException(
          "Deserialization denied for type %s. Provide an explicit mixin or allowlisted mapping."
              .formatted(className));
    }

    @Override
    public String getDescForKnownTypeIds() {
      return delegate.getDescForKnownTypeIds();
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
      return delegate.getMechanism();
    }
  }
}

