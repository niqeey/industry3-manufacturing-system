import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Form, Alert, Badge } from 'react-bootstrap';
import { productAPI } from '../services/api';

const Products = () => {
  const [products, setProducts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [formData, setFormData] = useState({
    productCode: '',
    name: '',
    description: '',
    unitPrice: '',
    status: 'ACTIVE',
    minimumStockLevel: '',
    maximumStockLevel: '',
    reorderPoint: '',
    leadTimeDays: ''
  });

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      setLoading(true);
      const response = await productAPI.getAll();
      setProducts(response.data);
    } catch (err) {
      console.error('Error loading products:', err);
      setError('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (searchTerm.trim()) {
      try {
        const response = await productAPI.search(searchTerm);
        setProducts(response.data);
      } catch (err) {
        console.error('Error searching products:', err);
        setError('Failed to search products');
      }
    } else {
      loadProducts();
    }
  };

  const resetForm = () => {
    setFormData({
      productCode: '',
      name: '',
      description: '',
      unitPrice: '',
      status: 'ACTIVE',
      minimumStockLevel: '',
      maximumStockLevel: '',
      reorderPoint: '',
      leadTimeDays: ''
    });
    setEditingProduct(null);
  };

  const handleShowModal = (product = null) => {
    if (product) {
      setEditingProduct(product);
      setFormData({
        productCode: product.productCode || '',
        name: product.name || '',
        description: product.description || '',
        unitPrice: product.unitPrice || '',
        status: product.status || 'ACTIVE',
        minimumStockLevel: product.minimumStockLevel || '',
        maximumStockLevel: product.maximumStockLevel || '',
        reorderPoint: product.reorderPoint || '',
        leadTimeDays: product.leadTimeDays || ''
      });
    } else {
      resetForm();
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    resetForm();
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const productData = {
        ...formData,
        unitPrice: parseFloat(formData.unitPrice),
        minimumStockLevel: parseInt(formData.minimumStockLevel) || null,
        maximumStockLevel: parseInt(formData.maximumStockLevel) || null,
        reorderPoint: parseInt(formData.reorderPoint) || null,
        leadTimeDays: parseInt(formData.leadTimeDays) || null
      };

      if (editingProduct) {
        await productAPI.update(editingProduct.id, productData);
      } else {
        await productAPI.create(productData);
      }

      handleCloseModal();
      loadProducts();
    } catch (err) {
      console.error('Error saving product:', err);
      setError('Failed to save product');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this product?')) {
      try {
        await productAPI.delete(id);
        loadProducts();
      } catch (err) {
        console.error('Error deleting product:', err);
        setError('Failed to delete product');
      }
    }
  };

  const getStatusBadge = (status) => {
    const statusClasses = {
      'ACTIVE': 'success',
      'DISCONTINUED': 'danger',
      'DRAFT': 'warning'
    };
    
    return (
      <Badge bg={statusClasses[status] || 'secondary'}>
        {status}
      </Badge>
    );
  };

  if (loading) {
    return (
      <Container className="mt-4">
        <div className="text-center">
          <div className="spinner-border" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      </Container>
    );
  }

  return (
    <Container fluid>
      <Row className="mb-4">
        <Col>
          <h1>Product Management</h1>
          <p className="text-muted">Manage your product catalog</p>
        </Col>
        <Col md="auto">
          <Button variant="primary" onClick={() => handleShowModal()}>
            <i className="fas fa-plus me-2"></i>Add Product
          </Button>
        </Col>
      </Row>

      {error && (
        <Row className="mb-4">
          <Col>
            <Alert variant="danger" dismissible onClose={() => setError(null)}>
              {error}
            </Alert>
          </Col>
        </Row>
      )}

      {/* Search */}
      <Row className="mb-4">
        <Col md={6}>
          <div className="d-flex">
            <Form.Control
              type="text"
              placeholder="Search products..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            />
            <Button variant="outline-secondary" className="ms-2" onClick={handleSearch}>
              <i className="fas fa-search"></i>
            </Button>
            {searchTerm && (
              <Button variant="outline-danger" className="ms-2" onClick={() => {
                setSearchTerm('');
                loadProducts();
              }}>
                <i className="fas fa-times"></i>
              </Button>
            )}
          </div>
        </Col>
      </Row>

      {/* Products Table */}
      <Row>
        <Col>
          <Card>
            <Card.Body>
              {products.length === 0 ? (
                <p className="text-muted text-center">No products found</p>
              ) : (
                <div className="table-responsive">
                  <Table striped hover>
                    <thead>
                      <tr>
                        <th>Product Code</th>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Unit Price</th>
                        <th>Status</th>
                        <th>Reorder Point</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {products.map((product) => (
                        <tr key={product.id}>
                          <td><strong>{product.productCode}</strong></td>
                          <td>{product.name}</td>
                          <td>{product.description || 'N/A'}</td>
                          <td>${product.unitPrice?.toFixed(2) || '0.00'}</td>
                          <td>{getStatusBadge(product.status)}</td>
                          <td>{product.reorderPoint || 'Not set'}</td>
                          <td>
                            <Button
                              variant="outline-primary"
                              size="sm"
                              className="me-2"
                              onClick={() => handleShowModal(product)}
                            >
                              <i className="fas fa-edit"></i>
                            </Button>
                            <Button
                              variant="outline-danger"
                              size="sm"
                              onClick={() => handleDelete(product.id)}
                            >
                              <i className="fas fa-trash"></i>
                            </Button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Product Modal */}
      <Modal show={showModal} onHide={handleCloseModal} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {editingProduct ? 'Edit Product' : 'Add Product'}
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Product Code</Form.Label>
                  <Form.Control
                    type="text"
                    name="productCode"
                    value={formData.productCode}
                    onChange={handleInputChange}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Status</Form.Label>
                  <Form.Select
                    name="status"
                    value={formData.status}
                    onChange={handleInputChange}
                  >
                    <option value="ACTIVE">Active</option>
                    <option value="DRAFT">Draft</option>
                    <option value="DISCONTINUED">Discontinued</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Name</Form.Label>
              <Form.Control
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Description</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="description"
                value={formData.description}
                onChange={handleInputChange}
              />
            </Form.Group>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Unit Price</Form.Label>
                  <Form.Control
                    type="number"
                    step="0.01"
                    name="unitPrice"
                    value={formData.unitPrice}
                    onChange={handleInputChange}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Lead Time (Days)</Form.Label>
                  <Form.Control
                    type="number"
                    name="leadTimeDays"
                    value={formData.leadTimeDays}
                    onChange={handleInputChange}
                  />
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Minimum Stock</Form.Label>
                  <Form.Control
                    type="number"
                    name="minimumStockLevel"
                    value={formData.minimumStockLevel}
                    onChange={handleInputChange}
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Maximum Stock</Form.Label>
                  <Form.Control
                    type="number"
                    name="maximumStockLevel"
                    value={formData.maximumStockLevel}
                    onChange={handleInputChange}
                  />
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Reorder Point</Form.Label>
                  <Form.Control
                    type="number"
                    name="reorderPoint"
                    value={formData.reorderPoint}
                    onChange={handleInputChange}
                  />
                </Form.Group>
              </Col>
            </Row>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              {editingProduct ? 'Update' : 'Create'} Product
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};

export default Products;
