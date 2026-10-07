package com.nepbrain.technology.config.service;
import com.nepbrain.technology.config.payload.response.*;
public interface ConfigService {
    ConfigurationResponse configurations(boolean authenticated);
    DashboardResponse dashboardDetail();
    FirebaseDetailResponse firebaseDetail();
}
