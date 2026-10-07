# Flutter → Java API migration map

Source: `Handyman Service v11.14.3/lib/network/rest_apis.dart` (+ blog / helpdesk repositories).

Flutter base: `BASE_URL = $DOMAIN_URL/api/` (see `lib/utils/configs.dart`).

Java base (via Api-Gateway): `http://localhost:8090/api/v2/{domain}/...` → **Handyman-Service**.

Controller names below are the **intended** Spring `@RestController` classes in Handyman-Service. Adjust if the parallel Handyman-Service agent uses different class names; keep the URL paths stable.

---

## Path convention

| Flutter | Java |
|---------|------|
| `/api/{endpoint}` | `/api/v2/{domain}/{endpoint}` |

Query strings and HTTP methods stay the same unless noted.

---

## Auth → `AuthController` (`/api/v2/auth`)

| Flutter function | Method | Flutter endpoint | Java path |
|------------------|--------|------------------|-----------|
| `createUser` | POST | `register` | `/api/v2/auth/register` |
| `loginUser` | POST | `login` / `social-login` | `/api/v2/auth/login`, `/api/v2/auth/social-login` |
| `updateProfile` | POST | `update-profile` | `/api/v2/auth/update-profile` |
| (multipart) | POST | `update-profile` | `/api/v2/auth/update-profile` |
| `getUserDetail` | GET | `user-detail?id=` | `/api/v2/auth/user-detail` |
| `logoutApi` | GET | `logout` | `/api/v2/auth/logout` |
| `changeUserPassword` | POST | `change-password` | `/api/v2/auth/change-password` |
| `forgotPassword` | POST | `forgot-password` | `/api/v2/auth/forgot-password` |
| `deleteAccountCompletely` | POST | `delete-user-account` | `/api/v2/auth/delete-user-account` |
| `verifyUserEmail` | POST | `user-email-verify` | `/api/v2/auth/user-email-verify` |

---

## Catalog → `CatalogController` (`/api/v2/catalog`)

| Flutter function | Method | Flutter endpoint | Java path |
|------------------|--------|------------------|-----------|
| `userDashboard` | GET | `dashboard-detail` | `/api/v2/catalog/dashboard-detail` |
| `getCategoryList` / pagination | GET | `category-list` | `/api/v2/catalog/category-list` |
| `getSubCategoryList` | GET | `subcategory-list` | `/api/v2/catalog/subcategory-list` |
| `getServiceDetails` | POST | `service-detail` | `/api/v2/catalog/service-detail` |
| `searchServiceAPI` | GET | `search-list` | `/api/v2/catalog/search-list` |
| `getMyServiceList` | GET | `service-list` | `/api/v2/catalog/service-list` |
| `deleteServiceRequest` | POST | `service-delete/{id}` | `/api/v2/catalog/service-delete/{id}` |
| (multipart create) | POST | `service-save` | `/api/v2/catalog/service-save` |
| `addWishList` / `removeWishList` | POST | `save-favourite` / `delete-favourite` | `/api/v2/catalog/save-favourite`, `.../delete-favourite` |
| favourites list | GET | `user-favourite-service` | `/api/v2/catalog/user-favourite-service` |
| `getCouponList` | GET | `coupon-list` | `/api/v2/catalog/coupon-list` |

---

## Booking → `BookingController` (`/api/v2/booking`)

| Flutter function | Method | Flutter endpoint | Java path |
|------------------|--------|------------------|-----------|
| booking list | GET | `booking-list` | `/api/v2/booking/booking-list` |
| `getBookingDetail` | POST | `booking-detail` | `/api/v2/booking/booking-detail` |
| `saveBooking` | POST | `booking-save` | `/api/v2/booking/booking-save` |
| `updateBooking` | POST | `booking-update` | `/api/v2/booking/booking-update` |
| status dropdown | GET | `booking-status` | `/api/v2/booking/booking-status` |
| `getProviderLocation` | GET | `get-location` | `/api/v2/booking/get-location` |
| `sentInvoiceOnMail` | POST | `download-invoice` | `/api/v2/booking/download-invoice` |

---

## Provider → `ProviderController` (`/api/v2/provider`)

| Flutter function | Method | Flutter endpoint | Java path |
|------------------|--------|------------------|-----------|
| `getProvider` / handyman list | GET | `user-list?user_type=...` | `/api/v2/provider/user-list` |
| `getProviderDetail` / `getHandymanDetail` | GET | `user-detail?id=` | `/api/v2/provider/user-detail` |
| `addProviderWishList` / remove | POST | `save-favourite-provider` / `delete-favourite-provider` | `/api/v2/provider/save-favourite-provider`, `.../delete-favourite-provider` |
| favourite providers | GET | `user-favourite-provider` | `/api/v2/provider/user-favourite-provider` |

> Note: customer `user-detail` for the logged-in user stays under `/api/v2/auth/user-detail`; provider/handyman public profiles use `/api/v2/provider/user-detail`.

---

## Payment → `PaymentController` (`/api/v2/payment`)

