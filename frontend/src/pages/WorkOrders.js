import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Form, Alert, Badge } from 'react-bootstrap';
import { workOrderAPI, productAPI } from '../services/api';

const WorkOrders = () => {
  const [workOrders, setWorkOrders] = useState([]);
  const [products, setProducts] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingWorkOrder, setEditingWorkOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [formData, setFormData] = useState({
    workOrderNumber: '',
    productId: '',
    quantityOrdered: '',
    priority: 'NORMAL',
    plannedStartDate: '',
    plannedEndDate: '',
    notes: ''
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [workOrdersResponse, productsResponse] = await Promise.all([
        workOrderAPI.getAll(),
        productAPI.getActive()
      ]);
      setWorkOrders(workOrdersResponse.data);
      setProducts(productsResponse.data);
    } catch (err) {
      console.error('Error loading data:', err);
      setError('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      workOrderNumber: '',
      productId: '',
      quantityOrdered: '',
      priority: 'NORMAL',
      plannedStartDate: '',
      plannedEndDate: '',
      notes: ''
    });
    setEditingWorkOrder(null);
  };

  const handleShowModal = (workOrder = null) => {
    if (workOrder) {
      setEditingWorkOrder(workOrder);
      setFormData({
        workOrderNumber: workOrder.workOrderNumber || '',
        productId: workOrder.product?.id || '',
        quantityOrdered: workOrder.quantityOrdered || '',
        priority: workOrder.priority || 'NORMAL',
        plannedStartDate: workOrder.plannedStartDate ? workOrder.plannedStartDate.substring(0, 16) : '',
        plannedEndDate: workOrder.plannedEndDate ? workOrder.plannedEndDate.substring(0, 16) : '',
        notes: workOrder.notes || ''
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
      const selectedProduct = products.find(p => p.id === parseInt(formData.productId));
      
      const workOrderData = {
        workOrderNumber: formData.workOrderNumber,
        product: selectedProduct,
        quantityOrdered: parseInt(formData.quantityOrdered),
        priority: formData.priority,
        plannedStartDate: formData.plannedStartDate || null,
        plannedEndDate: formData.plannedEndDate || null,
        notes: formData.notes
      };

      if (editingWorkOrder) {
        await workOrderAPI.update(editingWorkOrder.id, workOrderData);
      } else {
        await workOrderAPI.create(workOrderData);
      }

      handleCloseModal();
      loadData();
    } catch (err) {
      console.error('Error saving work order:', err);
      setError('Failed to save work order');
    }
  };

  const handleStart = async (workOrderId) => {
    try {
      // For demo purposes, use a dummy operator
      const operator = { id: 3, username: 'operator1' };
      await workOrderAPI.start(workOrderId, operator);
      loadData();
    } catch (err) {
      console.error('Error starting work order:', err);
      setError('Failed to start work order');
    }
  };

  const handleComplete = async (workOrderId, quantityOrdered) => {
    const quantityCompleted = prompt(`Enter quantity completed (max: ${quantityOrdered}):`);
    if (quantityCompleted && !isNaN(quantityCompleted)) {
      try {
        await workOrderAPI.complete(workOrderId, parseInt(quantityCompleted));
        loadData();
      } catch (err) {
        console.error('Error completing work order:', err);
        setError('Failed to complete work order');
      }
    }
  };

  const handleCancel = async (workOrderId) => {
    const reason = prompt('Enter cancellation reason:');
    if (reason) {
      try {
        await workOrderAPI.cancel(workOrderId, reason);
        loadData();
      } catch (err) {
        console.error('Error cancelling work order:', err);
        setError('Failed to cancel work order');
      }
    }
  };

  const getStatusBadge = (status) => {
    const statusClasses = {
      'PLANNED': 'secondary',
      'IN_PROGRESS': 'primary',
      'COMPLETED': 'success',
      'CANCELLED': 'danger',
      'ON_HOLD': 'warning'
    };
    
    return (
      <Badge bg={statusClasses[status] || 'secondary'}>
        {status.replace('_', ' ')}
      </Badge>
    );
  };

  const getPriorityClass = (priority) => {
    const priorityClasses = {
      'HIGH': 'text-danger',
      'URGENT': 'text-danger fw-bold',
      'NORMAL': 'text-info',
      'LOW': 'text-muted'
    };
    
    return priorityClasses[priority] || 'text-info';
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
          <h1>Work Order Management</h1>
          <p className="text-muted">Manage your production work orders</p>
        </Col>
        <Col md="auto">
          <Button variant="primary" onClick={() => handleShowModal()}>
            <i className="fas fa-plus me-2"></i>Create Work Order
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

      {/* Work Orders Table */}
      <Row>
        <Col>
          <Card>
            <Card.Body>
              {workOrders.length === 0 ? (
                <p className="text-muted text-center">No work orders found</p>
              ) : (
                <div className="table-responsive">
                  <Table striped hover>
                    <thead>
                      <tr>
                        <th>Work Order #</th>
                        <th>Product</th>
                        <th>Quantity</th>
                        <th>Status</th>
                        <th>Priority</th>
                        <th>Planned Start</th>
                        <th>Progress</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {workOrders.map((workOrder) => (
                        <tr key={workOrder.id}>
                          <td><strong>{workOrder.workOrderNumber}</strong></td>
                          <td>{workOrder.product?.name || 'N/A'}</td>
                          <td>
                            {workOrder.quantityCompleted}/{workOrder.quantityOrdered}
                          </td>
                          <td>{getStatusBadge(workOrder.status)}</td>
                          <td>
                            <span className={getPriorityClass(workOrder.priority)}>
                              {workOrder.priority}
                            </span>
                          </td>
                          <td>
                            {workOrder.plannedStartDate 
                              ? new Date(workOrder.plannedStartDate).toLocaleDateString()
                              : 'Not scheduled'
                            }
                          </td>
                          <td>
                            <div className="progress" style={{height: '20px'}}>
                              <div
                                className="progress-bar"
                                role="progressbar"
                                style={{
                                  width: `${(workOrder.quantityCompleted / workOrder.quantityOrdered) * 100}%`
                                }}
                              >
                                {Math.round((workOrder.quantityCompleted / workOrder.quantityOrdered) * 100)}%
                              </div>
                            </div>
                          </td>
                          <td>
                            <div className="btn-group-sm">
                              <Button
                                variant="outline-primary"
                                size="sm"
                                className="me-1"
                                onClick={() => handleShowModal(workOrder)}
                              >
                                <i className="fas fa-edit"></i>
                              </Button>
                              {workOrder.status === 'PLANNED' && (
                                <Button
                                  variant="outline-success"
                                  size="sm"
                                  className="me-1"
                                  onClick={() => handleStart(workOrder.id)}
                                >
                                  <i className="fas fa-play"></i>
                                </Button>
                              )}
                              {workOrder.status === 'IN_PROGRESS' && (
                                <Button
                                  variant="outline-warning"
                                  size="sm"
                                  className="me-1"
                                  onClick={() => handleComplete(workOrder.id, workOrder.quantityOrdered)}
                                >
                                  <i className="fas fa-check"></i>
                                </Button>
                              )}
                              {(workOrder.status === 'PLANNED' || workOrder.status === 'IN_PROGRESS') && (
                                <Button
                                  variant="outline-danger"
                                  size="sm"
                                  onClick={() => handleCancel(workOrder.id)}
                                >
                                  <i className="fas fa-times"></i>
                                </Button>
                              )}
                            </div>
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

      {/* Work Order Modal */}
      <Modal show={showModal} onHide={handleCloseModal} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>
            {editingWorkOrder ? 'Edit Work Order' : 'Create Work Order'}
          </Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Work Order Number</Form.Label>
                  <Form.Control
                    type="text"
                    name="workOrderNumber"
                    value={formData.workOrderNumber}
                    onChange={handleInputChange}
                    placeholder="Leave empty to auto-generate"
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Priority</Form.Label>
                  <Form.Select
                    name="priority"
                    value={formData.priority}
                    onChange={handleInputChange}
                  >
                    <option value="LOW">Low</option>
                    <option value="NORMAL">Normal</option>
                    <option value="HIGH">High</option>
                    <option value="URGENT">Urgent</option>
                  </Form.Select>
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={8}>
                <Form.Group className="mb-3">
                  <Form.Label>Product</Form.Label>
                  <Form.Select
                    name="productId"
                    value={formData.productId}
                    onChange={handleInputChange}
                    required
                  >
                    <option value="">Select a product...</option>
                    {products.map(product => (
                      <option key={product.id} value={product.id}>
                        {product.productCode} - {product.name}
                      </option>
                    ))}
                  </Form.Select>
                </Form.Group>
              </Col>
              <Col md={4}>
                <Form.Group className="mb-3">
                  <Form.Label>Quantity Ordered</Form.Label>
                  <Form.Control
                    type="number"
                    name="quantityOrdered"
                    value={formData.quantityOrdered}
                    onChange={handleInputChange}
                    required
                    min="1"
                  />
                </Form.Group>
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Planned Start Date</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    name="plannedStartDate"
                    value={formData.plannedStartDate}
                    onChange={handleInputChange}
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Planned End Date</Form.Label>
                  <Form.Control
                    type="datetime-local"
                    name="plannedEndDate"
                    value={formData.plannedEndDate}
                    onChange={handleInputChange}
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Notes</Form.Label>
              <Form.Control
                as="textarea"
                rows={3}
                name="notes"
                value={formData.notes}
                onChange={handleInputChange}
                placeholder="Additional notes or instructions..."
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              {editingWorkOrder ? 'Update' : 'Create'} Work Order
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </Container>
  );
};

export default WorkOrders;
