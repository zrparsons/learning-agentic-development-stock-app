export interface User {
  id: string;
  username: string;
  email: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  createdAt: string;
  updatedAt: string;
  userId: string;
}

export interface ProductCreateRequest {
  name: string;
  description: string;
  price: number;
}

export interface ProductUpdateRequest {
  name?: string;
  description?: string;
  price?: number;
}

export interface ErrorResponse {
  error: string;
}

