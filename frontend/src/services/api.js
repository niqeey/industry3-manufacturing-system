import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Product API
export const productAPI = {
  getAll: () => apiClient.get('/products'),
  getActive: () => apiClient.get('/products/active'),
  getById: (id) => apiClient.get(`/products/${id}`),
  getByCode: (code) => apiClient.get(`/products/code/${code}`),
  search: (term) => apiClient.get(`/products/search?term=${term}`),
  create: (product) => apiClient.post('/products', product),
  update: (id, product) => apiClient.put(`/products/${id}`, product),
  delete: (id) => apiClient.delete(`/products/${id}`),
};

// Work Order API
export const workOrderAPI = {
  getAll: () => apiClient.get('/work-orders'),
  getActive: () => apiClient.get('/work-orders/active'),
  getById: (id) => apiClient.get(`/work-orders/${id}`),
  getByNumber: (number) => apiClient.get(`/work-orders/number/${number}`),
  getByStatus: (status) => apiClient.get(`/work-orders/status/${status}`),
  getCountByStatus: (status) => apiClient.get(`/work-orders/status-count/${status}`),
  create: (workOrder) => apiClient.post('/work-orders', workOrder),
  update: (id, workOrder) => apiClient.put(`/work-orders/${id}`, workOrder),
  start: (id, operator) => apiClient.post(`/work-orders/${id}/start`, operator),
  complete: (id, quantityCompleted) => apiClient.post(`/work-orders/${id}/complete`, { quantityCompleted }),
  cancel: (id, reason) => apiClient.post(`/work-orders/${id}/cancel`, { reason }),
};

// Inventory API
export const inventoryAPI = {
  getAll: () => apiClient.get('/inventory'),
  getById: (id) => apiClient.get(`/inventory/${id}`),
  getLowStock: () => apiClient.get('/inventory/low-stock'),
  getOutOfStock: () => apiClient.get('/inventory/out-of-stock'),
  search: (term) => apiClient.get(`/inventory/search?term=${term}`),
  update: (id, inventory) => apiClient.put(`/inventory/${id}`, inventory),
  addStock: (id, quantity) => apiClient.post(`/inventory/${id}/add-stock`, { quantity }),
};

export default apiClient;
