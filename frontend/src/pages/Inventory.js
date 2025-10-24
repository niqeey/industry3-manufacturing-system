import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Form, Alert, Badge } from 'react-bootstrap';
import { inventoryAPI } from '../services/api';

const Inventory = () => {
  const [inventory, setInventory] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [stockQuantity, setStockQuantity] = useState('');

  useEffect(() => {
    loadInventory();
  }, []);

  const loadInventory = async () => {
    try {
      setLoading(true);
      const response = await inventoryAPI.getAll();
      setInventory(response.data);
    } catch (err) {
      console.error('Error loading inventory:', err);
      // For demo purposes, create mock inventory data
      setInventory([
        {
          id: 1,
          product: { 
            id: 1, 
            productCode: 'WIDGET-001', 
            name: 'Standard Widget',
            unitPrice: 12.50,
            reorderPoint: 150
          },
          currentStock: 850,
          reservedStock: 0,
          availableStock: 850,
          location: 'Warehouse A',
          binNumber: 'A1-001'
        },
        {
          id: 2,
          product: { 
            id: 2, 
            productCode: 'GEAR-002', 
            name: 'Precision Gear',
            unitPrice: 45.00,
            reorderPoint: 75
          },
          currentStock: 425,
          reservedStock: 50,
          availableStock: 375,
          location: 'Warehouse A',
          binNumber: 'A2-015'
        },
        {
          id: 3,
          product: { 
            id: 3, 
            productCode: 'BEARING-003', 
            name: 'Industrial Bearing',
            unitPrice: 89.99,
            reorderPoint: 40
          },
          currentStock: 35, // Low stock
          reservedStock: 0,
          availableStock: 35,
          location: 'Warehouse B',
          binNumber: 'B1-008'
        },
        {
          id: 4,
          product: { 
            id: 4, 
            productCode: 'MOTOR-004', 
            name: 'Electric Motor Assembly',
            unitPrice: 299.95,
            reorderPoint: 15
          },
          currentStock: 85,
          reservedStock: 10,
          availableStock: 75,
          location: 'Warehouse B',
          binNumber: 'B3-012'
        }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (searchTerm.trim()) {
      try {
        const response = await inventoryAPI.search(searchTerm);
        setInventory(response.data);
      } catch (err) {
        console.error('Error searching inventory:', err);
        // Filter mock data for demo
        const filtered = inventory.filter(item => 
          item.product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          item.product.productCode.toLowerCase().includes(searchTerm.toLowerCase())
        );
        setInventory(filtered);
      }
    } else {
      loadInventory();
    }
  };

  const handleShowModal = (item) => {
    setSelectedItem(item);
    setStockQuantity('');
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedItem(null);
    setStockQuantity('');
  };

  const handleAddStock = async (e) => {
    e.preventDefault();
    if (!selectedItem || !stockQuantity || isNaN(stockQuantity)) return;

    try {
      await inventoryAPI.addStock(selectedItem.id, parseInt(stockQuantity));
      handleCloseModal();
      loadInventory();
    } catch (err) {
      console.error('Error adding stock:', err);
      // For demo purposes, update local state
      setInventory(prev => prev.map(item => 
        item.id === selectedItem.id 
          ? {
              ...item,
              currentStock: item.currentStock + parseInt(stockQuantity),
              availableStock: item.availableStock + parseInt(stockQuantity)
            }
          : item
      ));
      handleCloseModal();
    }
  };

  const getStockStatus = (item) => {
    if (item.currentStock === 0) {
      return { badge: 'danger', text: 'Out of Stock' };
    } else if (item.currentStock <= item.product.reorderPoint) {
      return { badge: 'warning', text: 'Low Stock' };
    } else {
      return { badge: 'success', text: 'In Stock' };
    }
  };

  const calculateTotalValue = () => {
    return inventory.reduce((total, item) => 
      total + (item.currentStock * item.product.unitPrice), 0
    ).toFixed(2);
  };

  const getLowStockItems = () => {
    return inventory.filter(item => item.currentStock <= item.product.reorderPoint);
  };

  const getOutOfStockItems = () => {
    return inventory.filter(item => item.currentStock === 0);
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
          <h1>Inventory Management</h1>
          <p className="text-muted">Monitor and manage your inventory levels</p>
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

      {/* Inventory Summary Cards */}
      <Row className="mb-4">
        <Col md={3}>
          <Card className="dashboard-metric bg-primary text-white">
            <Card.Body>
              <h3>{inventory.length}</h3>
              <p>Total Items</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="dashboard-metric bg-warning text-white">
            <Card.Body>
              <h3>{getLowStockItems().length}</h3>
              <p>Low Stock Items</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="dashboard-metric bg-danger text-white">
            <Card.Body>
              <h3>{getOutOfStockItems().length}</h3>
              <p>Out of Stock</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="dashboard-metric bg-success text-white">
            <Card.Body>
              <h3>${calculateTotalValue()}</h3>
              <p>Total Value</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Search */}
      <Row className="mb-4">
        <Col md={6}>
          <div className="d-flex">
            <Form.Control
              type="text"
              placeholder="Search inventory..."
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
                loadInventory();
              }}>
                <i className="fas fa-times"></i>
              </Button>
            )}
          </div>
        </Col>
      </Row>

      {/* Inventory Table */}
      <Row>
        <Col>
          <Card>
            <Card.Body>
              {inventory.length === 0 ? (
                <p className="text-muted text-center">No inventory items found</p>
              ) : (
                <div className="table-responsive">
                  <Table striped hover>
                    <thead>
                      <tr>
                        <th>Product Code</th>
                        <th>Product Name</th>
                        <th>Current Stock</th>
                        <th>Available</th>
                        <th>Reserved</th>
                        <th>Location</th>
                        <th>Status</th>
                        <th>Unit Value</th>
                        <th>Total Value</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {inventory.map((item) => {
                        const status = getStockStatus(item);
                        return (
                          <tr key={item.id}>
                            <td><strong>{item.product.productCode}</strong></td>
                            <td>{item.product.name}</td>
                            <td>
                              <span className={item.currentStock <= item.product.reorderPoint ? 'text-warning fw-bold' : ''}>
                                {item.currentStock}
                              </span>
                              {item.product.reorderPoint && (
                                <small className="text-muted d-block">
                                  Reorder: {item.product.reorderPoint}
                                </small>
                              )}
                            </td>
                            <td>{item.availableStock}</td>
                            <td>{item.reservedStock}</td>
                            <td>
                              {item.location}
                              {item.binNumber && (
                                <small className="text-muted d-block">
                                  Bin: {item.binNumber}
                                </small>
                              )}
                            </td>
                            <td>
                              <Badge bg={status.badge}>
                                {status.text}
                              </Badge>
                            </td>
                            <td>${item.product.unitPrice?.toFixed(2) || '0.00'}</td>
                            <td>
                              <strong>
                                ${(item.currentStock * item.product.unitPrice).toFixed(2)}
                              </strong>
                            </td>
                            <td>
                              <Button
                                variant="outline-primary"
                                size="sm"
                                onClick={() => handleShowModal(item)}
                              >
                                <i className="fas fa-plus me-1"></i>
                                Add Stock
                              </Button>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </Table>
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Add Stock Modal */}
      <Modal show={showModal} onHide={handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>Add Stock</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleAddStock}>
          <Modal.Body>
            {selectedItem && (
              <>
                <div className="mb-3">
                  <strong>Product:</strong> {selectedItem.product.name}<br/>
                  <strong>Product Code:</strong> {selectedItem.product.productCode}<br/>
                  <strong>Current Stock:</strong> {selectedItem.currentStock}<br/>
                  <strong>Location:</strong> {selectedItem.location}
                </div>
                <Form.Group className="mb-3">
                  <Form.Label>Quantity to Add</Form.Label>
                  <Form.Control
                    type="number"
                    value={stockQuantity}
                    onChange={(e) => setStockQuantity(e.target.value)}
                    required
                    min="1"
                    placeholder="Enter quantity to add..."
                  />
                </Form.Group>
              </>
            )}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" disabled={!stockQuantity}>
              Add Stock
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};

export default Inventory;
