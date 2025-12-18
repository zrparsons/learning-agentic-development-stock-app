import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { productAPI, userAPI } from '../services/api';
import type { Product } from '../types';
import './ProductList.css';

const ProductList: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [userCache, setUserCache] = useState<Map<string, string>>(new Map());
  const { logout, user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      setLoading(true);
      const data = await productAPI.getAll();
      setProducts(data);
      setError('');
      
      // Extract unique user IDs from products
      const userIds = new Set<string>();
      data.forEach(product => {
        userIds.add(product.createdBy);
        userIds.add(product.updatedBy);
      });
      
      // Fetch usernames for all unique user IDs in parallel
      const userPromises = Array.from(userIds).map(async (userId) => {
        try {
          const userData = await userAPI.getById(userId);
          return { userId, username: userData.username };
        } catch (err) {
          console.error(`Failed to fetch user ${userId}:`, err);
          return { userId, username: userId.substring(0, 8) }; // Fallback to abbreviated ID
        }
      });
      
      const userResults = await Promise.all(userPromises);
      const newUserCache = new Map<string, string>();
      userResults.forEach(({ userId, username }) => {
        newUserCache.set(userId, username);
      });
      setUserCache(newUserCache);
    } catch (err: any) {
      setError(err.response?.data?.error || 'Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Are you sure you want to delete this product?')) {
      return;
    }

    try {
      await productAPI.delete(id);
      setProducts(products.filter((p) => p.id !== id));
    } catch (err: any) {
      alert(err.response?.data?.error || 'Failed to delete product');
    }
  };

  const handleStockChange = async (id: string, currentStock: number, delta: number) => {
    const newStock = currentStock + delta;
    
    if (newStock < 0) {
      alert('Stock count cannot be negative');
      return;
    }

    try {
      // Optimistic update
      setProducts(products.map(p => 
        p.id === id ? { ...p, stockCount: newStock } : p
      ));

      // Update on server
      const updatedProduct = await productAPI.update(id, { stockCount: newStock });
      
      // Update with server response
      setProducts(products.map(p => 
        p.id === id ? updatedProduct : p
      ));
    } catch (err: any) {
      // Revert on error
      setProducts(products.map(p => 
        p.id === id ? { ...p, stockCount: currentStock } : p
      ));
      alert(err.response?.data?.error || 'Failed to update stock count');
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(price);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const getUsernameDisplay = (userId: string) => {
    const username = userCache.get(userId);
    if (!username) {
      return 'Loading...';
    }
    return username === user?.username ? 'You' : username;
  };

  const isCurrentUser = (userId: string) => {
    const username = userCache.get(userId);
    return username === user?.username;
  };

  if (loading) {
    return (
      <div className="product-list-container">
        <div className="loading">Loading products...</div>
      </div>
    );
  }

  return (
    <div className="product-list-container">
      <header className="product-header">
        <div>
          <h1>Product Catalog</h1>
          <p className="user-info">Welcome, {user?.username}!</p>
        </div>
        <div className="header-actions">
          <button onClick={() => navigate('/products/new')} className="btn btn-primary">
            Add Product
          </button>
          <button onClick={logout} className="btn btn-secondary">
            Logout
          </button>
        </div>
      </header>

      {error && <div className="error-message">{error}</div>}

      {products.length === 0 ? (
        <div className="empty-state">
          <p>No products found. Create your first product!</p>
          <button onClick={() => navigate('/products/new')} className="btn btn-primary">
            Add Product
          </button>
        </div>
      ) : (
        <div className="product-table-container">
          <table className="product-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Price</th>
                <th>Stock Count</th>
                <th>Created</th>
                <th>Created By</th>
                <th>Updated</th>
                <th>Updated By</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td>{product.name}</td>
                  <td className="description-cell">{product.description}</td>
                  <td>{formatPrice(product.price)}</td>
                  <td>
                    <div className="stock-controls">
                      <button
                        onClick={() => handleStockChange(product.id, product.stockCount, -1)}
                        className="btn btn-stock btn-decrement"
                        title="Decrease stock"
                      >
                        −
                      </button>
                      <span className="stock-count">{product.stockCount}</span>
                      <button
                        onClick={() => handleStockChange(product.id, product.stockCount, 1)}
                        className="btn btn-stock btn-increment"
                        title="Increase stock"
                      >
                        +
                      </button>
                    </div>
                  </td>
                  <td>{formatDate(product.createdAt)}</td>
                  <td>
                    <span 
                      className={`user-badge ${isCurrentUser(product.createdBy) ? 'current-user' : ''}`}
                    >
                      {getUsernameDisplay(product.createdBy)}
                    </span>
                  </td>
                  <td>{formatDate(product.updatedAt)}</td>
                  <td>
                    <span 
                      className={`user-badge ${isCurrentUser(product.updatedBy) ? 'current-user' : ''}`}
                    >
                      {getUsernameDisplay(product.updatedBy)}
                    </span>
                  </td>
                  <td>
                    <div className="action-buttons">
                      <button
                        onClick={() => navigate(`/products/edit/${product.id}`)}
                        className="btn btn-small btn-edit"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => handleDelete(product.id)}
                        className="btn btn-small btn-delete"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default ProductList;

