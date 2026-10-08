package com.nepbrain.technology.config.service;

import com.nepbrain.technology.core.payload.response.GlobalResponse;

public interface ConfigService {

    GlobalResponse configurations(boolean authenticated);

    GlobalResponse dashboardDetail();

    GlobalResponse firebaseDetail();
}
