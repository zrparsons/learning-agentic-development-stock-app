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
  stockCount: number;
  createdBy: string;
  updatedBy: string;
  createdAt: string;
  updatedAt: string;
}

export interface ProductCreateRequest {
  name: string;
  description: string;
  price: number;
  stockCount?: number;
}

export interface ProductUpdateRequest {
  name?: string;
  description?: string;
  price?: number;
  stockCount?: number;
}

export interface ErrorResponse {
  error: string;
}