| Flutter function | Method | Flutter endpoint | Java path |
|------------------|--------|------------------|-----------|
| `savePayment` | POST | `save-payment` | `/api/v2/payment/save-payment` |
| payment gateways | GET | `payment-gateways` | `/api/v2/payment/payment-gateways` |
| payment list | GET | `payment-list` | `/api/v2/payment/payment-list` |
| `getUserWalletBalance` | GET | `user-wallet-balance` | `/api/v2/payment/user-wallet-balance` |
| wallet history | GET | `wallet-history` | `/api/v2/payment/wallet-history` |
| `walletTopUp` | POST | `wallet-top-up` | `/api/v2/payment/wallet-top-up` |
| `walletMoneyWithdrawal` | POST | `withdraw-money` | `/api/v2/payment/withdraw-money` |
| `providerPayOut` | POST | `provider-payout` | `/api/v2/payment/provider-payout` |
| bank list | GET | `user-bank-detail` | `/api/v2/payment/user-bank-detail` |
| (multipart) | POST | `save-bank` | `/api/v2/payment/save-bank` |
| `chooseDefaulBank` | POST | `default-bank` | `/api/v2/payment/default-bank` |
| `deleteBank` | POST | `delete-bank/{id}` | `/api/v2/payment/delete-bank/{id}` |
| `deleteImage` | POST | `remove-file` | `/api/v2/payment/remove-file` *or* Bucket-Service |

External (not Handyman-Service): Flutterwave verify, Sadad login/invoice — keep as client-side third-party calls.

---

## Post-job → `PostJobController` (`/api/v2/postjob`)

| Flutter function | Method | Flutter endpoint | Java path |
|------------------|--------|------------------|-----------|
| `savePostJob` | POST | `save-post-job` | `/api/v2/postjob/save-post-job` |
| `deletePostRequest` | POST | `post-job-delete/{id}` | `/api/v2/postjob/post-job-delete/{id}` |
| my post jobs | GET | `get-post-job` | `/api/v2/postjob/get-post-job` |
| `getPostJobDetail` | POST | `get-post-job-detail` | `/api/v2/postjob/get-post-job-detail` |

---

## Review → `ReviewController` (`/api/v2/review`)

| Flutter function | Method | Flutter endpoint | Java path |
|------------------|--------|------------------|-----------|
| `updateReview` / save booking rating | POST | `save-booking-rating` | `/api/v2/review/save-booking-rating` |
| `deleteReview` | POST | `delete-booking-rating` | `/api/v2/review/delete-booking-rating` |
| service reviews | POST | `service-reviews` | `/api/v2/review/service-reviews` |
| user ratings | GET | `get-user-ratings` | `/api/v2/review/get-user-ratings` |
| `handymanRating` | POST | `save-handyman-rating` | `/api/v2/review/save-handyman-rating` |
| `deleteHandymanReview` | POST | `delete-handyman-rating` | `/api/v2/review/delete-handyman-rating` |
| handyman reviews | POST | `handyman-reviews` | `/api/v2/review/handyman-reviews` |

---

## Helpdesk → `HelpdeskController` (`/api/v2/helpdesk`)

Source: `lib/screens/helpDesk/help_desk_repository.dart`

| Function / action | Method | Flutter endpoint | Java path |
|-------------------|--------|------------------|-----------|
| list | GET | `helpdesk-list` | `/api/v2/helpdesk/helpdesk-list` |
| detail | GET | `helpdesk-detail` | `/api/v2/helpdesk/helpdesk-detail` |
| create (multipart) | POST | `helpdesk-save` | `/api/v2/helpdesk/helpdesk-save` |
| activity (multipart) | POST | `helpdesk-activity-save/{id}` | `/api/v2/helpdesk/helpdesk-activity-save/{id}` |
| close | POST | `helpdesk-closed/{id}` | `/api/v2/helpdesk/helpdesk-closed/{id}` |

---

## Geo → `GeoController` (`/api/v2/geo`)

| Flutter function | Method | Flutter endpoint | Java path |
|------------------|--------|------------------|-----------|
| `getCountryList` | POST | `country-list` | `/api/v2/geo/country-list` |
| `getStateList` | POST | `state-list` | `/api/v2/geo/state-list` |
| `getCityList` | POST | `city-list` | `/api/v2/geo/city-list` |

---

## Config → `ConfigController` (`/api/v2/config`)

| Flutter function | Method | Flutter endpoint | Java path |
|------------------|--------|------------------|-----------|
| `getAppConfigurations` | POST | `configurations` | `/api/v2/config/configurations` |

---

## Notification → `NotificationController` (`/api/v2/notification`)

| Source | Method | Flutter endpoint | Java path |
|--------|--------|------------------|-----------|
| `rest_apis` notification list | POST | `notification-list` | `/api/v2/notification/notification-list` |
| `notification_services.dart` | GET | `firebase-detail` | `/api/v2/notification/firebase-detail` |

---

## Blog → `BlogController` (`/api/v2/blog`)

Source: `lib/screens/blog/blog_repository.dart`

| Action | Method | Flutter endpoint | Java path |
|--------|--------|------------------|-----------|
| list | GET | `blog-list` | `/api/v2/blog/blog-list` |
| detail | POST | `blog-detail` | `/api/v2/blog/blog-detail` |

---

## Supporting services (not Handyman-Service)

| Concern | Service | Gateway path |
|---------|---------|--------------|
| OAuth2 introspect / tokens | Auth-Service | `/oauth2/**` |
| Object storage | Bucket-Service | `/api/v2/bucket/**` |
| OTP / SMS / email | Communication-Service | `/api/v2/communication/**` |
| Optional customer master | Customer-Service | `/api/v2/customer/**` |

---

## Compatibility tips

1. Prefer keeping **endpoint suffixes identical** (`booking-save`, `category-list`, …) so Flutter needs only a domain prefix change.  
2. Multipart endpoints (`update-profile`, `helpdesk-save`, `service-save`, `save-bank`) should accept the same form field names as the Laravel API where possible.  
3. Response envelopes should match Flutter models (`status`, `message`, `data`, …) defined under `lib/model/`.  
4. When Handyman-Service controllers land, update this doc’s class names if they diverge; keep paths as the contract.
