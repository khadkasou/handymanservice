export type ApiStatus = "SUCCESS" | "FAILED" | "PENDING";

export interface GlobalResponse<T> {
  status: ApiStatus;
  code?: string;
  message?: string;
  data?: T;
}

export interface User {
  id: string;
  email?: string;
  username?: string;
  firstName?: string;
  lastName?: string;
  contactNumber?: string;
  userType?: string;
  status?: string;
  profileImage?: string;
  address?: string;
  countryId?: string;
  stateId?: string;
  cityId?: string;
  displayName?: string;
  designation?: string;
  isFeatured?: boolean;
  providerId?: string;
  apiToken?: string;
  emailVerified?: boolean;
}

export interface LoginResult {
  status?: boolean;
  message?: string;
  userData: User;
}

export interface Category {
  id: string;
  name?: string;
  description?: string;
  image?: string;
  parentId?: string;
  status?: string;
  isFeatured?: boolean;
}

export interface Service {
  id: string;
  name?: string;
  description?: string;
  categoryId?: string;
  subcategoryId?: string;
  providerId?: string;
  price?: number;
  discount?: number;
  duration?: string;
  image?: string;
  status?: string;
  isFeatured?: boolean;
  serviceType?: string;
}

export interface Booking {
  id: string;
  customerId?: string;
  serviceId?: string;
  providerId?: string;
  date?: string;
  startAt?: string;
  endAt?: string;
  amount?: number;
  discount?: number;
  totalAmount?: number;
  status?: string;
  address?: string;
  latitude?: number;
  longitude?: number;
  bookingSlot?: string;
  couponId?: string;
  handymanId?: string;
  quantity?: number;
  description?: string;
}

export interface Coupon {
  id: string;
  code?: string;
  description?: string;
  discount?: number;
  discountType?: string;
  serviceId?: string;
  expireDate?: string;
  status?: string;
}

export interface BookingStatus {
  id: string;
  value?: string;
  label?: string;
  color?: string;
  sortOrder?: number;
}

export interface PostJob {
  id: string;
  title?: string;
  description?: string;
  customerId?: string;
  status?: string;
  price?: number;
  address?: string;
  categoryId?: string;
}

export interface Wallet {
  id?: string;
  userId?: string;
  amount?: number;
}

export interface WalletTransaction {
  id: string;
  userId?: string;
  amount?: number;
  type?: string;
  activityType?: string;
  datetime?: string;
}

export interface PaymentGateway {
  id: string;
  title?: string;
  type?: string;
  isTest?: boolean;
  status?: string;
}

export interface Payment {
  id: string;
  bookingId?: string;
  amount?: number;
  paymentType?: string;
  txnId?: string;
  paymentStatus?: string;
  datetime?: string;
}

export interface BlogPost {
  id: string;
  title?: string;
  description?: string;
  image?: string;
  isFeatured?: boolean;
  status?: string;
  authorName?: string;
}

export interface HelpdeskActivity {
  id?: string;
  helpdeskId?: string;
  senderId?: string;
  messages?: string;
  senderType?: string;
}

export interface HelpdeskTicket {
  id: string;
  customerId?: string;
  subject?: string;
  description?: string;
  status?: string;
  mode?: string;
  type?: string;
  activities?: HelpdeskActivity[];
}

export interface AppNotification {
  id: string;
  customerId?: string;
  type?: string;
  subject?: string;
  message?: string;
  isRead?: boolean;
  dataId?: string;
}

export interface Review {
  id: string;
  bookingId?: string;
  serviceId?: string;
  customerId?: string;
  rating?: number;
  review?: string;
}

export interface Place {
  id: string;
  name?: string;
  code?: string;
  countryId?: string;
  stateId?: string;
}

export interface Bank {
  id: string;
  userId?: string;
  bankName?: string;
  branchName?: string;
  accountNumber?: string;
  ifscNo?: string;
  accountHolderName?: string;
  isDefault?: boolean;
}

export interface ProviderLocation {
  bookingId?: string;
  latitude?: number;
  longitude?: number;
  address?: string;
}

export interface AppConfig {
  configurations?: Record<string, string>;
}

export interface Session {
  user: User;
  token: string;
}
